"""
Kuikly Page Generator — Web UI (Streamlit)
=========================================
把 CLI 工具包成网页：左侧输入自然语言需求，右侧展示 6 步 Graph 时间线 +
最终生成的 .kt 代码（可下载）。

运行：
    cd Graph
    streamlit run webui/app.py
浏览器打开 http://localhost:8501

架构借力：yigit353/LangGraph-FastAPI-Streamlit（分层：LangGraph 后端 / 前端 UI）、
aminghrz/Langgraph-Streamlit-Chat-Interface（轻量单文件模板）。
本文件只做前端展示，执行逻辑全部复用 src/run_pipeline.py，不重造流水线。
"""

from __future__ import annotations

import os
import sys

# 项目根目录（webui/ 的父目录）加入 path，确保能 import src
_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

import streamlit as st
from src.run_pipeline import run_pipeline, truncate_field


# 时间线里要展示的状态字段（长代码字段单独截断）
_STEP_FIELDS = [
    "user_requirement", "page_name", "page_type", "components_needed",
    "layout_plan", "input_plan", "style_plan",
    "layout_code", "input_code", "style_code",
    "assembled_code", "imports_needed",
    "compile_passed", "compile_errors", "error_count",
    "fix_attempts", "fix_applied", "final_code", "success",
]


def _render_timeline(steps: list[dict]) -> None:
    """把每一步的完整 state 快照渲染成可折叠的时间线。"""
    if not steps:
        st.warning("没有采集到步骤快照。")
        return

    st.subheader(f"Graph 执行时间线（共 {len(steps)} 步）")
    for i, snap in enumerate(steps):
        # 找出这一步相对上一步「新增/变化」的关键字段，作为步骤标题
        title_parts = []
        if snap.get("page_name"):
            title_parts.append(f"页面={snap['page_name']}")
        if snap.get("page_type"):
            title_parts.append(f"类型={snap['page_type']}")
        if "compile_passed" in snap and snap.get("compile_passed") is not None:
            title_parts.append("编译检查")
        if snap.get("fix_attempts"):
            title_parts.append(f"修正#{snap['fix_attempts']}")
        label = f"Step {i}" + (f" · {' / '.join(title_parts)}" if title_parts else "")
        with st.expander(label, expanded=(i == len(steps) - 1)):
            for f in _STEP_FIELDS:
                if f not in snap:
                    continue
                v = snap.get(f)
                if v in (None, "", [], {}):
                    continue
                if f in ("assembled_code", "final_code", "layout_code", "input_code", "style_code"):
                    st.code(truncate_field(str(v), 240), language="kotlin")
                elif f == "compile_errors" and isinstance(v, list):
                    for e in v:
                        st.text(f"  - {e}")
                else:
                    st.write(f"**{f}**: {truncate_field(str(v), 200)}")


def main() -> None:
    st.set_page_config(page_title="Kuikly 页面生成器", layout="wide")
    st.title("Kuikly 页面生成器")
    st.caption("自然语言 → LangGraph 6 节点流水线 → Kuikly Kotlin 代码")

    with st.sidebar:
        st.header("输入")
        requirement = st.text_area(
            "需求描述",
            height=120,
            placeholder="例如：一个登录页面，有用户名密码输入框和登录按钮",
        )
        page_name = st.text_input("页面名（可选）", "")
        run_btn = st.button("生成页面", type="primary", use_container_width=True)

    if not run_btn:
        st.info("在左侧输入需求，点击「生成页面」。")
        return

    if not requirement.strip():
        st.error("请先输入需求描述。")
        return

    with st.spinner("正在通过 Graph 流水线生成页面…"):
        try:
            final, steps = run_pipeline(requirement, page_name=page_name, collect_steps=True)
        except Exception as exc:  # noqa: BLE001 — UI 层兜底，不让异常崩掉页面
            st.error(f"生成失败：{exc}")
            return

    # 时间线
    _render_timeline(steps)

    # 结果区
    success = final.get("success", False)
    elapsed = final.get("elapsed_seconds", 0)
    code = final.get("final_code", "")

    st.divider()
    col1, col2, col3 = st.columns(3)
    col1.metric("状态", "成功 ✅" if success else "未完成 ⚠️")
    col2.metric("耗时", f"{elapsed}s")
    col3.metric("修正次数", final.get("fix_attempts", 0))

    if code:
        st.subheader(f"生成代码：{final.get('page_name', 'GeneratedPage')}.kt")
        st.code(code, language="kotlin")
        st.download_button(
            label="下载 .kt 文件",
            data=code,
            file_name=f"{final.get('page_name', 'GeneratedPage')}.kt",
            mime="text/plain",
        )
    else:
        st.warning("未能生成最终代码，请查看上方时间线中的错误信息。")


if __name__ == "__main__":
    main()
