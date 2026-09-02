# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-03 00:17:09
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**80%** (8/10)
- 平均自动修正次数：1.4
- 平均耗时：83.29s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 1 | 0 | 103.4 | 13809 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 1 | 0 | 141.86 | 10184 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 0 | 0 | 31.68 | 4263 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 1 | 0 | 57.46 | 5752 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 8 | 107.01 | 6822 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ✅ | 2 | 0 | 97.2 | 9897 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 1 | 0 | 56.63 | 8066 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ❌ | 3 | 2 | 138.66 | 4557 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ✅ | 1 | 0 | 59.64 | 8461 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 1 | 0 | 39.38 | 2262 | 7/7 |

## 未通过用例

- #5 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 8 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpxpa5d6u_.kt:118:41: error: unresolved reference 'longClick'.
    - 第 89 行：vfor 指令使用错误，vfor 需要接收一个 lambda 参数，但当前写法不符合 Kuikly 的 vfor 语法规范。正确的用法是 vfor({ ctx.chatList }) { item -> ... }，但这里 lambda 参数类型不匹配，且 vfor 内部不能直接使用 View 组件，应使用 vfor 包裹的组件列表。
    - 第 90 行：vfor 内部不能直接使用 View 组件，vfor 应直接包裹在需要循环的组件上，而不是在 View 内部使用 vfor。
    - 第 100 行：longClick 事件在 Kuikly 中不支持，Kuikly 只支持 click 事件。
    - 第 108 行：Image 组件的 src 属性应使用字符串常量或 observable 变量，但这里直接使用 item.avatar，可能导致编译错误，因为 Kuikly 的 Image 组件要求 src 为静态资源或网络 URL 字符串。
    - 第 130 行：textAlignRight() 方法不存在，Kuikly 中 Text 组件没有 textAlignRight 方法，应使用 textAlign(TextAlign.RIGHT) 或类似方式。
    - 第 83 行：refreshStateDidChange 事件回调中直接修改 observable 变量（isRefreshing、page、chatList）可能导致状态更新问题，建议使用异步或延迟更新。
    - 第 84 行：在 refreshStateDidChange 回调中直接调用 chatList.clear() 可能触发多次 UI 更新，建议使用 chatList = mutableListOf() 或类似方式。
- #8 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 — 修正 3 次后仍剩 2 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpazkbkcwx.kt:54:29: error: unresolved reference 'paddingLeft'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpazkbkcwx.kt:55:29: error: unresolved reference 'paddingRight'.

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
