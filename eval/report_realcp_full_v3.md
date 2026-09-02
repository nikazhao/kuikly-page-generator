# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 23:40:38
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**70%** (7/10)
- 平均自动修正次数：1.9
- 平均耗时：69.62s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ❌ | 3 | 3 | 84.38 | 6533 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 1 | 0 | 53.51 | 6948 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 0 | 0 | 30.67 | 5181 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 2 | 0 | 65.85 | 4970 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 25 | 103.12 | 6512 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ✅ | 2 | 0 | 72.09 | 8534 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 1 | 0 | 45.45 | 4784 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ✅ | 3 | 0 | 103.64 | 3762 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ❌ | 3 | 1 | 99.81 | 8968 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 1 | 0 | 37.64 | 2646 | 7/7 |

## 未通过用例

- #1 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击后验证输入 — 修正 3 次后仍剩 3 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpisa_9i5c.kt:15:46: error: unresolved reference 'Border'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpisa_9i5c.kt:71:40: error: unresolved reference 'Border'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpisa_9i5c.kt:100:40: error: unresolved reference 'Border'.
- #5 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 25 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpo8d4taqm.kt:122:45: error: unresolved reference 'textAlignEnd'.
    - 第 1 行: package 声明格式错误，应为 'package com.tencent.kuikly.demo.pages' 但实际为 'package com.tencent.kuikly.demo.pages'（实际正确，但需检查是否有多余空格或换行）
    - 第 3 行: import 语句 'com.tencent.kuikly.core.annotations.Page' 正确，但后续存在重复导入 'com.tencent.kuikly.core.views.*' 与具体类导入冲突
    - 第 17 行: 重复导入 'com.tencent.kuikly.core.views.*' 与之前的具体类导入冲突，应移除通配符导入
    - 第 18 行: 导入 'com.tencent.kuikly.core.module.Module' 未使用
    - 第 30 行: 'observableList' 声明格式错误，应为 'private var chatList by observableList<ChatItemData>()' 但缺少括号
    - 第 31 行: 'observable' 声明格式错误，应为 'private var refreshing by observable(false)' 但缺少括号
    - 第 40 行: 'Refresh' 组件缺少 'attr { }' 块，但实际存在，需检查括号匹配
    - 第 42 行: 'refreshEnable' 属性可能不存在于 Kuikly 框架中，应为 'refreshEnabled'
    - 第 44 行: 'refreshStateDidChange' 事件名可能不正确，应为 'onRefreshStateChange'
    - 第 47 行: 'acquireModule' 方法可能不存在，应为 'getModule'
    - 第 48 行: 'requestGet' 方法参数错误，应为 'requestGet(url, params, callback)' 但缺少 params 参数
    - 第 50 行: 'response.optString' 方法可能不存在，应为 'response.getString'
    - 第 55 行: 'setObject' 方法可能不存在，应为 'putString' 或 'putObject'
    - 第 56 行: 'System.currentTimeMillis()' 在 Kuikly 中不可用，应使用 'Date().time' 或平台相关 API
    - 第 66 行: 'vforLazy' 函数参数错误，应为 'vforLazy(items, { item, index -> ... })' 但多了一个参数
    - 第 67 行: 'flexDirectionRow()' 方法可能不存在，应为 'flexDirection = FlexDirection.ROW'
    - 第 68 行: 'alignItemsCenter()' 方法可能不存在，应为 'alignItems = Align.CENTER'
    - 第 69 行: 'padding(12f, 10f, 12f, 10f)' 参数顺序可能错误，应为 'padding(top, right, bottom, left)' 或 'padding(all)'
    - 第 73 行: 'src(item.avatar)' 属性可能不存在，应为 'source = item.avatar'
    - 第 80 行: 'fontWeightBold()' 方法可能不存在，应为 'fontWeight = FontWeight.BOLD'
    - 第 86 行: 'lines(1)' 方法可能不存在，应为 'maxLines = 1'
    - 第 91 行: 'textAlignEnd()' 方法可能不存在，应为 'textAlign = TextAlign.END'
    - 第 100 行: 'parseChatList' 函数返回类型应为 'List<ChatItemData>' 而非 'MutableList'
    - 第 101-104 行: 硬编码数据不符合实际网络请求逻辑，应解析 response 数据
- #9 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条和一个列表 — 修正 3 次后仍剩 1 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpkm6p_kok.kt:183:29: error: unresolved reference 'padding'.

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
