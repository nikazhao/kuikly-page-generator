# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 23:01:50
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：104.53s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 19 | 104.53 | 6982 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 19 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpepg8g8_7.kt:54:57: error: 'fun <T : Module> getModule(name: String): T?' cannot be called in this context with an implicit receiver. Use an explicit receiver if necessary.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpepg8g8_7.kt:55:52: error: none of the following candidates is applicable:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpepg8g8_7.kt:57:41: error: null cannot be a value of a non-null type 'JSONObject'.
    - 第 13 行：import com.tencent.kuikly.core.views.* 与前面具体的 import 冲突，应移除通配符导入
    - 第 14 行：import com.tencent.kuikly.core.module.Module 未使用，应移除
    - 第 27 行：observableList 初始化缺少默认值，应改为 observableList<ChatItemData>()
    - 第 37 行：Scroller 组件缺少 attr 块中的 scrollDirection 属性（必须指定滚动方向）
    - 第 40 行：Refresh 组件缺少 attr 块中的 refreshEnable 属性（应直接设置 refreshEnable = true 但需要确认该属性是否在 attr 块内支持）
    - 第 41 行：Refresh 组件的 event 块中 refreshStateDidChange 回调参数类型不匹配，应为 (RefreshViewState) -> Unit
    - 第 43 行：NetworkModule 的 requestGet 方法签名不匹配，回调参数应为 (JSONObject?, Boolean, String) -> Unit
    - 第 44 行：JSONObject 的 optJSONArray 方法返回 JSONArray?，但后续直接调用 length() 可能空指针
    - 第 47 行：dataList.length() 应使用 ?.let 安全调用
    - 第 60 行：vfor 使用方式错误，vfor 应作为组件属性而非独立调用，且 lambda 参数应为 (Int, ChatItemData) -> Unit
    - 第 62 行：flexDirection(FlexDirection.ROW) 应使用 flexDirectionRow() 简写方法
    - 第 63 行：padding(12f, 10f, 12f, 10f) 参数顺序应为 (top, right, bottom, left) 但 Kuikly 中 padding 方法签名可能不同
    - 第 64 行：alignItemsCenter() 方法不存在，应使用 alignItems(AlignItems.CENTER)
    - 第 70 行：src(item.avatar) 应使用 imageUrl(item.avatar) 或类似方法
    - 第 79 行：fontWeightMedium() 方法不存在，应使用 fontWeight(FontWeight.MEDIUM)
    - 第 88 行：lines(1) 应使用 maxLines(1) 或 lineLimit(1)

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
