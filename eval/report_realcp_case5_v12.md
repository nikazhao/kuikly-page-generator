# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 23:08:32
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：113.81s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 7 | 113.81 | 7229 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 7 个问题
    - 第 27 行：data class ChatItemData 定义在文件顶层，但 Kuikly 要求所有数据类必须在 Page 类内部定义或使用 @Keep 注解
    - 第 37 行：observableList 的泛型参数 ChatItemData 未在 Page 类内部定义，可能导致序列化问题
    - 第 80 行：vforLazy 的第三个参数是 Int 类型，但 lambda 中声明了三个参数 (item, index, _)，实际 Kuikly 的 vforLazy 只支持两个参数 (item, index)
    - 第 57 行：acquireModule 方法签名可能为 acquireModule<T>(name: String, clazz: Class<T>)，缺少第二个参数
    - 第 59 行：requestGet 回调参数顺序可能为 (response, error) 而非 (response, success, error)
    - 第 63 行：JSONArray 的 length() 方法在 Kuikly 中可能为 size() 或 count()
    - 第 77 行：List 组件内部不能直接使用 vforLazy，vforLazy 应放在 View 或 Scroller 等容器组件上

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
