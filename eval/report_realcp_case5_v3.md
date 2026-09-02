# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 22:22:25
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：125.75s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 6 | 125.75 | 5353 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 6 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgaozsegc.kt:96:25: error: unresolved reference 'refreshStateDidChange'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgaozsegc.kt:96:49: error: cannot infer type for value parameter 'state'. Specify it explicitly.
    - 第 62 行：Refresh 组件缺少 attr 块，必须包含 attr { } 配置
    - 第 67 行：List 组件缺少 attr 块，必须包含 attr { } 配置
    - 第 69 行：vforLazy 使用错误，该指令应直接应用于 List 组件内部，但当前写法可能导致编译错误
    - 第 105 行：refreshStateDidChange 事件绑定语法错误，应使用 event { refreshStateDidChange { state -> ... } } 格式

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
