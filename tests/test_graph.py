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
    # 继承/override 连锁噪声（基类不在 classpath 时的误报）
    assert _is_classpath_noise("/x.kt:16:28: error: this type is final, so it cannot be extended.")
    assert _is_classpath_noise("/x.kt:16:28: error: none of the following candidates is applicable:")
    assert _is_classpath_noise("/x.kt:17:5: error: 'moduleName' overrides nothing.")
    # 比较符作用在类型未知的接收者上（用例4实测的误报）
    assert _is_classpath_noise(
        "/x.kt:111:47: error: 'operator' modifier is required on 'fun <T> Comparable<T>.compareTo(other: T): Int'"
    )
    # 真语法错误必须保留
    assert not _is_classpath_noise("/x.kt:6:6: error: syntax error: Expecting '}'.")
    assert not _is_classpath_noise("/x.kt:2:10: error: Expecting ')'.")


def test_dedupe_class_imports():
    """测试同名类多路径导入去重（用例4实测修复）。

    LLM 组装可能写错组件包路径（views.Center），确定性注入又会补正确路径
    （views.layout.Center）→ 同名类双路径共存。去重必须保留白名单优选路径、
    删除其余，且不动通配 import 和唯一导入。
    """
    from src.nodes import _dedupe_class_imports
    code = "\n".join([
        "package com.tencent.kuikly.demo.pages",
        "",
        "import com.tencent.kuikly.core.views.Center",          # 错误路径，应删
        "import com.tencent.kuikly.core.views.*",               # 通配，保留
        "import com.tencent.kuikly.core.views.layout.Center",   # 正确路径，保留
        "import com.tencent.kuikly.core.base.Color",            # 唯一导入，保留
        "",
        "@Page(\"P\")",
        "internal class P : Pager() {",
        "    override fun body(): ViewBuilder { return {} }",
        "}",
    ])
    out = _dedupe_class_imports(code)
    assert "import com.tencent.kuikly.core.views.layout.Center" in out
    assert "import com.tencent.kuikly.core.views.Center\n" not in out
    assert "import com.tencent.kuikly.core.views.*" in out
    assert "import com.tencent.kuikly.core.base.Color" in out
    # 无重复时原样返回
    assert _dedupe_class_imports("import com.tencent.kuikly.core.base.Color\n\nclass X") == \
        "import com.tencent.kuikly.core.base.Color\n\nclass X"


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


# ──────────────────────────────────────────────────────────────
# 自审证据纪律（用例2实测：真假指控混排耗尽修正次数导致不收敛）
# ──────────────────────────────────────────────────────────────
def test_annotate_error_source():
    """错误来源标注必须三分辨：kotlinc 行 / 结构规则 / LLM 自审。"""
    from src.nodes import _annotate_error_source
    kotlinc = "/tmp/xx.kt:127:77: error: no parameter with name 'isOn' found."
    assert _annotate_error_source(kotlinc).startswith("[编译器]")
    assert _annotate_error_source("缺少 @Page 注解").startswith("[结构规则]")
    # LLM 自审的"缺少 xxx"不能因开头撞词被误判成结构规则
    assert _annotate_error_source("缺少背景色设置，建议补充").startswith("[AI自审]")
    assert _annotate_error_source("第 72 行：attr 块中 flexDirectionRow() 方法不存在").startswith("[AI自审]")


def test_compile_check_unbacked_fail_passes():
    """审查器给 passed=false 却列不出任何问题 → 无据可修，应视为通过。

    用例2家族漏洞：LLM 自审软判 fail 但 errors 为空时，auto_fix 会拿空
    问题列表空转，白烧 3 次修正额度导致整页失败。
    """
    import src.nodes as n

    code = '''package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager

@Page("T")
internal class T : Pager() {
    override fun body(): ViewBuilder {
        return { }
    }
}'''
    orig_rule = n._rule_check
    orig_json = n.call_llm_json
    orig_llm = n._get_llm
    n._rule_check = lambda c: []
    n._get_llm = lambda: object()
    n.call_llm_json = lambda llm, prompt: {"passed": False, "errors": [], "warnings": ["整体感觉不佳"]}
    try:
        state = n.node_compile_check({"assembled_code": code})
    finally:
        n._rule_check = orig_rule
        n.call_llm_json = orig_json
        n._get_llm = orig_llm
    assert state["compile_passed"] is True, "无据软判 fail 不应挡住快乐路径"
    assert state["error_count"] == 0
    assert state["final_code"], "通过时必须落盘最终代码"


# ──────────────────────────────────────────────────────────────
# 路 B：组件白名单 + Module 知识 + 真编译验证
# ──────────────────────────────────────────────────────────────
def test_component_whitelist_text():
    """组件白名单应非空，且标注 Button（compose 包）/ Center（layout 包）精确 import。"""
    from src.prompts import _component_whitelist_text, KUIKLY_COMPONENT_WHITELIST
    text = _component_whitelist_text()
    assert len(KUIKLY_COMPONENT_WHITELIST) >= 20, "白名单应覆盖官方组件清单"
    assert "com.tencent.kuikly.core.views.compose.Button" in text
    assert "com.tencent.kuikly.core.views.layout.Center" in text
    # 白名单里不应有木鱼实测时猜错的那类"不存在组件"（这里只抽查名字格式）
    names = [n for n, _, _ in KUIKLY_COMPONENT_WHITELIST]
    assert "View" in names and "Text" in names and "Button" in names


def test_module_reference_loaded():
    """Module 知识常量应包含持久化与音频/震动的真实 API。"""
    from src.prompts import KUIKLY_MODULE_REFERENCE
    # 持久化（内置 SharedPreferencesModule 真实方法）
    assert "SharedPreferencesModule" in KUIKLY_MODULE_REFERENCE
    assert "setInt" in KUIKLY_MODULE_REFERENCE and "getInt" in KUIKLY_MODULE_REFERENCE
    # 音频/震动（自定义 Module 真实写法）
    assert "asyncToNativeMethod" in KUIKLY_MODULE_REFERENCE
    assert "createExternalModules" in KUIKLY_MODULE_REFERENCE
    # 应明确提示"无内置音频/震动"，防止 LLM 臆造平台 API
    assert "AudioHapticsModule" in KUIKLY_MODULE_REFERENCE


def test_infer_imports_button_compose_pkg():
    """Button 应推断到 compose 包（不是 views 包），这是木鱼猜错 import 的坑。"""
    from src.nodes import _infer_imports
    code = "Button { attr { text(\"ok\") } }"
    imports = _infer_imports(code)
    assert "com.tencent.kuikly.core.views.compose.Button" in imports
    # 不应出现错误的 views 包 Button
    assert "com.tencent.kuikly.core.views.Button" not in imports


def test_infer_imports_module_classes():
    """Module 相关类应推断出对应 import。"""
    from src.nodes import _infer_imports
    code = """
class AudioHapticsModule : Module() {
    override fun moduleName(): String = "KRAudioHapticsModule"
    fun playSound(s: String) { asyncToNativeMethod("playSound", JSONObject().put("s", s), null) }
}
"""
    imports = _infer_imports(code)
    assert "com.tencent.kuikly.core.module.Module" in imports
    assert "com.tencent.kuikly.core.nvi.serialization.json.JSONObject" in imports


def test_find_kuikly_classpath_env():
    """KUIKLY_CLASSPATH 环境变量应被识别；未设置时返回 None（降级纯语法）。"""
    import importlib
    import os
    import src.nodes as nodes
    importlib.reload(nodes)
    # 未设置 → None
    os.environ.pop("KUIKLY_CLASSPATH", None)
    assert nodes._find_kuikly_classpath() is None
    # 设置 → 返回该值
    os.environ["KUIKLY_CLASSPATH"] = "/tmp/kuikly.jar"
    assert nodes._find_kuikly_classpath() == "/tmp/kuikly.jar"
    os.environ.pop("KUIKLY_CLASSPATH", None)


def test_verify_compile_returns_mode():
    """verify_compile 应返回 mode/classpath_used/errors 三字段。"""
    import importlib
    import os
    import src.nodes as nodes
    importlib.reload(nodes)
    os.environ.pop("KUIKLY_CLASSPATH", None)
    code = "package x\n@Page(\"A\")\nclass A : Pager() {\n override fun body(): ViewBuilder { return {} }\n}"
    res = nodes.verify_compile(code)
    assert res["mode"] in ("syntax", "unavailable", "full")
    assert "classpath_used" in res
    assert "errors" in res


def test_examples_module_files_exist():
    """路 B 新增的 few-shot 示例文件应存在。"""
    examples_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "examples")
    for f in ["06_audio_module.kt", "07_shared_prefs.kt"]:
        assert os.path.exists(os.path.join(examples_dir, f)), f"缺少示例: {f}"


# ──────────────────────────────────────────────────────────────
# 路 B③ 修复：持久化信号传导 + 幻觉 API 补漏
# ──────────────────────────────────────────────────────────────
def test_decompose_gen_input_prompts_propagate_requirement():
    """拆解/交互两个节点的 prompt 必须含 user_requirement 占位，
    否则"要持久化"信号在拆解阶段就被截断，导致能力随机丢失（v3 实测暴露）。"""
    from src.prompts import PROMPT_DECOMPOSE_PAGE, PROMPT_GEN_INPUT
    assert "{user_requirement}" in PROMPT_DECOMPOSE_PAGE
    assert "{user_requirement}" in PROMPT_GEN_INPUT
    # 解析结论（含"含持久化/含音效"标注）也应传导，双保险
    assert "{parsed_intent}" in PROMPT_DECOMPOSE_PAGE
    assert "{parsed_intent}" in PROMPT_GEN_INPUT


def test_hallucination_blacklist_covers_v3_residuals():
    """幻觉 API 黑名单必须覆盖 v3 实测暴露的 onClick/textColor/ImageUri 三类。"""
    from src.prompts import KUIKLY_API_REFERENCE
    assert "onClick" in KUIKLY_API_REFERENCE
    assert "textColor" in KUIKLY_API_REFERENCE
    assert "base.attr.ImageUri" in KUIKLY_API_REFERENCE


def test_infer_imports_image_uri_attr_pkg():
    """ImageUri 应推断到 base.attr 包（v3 猜成了 views.ImageUri 是错的）。"""
    from src.nodes import _infer_imports
    imports = _infer_imports("Image { attr { src(ImageUri.pageAssets(\"a.png\")) } }")
    assert "com.tencent.kuikly.core.base.attr.ImageUri" in imports


def test_merge_imports_injects_missing_and_dedups():
    """_merge_imports 应注入缺失 import、跳过已有、不插进 class 体。"""
    from src.nodes import _merge_imports
    code = """package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager

@Page("A")
internal class A : Pager() {
    override fun body(): ViewBuilder { return {} }
}"""
    merged = _merge_imports(code, [
        "com.tencent.kuikly.core.annotations.Page",  # 已存在，应跳过
        "com.tencent.kuikly.core.base.attr.ImageUri",  # 缺失，应注入
    ])
    # 缺失项被注入
    assert "import com.tencent.kuikly.core.base.attr.ImageUri" in merged
    # 已有项不重复
    assert merged.count("import com.tencent.kuikly.core.annotations.Page") == 1
    # 注入位置在 package 与既有 import 之后、@Page 之前
    assert merged.index("import com.tencent.kuikly.core.base.attr.ImageUri") < merged.index("@Page")
    # 无 import 的代码原样返回
    assert _merge_imports("just text", []) == "just text"


def test_assemble_uses_deterministic_imports():
    """组装节点必须调用 _merge_imports（不能只算 imports_needed 却不注入代码）。"""
    import inspect
    from src import nodes as n
    src = inspect.getsource(n.node_assemble)
    assert "_merge_imports(code, imports)" in src, "node_assemble 应把推断的 import 注入代码"


def test_autofix_uses_deterministic_imports():
    """自动修正节点也必须重注入 import（否则修正一次就丢 ImageUri 等 import）。"""
    import inspect
    from src import nodes as n
    src = inspect.getsource(n.node_auto_fix)
    assert "_infer_imports(fixed_code)" in src, "node_auto_fix 应重算 import"
    assert "_merge_imports(fixed_code, imports)" in src, "node_auto_fix 应重注入 import"


def test_drop_unbacked_api_claims():
    """真编译 0 错误时，无编译佐证的「API 存在性」指控应被丢弃（v8 实测 12/13 假）。

    生成器经漂移块学会 jar 真实 API（pagerData/titleAttr 等），审查器知识落后
    把它们指控为「不存在」——但代码 kotlinc 真编译 0 错误，编译器才是 API 真假
    的唯一权威。非真编译口径 / kotlinc 本身报错时不启用。
    """
    import src.nodes as n

    fake_cp = "/tmp/fake-classes.jar"

    # 场景1：classpath 有 + 编译 0 错误 → API 存在性指控丢弃，其他保留
    orig = n._find_kuikly_classpath
    n._find_kuikly_classpath = lambda: fake_cp
    try:
        llm_errors = [
            "第 56 行：pagerData 属性不存在于 Pager 类中，应使用 pageWidth",
            "第 72 行：Input 组件不支持 textDidChange 事件，应使用 textChange 事件",
            "第 17 行：import 与前面的具体 import 冲突，应移除通配符导入",
        ]
        kept = n._drop_unbacked_api_claims(llm_errors, [])
        assert len(kept) == 1, f"应只剩 1 条非存在性指控，实际 {kept}"
        assert "通配符" in kept[0]

        # 场景2：classpath 无 → 不过滤，原样保留
        n._find_kuikly_classpath = lambda: ""
        kept = n._drop_unbacked_api_claims(llm_errors, [])
        assert len(kept) == 3, "无 classpath 时不启用过滤"

        # 场景3：classpath 有但 kotlinc 本身报错 → 不过滤（编译器正忙，无权威）
        n._find_kuikly_classpath = lambda: fake_cp
        kept = n._drop_unbacked_api_claims(
            llm_errors, ["/tmp/x.kt:12:34: error: unresolved reference 'foo'"]
        )
        assert len(kept) == 3, "kotlinc 有真错误时不启用过滤"
    finally:
        n._find_kuikly_classpath = orig

    # 场景4：正则覆盖 v8 全部假指控形态
    v8_claims = [
        "asyncToNativeMethod 方法不存在于 Kuikly Module 基类中，应使用 callNativeMethod",
        "pagerData 属性不存在于 Pager 类中",
        "Input 组件不支持 textDidChange 事件",
        "Button 组件不支持 titleAttr 属性",
        "AlertDialog 组件不支持 clickActionButton 事件，应使用 buttonClick 事件",
    ]
    for c in v8_claims:
        assert n._API_CLAIM_RE.search(c), f"正则应命中: {c}"
