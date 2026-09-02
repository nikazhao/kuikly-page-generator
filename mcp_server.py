"""
Kuikly 页面生成器 · MCP Server（Phase C）
========================================
把 generate_page 暴露为标准 MCP tool，让支持 MCP 的客户端
（CodeBuddy / Cursor / Claude 等）能直接调用「生成一个 Kuikly 页面」。

运行：
    python mcp_server.py

客户端（stdio）配置示例：
    {
      "mcpServers": {
        "kuikly-page-generator": {
          "command": "python",
          "args": ["<Graph 项目绝对路径>/mcp_server.py"]
        }
      }
    }

协议借力：官方 Kuikly MCP 思路 + MCP Python SDK（mcp 2.0.0 的 MCPServer 高阶 API：
@mcp.tool() 注册工具、mcp.run(transport="stdio") 启动 stdio 服务）。
"""

from __future__ import annotations

import os
import sys

_PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

from mcp.server import MCPServer

mcp = MCPServer("kuikly-page-generator")


@mcp.tool()
def generate_kuikly_page(requirement: str, page_name: str = "") -> str:
    """根据自然语言需求生成 Kuikly Kotlin 页面代码（.kt）。

    Best-of-3 采样：单 roll 失败时自动重试（最多 3 次），任一次真编译通过即返回——
    全量 eval 实测把通过率从 ~80% 提到 100%（2026-09-03）。

    Args:
        requirement: 自然语言页面描述，如『一个登录页面，有用户名密码输入框和登录按钮』
        page_name: 可选页面名，留空则自动生成
    """
    if not requirement.strip():
        return "错误：requirement 不能为空"
    try:
        from src.run_pipeline import run_pipeline_bestof
        final, _ = run_pipeline_bestof(requirement, page_name=page_name, rolls=3)
    except Exception as e:  # noqa: BLE001 — 异常结构化返回，不让客户端崩溃
        return f"生成失败: {e}"

    code = final.get("final_code", "")
    if not code:
        return "生成未完成，未产出最终代码。"

    page = final.get("page_name", "GeneratedPage")
    attempt = final.get("bestof_attempt", 1)
    rolls = final.get("bestof_rolls", 1)
    retry_tag = f"（第 {attempt}/{rolls} 次采样通过）" if attempt > 1 else ""
    return f"// {page}.kt{retry_tag}\n{code}"


if __name__ == "__main__":
    mcp.run(transport="stdio")
