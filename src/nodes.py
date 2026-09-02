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
    fill_prompt, get_prompt, KUIKLY_API_REFERENCE, get_kuikly_api_reference, _load_example,
    KUIKLY_MODULE_REFERENCE, _component_whitelist_text,
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
        component_whitelist=_component_whitelist_text(),
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
        user_requirement=state.get("user_requirement", ""),
        parsed_intent=state.get("parsed_intent", ""),
        page_name=state.get("page_name", "Page"),
        page_type=state.get("page_type", "other"),
        components_needed=", ".join(state.get("components_needed", [])),
        api_reference=get_kuikly_api_reference(),
        module_reference=KUIKLY_MODULE_REFERENCE,
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
        user_requirement=state.get("user_requirement", ""),
        parsed_intent=state.get("parsed_intent", ""),
        page_name=state.get("page_name", "Page"),
        input_plan=state.get("input_plan", ""),
        module_reference=KUIKLY_MODULE_REFERENCE,
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
        example_audio=_load_example("06_audio_module.kt"),
        module_reference=KUIKLY_MODULE_REFERENCE,
    )

    code = call_llm(_get_llm(), prompt)
    code = _strip_markdown(code)  # 清洗 markdown 包装

    # 推断 import，并**确定性注入**缺失的 import（不能只算不用）。
    # 木鱼 v4 实测：LLM 组装时漏了 ImageUri 的 import（代码用了 ImageUri.pageAssets
    # 却没 import），第①档语法校验会把它当噪声放过，但真编译（第②档）会 unresolved
    # reference。这里用 _infer_imports 的确定性结论补上，避免"猜 import 骗过校验"。
    imports = _infer_imports(code)
    code = _merge_imports(code, imports)
    print(f"  组装完成: {len(code)} chars, {len(imports)} imports（已注入缺失项）")

    return {"assembled_code": code.strip(), "imports_needed": imports}


def _merge_imports(code: str, imports: list[str]) -> str:
    """把 `_infer_imports` 推断的 import 合并进代码（只做加法：去重、跳过已有）。

    插入点：package 声明与既有 import 区块之后（最后一个 package/import 行的下一行），
    保证新 import 紧跟既有 import，不会插进 class 体或 @Page 注解前造成语法破坏。
    已有 import 按整行字符串去重；`import {imp}` 已存在则跳过。
    """
    if not imports or not code.strip():
        return code
    lines = code.split("\n")
    existing = {ln.strip() for ln in lines if ln.strip().startswith("import ")}
    missing = [f"import {imp}" for imp in imports if f"import {imp}" not in existing]
    if not missing:
        return code

    insert_at = None
    for i, ln in enumerate(lines):
        s = ln.strip()
        if s.startswith("package ") or s.startswith("import "):
            insert_at = i + 1
        elif s:  # 遇到第一个非空、非 package/import 的行（如 @Page / class），停止
            break
    if insert_at is None:
        insert_at = 0
    lines[insert_at:insert_at] = missing
    return "\n".join(lines)


def _dedupe_class_imports(code: str) -> str:
    """同名类被 import 到多个路径时只保留一个（确定性修复，不依赖 LLM）。

    用例4 实测：LLM 组装写了 `import com.tencent.kuikly.core.views.Center`（错误
    路径），确定性注入又补了 `import com.tencent.kuikly.core.views.layout.Center`
    （正确路径）→ 同名类双路径共存，LLM 自审反复报「重复导入冲突」且 auto_fix
    无法收敛。按简单类名分组，保留白名单优选路径，其余整行删除。
    通配 import（`.*`）不参与去重（Kotlin 允许通配与精确并存，不构成同名冲突）。
    """
    if "import " not in code:
        return code
    # 同名类的优选包：精确映射优先（路 B② 白名单坑位），无映射时保留首次出现
    _PREFERRED = {
        "Center": "com.tencent.kuikly.core.views.layout",
        "Button": "com.tencent.kuikly.core.views.compose",
    }
    lines = code.split("\n")
    by_name: dict[str, list[tuple[int, str, str]]] = {}  # 类名 -> [(行号, 全路径, 包名)]
    for i, ln in enumerate(lines):
        s = ln.strip()
        if not s.startswith("import "):
            continue
        path = s[len("import "):].strip()
        if path.endswith(".*") or "." not in path:
            continue
        pkg, name = path.rsplit(".", 1)
        by_name.setdefault(name, []).append((i, path, pkg))
    drop_idx = set()
    for entries in by_name.values():
        if len(entries) <= 1:
            continue
        preferred_pkg = _PREFERRED.get(entries[0][1].rsplit(".", 1)[1])
        keep = None
        if preferred_pkg:
            for e in entries:
                if e[2] == preferred_pkg:
                    keep = e
                    break
        if keep is None:
            keep = entries[0]
        for e in entries:
            if e is not keep:
                drop_idx.add(e[0])
    if not drop_idx:
        return code
    return "\n".join(ln for i, ln in enumerate(lines) if i not in drop_idx)


def _infer_imports(code: str) -> list[str]:
    """从代码中推断需要的 import 语句（含路 B 扩充的组件与 Module 类）。

    注意：Button 在 `com.tencent.kuikly.core.views.compose` 包（不是 views 包），
    Center 在 `views.layout` 包，均需精确推断，否则真编译时 unresolved reference。
    """
    imports = []
    if "@Page" in code:
        imports.append("com.tencent.kuikly.core.annotations.Page")
    if "Pager()" in code:
        imports.append("com.tencent.kuikly.core.pager.Pager")
    if "ViewBuilder" in code:
        imports.append("com.tencent.kuikly.core.base.ViewBuilder")
    if "Color(" in code or "Color." in code:
        imports.append("com.tencent.kuikly.core.base.Color")
    if "Border(" in code:
        imports.append("com.tencent.kuikly.core.base.Border")
        imports.append("com.tencent.kuikly.core.base.BorderStyle")
    if "observable(" in code:
        imports.append("com.tencent.kuikly.core.reactive.handler.observable")

    # ── 组件 import（精确路径） ──
    # Button 在 compose 包，Center 在 layout 包，其余 views 组件用 views.* 通配兜底
    if "Button {" in code or "Button(" in code:
        imports.append("com.tencent.kuikly.core.views.compose.Button")
    if "Center {" in code:
        imports.append("com.tencent.kuikly.core.views.layout.Center")
    _VIEWS_COMPONENTS = (
        "View {", "Text {", "Image {", "Scroller {", "List {", "Modal {",
        "Input {", "TextArea {", "RichText {", "Switch {", "CheckBox {",
        "Slider {", "Tabs {", "AlertDialog {", "ActionSheet {", "DatePicker {",
        "ScrollPicker {", "Refresh {", "FooterRefresh {", "PageList {",
        "SliderPage {", "WaterFallList {", "Video {", "Canvas {", "Mask {",
        "ActivityIndicator {",
    )
    if any(c in code for c in _VIEWS_COMPONENTS):
        imports.append("com.tencent.kuikly.core.views.*")

    # ── Module 相关 import ──
    if "Module()" in code or ": Module" in code:
        imports.append("com.tencent.kuikly.core.module.Module")
    if "SharedPreferencesModule" in code:
        imports.append("com.tencent.kuikly.core.module.SharedPreferencesModule")
    if "RouterModule" in code:
        imports.append("com.tencent.kuikly.core.module.RouterModule")
    if "NotifyModule" in code:
        imports.append("com.tencent.kuikly.core.module.NotifyModule")
    if "MemoryCacheModule" in code:
        imports.append("com.tencent.kuikly.core.module.MemoryCacheModule")
    if "NetworkModule" in code:
        imports.append("com.tencent.kuikly.core.module.NetworkModule")
    if "acquireModule" in code or "getModule" in code or "asyncToNativeMethod" in code or "syncToNativeMethod" in code:
        imports.append("com.tencent.kuikly.core.module.Module")
    if "JSONObject" in code:
        imports.append("com.tencent.kuikly.core.nvi.serialization.json.JSONObject")
    if "CallbackFn" in code:
        imports.append("com.tencent.kuikly.core.module.CallbackFn")
    # ImageUri 在 base.attr 包（不是 views 包），需精确推断，否则 unresolved reference
    if "ImageUri" in code:
        imports.append("com.tencent.kuikly.core.base.attr.ImageUri")

    return list(dict.fromkeys(imports))  # 去重保序


# ═══════════════════════════════════════════════════════════════
# 节点⑤：编译检查（规则 + LLM 双重检查）
# ═══════════════════════════════════════════════════════════════
_API_CLAIM_RE = re.compile(r"不存在|不支持|应使用|应改用|应为|并非|并没有")


def _drop_unbacked_api_claims(llm_errors: list[str], rule_errors: list[str]) -> list[str]:
    """真编译口径下丢弃无编译佐证的「API 存在性」指控（v8 实测 13 条假指控中 12 条命中）。

    背景：漂移块注入后生成器会使用 jar 真实 API（pagerData/titleAttr/clickActionButton…），
    而 compile_check 刻意不注入知识源（eval/report_apiref.md 证伪注入有害），审查器按
    旧知识把这些正确 API 指控为「不存在」——但同一份代码 kotlinc 真编译 0 错误。
    API 真假编译器是唯一权威：真编译 0 错误时，存在性断言类指控必为假。非真编译
    口径（无 classpath）或 kotlinc 本身报错时不启用，保持原行为。
    """
    if not _find_kuikly_classpath():
        return llm_errors
    if any(".kt:" in e and ": error:" in e for e in rule_errors):
        return llm_errors  # 编译器自己报错，无法作为「代码合法」的权威
    kept = [e for e in llm_errors if not _API_CLAIM_RE.search(e)]
    dropped = len(llm_errors) - len(kept)
    if dropped:
        print(f"  [过滤] 真编译 0 错误，丢弃 {dropped} 条无编译佐证的 API 存在性指控")
    return kept


def node_compile_check(state: Dict[str, Any]) -> Dict[str, Any]:
    """对生成的代码做编译检查（先规则检查，再 LLM 检查）"""
    state = _state_values(state)
    print("\n⑤ 编译检查")

    code = state.get("assembled_code", state.get("final_code", ""))
    code = _dedupe_class_imports(code)  # 确定性修同名类多路径导入（用例4实测），两条路都受益

    # 规则检查（快）
    rule_errors = _rule_check(code)

    # LLM 检查（深）。注意：刻意不注入 api_reference——实测（eval/report_apiref.md）
    # 注入后审查器把参考里的风格建议升级成海量假指控（"10.0f 应写 10f"逐行报、
    # 自相矛盾的"不支持 text 应用 title"+"不支持 title 应用 text"成对出现），
    # 单案错误数冲到 116、全量 9/10→6/10，已回退。指控纪律保留"无据不指控"。
    prompt = fill_prompt(get_prompt("compile_check"), code=code)
    llm_result = call_llm_json(_get_llm(), prompt)

    llm_errors = llm_result.get("errors", [])
    # 真编译口径下，无编译佐证的「API 存在性」假指控确定性丢弃（v8 实测 12/13 假）
    llm_errors = _drop_unbacked_api_claims(llm_errors, rule_errors)
    # 防御：审查器给 passed=false 却列不出任何问题 → 无据可修，视为通过。
    # 否则 auto_fix 会拿着空问题列表空转，白烧 3 次修正额度（prompt 的指控
    # 纪律要求"passed=true 当且仅当 errors 为空"，这里是同一纪律的确定性兜底）。
    llm_passed = llm_result.get("passed", True) if llm_errors else True
    errors = rule_errors + llm_errors
    passed = len(errors) == 0 and llm_passed

    print(f"  规则检查: {len(rule_errors)} 问题")
    print(f"  LLM 检查: {'PASS' if llm_result.get('passed') else 'FAIL'}")
    print(f"  总计: {len(errors)} 问题, {'✓ 通过' if passed else '✗ 需修正'}")

    return {
        "compile_passed": passed,
        "compile_errors": errors,
        "error_count": len(errors),
        # 快乐路径：首次检查就通过时不会进 auto_fix，这里必须把最终代码落盘
        "final_code": code if passed else state.get("final_code", ""),
        # 失败时把去重后的代码回写，auto_fix 基于干净代码修正（否则同名类导入会
        # 一直在 LLM 自审里报"重复导入"导致循环不收敛——用例4实测）
        **({"assembled_code": code} if not passed else {}),
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
        # 继承/override 连锁噪声：代码继承 com.tencent.kuikly.* 的类（Pager/Module 等）时，
        # 若基类无法解析，kotlinc 会连锁误报以下三类，根因都是"基类不在 classpath"：
        "this type is final",            # 继承无法解析的基类，被误报为 final 不可继承
        "none of the following candidates",  # 构造函数无法解析（基类缺失）
        "overrides nothing",             # override 的方法在无法解析的基类中找不到
        # 比较符作用在类型未知的接收者上：`a > b` 里 a 的类型缺 Kuikly 签名解析不了，
        # kotlinc 把 > 解析到 Comparable.compareTo 并抱怨缺 operator 修饰（用例4实测）
        "modifier is required",
    )
    low = line.lower()
    return any(n in low for n in _CLASSPATH_NOISE)


def _find_kuikly_classpath() -> str | None:
    """查找可选的 Kuikly classpath（路 B③ 第②档：真编译验证）。

    优先级：
    1. 环境变量 `KUIKLY_CLASSPATH`（用户/CI 显式指定，指向 Kuikly core 编译产物
       jar 或 classes 目录，多个路径用系统分隔符 `:` 或 `;` 连接）；
    2. 本地常见路径探测（当前本机无 gradle/Kuikly 制品，暂留空，等有产物后扩展）。

    找不到返回 None，调用方降级第①档纯语法校验。
    """
    env = os.getenv("KUIKLY_CLASSPATH", "").strip()
    if env:
        return env
    return None


def _kotlinc_compile_errors(code: str, classpath: str | None = None) -> list[str]:
    """用 kotlinc 编译生成代码，返回真实 error 列表（路 B③ 第②档）。

    分档行为：
    - **第②档（classpath 非空）**：真编译。此时 `unresolved reference` 是**真错误**
      （说明 import 写错包名 / 组件名不存在），**不再当噪声过滤**，直接保留，
      从而拦住"猜 import"骗过校验的情况（木鱼实测暴露的问题）。
    - **第①档（classpath 为空，降级）**：纯语法校验，过滤 classpath 缺失导致的
      类型/符号噪声（见 `_is_classpath_noise`），只保留真语法错误。
    - kotlinc 不可用：返回空列表，由调用方降级括号匹配。

    第③档（Gradle 完整编译，含 Kuikly 全依赖 + 三端产物）需 gradle 环境，
    不在 node 内执行，入口见 `ci/` 门禁脚本注释。
    """
    kotlinc = _kotlinc_path()
    if not kotlinc:
        return []
    with tempfile.NamedTemporaryFile("w", suffix=".kt", delete=False, encoding="utf-8") as f:
        f.write(code)
        path = f.name
    cmd = [kotlinc]
    if classpath:
        cmd += ["-classpath", classpath]
    cmd += [path]
    try:
        proc = subprocess.run(
            cmd, capture_output=True, text=True, timeout=120,
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
        # 有 classpath 时 unresolved reference 是 import 真错误，保留；
        # 无 classpath 时是缺库噪声，过滤。
        if classpath is None and _is_classpath_noise(line):
            continue
        errors.append(line.strip())
    return errors


def _kotlinc_syntax_errors(code: str) -> list[str]:
    """第①档：纯语法校验（无 classpath）。保留此函数以兼容既有调用。"""
    return _kotlinc_compile_errors(code, classpath=None)


# 结构层规则的固定文案（_rule_check 产出与 _annotate_error_source 识别共用同一常量，
# 避免用前缀猜来源时把 LLM 指控里的"缺少 xxx"误判成结构规则——用例2家族的混排问题）
_RULE_MISSING_PAGE = "缺少 @Page 注解"
_RULE_MISSING_PAGER = "未继承 Pager() 或 BasePager()"
_RULE_MISSING_BODY = "缺少 body() 方法重写"
_RULE_MISSING_VIEWBUILDER = "缺少 ViewBuilder 返回类型"
_RULE_MISSING_PACKAGE = "缺少 package 声明"
_RULE_BRACE_MISMATCH = "大括号不匹配"
_RULE_PAREN_MISMATCH = "圆括号不匹配"
_STRUCTURAL_RULES = (_RULE_MISSING_PAGE, _RULE_MISSING_PAGER, _RULE_MISSING_BODY,
                     _RULE_MISSING_VIEWBUILDER, _RULE_MISSING_PACKAGE,
                     _RULE_BRACE_MISMATCH, _RULE_PAREN_MISMATCH)


def _rule_check(code: str) -> list[str]:
    """基于规则的编译检查：优先 kotlinc 真语法/真编译校验，不可用时降级括号匹配。

    结构类规则（@Page / Pager / body / package 等）与 classpath 无关，始终生效。
    """
    errors = []

    # ① 语法层：优先 kotlinc。若配置了 KUIKLY_CLASSPATH 则走第②档真编译
    #    （unresolved reference 保留为真错误），否则第①档纯语法（过滤噪声）。
    classpath = _find_kuikly_classpath()
    syntax_errors = _kotlinc_compile_errors(code, classpath=classpath)
    if syntax_errors:
        errors.extend(syntax_errors)
    else:
        # kotlinc 不可用时的降级：括号匹配
        if code.count("{") != code.count("}"):
            errors.append(f"{_RULE_BRACE_MISMATCH}: {{={code.count('{')}, }}={code.count('}')}")
        if code.count("(") != code.count(")"):
            errors.append(f"{_RULE_PAREN_MISMATCH}: (={code.count('(')}, )={code.count(')')}")

    # ② 结构层规则（与 classpath 无关，始终检查）
    if "@Page(" not in code:
        errors.append(_RULE_MISSING_PAGE)
    if ": Pager()" not in code and ": BasePager()" not in code:
        errors.append(_RULE_MISSING_PAGER)
    if "override fun body()" not in code:
        errors.append(_RULE_MISSING_BODY)
    if "ViewBuilder" not in code:
        errors.append(_RULE_MISSING_VIEWBUILDER)
    if not code.strip().startswith("package "):
        errors.append(_RULE_MISSING_PACKAGE)
    return errors


def _annotate_error_source(e: str) -> str:
    """给编译错误标注来源，供 auto_fix 决定修复优先级（用例2实测）。

    用例2 全量跑失败时 17 个问题 = 2 个 kotlinc 真错误 + 15 条 LLM 自审指控，
    真假混排导致修复器被假指控牵制、真错误反而没修掉。标注后修复器可执行
    「确定性结果优先修、AI 审查先核实」的纪律。判定依据：
    - kotlinc 行特征：含 ".kt:" 与 ": error:"（临时文件路径 + 编译器报错格式）；
    - 结构规则：与 _STRUCTURAL_RULES 常量精确匹配（_rule_check 的固定文案）；
    - 其余（自由文本）一律视为 LLM 自审意见。
    """
    if ".kt:" in e and ": error:" in e:
        return f"[编译器] {e}"
    if e.startswith(_STRUCTURAL_RULES):
        return f"[结构规则] {e}"
    return f"[AI自审] {e}"


def verify_compile(code: str) -> dict:
    """独立编译验证入口（供 CI 质量门禁直接调用，不依赖 LLM）。

    返回:
        {
          "mode": "full" | "syntax" | "unavailable",
          "classpath_used": bool,
          "errors": [str],
        }
    - "full"：配置了 KUIKLY_CLASSPATH，走了真编译（unresolved reference 为真错误）；
    - "syntax"：无 classpath，走了纯语法校验（过滤缺库噪声）；
    - "unavailable"：kotlinc 不可用。
    """
    classpath = _find_kuikly_classpath()
    kotlinc = _kotlinc_path()
    if not kotlinc:
        return {"mode": "unavailable", "classpath_used": False, "errors": []}
    if classpath:
        return {"mode": "full", "classpath_used": True,
                "errors": _kotlinc_compile_errors(code, classpath=classpath)}
    return {"mode": "syntax", "classpath_used": False,
            "errors": _kotlinc_compile_errors(code, classpath=None)}


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
        # 逐条标注来源（[编译器]/[结构规则]/[AI自审]），配合 prompt 里的
        # 修复纪律：确定性结果优先修、AI 审查先核实再改（用例2实测）。
        errors="\n".join(f"- {_annotate_error_source(e)}" for e in errors),
        fix_attempts=fix_attempts - 1,
        api_reference=get_kuikly_api_reference(),
    )

    fixed_code = call_llm(_get_llm(), prompt)
    fixed_code = _strip_markdown(fixed_code)
    # 修正后同样补确定性 import：auto_fix 是 LLM 重写，可能再次漏写 import
    # （如 ImageUri），须与 node_assemble 保持同一套注入逻辑，否则修正一次就丢 import。
    imports = _infer_imports(fixed_code)
    fixed_code = _merge_imports(fixed_code, imports)
    print(f"  修正完成: {len(fixed_code)} chars（已注入缺失 import {len(imports)} 项）")

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
