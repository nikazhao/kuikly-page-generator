# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 23:59:42
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**70%** (7/10)
- 平均自动修正次数：2.0
- 平均耗时：82.13s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 2 | 0 | 82.62 | 5924 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 1 | 0 | 48.23 | 5134 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 3 | 0 | 75.88 | 4615 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ❌ | 3 | 2 | 96.55 | 5337 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ✅ | 1 | 0 | 65.17 | 8851 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ❌ | 3 | 4 | 94.52 | 8495 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 2 | 0 | 188.8 | 5839 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ❌ | 3 | 7 | 73.8 | 3459 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ✅ | 1 | 0 | 59.61 | 8591 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 1 | 0 | 36.09 | 2725 | 7/7 |

## 未通过用例

- #4 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底部是功能菜单列表 — 修正 3 次后仍剩 2 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphc8yvwym.kt:18:37: error: unresolved reference 'PositionType'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmphc8yvwym.kt:122:50: error: unresolved reference 'PositionType'.
- #6 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有获取按钮，底部注册按 — 修正 3 次后仍剩 4 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp8stab0ze.kt:17:8: error: unresolved reference 'kotlinx'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp8stab0ze.kt:28:33: error: unresolved reference 'Job'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp8stab0ze.kt:137:58: error: unresolved reference 'MainScope'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp8stab0ze.kt:139:45: error: unresolved reference 'delay'.
- #8 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 — 修正 3 次后仍剩 7 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmp35h6eyz5.kt:73:37: error: unresolved reference 'padding'.
    - 第 18 行：hotTags 声明为 observableList<String>()，但 observableList 需要初始值，应改为 observableListOf<String>() 或 mutableListOf<String>()
    - 第 19 行：selectedTag 声明为 observable("")，但 observable 泛型推断可能有问题，建议显式指定类型 observable<String>("")
    - 第 28 行：pagerData.pageViewWidth 在 Kuikly 中不存在，应使用 pagerData.width 或 pagerData.height
    - 第 36 行：pagerData.pageViewWidth 同上，应使用 pagerData.width
    - 第 50 行：pagerData.pageViewWidth 同上，应使用 pagerData.width
    - 第 60 行：vfor 指令语法错误，应为 vfor({ ctx.hotTags }) { tag -> ... }，但当前写法缺少 lambda 参数类型声明，且 vfor 在 Kuikly 中可能不支持直接使用 observableList，需要转换为普通 List

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
