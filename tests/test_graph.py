"""
单元测试：验证 Graph 拓扑结构和代码逻辑
"""
import json
import os
import sys
import pytest

# 确保项目根目录在 path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def test_state_initialization():
    """测试状态初始化"""
    from src.state import initial_state
    state = initial_state("一个登录页面", "LoginPage")
    assert state["user_requirement"] == "一个登录页面"
    assert state["page_name"] == "LoginPage"
    assert state["fix_attempts"] == 0
    assert state["success"] is False


def test_state_auto_page_name():
    """测试自动生成 Page 名"""
    from src.state import initial_state
    state = initial_state("一个天气展示页面")
    assert state["page_name"].endswith("Page")


def test_state_pydantic_model():
    """测试 Phase 2.2：GraphState 已升级为 Pydantic 强类型模型。"""
    from src.state import GraphState
    from pydantic import BaseModel, ValidationError
    assert issubclass(GraphState, BaseModel)
    # 合法构造
    m = GraphState(user_requirement="x", page_name="XPage")
    assert m.user_requirement == "x"
    # 强类型：错误类型应在"写回"时被 Pydantic 拒绝（节点传参出错早暴露）
    try:
        GraphState(error_count="不是数字")
        raised = False
    except ValidationError:
        raised = True
    assert raised, "error_count 应为 int，错误类型必须被 Pydantic 拦截"


def test_state_values_shim():
    """测试 _state_values：LangGraph 在 Pydantic 模式下把 state 作为模型传给节点，
    shim 须能把它转回 dict，兼容节点内的 state.get() 习惯。"""
    from src.state import GraphState, _state_values
    model = GraphState(user_requirement="新闻列表", page_name="NewsListPage")
    as_dict = _state_values(model)
    assert isinstance(as_dict, dict)
    assert as_dict["user_requirement"] == "新闻列表"
    # dict 入参原样返回
    assert _state_values({"a": 1}) == {"a": 1}


def test_graph_builds():
    """测试 Graph 能成功编译"""
    from src.graph import build_graph
    graph = build_graph()
    assert graph is not None


def test_rule_check_valid_code():
    """测试规则检查 — 正确代码应通过"""
    from src.nodes import _rule_check
    valid_code = '''package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager

@Page("TestPage")
internal class TestPage : Pager() {
    override fun body(): ViewBuilder {
        return {
        }
    }
}'''
    errors = _rule_check(valid_code)
    assert len(errors) == 0, f"不应有错误: {errors}"


def test_rule_check_missing_annotation():
    """测试规则检查 — 缺少 @Page"""
    from src.nodes import _rule_check
    code = "package test\nclass A : Pager() {\n override fun body(): ViewBuilder { return {} }\n}"
    errors = _rule_check(code)
    assert any("@Page" in e for e in errors)


def test_rule_check_bracket_mismatch():
    """测试规则检查 — 括号不匹配（kotlinc 可用时报 syntax error，不可用时降级括号匹配）"""
    from src.nodes import _rule_check
    code = "package test\n@Page(\"x\")\nclass A : Pager() {\n override fun body(): ViewBuilder { return {"
    errors = _rule_check(code)
    # kotlinc 主路径报 "syntax error: Expecting '}'"；降级路径报 "大括号不匹配"
    assert errors, "应检测到括号不匹配"
    assert any(
        ("大括号" in e) or ("syntax error" in e.lower()) or ("expecting" in e.lower())
        for e in errors
    )


def test_infer_imports():
    """测试 import 推断"""
    from src.nodes import _infer_imports
    code = '''
@Page("TestPage")
class TestPage : Pager() {
    private var title by observable("")
    override fun body(): ViewBuilder {
        return {
            View { attr { backgroundColor(Color.RED) } }
            Text { attr { text("hi") } }
            Center { }
        }
    }
}'''
    imports = _infer_imports(code)
    assert "com.tencent.kuikly.core.annotations.Page" in imports
    assert "com.tencent.kuikly.core.pager.Pager" in imports
    assert "com.tencent.kuikly.core.reactive.handler.observable" in imports


def test_route_after_check_pass():
    """测试路由 — 编译通过时返回 end"""
    from src.nodes import route_after_check
    assert route_after_check({"compile_passed": True}) == "end"


def test_route_after_check_fail():
    """测试路由 — 编译失败时返回 fix"""
    from src.nodes import route_after_check
    assert route_after_check({"compile_passed": False, "fix_attempts": 0}) == "fix"


def test_route_after_check_max_attempts():
    """测试路由 — 达到最大修正次数返回 end"""
    from src.nodes import route_after_check
    assert route_after_check({"compile_passed": False, "fix_attempts": 3}) == "end"


def test_examples_exist():
    """测试 Few-shot 示例文件存在"""
    examples_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "examples")
    for f in ["01_border_test.kt", "02_event_and_module.kt", "05_login_page.kt"]:
        assert os.path.exists(os.path.join(examples_dir, f)), f"缺少示例: {f}"


def test_classpath_noise_filter():
    """测试 kotlinc classpath 噪声过滤（保护 Phase 3 ① 假阳性修复）。

    合法 Kuikly 代码引用的 com.tencent.kuikly.* 类型不在本机 classpath，
    kotlinc 会报 unresolved reference / cannot infer type 等，但这些都不是真语法
    错误，必须被过滤，否则会把通过的页面误判为失败。
    """
    from src.nodes import _is_classpath_noise
    assert _is_classpath_noise("/x.kt:5:9: error: unresolved reference: Color")
    assert _is_classpath_noise(
        "/x.kt:68:46: error: cannot infer type for value parameter 'newValue'. Specify it explicitly."
    )
    assert _is_classpath_noise("/x.kt:10:10: error: type mismatch: ...")
    assert _is_classpath_noise("/x.kt:10:10: error: unresolved type: Foo")
    # 真语法错误必须保留
    assert not _is_classpath_noise("/x.kt:6:6: error: syntax error: Expecting '}'.")
    assert not _is_classpath_noise("/x.kt:2:10: error: Expecting ')'.")


def test_test_cases_json():
    """测试测试用例 JSON 格式正确"""
    test_file = os.path.join(os.path.dirname(__file__), "test_cases.json")
    with open(test_file, "r") as f:
        cases = json.load(f)
    assert len(cases) == 10
    for case in cases:
        assert "requirement" in case
        assert "expected_type" in case


# ──────────────────────────────────────────────────────────────
# P0① LLM 重试容错
# ──────────────────────────────────────────────────────────────
def test_is_retryable_error():
    """瞬态错误（限流/超时/5xx）应判可重试；鉴权/参数错误不应重试。"""
    from src.llm import _is_retryable_error

    class RateLimitError(Exception):
        status_code = 429

    assert _is_retryable_error(RateLimitError())
    assert _is_retryable_error(TimeoutError("request timed out"))
    assert _is_retryable_error(Exception("service unavailable"))
    # 非瞬态：不可重试
    assert not _is_retryable_error(ValueError("invalid api key"))
    assert not _is_retryable_error(Exception("authentication failed"))


def test_call_llm_retry_recovers():
    """第一次调用抛瞬态错误，重试后应恢复并返回正确结果。"""
    from src.llm import call_llm

    class FakeLLM:
        def __init__(self):
            self.calls = 0

        def invoke(self, prompt):
            self.calls += 1
            if self.calls == 1:
                raise TimeoutError("request timed out")
            return type("Resp", (), {"content": "ok"})()

        def stream(self, prompt):
            raise AssertionError("不应走到 stream")

    llm = FakeLLM()
    out = call_llm(llm, "hi", max_retries=3, backoff=1.0, initial_delay=0.0)
    assert out == "ok"
    assert llm.calls == 2, "应重试 1 次后成功"


def test_call_llm_nonretryable_raises():
    """非瞬态错误不应无限重试，最终抛出 RuntimeError。"""
    from src.llm import call_llm

    class FakeLLM:
        def invoke(self, prompt):
            raise ValueError("invalid request")

        def stream(self, prompt):
            raise ValueError("invalid request")

    with pytest.raises(RuntimeError):
        call_llm(FakeLLM(), "hi", max_retries=2, backoff=1.0, initial_delay=0.0)


# ──────────────────────────────────────────────────────────────
# P0② 确定性规则符合度评分
# ──────────────────────────────────────────────────────────────
def test_kuikly_compliance_valid_code():
    """合法 Kuikly 页面应拿满分硬规则符合度。"""
    from src.nodes import _kuikly_compliance
    code = '''package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.*

@Page("TestPage")
internal class TestPage : Pager() {
    private var title by observable("")
    override fun body(): ViewBuilder {
        return {
            View { attr { size(100f, 50f); backgroundColor(Color(0xFF000000)) } }
            Text { attr { text("hi") } event { click { title = "x" } } }
        }
    }
}'''
    res = _kuikly_compliance(code)
    assert res["score"] == 1.0
    assert res["hard_passed"] == res["hard_total"] == 7
    assert res["hard_failed"] == []


def test_kuikly_compliance_bad_code():
    """坏代码应拿低分，且能列出未通过的硬规则。"""
    from src.nodes import _kuikly_compliance
    res = _kuikly_compliance("just some random text")
    assert res["score"] < 1.0
    assert res["hard_failed"], "应列出未通过的硬规则"

    res_empty = _kuikly_compliance("")
    assert res_empty["score"] == 0.0
    assert res_empty["hard_passed"] == 0
