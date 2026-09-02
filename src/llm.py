"""
LLM 配置
=========
支持 CodeBuddy API 和 OpenAI 兼容接口。
通过环境变量 .env 配置。

CodeBuddy API 的 /v2 端点仅支持流式请求，
本模块通过 OpenAI SDK 的流式接口自动处理。
"""

from __future__ import annotations

import os
import time
from typing import Optional

from langchain_openai import ChatOpenAI


# ─── CodeBuddy API 常量 ──────────────────────────────────────
CODEBUDDY_BASE_URL = "https://copilot.tencent.com/v2"
CODEBUDDY_DEFAULT_MODEL = "deepseek-v3"


def _load_env():
    """加载 .env 文件"""
    env_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), ".env")
    if os.path.exists(env_path):
        with open(env_path, "r") as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, _, val = line.partition("=")
                    os.environ.setdefault(key.strip(), val.strip().strip('"').strip("'"))


_load_env()


def get_llm(
    model: Optional[str] = None,
    temperature: float = 0.3,
    max_tokens: int = 4096,
) -> ChatOpenAI:
    """
    创建 LLM 实例

    自动选择后端：
    1. CODEBUDDY_API_KEY → CodeBuddy /v2 端点（腾讯内部，推荐）
    2. OPENAI_API_KEY    → OpenAI 兼容接口（SiliconFlow / DeepSeek / OpenAI）

    默认模型从 MODEL 环境变量读取。
    """
    # 优先检查 CodeBuddy API Key
    codebuddy_key = os.getenv("CODEBUDDY_API_KEY", "").strip()
    if codebuddy_key:
        model_name = model or os.getenv("MODEL", CODEBUDDY_DEFAULT_MODEL)
        return ChatOpenAI(
            model=model_name,
            temperature=temperature,
            max_tokens=max_tokens,
            api_key=codebuddy_key,
            base_url=CODEBUDDY_BASE_URL,
            # CodeBuddy /v2 仅支持流式，LangChain ChatOpenAI 默认会处理 stream
        )

    # 回退到 OpenAI 兼容接口
    api_key = os.getenv("OPENAI_API_KEY", "").strip()
    if api_key:
        base_url = os.getenv("OPENAI_BASE_URL", "https://api.siliconflow.cn/v1")
        model_name = model or os.getenv("MODEL", "deepseek-ai/DeepSeek-V3")
        return ChatOpenAI(
            model=model_name,
            temperature=temperature,
            max_tokens=max_tokens,
            api_key=api_key,
            base_url=base_url,
        )

    # 没有 API Key
    raise ValueError(
        "未找到 API Key。请在 .env 中配置：\n\n"
        "方案 1（推荐·腾讯内部）— CodeBuddy API：\n"
        "  1. 打开 https://copilot.tencent.com/profile/keys\n"
        "  2. 创建 API Key，复制 ck_ 开头的 Key\n"
        "  3. 在 .env 中填入：\n"
        "     CODEBUDDY_API_KEY=ck_你的key\n\n"
        "方案 2（外部免费）— 硅基流动：\n"
        "  1. 注册 https://cloud.siliconflow.cn\n"
        "  2. 左侧「API 密钥」→ 新建密钥 → 复制\n"
        "  3. 在 .env 中填入：\n"
        "     OPENAI_API_KEY=sk-你的key\n"
        "     OPENAI_BASE_URL=https://api.siliconflow.cn/v1\n"
        "     MODEL=deepseek-ai/DeepSeek-V3"
    )


# ─── 调用 LLM 的工具函数 ─────────────────────────────────────
# 可重试的瞬态错误关键词：限流 / 超时 / 连接中断 / 服务端 5xx 等。
# 这类错误通常是临时的，重试大概率能恢复；其余错误（如鉴权失败、参数错误）重试无意义。
_RETRYABLE_KEYWORDS = (
    "timeout",
    "timed out",
    "ratelimit",
    "rate limit",
    "rate_limit",
    "connection",
    "connection error",
    "overloaded",
    "service unavailable",
    "internal server error",
    "gateway",
    "too many requests",
    "429",
)


def _is_retryable_error(exc: Exception) -> bool:
    """判断异常是否属于"值得重试"的瞬态错误。

    通过异常类名 / 消息 / __cause__ 链里的状态码综合判断，避免硬依赖
    openai SDK 的版本差异（底层异常可能被 LangChain 包装一层）。
    """
    for e in (exc, getattr(exc, "__cause__", None)):
        if e is None:
            continue
        name = type(e).__name__.lower()
        msg = str(e).lower()
        if any(k in name or k in msg for k in _RETRYABLE_KEYWORDS):
            return True
        status = getattr(e, "status_code", None)
        if status in (408, 429) or (isinstance(status, int) and status >= 500):
            return True
    return False


def _call_llm_once(llm: ChatOpenAI, prompt: str) -> str:
    """单次 LLM 调用：先非流式，失败回退流式（CodeBuddy /v2 仅支持流式）。"""
    try:
        # 先尝试非流式（适用于 SiliconFlow / OpenAI 等）
        response = llm.invoke(prompt)
        return response.content if hasattr(response, "content") else str(response)
    except Exception as non_stream_err:
        # CodeBuddy /v2 不支持非流式，回退到流式
        try:
            chunks = []
            for chunk in llm.stream(prompt):
                if hasattr(chunk, "content") and chunk.content:
                    chunks.append(chunk.content)
            return "".join(chunks) if chunks else ""
        except Exception as stream_err:
            raise RuntimeError(
                f"LLM 调用失败（非流式: {non_stream_err} / 流式: {stream_err}）"
            ) from stream_err


def call_llm(
    llm: ChatOpenAI,
    prompt: str,
    max_retries: int = 3,
    backoff: float = 1.5,
    initial_delay: float = 1.0,
) -> str:
    """
    同步调用 LLM，返回文本响应。带指数退避重试。

    遇到限流(429)/超时/连接中断/5xx 等瞬态错误时自动重试，最多额外重试
    max_retries 次，每次等待 initial_delay * backoff^n 秒。演示或生产环境
    下偶发的 API 抖动不会直接整页生成失败，而是自行恢复。
    """
    delay = initial_delay
    last_err: Exception | None = None
    for attempt in range(max_retries + 1):
        try:
            return _call_llm_once(llm, prompt)
        except Exception as e:  # noqa: BLE001 — 判定是否重试后统一抛
            last_err = e
            if attempt >= max_retries or not _is_retryable_error(e):
                break
            time.sleep(delay)
            delay *= backoff
    raise RuntimeError(
        f"LLM 调用失败（重试 {max_retries} 次后仍失败）: {last_err}"
    ) from last_err


def call_llm_json(llm: ChatOpenAI, prompt: str) -> dict:
    """调用 LLM 并解析 JSON 响应（容错处理）"""
    import json
    import re

    raw = call_llm(llm, prompt)

    # 尝试直接解析
    try:
        return json.loads(raw)
    except json.JSONDecodeError:
        pass

    # 尝试提取 ```json ... ``` 块
    match = re.search(r"```(?:json)?\s*(.*?)\s*```", raw, re.DOTALL)
    if match:
        try:
            return json.loads(match.group(1))
        except json.JSONDecodeError:
            pass

    # 尝试提取第一个 { ... } 块
    match = re.search(r"\{.*\}", raw, re.DOTALL)
    if match:
        try:
            return json.loads(match.group(0))
        except json.JSONDecodeError:
            pass

    # 解析失败，返回原始文本
    return {"_raw": raw, "_error": "JSON parse failed"}
