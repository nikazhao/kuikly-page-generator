"""
LangGraph State 定义
====================
定义整个 Graph 中流动的数据结构，覆盖 6 个节点的输入输出。
Phase 2.2 升级：从裸 TypedDict 改为 **Pydantic 强类型模型**，节点交接时自动
校验字段类型，传参出错早暴露（而不是跑到后面才崩）。
保留 reducer（Annotated[T, reducer]）以支持 Fan-out / Fan-in 并行节点。
"""

from __future__ import annotations

from typing import Annotated, Optional
from pydantic import BaseModel, ConfigDict, Field
from dataclasses import dataclass, field


# ─── reducer 函数 ────────────────────────────────────────────
def take_last(left, right):
    """取最后一个值（默认行为，显式声明）"""
    return right if right is not None else left


def _state_values(state):
    """把节点收到的 state 统一成 dict。

    LangGraph 在 Pydantic state 模式下会把 state 作为 GraphState 实例传给节点，
    而节点内部习惯用 state.get() / state['k'] 访问。这里统一转回 dict，
    既保留 Pydantic 在"写回"时的强类型校验，又不破坏节点内的 dict 访问习惯。
    """
    if isinstance(state, dict):
        return state
    return state.model_dump()


# ─── 核心状态（Pydantic 强类型） ─────────────────────────────
class GraphState(BaseModel):
    """Kuikly Page Generator 的全局状态。

    - model_config: extra="allow" → 节点返回 schema 外的字段也不报错（兜底兼容）；
                     arbitrary_types_allowed=True → 允许任何 Python 类型作为通道值。
    - 所有字段给默认值，节点只回传"自己负责的字段"，LangGraph 按通道增量合并。
    - components_needed 用 take_last reducer，保证并行/循环覆盖语义确定。
    """

    model_config = ConfigDict(extra="allow", arbitrary_types_allowed=True)

    # ── 输入 ──
    user_requirement: str = ""     # 用户自然语言描述
    page_name: str = ""            # 生成的 @Page 注解名

    # ── 节点①：需求解析 ──
    parsed_intent: str = ""        # 解析后的结构化意图
    page_type: str = ""            # 页面类型 (login/list/detail/form/settings/profile)
    components_needed: Annotated[list[str], take_last] = Field(default_factory=list)

    # ── 节点②：页面拆解 ──
    layout_plan: str = ""          # 布局结构描述 (JSON string)
    input_plan: str = ""           # 交互/输入元素描述
    style_plan: str = ""           # 样式/主题描述

    # ── 节点③a/③b/③c：并行代码生成 ──
    layout_code: str = ""          # ③a 布局代码片段
    input_code: str = ""           # ③b 交互代码片段
    style_code: str = ""           # ③c 样式代码片段

    # ── 节点④：代码组装 ──
    assembled_code: str = ""       # 组装后的完整 .kt 代码
    imports_needed: list[str] = Field(default_factory=list)

    # ── 节点⑤：编译检查 ──
    compile_passed: Optional[bool] = None   # 是否通过编译检查（None 表示尚未检查）
    compile_errors: list[str] = Field(default_factory=list)
    error_count: int = 0

    # ── 节点⑥：自动修正 ──
    fix_attempts: int = 0          # 已尝试修正次数
    fix_applied: str = ""          # 本次修正描述

    # ── 输出 ──
    final_code: str = ""           # 最终生成的代码
    success: bool = False          # 是否成功
    elapsed_seconds: float = 0.0   # 耗时


# ─── 辅助数据结构 ────────────────────────────────────────────
@dataclass
class PageComponent:
    """页面组件描述"""
    name: str           # 组件类型 (View/Text/Image/Button/List/Scroller...)
    role: str            # 用途 (container/title/icon/action/scroll/list_item...)
    attributes: dict = field(default_factory=dict)
    children: list = field(default_factory=list)


@dataclass
class CompileIssue:
    """编译检查发现的问题"""
    severity: str       # "error" | "warning"
    category: str       # "syntax" | "import" | "api" | "style"
    message: str
    line_hint: str      # 大致的代码位置
    suggestion: str     # 修正建议


# ─── 默认值 ──────────────────────────────────────────────────
def initial_state(user_requirement: str, page_name: str = "") -> dict:
    """创建初始状态（返回 dict，作为 Graph 输入）"""
    return {
        "user_requirement": user_requirement,
        "page_name": page_name or user_requirement.strip()[:20].replace(" ", "_").title() + "Page",
        "components_needed": [],
        "compile_errors": [],
        "error_count": 0,
        "fix_attempts": 0,
        "success": False,
        "elapsed_seconds": 0.0,
    }
