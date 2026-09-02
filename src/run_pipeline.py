"""
Graph 流水线封装（供 Web UI / eval / MCP 复用）
===========================================
把 main.generate_page 的「打印逻辑」与「执行逻辑」解耦：
- run_pipeline()   同步执行整条流水线，返回 (final_state, steps)
- steps 是每一步的完整 state 快照列表（graph.stream stream_mode="values"），
  供 Web UI 做步骤时间线、供 eval 做过程分析复用。

设计原则：
- 节点内部的 print（nodes.py 里各节点自带的打印）仍走 stdout（服务端控制台），
  不污染 UI；UI 的时间线完全由 steps 快照驱动。
- 不依赖 main.py 的打印副作用，Web/MCP/eval 三条消费方共用此模块。
"""

from __future__ import annotations

import os
import sys
import time

# 确保项目根目录在 path 中（webui / eval / mcp_server 从不同目录 import 时也能用）
_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

from src.state import initial_state
from src.graph import get_graph


def run_pipeline(
    user_requirement: str,
    page_name: str = "",
    collect_steps: bool = True,
    recursion_limit: int = 30,
) -> tuple[dict, list[dict]]:
    """端到端执行 Kuikly 页面生成流水线。

    Args:
        user_requirement: 自然语言需求
        page_name: 可选页面名
        collect_steps: True 时用 stream 收集每一步 state 快照（供 UI/eval）；
                       False 时直接 invoke，仅返回最终 state（更快，无需过程）
        recursion_limit: LangGraph 递归上限

    Returns:
        (final_state, steps)
        - final_state: dict，含 final_code / success / elapsed_seconds / fix_attempts 等
        - steps: 每一步的完整 state 快照（collect_steps=True 时非空）
    """
    start = time.time()
    state = initial_state(user_requirement, page_name)
    graph = get_graph()

    steps: list[dict] = []
    if collect_steps:
        for _step, values in enumerate(
            graph.stream(state, {"recursion_limit": recursion_limit}, stream_mode="values")
        ):
            steps.append(dict(values))
            final = values
    else:
        final = graph.invoke(state, {"recursion_limit": recursion_limit})

    final = dict(final)
    final["elapsed_seconds"] = round(time.time() - start, 2)

    # 计算成功状态（与 main.generate_page 一致）
    if final.get("compile_passed"):
        final["success"] = True
    elif final.get("fix_attempts", 0) >= 3:
        final["success"] = False
    else:
        final["success"] = bool(final.get("compile_passed", False))

    return final, steps


def truncate_field(value, limit: int = 120) -> str:
    """把长文本字段截断，用于 UI 时间线展示，避免刷屏。"""
    if not isinstance(value, str):
        return repr(value)
    if len(value) <= limit:
        return value
    return value[:limit] + f"...(共 {len(value)} 字符)"
