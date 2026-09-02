# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 22:17:24
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：97.2s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 18 | 97.2 | 6289 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 18 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp5jt0ll_9.kt:114:56: error: check for instance is always 'false'.
    - 第 1 行: package 声明格式错误，应为 'package com.tencent.kuikly.demo.pages' 但实际为 'package com.tencent.kuikly.demo.pages'（实际检查发现 package 声明正确，但后续检查发现其他问题）
    - 第 10 行: 重复导入 'com.tencent.kuikly.core.views.*' 与前面的具体导入冲突
    - 第 30 行: 'observableList' 使用错误，应为 'observableListOf' 或 'mutableObservableListOf'
    - 第 31 行: 'observable' 使用错误，应为 'observableOf' 或 'mutableObservableOf'
    - 第 38 行: 'pagerData' 未定义，应为 'pageData' 或通过其他方式获取页面尺寸
    - 第 44 行: 'vfor' 指令使用错误，应为 'vFor' 或 'forEach'
    - 第 46 行: 'flexDirectionRow()' 方法不存在，应为 'flexDirection(FlexDirection.ROW)'
    - 第 56 行: 'justifyContent(FlexJustifyContent.CENTER)' 使用错误，应为 'justifyContent(FlexJustifyContent.CENTER)' 但 'justifyContent' 方法可能不存在，应为 'justifyContent(FlexJustifyContent.CENTER)' 或直接设置属性
    - 第 60 行: 'fontWeightBold()' 方法不存在，应为 'fontWeight(FontWeight.BOLD)'
    - 第 69 行: 'textAlignRight()' 方法不存在，应为 'textAlign(TextAlign.RIGHT)'
    - 第 70 行: 'alignSelf(FlexAlign.FLEX_END)' 使用错误，应为 'alignSelf(FlexAlign.FLEX_END)' 但 'alignSelf' 方法可能不存在，应为 'alignSelf(FlexAlign.FLEX_END)' 或直接设置属性
    - 第 79 行: 'acquireModule' 方法不存在，应为 'getModule' 或 'requireModule'
    - 第 80 行: 'requestGet' 方法参数错误，应为 'requestGet(url, params, callback)' 但回调参数顺序可能不正确
    - 第 82 行: 'response is JSONArray' 类型判断错误，应为 'response is JSONArray' 但 response 可能为 JSONObject 类型
    - 第 84 行: 'parseChatList' 方法未定义，应为 'parseChatList' 但实际已定义，但调用时可能存在问题
    - 第 96 行: 'optJSONObject' 方法不存在，应为 'getJSONObject' 或 'optJSONObject' 但可能不存在于 JSONArray 中
    - 第 97-101 行: 'optString' 方法不存在，应为 'getString' 或 'optString' 但可能不存在于 JSONObject 中

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
