"""
LangGraph 节点实现
==================
6 个节点的具体实现，每个节点是一个纯函数：state → state (partial)

拓扑结构：
  ①需求解析 → ②页面拆解 → ┬→ ③a 布局代码 ─┐
                           ├→ ③b 交互代码 ─┼→ ④代码组装 → ⑤编译检查 ─→ ⑥自动修正 ─┐
                           └→ ③c 样式代码 ─┘                                      │
                                                                                 └──→ 回⑤
"""

from __future__ import annotations

import time
import json
import re
import os
import shutil
import subprocess
import tempfile
from typing import Dict, Any

from .state import GraphState, initial_state, _state_values
from .llm import get_llm, call_llm, call_llm_json
from .prompts import (
    fill_prompt, get_prompt, KUIKLY_API_REFERENCE, get_kuikly_api_reference, _load_example
)


# ─── 全局 LLM 实例（惰性加载） ───────────────────────────────
_llm = None


def _get_llm():
    global _llm
    if _llm is None:
        _llm = get_llm()
    return _llm


# ═══════════════════════════════════════════════════════════════
# 节点①：需求解析
# ═══════════════════════════════════════════════════════════════
def node_parse_requirement(state: Dict[str, Any]) -> Dict[str, Any]:
    """解析用户自然语言需求 → 结构化页面描述"""
    state = _state_values(state)
    print("\n━" * 30)
    print("① 需求解析")
    print("━" * 30)
    print(f"  输入: {state.get('user_requirement', '')[:80]}")

    prompt = fill_prompt(
        get_prompt("parse_requirement"),
        user_requirement=state["user_requirement"],
    )

    result = call_llm_json(_get_llm(), prompt)

    return {
        "page_name": result.get("page_name", state.get("page_name", "GeneratedPage")),
        "page_type": result.get("page_type", "other"),
        "components_needed": result.get("components_needed", ["View", "Text"]),
        "parsed_intent": result.get("description", ""),
    }


# ═══════════════════════════════════════════════════════════════
# 节点②：页面拆解
# ═══════════════════════════════════════════════════════════════
def node_decompose_page(state: Dict[str, Any]) -> Dict[str, Any]:
    """将页面拆解为布局/交互/样式三模块"""
    state = _state_values(state)
    print("\n② 页面拆解")
    print(f"  页面: {state.get('page_name')} ({state.get('page_type')})")

    prompt = fill_prompt(
        get_prompt("decompose_page"),
        page_name=state.get("page_name", "Page"),
        page_type=state.get("page_type", "other"),
        components_needed=", ".join(state.get("components_needed", [])),
        api_reference=get_kuikly_api_reference(),
        example_border=_load_example("01_border_test.kt"),
        example_event=_load_example("02_event_and_module.kt"),
    )

    result = call_llm_json(_get_llm(), prompt)

    return {
        "layout_plan": result.get("layout_plan", ""),
        "input_plan": result.get("input_plan", ""),
        "style_plan": result.get("style_plan", ""),
    }


# ═══════════════════════════════════════════════════════════════
# 节点③a：布局代码生成
# ═══════════════════════════════════════════════════════════════
def node_gen_layout(state: Dict[str, Any]) -> Dict[str, Any]:
    """生成布局视图树代码"""
    state = _state_values(state)
    print("  ├─ ③a 布局代码生成...")

    prompt = fill_prompt(
        get_prompt("gen_layout"),
        page_name=state.get("page_name", "Page"),
        layout_plan=state.get("layout_plan", ""),
        api_reference=get_kuikly_api_reference(),
        example_border=_load_example("01_border_test.kt"),
    )

    code = call_llm(_get_llm(), prompt)
    return {"layout_code": code.strip()}


# ═══════════════════════════════════════════════════════════════
# 节点③b：交互代码生成
# ═══════════════════════════════════════════════════════════════
def node_gen_input(state: Dict[str, Any]) -> Dict[str, Any]:
    """生成交互逻辑代码"""
    state = _state_values(state)
    print("  ├─ ③b 交互代码生成...")

    prompt = fill_prompt(
        get_prompt("gen_input"),
        page_name=state.get("page_name", "Page"),
        input_plan=state.get("input_plan", ""),
    )

    code = call_llm(_get_llm(), prompt)
    return {"input_code": code.strip()}


# ═══════════════════════════════════════════════════════════════
# 节点③c：样式代码生成
# ═══════════════════════════════════════════════════════════════
def node_gen_style(state: Dict[str, Any]) -> Dict[str, Any]:
    """生成样式代码片段"""
    state = _state_values(state)
    print("  └─ ③c 样式代码生成...")

    prompt = fill_prompt(
        get_prompt("gen_style"),
        page_name=state.get("page_name", "Page"),
        style_plan=state.get("style_plan", ""),
    )

    code = call_llm(_get_llm(), prompt)
    return {"style_code": code.strip()}


def _strip_markdown(text: str) -> str:
    """去除 LLM 输出中的 markdown 包装（```kotlin ... ```）"""
    import re
    # 去掉开头的 ```kotlin / ```kotlin / ``` 等
    text = re.sub(r'^\s*```(?:kotlin|kt|java)?\s*\n?', '', text)
    # 去掉结尾的 ```
    text = re.sub(r'\n?```\s*$', '', text)
    return text.strip()


# ═══════════════════════════════════════════════════════════════
# 节点④：代码组装
# ═══════════════════════════════════════════════════════════════
def node_assemble(state: Dict[str, Any]) -> Dict[str, Any]:
    """将三模块代码片段组装为完整 .kt 文件"""
    state = _state_values(state)
    print("\n④ 代码组装")

    prompt = fill_prompt(
        get_prompt("assemble"),
        page_name=state.get("page_name", "GeneratedPage"),
        page_type=state.get("page_type", "other"),
        layout_code=state.get("layout_code", ""),
        input_code=state.get("input_code", ""),
        style_code=state.get("style_code", ""),
        example_login=_load_example("05_login_page.kt"),
    )

    code = call_llm(_get_llm(), prompt)
    code = _strip_markdown(code)  # 清洗 markdown 包装

    # 推断 import
    imports = _infer_imports(code)
    print(f"  组装完成: {len(code)} chars, {len(imports)} imports")

    return {"assembled_code": code.strip(), "imports_needed": imports}


def _infer_imports(code: str) -> list[str]:
    """从代码中推断需要的 import 语句"""
    imports = []
    if "@Page" in code:
        imports.append("com.tencent.kuikly.core.annotations.Page")
    if "Pager()" in code or "Pager()" in code:
        imports.append("com.tencent.kuikly.core.pager.Pager")
    if "ViewBuilder" in code:
        imports.append("com.tencent.kuikly.core.base.ViewBuilder")
    if "Color(" in code:
        imports.append("com.tencent.kuikly.core.base.Color")
    if "Border(" in code:
        imports.append("com.tencent.kuikly.core.base.Border")
        imports.append("com.tencent.kuikly.core.base.BorderStyle")
    if "observable(" in code:
        imports.append("com.tencent.kuikly.core.reactive.handler.observable")
    if "View {" in code or "Text {" in code or "Image {" in code:
        imports.append("com.tencent.kuikly.core.views.*")
    if "Center {" in code:
        imports.append("com.tencent.kuikly.core.views.layout.Center")
    if "Scroller {" in code:
        imports.append("com.tencent.kuikly.core.views.Scroller")
    return list(dict.fromkeys(imports))  # 去重保序


# ═══════════════════════════════════════════════════════════════
# 节点⑤：编译检查（规则 + LLM 双重检查）
# ═══════════════════════════════════════════════════════════════
def node_compile_check(state: Dict[str, Any]) -> Dict[str, Any]:
    """对生成的代码做编译检查（先规则检查，再 LLM 检查）"""
    state = _state_values(state)
    print("\n⑤ 编译检查")

    code = state.get("assembled_code", state.get("final_code", ""))

    # 规则检查（快）
    rule_errors = _rule_check(code)

    # LLM 检查（深）
    prompt = fill_prompt(get_prompt("compile_check"), code=code)
    llm_result = call_llm_json(_get_llm(), prompt)

    errors = rule_errors + llm_result.get("errors", [])
    passed = len(errors) == 0 and llm_result.get("passed", True)

    print(f"  规则检查: {len(rule_errors)} 问题")
    print(f"  LLM 检查: {'PASS' if llm_result.get('passed') else 'FAIL'}")
    print(f"  总计: {len(errors)} 问题, {'✓ 通过' if passed else '✗ 需修正'}")

    return {
        "compile_passed": passed,
        "compile_errors": errors,
        "error_count": len(errors),
        # 快乐路径：首次检查就通过时不会进 auto_fix，这里必须把最终代码落盘
        "final_code": code if passed else state.get("final_code", ""),
    }


def _kotlinc_path() -> str | None:
    """定位 kotlinc 可执行文件（兼容 PATH 未含 brew 的情况）。"""
    p = shutil.which("kotlinc")
    if p:
        return p
    for cand in ("/opt/homebrew/bin/kotlinc", "/usr/local/bin/kotlinc"):
        if os.path.isfile(cand):
            return cand
    return None


def _is_classpath_noise(line: str) -> bool:
    """判断一行 kotlinc 报错是否属于 classpath 缺失导致的"类型/符号噪声"。

    本机没有 Kuikly classpath，kotlinc 会把"未解析的引用""lambda 形参类型推断失败"
    等也报成 error，但它们都不是真语法错误（代码本身能解析），且必然随 Kuikly
    类库缺失而出现，因此当作噪声过滤，只保留真正的解析/语法错误。
    真语法错误典型形如 "...: error: syntax error: Expecting '}'"。
    """
    _CLASSPATH_NOISE = (
        "unresolved reference",          # 引用的类/符号不在 classpath（缺 Kuikly 库）
        "cannot infer type",             # lambda 形参类型推断失败（缺 Kuikly 签名）
        "type mismatch",                 # 类型比对失败（缺 Kuikly 类型信息）
        "no value passed for parameter", # 形参缺失（接收者类型未知时误报）
        "overload resolution ambiguity", # 重载歧义（缺 Kuikly 重载信息）
        "unresolved type",               # 未解析的类型名（缺 Kuikly 类型）
    )
    low = line.lower()
    return any(n in low for n in _CLASSPATH_NOISE)


def _kotlinc_syntax_errors(code: str) -> list[str]:
    """用 kotlinc 做真实 Kotlin 语法检查。

    说明：生成的代码引用了大量 com.tencent.kuikly.* 类，但本地没有 Kuikly
    classpath，所以 kotlinc 会把"未解析的引用"也报成 error。这里只收集
    **真实语法错误**（括号/关键字/结构错误），过滤掉 classpath 缺失导致的
    类型/符号噪声（见 `_is_classpath_noise`），从而得到"真语法校验"而非"真编译"。
    kotlinc 不可用时返回空列表，由调用方降级处理。
    """
    kotlinc = _kotlinc_path()
    if not kotlinc:
        return []
    with tempfile.NamedTemporaryFile("w", suffix=".kt", delete=False, encoding="utf-8") as f:
        f.write(code)
        path = f.name
    try:
        proc = subprocess.run(
            [kotlinc, path],
            capture_output=True, text=True, timeout=60,
        )
    except (subprocess.TimeoutExpired, OSError):
        return []
    finally:
        try:
            os.unlink(path)
        except OSError:
            pass

    errors = []
    for line in (proc.stderr + proc.stdout).splitlines():
        # kotlinc 报错行形如 "xxx.kt:行号: error: 描述"
        if ": error:" not in line:
            continue
        # 容忍 classpath 缺失导致的类型/符号噪声（非语法问题，交给 LLM 检查兜底）
        if _is_classpath_noise(line):
            continue
        errors.append(line.strip())
    return errors


def _rule_check(code: str) -> list[str]:
    """基于规则的编译检查：优先 kotlinc 真语法校验，不可用时降级括号匹配。

    结构类规则（@Page / Pager / body / package 等）与 classpath 无关，始终生效。
    """
    errors = []

    # ① 语法层：优先用 kotlinc 做真实语法检查
    syntax_errors = _kotlinc_syntax_errors(code)
    if syntax_errors:
        errors.extend(syntax_errors)
    else:
        # kotlinc 不可用时的降级：括号匹配
        if code.count("{") != code.count("}"):
            errors.append(f"大括号不匹配: {{={code.count('{')}, }}={code.count('}')}")
        if code.count("(") != code.count(")"):
            errors.append(f"圆括号不匹配: (={code.count('(')}, )={code.count(')')}")

    # ② 结构层规则（与 classpath 无关，始终检查）
    if "@Page(" not in code:
        errors.append("缺少 @Page 注解")
    if ": Pager()" not in code and ": BasePager()" not in code:
        errors.append("未继承 Pager() 或 BasePager()")
    if "override fun body()" not in code:
        errors.append("缺少 body() 方法重写")
    if "ViewBuilder" not in code:
        errors.append("缺少 ViewBuilder 返回类型")
    if not code.strip().startswith("package "):
        errors.append("缺少 package 声明")
    return errors


# ═══════════════════════════════════════════════════════════════
# 节点⑥：自动修正
# ═══════════════════════════════════════════════════════════════
def node_auto_fix(state: Dict[str, Any]) -> Dict[str, Any]:
    """基于编译检查结果自动修正代码"""
    state = _state_values(state)
    fix_attempts = state.get("fix_attempts", 0) + 1
    errors = state.get("compile_errors", [])
    code = state.get("assembled_code", state.get("final_code", ""))

    print(f"\n⑥ 自动修正 (第 {fix_attempts} 次)")
    print(f"  问题数: {len(errors)}")

    if fix_attempts > 3:
        print("  ⚠ 已达最大修正次数 (3)，停止修正")
        return {
            "final_code": code,
            "success": False,
            "fix_attempts": fix_attempts,
            "fix_applied": "已达最大修正次数",
        }

    prompt = fill_prompt(
        get_prompt("auto_fix"),
        code=code,
        errors="\n".join(f"- {e}" for e in errors),
        fix_attempts=fix_attempts - 1,
        api_reference=get_kuikly_api_reference(),
    )

    fixed_code = call_llm(_get_llm(), prompt)
    fixed_code = _strip_markdown(fixed_code)
    print(f"  修正完成: {len(fixed_code)} chars")

    return {
        "assembled_code": fixed_code.strip(),
        "final_code": fixed_code.strip(),
        "fix_attempts": fix_attempts,
        "fix_applied": f"第{fix_attempts}次修正",
        "compile_passed": None,  # 触发重新检查
        "compile_errors": [],
        "error_count": 0,
    }


# ═══════════════════════════════════════════════════════════════
# 确定性规则符合度评分（P0② 诚实化评估）
# ═══════════════════════════════════════════════════════════════
# 与 _rule_check 的区别：
#   - _rule_check 是 pipeline 内的「要不要进 auto_fix」布尔门（影响 success）；
#   - _kuikly_compliance 是对最终代码的**确定性、可量化**打分，完全不依赖 LLM 自审，
#     用于 eval 报告 / CI 质量门禁，解决「success 由 LLM 自审软判、不够硬」的问题。
#
# 规则分两类：
#   - 硬规则（hard）：任何合法 Kuikly Page 都必须满足，评分只按硬规则算；
#   - 软规则（soft）：视页面场景而定（无交互页面可能没有 event/observable），
#     单独列出，不参与评分，避免误伤合法页面。
_HARD_RULES = [
    ("page_annotation", "声明 @Page 注解", lambda c: "@Page(" in c),
    ("extends_pager", "继承 Pager() / BasePager()", lambda c: ": Pager()" in c or ": BasePager()" in c),
    ("body_signature", "重写 body(): ViewBuilder", lambda c: "override fun body(): ViewBuilder" in c),
    ("body_lambda", "body() 返回 lambda 树", lambda c: "return {" in c),
    ("package_decl", "声明 package", lambda c: c.strip().startswith("package ")),
    ("kuikly_import", "导入 com.tencent.kuikly 类", lambda c: "import com.tencent.kuikly" in c),
    ("attr_block", "组件使用 attr {} 块", lambda c: "attr {" in c),
]

_SOFT_RULES = [
    ("observable_state", "状态用 by observable 声明", lambda c: "by observable(" in c),
    ("event_handler", "事件用 event { } 处理", lambda c: "event {" in c),
    ("color_literal", "颜色用 Color(...) 表达", lambda c: "Color(" in c or "Color." in c),
]


def _kuikly_compliance(code: str) -> dict:
    """对最终代码做确定性规则符合度评分（不依赖 LLM 自审）。

    返回:
        {
          "score": 0.0~1.0,     # 硬规则通过比例
          "hard_total": int,
          "hard_passed": int,
          "hard_failed": [str], # 未通过的硬规则描述
          "soft_passed": [str],
          "soft_failed": [str],
        }
    """
    if not code:
        return {
            "score": 0.0,
            "hard_total": len(_HARD_RULES),
            "hard_passed": 0,
            "hard_failed": [desc for _, desc, _ in _HARD_RULES],
            "soft_passed": [],
            "soft_failed": [desc for _, desc, _ in _SOFT_RULES],
        }
    hard_passed = [desc for _, desc, check in _HARD_RULES if check(code)]
    hard_failed = [desc for _, desc, check in _HARD_RULES if not check(code)]
    soft_passed = [desc for _, desc, check in _SOFT_RULES if check(code)]
    soft_failed = [desc for _, desc, check in _SOFT_RULES if not check(code)]
    return {
        "score": round(len(hard_passed) / len(_HARD_RULES), 4),
        "hard_total": len(_HARD_RULES),
        "hard_passed": len(hard_passed),
        "hard_failed": hard_failed,
        "soft_passed": soft_passed,
        "soft_failed": soft_failed,
    }


# ═══════════════════════════════════════════════════════════════
# 路由函数
# ═══════════════════════════════════════════════════════════════
def route_after_check(state: Dict[str, Any]) -> str:
    """编译检查后的路由：通过→结束，失败→自动修正"""
    state = _state_values(state)
    if state.get("compile_passed", False):
        return "end"
    if state.get("fix_attempts", 0) >= 3:
        return "end"
    return "fix"
