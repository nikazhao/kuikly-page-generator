# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 18:45:08
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**90%** (9/10)
- 平均自动修正次数：0.6
- 平均耗时：45.02s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：`success` 由 kotlinc 语法校验（已过滤 classpath 噪声）＋结构规则＋LLM 自审三层构成，**并未在真实 Kuikly classpath 下编译运行**；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 0 | 0 | 32.99 | 6544 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 0 | 0 | 34.14 | 7809 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ✅ | 0 | 0 | 27.63 | 3448 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ✅ | 0 | 0 | 34.76 | 7622 | 7/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ✅ | 3 | 0 | 81.21 | 5538 | 7/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ✅ | 0 | 0 | 36.05 | 7773 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 0 | 0 | 35.36 | 7317 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ❌ | 3 | 12 | 81.16 | 3840 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ✅ | 0 | 0 | 63.05 | 23553 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 0 | 0 | 23.89 | 2566 | 7/7 |

## 未通过用例

- #8 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 — 修正 3 次后仍剩 12 个问题
    - 第 11 行：import com.tencent.kuikly.core.directives.vfor 不存在，vfor 是 ViewBuilder 的内置函数，无需单独导入
    - 第 12 行：import com.tencent.kuikly.core.pager.Pager 不存在，应使用 import com.tencent.kuikly.core.base.Pager
    - 第 19 行：import com.tencent.kuikly.core.views.* 与前面的具体 import 冲突，应移除通配符导入
    - 第 24 行：hotTags 声明为 observableList<String>() 但未提供初始值，应改为 observableListOf<String>()
    - 第 37 行：pagerData 属性在 Pager 中不存在，应使用 pageWidth 和 pageHeight 或通过 context 获取
    - 第 37 行：size() 方法参数应为 (width, height)，但 pagerData.pageViewWidth 和 pagerData.pageViewHeight 不是有效属性
    - 第 50 行：Input 组件在 Kuikly 中不存在，应使用 TextInput 或类似组件
    - 第 56 行：onTextChange 事件在 Kuikly 中不存在，应使用 onValueChange
    - 第 72 行：vfor 语法错误，应使用 vfor(tags) { tag -> } 而非 vfor({ ctx.hotTags })
    - 第 73 行：Text 组件中 attr 块内 text() 方法参数应为字符串，但 tag 变量类型可能不匹配
    - 第 83 行：onClick 事件在 Kuikly 中应使用 click { } 语法
    - 第 84 行：val clickedTag = tag 变量捕获可能导致闭包问题，应直接使用 tag

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
