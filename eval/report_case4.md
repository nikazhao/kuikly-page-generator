# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 18:01:26
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：999.55s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：`success` 由 kotlinc 语法校验（已过滤 classpath 噪声）＋结构规则＋LLM 自审三层构成，**并未在真实 Kuikly classpath 下编译运行**；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ❌ | 3 | 6 | 999.55 | 5513 | 7/7 |

## 未通过用例

- #1 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底部是功能菜单列表 — 修正 3 次后仍剩 6 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpsbu5b6b5.kt:111:47: error: 'operator' modifier is required on 'fun <T> Comparable<T>.compareTo(other: T): Int'.
    - 重复导入 com.tencent.kuikly.core.views.Center 和 com.tencent.kuikly.core.views.layout.Center，导致冲突
    - 重复导入 com.tencent.kuikly.core.views.* 与具体视图类导入冲突
    - vforIndex 使用方式错误，应为 vforIndex(ctx.menuList) { item, index -> ... }，缺少 count 参数或参数列表不匹配
    - border 属性在 attr 块内使用 if 条件语法不支持，需使用条件表达式或 builder 模式
    - pagerData 未在代码中定义或导入，可能无法访问 pageViewWidth/pageViewHeight

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
