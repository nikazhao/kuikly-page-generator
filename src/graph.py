"""
LangGraph 拓扑定义
===================
定义 6 节点的 Graph 拓扑，含 Fan-out/Fan-in 并行和条件循环。

拓扑图：

  ┌─────────────────────────────────────────────────┐
  │ ① parse_requirement                              │
  │      ↓                                           │
  │ ② decompose_page                                 │
  │      ↓                                           │
  │ ③a gen_layout ─┐                                │
  │ ③b gen_input ──┼─→ ④ assemble                   │
  │ ③c gen_style ──┘       ↓                        │
  │                   ⑤ compile_check               │
  │                        ↓                        │
  │                   ┌─ route ─┐                   │
  │                   ↓         ↓                   │
  │              ⑥ auto_fix    END                  │
  │                   ↓                             │
  │              ⑤ (重新检查)                       │
  └─────────────────────────────────────────────────┘
"""

from __future__ import annotations

from langgraph.graph import StateGraph, END, START

from .state import GraphState
from .nodes import (
    node_parse_requirement,
    node_decompose_page,
    node_gen_layout,
    node_gen_input,
    node_gen_style,
    node_assemble,
    node_compile_check,
    node_auto_fix,
    route_after_check,
)


def build_graph():
    """
    构建 Kuikly Page Generator 的 LangGraph 工作流

    Returns:
        CompiledGraph — 可调用的可执行图
    """
    graph = StateGraph(GraphState)

    # ─── 添加节点 ─────────────────────────────────────────
    graph.add_node("parse_requirement", node_parse_requirement)
    graph.add_node("decompose_page", node_decompose_page)
    graph.add_node("gen_layout", node_gen_layout)
    graph.add_node("gen_input", node_gen_input)
    graph.add_node("gen_style", node_gen_style)
    graph.add_node("assemble", node_assemble)
    graph.add_node("compile_check", node_compile_check)
    graph.add_node("auto_fix", node_auto_fix)

    # ─── 添加边 ───────────────────────────────────────────
    # 线性段
    graph.add_edge(START, "parse_requirement")
    graph.add_edge("parse_requirement", "decompose_page")

    # Fan-out：decompose → 3 个并行代码生成节点
    graph.add_edge("decompose_page", "gen_layout")
    graph.add_edge("decompose_page", "gen_input")
    graph.add_edge("decompose_page", "gen_style")

    # Fan-in：3 个生成节点 → 组装
    graph.add_edge("gen_layout", "assemble")
    graph.add_edge("gen_input", "assemble")
    graph.add_edge("gen_style", "assemble")

    # 组装 → 编译检查
    graph.add_edge("assemble", "compile_check")

    # 条件路由：编译检查 → END or auto_fix
    graph.add_conditional_edges(
        "compile_check",
        route_after_check,
        {
            "end": END,
            "fix": "auto_fix",
        },
    )

    # 修正 → 重新编译检查（循环）
    graph.add_edge("auto_fix", "compile_check")

    return graph.compile()


# ─── 单例 ───────────────────────────────────────────────────
_compiled_graph = None


def get_graph():
    """获取编译后的 Graph 单例"""
    global _compiled_graph
    if _compiled_graph is None:
        _compiled_graph = build_graph()
    return _compiled_graph
