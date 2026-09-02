# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 18:22:11
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**90%** (9/10)
- 平均自动修正次数：0.8
- 平均耗时：72.2s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：`success` 由 kotlinc 语法校验（已过滤 classpath 噪声）＋结构规则＋LLM 自审三层构成，**并未在真实 Kuikly classpath 下编译运行**；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 0 | 0 | 175.91 | 6357 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ❌ | 3 | 17 | 101.33 | 5045 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 3 | 0 | 167.94 | 6070 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 0 | 0 | 31.17 | 5779 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ✅ | 1 | 0 | 54.61 | 6170 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ✅ | 0 | 0 | 33.35 | 7915 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 0 | 0 | 30.6 | 5984 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ✅ | 0 | 0 | 38.59 | 10620 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ✅ | 1 | 0 | 64.06 | 9066 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 0 | 0 | 24.49 | 2608 | 7/7 |

## 未通过用例

- #2 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间模式等设置 — 修正 3 次后仍剩 17 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmparg4wsmr.kt:127:77: error: this declaration needs opt-in. Its usage must be marked with '@kotlin.ExperimentalStdlibApi' or '@OptIn(kotlin.ExperimentalStdlibApi::class)'
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmparg4wsmr.kt:127:82: error: no parameter with name 'isOn' found.
    - 第 72 行：attr 块中 size(0f, 80f) 的宽度参数 0f 不合法，Kuikly 中 size 的宽度不能为 0f，应使用 WRAP 或具体数值
    - 第 88 行：attr 块中 size(0f, 200f) 的宽度参数 0f 不合法，同上
    - 第 99 行：attr 块中 size(0f, 50f) 的宽度参数 0f 不合法，同上
    - 第 72 行：attr 块中 flexDirectionRow() 方法不存在，Kuikly 中应使用 flexDirection(Row) 或类似写法
    - 第 88 行：attr 块中 flexDirectionColumn() 方法不存在，应使用 flexDirection(Column)
    - 第 99 行：attr 块中 flexDirectionRow() 方法不存在，同上
    - 第 73 行：attr 块中 alignItemsCenter() 方法不存在，应使用 alignItems(Center) 或 alignItems(Align.Center)
    - 第 100 行：attr 块中 alignItemsCenter() 方法不存在，同上
    - 第 80 行：attr 块中 fontWeight600() 方法不存在，应使用 fontWeight(600) 或 fontWeight(FontWeight.SemiBold)
    - 第 81 行：attr 块中 text() 方法不应在 attr 块内使用，text 是 Text 组件的属性，应直接作为 Text 的 attr 属性或使用 text() 函数
    - 第 103 行：attr 块中 text() 方法同上问题
    - 第 106 行：Switch 组件的 attr 块中 isOn() 方法不存在，应使用 isOn 属性赋值或使用 checked() 方法
    - 第 109 行：event 块中 onChange 事件名称可能不正确，Kuikly 中 Switch 的事件应为 onCheckedChange 或类似名称
    - 第 110 行：ctx.switchItems.set(index, item.copy(isOn = isOn)) 中 observableList 的 set 方法可能不存在，应使用 update 或直接修改 item 的 isOn 属性（若 item 是 observable）
    - 第 4 行：import com.tencent.kuikly.core.views.* 与前面的具体 import 冲突，应移除通配符导入或保留具体导入

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
