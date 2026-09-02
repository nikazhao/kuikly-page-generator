# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-03 00:53:01
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**70%** (7/10)
- 平均自动修正次数：1.8
- 平均耗时：100.91s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ❌ | 3 | 5 | 183.2 | 6427 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 1 | 0 | 54.93 | 7604 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 0 | 0 | 27.56 | 3449 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 0 | 0 | 38.44 | 6944 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 11 | 236.62 | 8532 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ✅ | 3 | 0 | 103.33 | 8878 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 1 | 0 | 53.73 | 6386 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ✅ | 3 | 0 | 143.71 | 4201 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ❌ | 3 | 4 | 125.82 | 10158 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 1 | 0 | 41.79 | 2507 | 7/7 |

## 未通过用例

- #1 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击后验证输入 — 修正 3 次后仍剩 5 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp7hxqmbcz.kt:100:29: error: unresolved reference 'paddingLeft'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp7hxqmbcz.kt:101:29: error: unresolved reference 'paddingRight'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp7hxqmbcz.kt:121:29: error: unresolved reference 'paddingLeft'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp7hxqmbcz.kt:122:29: error: unresolved reference 'paddingRight'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp7hxqmbcz.kt:164:29: error: unresolved reference 'show'.
- #5 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 11 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpc7vps26h.kt:161:33: error: unresolved reference 'onLoadMore'.
    - 第 1 行：package 声明格式错误，应为 'package com.tencent.kuikly.demo.pages' 但实际为 'package com.tencent.kuikly.demo.pages'（实际正确，但需检查是否有多余空格或换行）
    - 第 3 行：import 语句 'com.tencent.kuikly.core.annotations.Page' 正确，但后续 import 中 'com.tencent.kuikly.core.views.*' 与前面具体 import 冲突，应移除通配符导入
    - 第 13 行：import 'com.tencent.kuikly.core.views.*' 与前面具体导入重复，应删除
    - 第 17 行：data class ChatItem 定义在文件顶层，但 Kuikly 要求所有数据类应在类内部或使用特定方式，建议移至 ChatListPage 内部或使用 @Data 注解
    - 第 22 行：object ChatListPageStyle 定义在文件顶层，Kuikly 不支持顶层 object，应移至类内部或使用 companion object
    - 第 53 行：'pagerData.pageViewWidth' 和 'pagerData.pageViewHeight' 属性名可能不正确，应为 'pageWidth' 和 'pageHeight'
    - 第 62 行：'RefreshViewState.REFRESHING' 引用错误，应为 'RefreshState.REFRESHING' 或类似枚举
    - 第 108 行：'fontWeightMedium()' 方法不存在，应为 'fontWeight(FontWeight.Medium)' 或类似
    - 第 120 行：'onLoadMore' 事件可能不存在，应为 'onScrollToBottom' 或类似
    - 第 146 行：'acquireModule' 方法可能不存在，应为 'getModule' 或 'requireModule'
- #9 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条和一个列表 — 修正 3 次后仍剩 4 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpyhqwze9d.kt:47:23: error: none of the following candidates is applicable:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpyhqwze9d.kt:50:13: error: class '<anonymous>' is not abstract and does not implement abstract member:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpyhqwze9d.kt:51:17: error: 'onResult' overrides nothing.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpyhqwze9d.kt:61:41: error: argument type mismatch: actual type is 'List<String?>', but 'Collection<String>' was expected.

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
