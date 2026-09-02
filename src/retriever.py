"""
Kuikly 官方知识源检索
====================
从已克隆的 Tencent-TDS/KuiklyUI-AI 仓库加载官方规则（.mdc），作为 Few-shot /
知识注入的权威来源，替代手写静态 API 表。

设计（Phase A 升级）：
- 抽象出 KuiklyKnowledgeSource 统一接口，上层（prompts.py / nodes.py）只依赖接口；
- LocalKuiklyRules：读本地克隆的 .mdc 文件（默认，零依赖）；
- McpKuiklyKnowledgeSource（src/mcp_kuikly.py）：运行时调官方 Kuikly MCP 拿实时
  知识，未配置时自动回退本地；
- get_knowledge_source() 工厂按环境变量 KUIKLY_MCP_URL 选择实现。
- 为兼容既有调用，保留模块级 load_kuikly_rules()（等价于本地实现）。
"""

from __future__ import annotations

import os
from typing import Protocol

_RULES_DIR = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    "knowledge", "KuiklyUI-AI", "rules",
)

_RULE_FILES = {
    "dsl": "kuiklyDSL.mdc",
    "compose": "kuiklyComposeDSL.mdc",
}


class KuiklyKnowledgeSource(Protocol):
    """知识源统一接口：上层只依赖 get_rules()，不关心来源是本地还是 MCP。"""

    def get_rules(self, dsl_type: str = "dsl") -> str:
        ...


def _strip_front_matter(text: str) -> str:
    """去掉 .mdc 的 YAML front-matter（--- ... ---）。"""
    if text.startswith("---"):
        end = text.find("\n---", 3)
        if end != -1:
            text = text[end + 4:].lstrip("\n")
    return text


class LocalKuiklyRules:
    """读本地克隆的 KuiklyUI-AI/rules/*.mdc（默认实现，零依赖）。"""

    def get_rules(self, dsl_type: str = "dsl") -> str:
        filename = _RULE_FILES.get(dsl_type, _RULE_FILES["dsl"])
        path = os.path.join(_RULES_DIR, filename)
        if not os.path.exists(path):
            return ""
        with open(path, "r", encoding="utf-8") as f:
            return _strip_front_matter(f.read())


_LOCAL = LocalKuiklyRules()


def get_knowledge_source() -> KuiklyKnowledgeSource:
    """按环境变量选择知识源：配置了 KUIKLY_MCP_URL 且可用则走官方 MCP，否则本地。"""
    mcp_url = os.getenv("KUIKLY_MCP_URL", "").strip()
    if mcp_url:
        try:
            from src.mcp_kuikly import McpKuiklyKnowledgeSource
            return McpKuiklyKnowledgeSource(mcp_url, fallback=_LOCAL)
        except Exception:
            return _LOCAL
    return _LOCAL


def load_kuikly_rules(dsl_type: str = "dsl") -> str:
    """兼容既有调用（prompts.py 使用）。等价于本地实现。"""
    return _LOCAL.get_rules(dsl_type)
