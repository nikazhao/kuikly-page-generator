# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 22:55:43
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：109.28s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 47 | 109.28 | 6860 | 7/7 |

## 未通过用例

- #1 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 47 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpow1e2drv.kt:7:39: error: unresolved reference 'BorderStyle'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpow1e2drv.kt:97:63: error: unresolved reference 'BorderStyle'.
    - 第 10 行：import com.tencent.kuikly.core.directives.vforLazy 路径错误，应为 com.tencent.kuikly.core.directives.vforLazy 或检查实际包名
    - 第 11 行：import com.tencent.kuikly.core.layout.BorderStyle 路径错误，BorderStyle 通常位于 com.tencent.kuikly.core.base 包下
    - 第 12 行：import com.tencent.kuikly.core.module.NetworkModule 路径错误，NetworkModule 通常位于 com.tencent.kuikly.core.network 包下
    - 第 13 行：import com.tencent.kuikly.core.nvi.serialization.json.JSONArray 路径错误，JSONArray 通常位于 com.tencent.kuikly.core.json 包下
    - 第 14 行：import com.tencent.kuikly.core.nvi.serialization.json.JSONObject 路径错误，JSONObject 通常位于 com.tencent.kuikly.core.json 包下
    - 第 15 行：import com.tencent.kuikly.core.pager.Pager 路径错误，Pager 通常位于 com.tencent.kuikly.core.base 包下
    - 第 16 行：import com.tencent.kuikly.core.reactive.handler.observable 路径错误，observable 通常位于 com.tencent.kuikly.core.reactive 包下
    - 第 17 行：import com.tencent.kuikly.core.reactive.handler.observableList 路径错误，observableList 通常位于 com.tencent.kuikly.core.reactive 包下
    - 第 18 行：import com.tencent.kuikly.core.views.Image 路径错误，Image 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 19 行：import com.tencent.kuikly.core.views.List 路径错误，List 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 20 行：import com.tencent.kuikly.core.views.Refresh 路径错误，Refresh 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 21 行：import com.tencent.kuikly.core.views.RefreshViewState 路径错误，RefreshViewState 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 22 行：import com.tencent.kuikly.core.views.Scroller 路径错误，Scroller 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 23 行：import com.tencent.kuikly.core.views.Text 路径错误，Text 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 24 行：import com.tencent.kuikly.core.views.View 路径错误，View 通常位于 com.tencent.kuikly.core.views 包下（但已存在通配符导入）
    - 第 25 行：import com.tencent.kuikly.core.base.Border 路径错误，Border 通常位于 com.tencent.kuikly.core.base 包下
    - 第 26 行：import com.tencent.kuikly.core.views.* 通配符导入与前面具体导入冲突，且可能引入不明确的类
    - 第 27 行：import com.tencent.kuikly.core.module.Module 路径错误，Module 通常位于 com.tencent.kuikly.core.base 包下
    - 第 30 行：data class ChatItemData 定义在类外部，Kuikly 中数据类应定义在类内部或使用 @Data 注解
    - 第 36 行：private var chatList by observableList<ChatItemData>() 缺少初始值，observableList 需要初始值如 mutableListOf()
    - 第 37 行：private var refreshing by observable(false) 格式正确，但 observable 需要导入正确路径
    - 第 40 行：pagerData.pageViewWidth 和 pagerData.pageViewHeight 属性名可能不正确，应为 pageWidth 和 pageHeight
    - 第 46 行：Refresh 组件缺少 attr 块中的 refreshEnable 属性，应使用 refreshEnabled 属性名
    - 第 48 行：refreshStateDidChange 事件名可能不正确，应为 onRefreshStateChanged
    - 第 50 行：ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME) 方法名可能不正确，应为 getModule
    - 第 51 行：networkModule?.requestGet 方法签名可能不正确，应为 requestGet(url, params, callback)
    - 第 55 行：response.optJSONArray 方法名可能不正确，应为 getJSONArray
    - 第 56 行：jsonArray.length() 方法名可能不正确，应为 size()
    - 第 57 行：jsonArray.optJSONObject(i) 方法名可能不正确，应为 getJSONObject(i)
    - 第 68 行：ctx.chatList.clear() 和 ctx.chatList.addAll() 在异步回调中修改 observableList 可能不会触发 UI 更新，应使用 ctx.chatList = newList
    - 第 76 行：vforLazy 指令使用方式可能不正确，应为 vforLazy(ctx.chatList) { item, index -> ... }
    - 第 80 行：flexDirectionRow() 方法名可能不正确，应为 flexDirection(Row)
    - 第 81 行：alignItemsCenter() 方法名可能不正确，应为 alignItems(Center)
    - 第 82 行：padding(12f, 16f, 12f, 16f) 参数顺序可能不正确，应为 padding(top, right, bottom, left)
    - 第 86 行：borderRadius(24f) 方法名可能不正确，应为 cornerRadius(24f)
    - 第 87 行：border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0))) 语法可能不正确，应为 border(1f, BorderStyle.Solid, Color(0xFFE0E0E0))
    - 第 88 行：src(item.avatar) 方法名可能不正确，应为 imageUrl(item.avatar)
    - 第 92 行：flexDirectionColumn() 方法名可能不正确，应为 flexDirection(Column)
    - 第 93 行：flex(1f) 方法名可能不正确，应为 flex(1)
    - 第 94 行：marginLeft(12f) 和 marginRight(12f) 方法名可能不正确，应为 margin(left=12f, right=12f)
    - 第 98 行：fontWeightBold() 方法名可能不正确，应为 fontWeight(Bold)
    - 第 99 行：fontSize(16f) 方法名可能不正确，应为 fontSize(16)
    - 第 105 行：lines(1) 方法名可能不正确，应为 maxLines(1)
    - 第 106 行：marginTop(4f) 方法名可能不正确，应为 margin(top=4f)
    - 第 112 行：textAlignCenter() 方法名可能不正确，应为 textAlign(Center)

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
