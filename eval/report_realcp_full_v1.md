# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 21:12:29
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**50%** (5/10)
- 平均自动修正次数：2.2
- 平均耗时：200.85s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 1 | 0 | 55.71 | 7541 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ❌ | 3 | 10 | 96.73 | 8670 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 2 | 0 | 221.56 | 4584 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 2 | 0 | 67.19 | 4645 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ❌ | 3 | 4 | 213.29 | 4887 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ❌ | 3 | 12 | 92.61 | 8537 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 1 | 0 | 129.93 | 32890 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ❌ | 3 | 7 | 83.18 | 4366 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ❌ | 3 | 2 | 108.68 | 10725 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 1 | 0 | 939.6 | 2562 | 7/7 |

## 未通过用例

- #2 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间模式等设置 — 修正 3 次后仍剩 10 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:40:41: error: unresolved reference 'getBoolean'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:41:38: error: unresolved reference 'getBoolean'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:42:34: error: unresolved reference 'getBoolean'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:43:38: error: unresolved reference 'getBoolean'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:131:37: error: unresolved reference 'switchChange'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:133:55: error: unresolved reference 'setBoolean'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:163:37: error: unresolved reference 'switchChange'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:165:55: error: unresolved reference 'setBoolean'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:194:37: error: unresolved reference 'switchChange'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphy64cxpl.kt:196:55: error: unresolved reference 'setBoolean'.
- #5 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — 修正 3 次后仍剩 4 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpbf05lsxf.kt:39:17: error: unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpbf05lsxf.kt:42:25: error: unresolved reference 'refreshing'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpbf05lsxf.kt:60:54: error: unresolved reference 'network'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpbf05lsxf.kt:98:25: error: unresolved reference 'onRefresh'.
- #6 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有获取按钮，底部注册按 — 修正 3 次后仍剩 12 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:7:32: error: unresolved reference 'network'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:16:32: error: unresolved reference 'network'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:119:65: error: inapplicable candidate(s): fun <T : Module> acquireModule(name: String): T
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:119:79: error: unresolved reference 'NetworkModule'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:119:94: error: unresolved reference 'NetworkModule'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:120:56: error: unresolved reference 'requestPost'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:125:54: error: unresolved reference 'Callback'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:179:57: error: inapplicable candidate(s): fun <T : Module> acquireModule(name: String): T
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:179:71: error: unresolved reference 'NetworkModule'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:179:86: error: unresolved reference 'NetworkModule'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:180:48: error: unresolved reference 'requestPost'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp2fwwp09k.kt:187:46: error: unresolved reference 'Callback'.
- #8 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 — 修正 3 次后仍剩 7 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpt4trc03t.kt:58:25: error: unresolved reference 'paddingLeft'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpt4trc03t.kt:59:25: error: unresolved reference 'paddingRight'.
    - 第 55 行：vfor 语法错误，应为 vfor(ctx.hotTags) { tag -> ... }，当前写法 vfor({ ctx.hotTags }) 不符合 Kuikly 编译规则
    - 第 55 行：vfor 的 lambda 参数 tag 未正确声明类型，应为 (tag: String) -> 或直接使用 tag 但需确保类型推断正确
    - 第 55 行：vfor 内部 View 的 event 块位置错误，事件应直接挂在组件上，但当前 event 块位于 vfor 的 lambda 内部且与 Text 同级，可能导致事件绑定异常
    - 第 55 行：vfor 内部 View 的 attr 块中缺少 size 或 flex 属性，可能导致布局异常
    - 第 55 行：vfor 内部 View 的 event 块中 click 事件未正确使用 params 参数，应为 click { params -> ... } 或 click { ... } 但当前写法不完整
- #9 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条和一个列表 — 修正 3 次后仍剩 2 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp_uxl65ml.kt:147:29: error: unresolved reference 'progress'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp_uxl65ml.kt:149:29: error: unresolved reference 'activeColor'.

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
