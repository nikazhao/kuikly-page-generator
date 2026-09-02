# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 22:27:02
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：119.55s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 45 | 119.55 | 8383 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 45 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:66:82: error: unresolved reference 'getJSONObject'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:76:70: error: unresolved reference 'clear'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:77:70: error: unresolved reference 'addAll'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:93:54: error: unresolved reference 'clear'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:94:54: error: unresolved reference 'addAll'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:100:25: error: cannot infer type for type parameter 'T'. Specify it explicitly.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:100:25: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:100:54: error: cannot infer type for type parameter 'T'. Specify it explicitly.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:101:29: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:102:38: error: argument type mismatch: actual type is 'DivAttr.() -> Unit', but 'DivAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:105:37: error: unresolved reference 'paddingHorizontal'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:106:37: error: unresolved reference 'paddingVertical'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:109:33: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:110:42: error: argument type mismatch: actual type is 'ImageAttr.() -> Unit', but 'ImageAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:113:50: error: unresolved reference 'avatar'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:116:33: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:117:42: error: argument type mismatch: actual type is 'DivAttr.() -> Unit', but 'DivAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:121:37: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:122:46: error: argument type mismatch: actual type is 'DivAttr.() -> Unit', but 'DivAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:127:41: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:128:50: error: argument type mismatch: actual type is 'TextAttr.() -> Unit', but 'TextAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:129:59: error: unresolved reference 'nickname'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:136:41: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:137:50: error: argument type mismatch: actual type is 'TextAttr.() -> Unit', but 'TextAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:138:59: error: unresolved reference 'time'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:145:37: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:146:46: error: argument type mismatch: actual type is 'TextAttr.() -> Unit', but 'TextAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpgcw3f2jm.kt:147:55: error: unresolved reference 'lastMessage'.
    - 第 14 行：import com.tencent.kuikly.core.views.* 与前面的具体 import 冲突，应移除通配符导入
    - 第 16 行：import com.tencent.kuikly.core.module.Module 未使用，应移除
    - 第 20 行：observableList 不能直接作为 observable 的初始值，应使用 observableListOf() 或 mutableListOf() 包裹
    - 第 21 行：isRefreshing 变量声明后未在 body 中使用，但无实际错误，仅警告
    - 第 30 行：Scroller 组件缺少 attr 块中的 scrollDirection 属性（默认垂直，但建议显式声明）
    - 第 33 行：Refresh 组件缺少 attr 块中的 refreshEnable 属性（已存在，但需确认属性名正确）
    - 第 36 行：refreshStateDidChange 事件回调中使用了 ctx.isRefreshing，但 isRefreshing 是 observable 变量，赋值方式正确
    - 第 40 行：networkModule.requestGet 回调中使用了 JSONObject() 作为参数，但 Kuikly 中 NetworkModule 的 requestGet 方法签名可能不同，需确认
    - 第 67 行：vforLazy 使用正确，但需确保 chatList 是 observableList 类型
    - 第 72 行：flexDirectionRow() 应为 flexDirection(Row) 或类似写法，Kuikly 中可能不支持直接调用 flexDirectionRow() 方法
    - 第 73 行：alignItemsCenter() 应为 alignItems(Center) 或类似写法
    - 第 74 行：paddingHorizontal(16f) 和 paddingVertical(12f) 可能不支持，应使用 padding(16f, 12f, 16f, 12f) 或类似写法
    - 第 82 行：marginLeft(12f) 可能不支持，应使用 margin(0f, 0f, 0f, 12f) 或类似写法
    - 第 86 行：fontWeightBold() 可能不支持，应使用 fontWeight(FontWeight.Bold) 或类似写法
    - 第 91 行：marginLeft(8f) 同上
    - 第 97 行：lines(1) 可能不支持，应使用 maxLines(1) 或类似写法
    - 第 98 行：marginTop(4f) 可能不支持，应使用 margin(4f, 0f, 0f, 0f) 或类似写法

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
