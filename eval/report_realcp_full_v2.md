# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 21:29:55
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**90%** (9/10)
- 平均自动修正次数：1.4
- 平均耗时：55.7s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 1 | 0 | 42.09 | 4474 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 0 | 0 | 38.94 | 7091 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 0 | 0 | 32.9 | 5093 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 1 | 0 | 50.9 | 5570 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 22 | 93.1 | 8159 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ✅ | 2 | 0 | 71.57 | 8529 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 1 | 0 | 49.69 | 6951 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ✅ | 3 | 0 | 74.26 | 4652 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ✅ | 1 | 0 | 55.11 | 8433 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 2 | 0 | 48.39 | 2636 | 7/7 |

## 未通过用例

- #5 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 22 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpb47wzpmq.kt:142:49: error: unresolved reference 'maxLines'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpb47wzpmq.kt:143:49: error: unresolved reference 'lineBreakMode'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpb47wzpmq.kt:143:63: error: unresolved reference 'LineBreakMode'.
    - 第 14 行：import com.tencent.kuikly.core.views.* 与前面具体导入的 View、Text、Image 等冲突，应移除通配符导入或移除具体导入
    - 第 30 行：observableList 的泛型参数缺失，应改为 observableList<ChatItem>()
    - 第 31 行：observable 的泛型参数缺失，应改为 observable(false)
    - 第 32 行：observable 的泛型参数缺失，应改为 observable(1)
    - 第 33 行：observable 的泛型参数缺失，应改为 observable(true)
    - 第 60 行：Refresh 组件缺少 attr 块中的 refreshEnable 属性，应使用 refreshEnable(true) 或类似方式
    - 第 62 行：refreshStateDidChange 事件回调中 state 类型应为 RefreshViewState，但未导入 RefreshViewState
    - 第 68 行：vforLazy 的 lambda 参数 index 和 count 未使用，但这不是错误，只是警告
    - 第 72 行：flexDirectionRow() 应为 flexDirection(Row) 或类似正确 API
    - 第 73 行：alignItemsCenter() 应为 alignItems(Center) 或类似正确 API
    - 第 80 行：acquireModule 方法可能不存在，应使用 getModule 或类似正确 API
    - 第 82 行：JSONObject 的 put 方法返回 Unit，不能链式调用，应分开写
    - 第 88 行：src(item.avatar) 应为 source(item.avatar) 或类似正确 API
    - 第 103 行：fontWeightMedium() 应为 fontWeight(Medium) 或类似正确 API
    - 第 113 行：lineBreakMode(LineBreakMode.TAIL) 中 LineBreakMode 未导入
    - 第 117 行：allCenter() 应为 justifyContent(Center) 和 alignItems(Center) 或类似正确 API
    - 第 123 行：fontWeightBold() 应为 fontWeight(Bold) 或类似正确 API
    - 第 68 行：vforLazy 的 lambda 中 count 参数未使用，但这不是错误
    - 第 60 行：Refresh 组件缺少 event 块中的 refreshStateDidChange 事件正确绑定方式

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
