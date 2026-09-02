"""
Kuikly 官方 MCP 知识源（Phase A · 条件实现）
===========================================
若配置了 KUIKLY_MCP_URL，则通过该 MCP 端点实时获取官方 Kuikly 文档 / 组件索引；
未配置、或连接 / 调用失败，则**回退到本地 LocalKuiklyRules**，保证生成流水线不中断。

官方 Kuikly MCP 为腾讯陆续发布的能力，端点地址、传输方式（stdio / streamable-http）、
工具名均以官方为准。本模块通过环境变量解耦：
- KUIKLY_MCP_URL：官方 MCP 端点（http(s) 或 stdio 命令由官方约定）
- KUIKLY_MCP_TOOL：要调用的工具名（默认 get_kuikly_rules）
失败时一律回退本地，因此永远不会让生成器因「知识源异常」而崩溃。
"""

from __future__ import annotations

import os


class McpKuiklyKnowledgeSource:
    """通过官方 Kuikly MCP 获取实时知识的知识源（条件启用）。"""

    def __init__(self, mcp_url: str, fallback=None):
        self.mcp_url = mcp_url
        self.fallback = fallback
        self.tool = os.getenv("KUIKLY_MCP_TOOL", "get_kuikly_rules")

    def get_rules(self, dsl_type: str = "dsl") -> str:
        # 条件实现：仅当 KUIKLY_MCP_URL 显式配置时才尝试实时调用；
        # 任何异常（传输未就绪 / 工具名不符 / 网络问题）都回退本地，绝不抛错。
        if not self.mcp_url:
            return self._fallback(dsl_type)
        try:
            return self._try_fetch(dsl_type)
        except Exception:
            return self._fallback(dsl_type)

    def _try_fetch(self, dsl_type: str) -> str:
        """尽力通过 MCP 客户端取实时规则；失败抛异常交给上层回退。"""
        if self.mcp_url.startswith(("http://", "https://")):
            import asyncio
            return asyncio.run(self._fetch_http(dsl_type))
        # 非 http(s) 端点（如 stdio 命令）暂不支持自动连接，直接回退
        return self._fallback(dsl_type)

    async def _fetch_http(self, dsl_type: str) -> str:
        from mcp.client.streamable_http import streamablehttp_client
        from mcp import ClientSession

        async with streamablehttp_client(self.mcp_url) as (r, w, _):
            async with ClientSession(r, w) as session:
                await session.initialize()
                result = await session.call_tool(
                    self.tool, {"dsl_type": dsl_type}
                )
                return "\n".join(
                    c.text for c in result.content if getattr(c, "type", "") == "text"
                )

    def _fallback(self, dsl_type: str) -> str:
        if self.fallback is not None:
            return self.fallback.get_rules(dsl_type)
        return ""
