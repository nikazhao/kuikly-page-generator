# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 22:47:02
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：242.59s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 33 | 242.59 | 7767 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 33 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:86:25: error: cannot infer type for type parameter 'T'. Specify it explicitly.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:86:25: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:87:29: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:88:38: error: argument type mismatch: actual type is 'DivAttr.() -> Unit', but 'DivAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:94:33: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:95:42: error: argument type mismatch: actual type is 'ImageAttr.() -> Unit', but 'ImageAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:102:33: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:103:42: error: argument type mismatch: actual type is 'DivAttr.() -> Unit', but 'DivAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:106:37: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:107:46: error: argument type mismatch: actual type is 'DivAttr.() -> Unit', but 'DivAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:111:41: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:112:50: error: argument type mismatch: actual type is 'TextAttr.() -> Unit', but 'TextAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:120:41: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:121:50: error: argument type mismatch: actual type is 'TextAttr.() -> Unit', but 'TextAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:129:37: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:130:46: error: argument type mismatch: actual type is 'TextAttr.() -> Unit', but 'TextAttr.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:139:39: error: argument type mismatch: actual type is 'DivEvent.() -> Unit', but 'DivEvent.() -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:140:47: error: argument type mismatch: actual type is '(TouchParams) -> Unit', but '(TouchParams) -> Unit' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:141:56: error: argument type mismatch: actual type is 'ChatItemData?', but 'K? (of fun <K> ELVIS_CALL)' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:141:89: error: argument type mismatch: actual type is 'Nothing', but 'K (of fun <K> ELVIS_CALL)' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:144:45: error: argument type mismatch: actual type is 'String', but 'String' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:145:45: error: argument type mismatch: actual type is 'JSONObject', but 'JSONObject?' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpk2l9i9bn.kt:145:58: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - 第 22 行：import com.tencent.kuikly.core.views.* 与前面的具体 import 冲突，应移除通配符导入
    - 第 25 行：import com.tencent.kuikly.core.module.Module 未使用，应移除
    - 第 30 行：observableList 的泛型参数应为 MutableList<ChatItemData>，当前写法可能无法正确推导
    - 第 63 行：vforLazy 的第三个参数类型应为 Int，但 lambda 中使用了 _: Int 占位，实际未使用，应移除该参数
    - 第 64 行：vforLazy 的 lambda 参数 index 类型应为 Int，但实际使用中可能为 Int?，需确认
    - 第 93 行：touchDown 事件回调参数类型应为 TouchEvent，但 lambda 中使用了 _ 忽略，应明确参数类型
    - 第 95 行：routerModule?.openPage 方法签名可能不匹配，openPage 通常需要 String 和 JSONObject，但此处 JSONObject.apply 可能返回类型不兼容
    - 第 101 行：event 块应放置在 vforLazy 内部组件上，但当前 event 块位于 View 组件上，可能导致事件绑定错误
    - 第 103 行：ctx.chatList.getOrNull(index) 中 index 可能为 Int?，需安全转换
    - 第 110 行：缺少右大括号 }，导致括号不匹配

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
