# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 19:04:56
- 用例来源：`tests/test_cases.json`
- 用例总数：**10**
- 通过率：**60%** (6/10)
- 平均自动修正次数：2.2
- 平均耗时：100.86s
- 平均规则符合度：**97%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：`success` 由 kotlinc 语法校验（已过滤 classpath 噪声）＋结构规则＋LLM 自审三层构成，**并未在真实 Kuikly classpath 下编译运行**；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ✅ | 1 | 0 | 44.37 | 4337 | 7/7 |
| 2 | 一个设置页面，有头像、昵称显示，下面是开关列表用于通知、夜间 | settings | ✅ | 3 | 0 | 222.96 | 4405 | 7/7 |
| 3 | 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购 | detail | ❌ | 3 | 27 | 106.74 | 5853 | 7/7 |
| 4 | 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 | profile | ❌ | 3 | 7 | 101.05 | 5132 | 6/7 |
| 5 | 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 | list | ✅ | 3 | 0 | 87.71 | 5131 | 6/7 |
| 6 | 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有 | form | ❌ | 3 | 60 | 106.39 | 6350 | 7/7 |
| 7 | 一个天气展示页面，顶部城市名和温度，中间天气图标，底部有风力 | detail | ✅ | 1 | 0 | 45.19 | 4051 | 7/7 |
| 8 | 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 | other | ❌ | 3 | 116 | 179.66 | 11493 | 7/7 |
| 9 | 一个数据看板页面，顶部展示三个数字指标卡片，下面是一个进度条 | dashboard | ✅ | 2 | 0 | 88.73 | 8503 | 7/7 |
| 10 | 一个空状态页面，中间大图标，下面提示文字和重试按钮 | other | ✅ | 0 | 0 | 25.85 | 2660 | 7/7 |

## 未通过用例

- #3 一个商品详情页，顶部大图，下面是商品标题、价格、描述和加入购物车按钮 — 修正 3 次后仍剩 27 个问题
    - 第 1 行：package 声明 'com.tencent.kuikly.demo.pages' 不符合规范，Kuikly 页面应位于业务模块的 commonMain 下，包名应反映模块路径
    - 第 7 行：import 'com.tencent.kuikly.core.module.CallbackFn' 不存在，Kuikly 中网络请求回调应使用内置的 Callback 类型或异步方式
    - 第 8 行：import 'com.tencent.kuikly.core.module.NetworkModule' 不存在，Kuikly 没有内置 NetworkModule，网络请求需通过自定义 Module 或第三方库实现
    - 第 9 行：import 'com.tencent.kuikly.core.module.SharedPreferencesModule' 不存在，Kuikly 没有内置 SharedPreferencesModule，数据持久化需通过自定义 Module 实现
    - 第 13 行：import 'com.tencent.kuikly.core.views.compose.Button' 不存在，Button 组件应直接从 'com.tencent.kuikly.core.views' 导入
    - 第 14 行：import 'com.tencent.kuikly.core.views.*' 与前面的具体 import 冲突，应移除通配符导入或保留具体导入
    - 第 17 行：class AudioHapticsModule 继承 Module() 但未实现必要方法，Module 需要实现 moduleName() 方法，但缺少其他可能需要的生命周期方法
    - 第 20 行：companion object 中 MODULE_NAME 常量命名不符合 Kotlin 命名规范，应使用大写加下划线格式，但此处为常量定义，实际无错误
    - 第 24 行：callNativeMethod 方法调用参数不正确，第三个参数应为 Callback 类型，但传入了 null，可能导致空指针异常
    - 第 32 行：callNativeMethod 方法调用参数不正确，第三个参数应为 Callback 类型，但传入了 null，可能导致空指针异常
    - 第 40 行：@Page 注解参数 'ProductDetailPage' 应使用小写开头的页面名称，如 'productDetailPage'，但这不是强制错误
    - 第 42 行：productData 使用 observable(JSONObject()) 初始化，但 JSONObject 不是基本类型，observable 应使用基本类型或可序列化类型
    - 第 47 行：createExternalModules() 方法返回类型为 Map<String, Module>?，但 AudioHapticsModule 的实例化方式可能导致模块注册失败
    - 第 52 行：acquireModule(SharedPreferencesModule.MODULE_NAME) 引用了不存在的模块，SharedPreferencesModule 不是 Kuikly 内置模块
    - 第 53 行：acquireModule(AudioHapticsModule.MODULE_NAME) 引用了自定义模块，但模块注册方式可能不正确
    - 第 54 行：acquireModule(NetworkModule.MODULE_NAME) 引用了不存在的模块，NetworkModule 不是 Kuikly 内置模块
    - 第 57 行：networkModule.requestGet 方法不存在，NetworkModule 未定义 requestGet 方法
    - 第 59 行：CallbackFn 类型不存在，应使用 Kuikly 内置的回调类型
    - 第 74 行：ctx.pagerData.pageWidth 属性访问方式不正确，pagerData 应通过 getPager().pagerData 访问
    - 第 75 行：ctx.pagerData.pageWidth * 300f / 375f 计算方式可能导致布局问题，应使用 pagerData 提供的比例方法
    - 第 78 行：ImageUri.commonAssets 方法不存在，应使用 ImageUri.commonAssets() 或 ImageUri.pageAssets()
    - 第 107 行：Button 组件导入路径错误，应使用 import com.tencent.kuikly.core.views.Button
    - 第 111 行：textAlignCenter() 和 justifyContentCenter() 在 Button 的 attr 块中同时使用可能导致布局冲突
    - 第 117 行：ctx.spModule.getInt 方法不存在，SharedPreferencesModule 未定义 getInt 方法
    - 第 118 行：ctx.spModule.setInt 方法不存在，SharedPreferencesModule 未定义 setInt 方法
    - 第 119 行：ctx.audioModule.playSound 方法调用可能导致空指针异常，因为 audioModule 可能未正确初始化
    - 第 120 行：ctx.audioModule.vibrate 方法调用可能导致空指针异常，因为 audioModule 可能未正确初始化
- #4 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底部是功能菜单列表 — 修正 3 次后仍剩 7 个问题
    - 未继承 Pager() 或 BasePager()
    - 第 12 行：import com.tencent.kuikly.core.views.* 与前面的具体 import 冲突，应移除通配符导入
    - 第 12 行：import com.tencent.kuikly.core.views.* 重复导入 com.tencent.kuikly.core.views 包，应移除
    - 第 49 行：event { click { val params = ... } } 事件回调缺少参数，click 回调应接收 clickEvent 参数
    - 第 62 行：event { click { val params = ... } } 事件回调缺少参数
    - 第 73 行：event { click { val params = ... } } 事件回调缺少参数
    - 第 91 行：event { click { val params = ... } } 事件回调缺少参数
- #6 一个注册表单页面，有手机号、验证码、密码输入框，验证码旁边有获取按钮，底部注册按 — 修正 3 次后仍剩 60 个问题
    - Button 组件不支持 text 属性，应使用 title 属性
    - Button 组件不支持 click 事件，应使用 onClick 事件
    - Input 组件不支持 textChange 事件，应使用 onTextChange 事件
    - Input 组件不支持 hint 属性，应使用 placeholder 属性
    - Text 组件不支持 fontSize 属性，应使用 fontSize14f() 等预定义方法
    - Color 构造参数格式错误：Color(0xFFE53935L) 应使用 Color(0xFFE53935) 或 Color(0xFFE53935L) 但需注意 L 后缀
    - Button 组件不支持 width 属性，应使用 flex 或 size 方法
    - Button 组件不支持 marginTop 属性，应使用 margin 方法
    - Button 组件不支持 marginLeft 属性，应使用 margin 方法
    - Button 组件不支持 marginRight 属性，应使用 margin 方法
    - Input 组件不支持 marginTop 属性，应使用 margin 方法
    - Input 组件不支持 marginLeft 属性，应使用 margin 方法
    - Input 组件不支持 marginRight 属性，应使用 margin 方法
    - Input 组件不支持 height 属性，应使用 size 方法
    - Text 组件不支持 marginTop 属性，应使用 margin 方法
    - Text 组件不支持 marginLeft 属性，应使用 margin 方法
    - View 组件不支持 marginTop 属性，应使用 margin 方法
    - View 组件不支持 marginLeft 属性，应使用 margin 方法
    - View 组件不支持 marginRight 属性，应使用 margin 方法
    - View 组件不支持 alignItemsCenter 属性，应使用 alignItems(Alignment.CENTER) 方法
    - View 组件不支持 flexDirectionColumn 属性，应使用 flexDirection(FlexDirection.COLUMN) 方法
    - View 组件不支持 flexDirectionRow 属性，应使用 flexDirection(FlexDirection.ROW) 方法
    - Input 组件不支持 flex 属性，应使用 size 或 flex 方法
    - Button 组件不支持 flex 属性，应使用 size 或 flex 方法
    - Button 组件不支持 title 属性，应使用 text 属性（但需确认 Button 是否支持 text 属性）
    - Button 组件不支持 onClick 事件，应使用 click 事件（但需确认 Button 是否支持 click 事件）
    - Input 组件不支持 onTextChange 事件，应使用 textChange 事件（但需确认 Input 是否支持 textChange 事件）
    - Input 组件不支持 placeholder 属性，应使用 hint 属性（但需确认 Input 是否支持 hint 属性）
    - Text 组件不支持 fontSize14f 方法，应使用 fontSize(14f) 属性
    - Text 组件不支持 color 属性，应使用 textColor 属性
    - Button 组件不支持 text 属性，应使用 title 属性
    - Button 组件不支持 click 事件，应使用 onClick 事件
    - Input 组件不支持 textChange 事件，应使用 onTextChange 事件
    - Input 组件不支持 hint 属性，应使用 placeholder 属性
    - Text 组件不支持 fontSize 属性，应使用 fontSize14f() 等预定义方法
    - Color 构造参数格式错误：Color(0xFFE53935L) 应使用 Color(0xFFE53935) 或 Color(0xFFE53935L) 但需注意 L 后缀
    - Button 组件不支持 width 属性，应使用 flex 或 size 方法
    - Button 组件不支持 marginTop 属性，应使用 margin 方法
    - Button 组件不支持 marginLeft 属性，应使用 margin 方法
    - Button 组件不支持 marginRight 属性，应使用 margin 方法
    - Input 组件不支持 marginTop 属性，应使用 margin 方法
    - Input 组件不支持 marginLeft 属性，应使用 margin 方法
    - Input 组件不支持 marginRight 属性，应使用 margin 方法
    - Input 组件不支持 height 属性，应使用 size 方法
    - Text 组件不支持 marginTop 属性，应使用 margin 方法
    - Text 组件不支持 marginLeft 属性，应使用 margin 方法
    - View 组件不支持 marginTop 属性，应使用 margin 方法
    - View 组件不支持 marginLeft 属性，应使用 margin 方法
    - View 组件不支持 marginRight 属性，应使用 margin 方法
    - View 组件不支持 alignItemsCenter 属性，应使用 alignItems(Alignment.CENTER) 方法
    - View 组件不支持 flexDirectionColumn 属性，应使用 flexDirection(FlexDirection.COLUMN) 方法
    - View 组件不支持 flexDirectionRow 属性，应使用 flexDirection(FlexDirection.ROW) 方法
    - Input 组件不支持 flex 属性，应使用 size 或 flex 方法
    - Button 组件不支持 flex 属性，应使用 size 或 flex 方法
    - Button 组件不支持 title 属性，应使用 text 属性（但需确认 Button 是否支持 text 属性）
    - Button 组件不支持 onClick 事件，应使用 click 事件（但需确认 Button 是否支持 click 事件）
    - Input 组件不支持 onTextChange 事件，应使用 textChange 事件（但需确认 Input 是否支持 textChange 事件）
    - Input 组件不支持 placeholder 属性，应使用 hint 属性（但需确认 Input 是否支持 hint 属性）
    - Text 组件不支持 fontSize14f 方法，应使用 fontSize(14f) 属性
    - Text 组件不支持 color 属性，应使用 textColor 属性
- #8 一个搜索页面，顶部搜索输入框，下面是热门搜索标签的宫格布局 — 修正 3 次后仍剩 116 个问题
    - 第 1 行：package 声明 'com.tencent.kuikly.demo' 与 Kuikly 框架标准包名不符，应为 'com.tencent.kuikly.demo' 但需确认实际模块路径
    - 第 13 行：import 'com.tencent.kuikly.core.views.*' 与前面具体 import 冲突，应移除通配符导入或移除具体导入
    - 第 17 行：observable 声明 'private var searchText by observable<String>("")' 格式正确，但 observable 泛型参数应使用 'observable<String>("")' 而非 'observable<String>("")'，实际写法正确
    - 第 18 行：observable 声明 'private var hotTags by observable<List<String>>(...)' 格式正确，但 observable 不支持 List 类型作为响应式字段，应使用 observableList<String>()
    - 第 20 行：observable 声明 'private var selectedTag by observable<String>("")' 格式正确
    - 第 27 行：attr 块内 'flex(1.0f)' 应使用 'flex(1f)' 而非 'flex(1.0f)'，Kuikly 中 flex 参数为 Float 类型，但推荐使用整数形式
    - 第 28 行：Color(0xFFF5F5F5L) 格式错误，Color 构造参数应为 Long 类型，但 0xFFF5F5F5L 中 L 后缀多余，应为 Color(0xFFF5F5F5)
    - 第 32 行：marginTop(10.0f) 应使用 marginTop(10f) 而非 marginTop(10.0f)
    - 第 33 行：marginLeft(16.0f) 应使用 marginLeft(16f) 而非 marginLeft(16.0f)
    - 第 34 行：marginRight(16.0f) 应使用 marginRight(16f) 而非 marginRight(16.0f)
    - 第 35 行：height(40.0f) 应使用 height(40f) 而非 height(40.0f)
    - 第 36 行：borderRadius(20.0f) 应使用 borderRadius(20f) 而非 borderRadius(20.0f)
    - 第 37 行：Color(0xFFFFFFFFL) 格式错误，应为 Color(0xFFFFFFFF)
    - 第 39 行：fontSize(14.0f) 应使用 fontSize(14f) 而非 fontSize(14.0f)
    - 第 44 行：onTextChange 事件回调参数类型应为 String?，但 lambda 参数 'text' 类型推断可能正确，需确认
    - 第 47 行：onSubmit 事件回调参数类型应为 String?，但 lambda 参数 'text' 类型推断可能正确，需确认
    - 第 56 行：flex(1.0f) 应使用 flex(1f)
    - 第 57 行：marginTop(16.0f) 应使用 marginTop(16f)
    - 第 58 行：marginLeft(16.0f) 应使用 marginLeft(16f)
    - 第 59 行：marginRight(16.0f) 应使用 marginRight(16f)
    - 第 63 行：flexWrap(Wrap) 中 Wrap 未导入，需导入 com.tencent.kuikly.core.base.Wrap
    - 第 64 行：flexDirection(Row) 中 Row 未导入，需导入 com.tencent.kuikly.core.base.Row
    - 第 68 行：fontWeightBold() 方法不存在，应使用 fontWeightBold() 或 fontWeight(700) 但 Kuikly 中 fontWeight 使用预定义方法，如 fontWeight700()
    - 第 70 行：size(ctx.pagerData.pageViewWidth - 32.0f, 0.0f) 中 pageViewWidth 属性不存在，应为 pagerData.pageWidth 或类似属性
    - 第 70 行：size() 方法参数应为 Float 类型，但 32.0f 和 0.0f 应改为 32f 和 0f
    - 第 76 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 77 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 78 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 79 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 80 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 81 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 83 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 96 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 97 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 98 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 99 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 100 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 101 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 103 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 116 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 117 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 118 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 119 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 120 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 121 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 123 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 136 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 137 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 138 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 139 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 140 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 141 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 143 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 156 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 157 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 158 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 159 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 160 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 161 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 163 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 176 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 177 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 178 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 179 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 180 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 181 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 183 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 196 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 197 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 198 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 199 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 200 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 201 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 203 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 216 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 217 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 218 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 219 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 220 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 221 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 223 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 236 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 237 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 238 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 239 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 240 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 241 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 243 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 256 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 257 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 258 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 259 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 260 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 261 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 263 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 276 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 277 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 278 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 279 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 280 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 281 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 283 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 296 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 297 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 298 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 299 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 300 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 301 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 303 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认
    - 第 316 行：Color(0xFFFFFFFFL) 应为 Color(0xFFFFFFFF)
    - 第 317 行：borderRadius(16.0f) 应使用 borderRadius(16f)
    - 第 318 行：marginLeft(12.0f) 应使用 marginLeft(12f)
    - 第 319 行：marginRight(12.0f) 应使用 marginRight(12f)
    - 第 320 行：marginTop(6.0f) 应使用 marginTop(6f)
    - 第 321 行：marginBottom(6.0f) 应使用 marginBottom(6f)
    - 第 323 行：click 事件回调参数类型应为 ClickEvent?，但 lambda 参数未声明类型，需确认

## 硬规则未通过明细

- #4 一个用户个人中心页面，头像在顶部居中，下面显示昵称、签名，底 — 继承 Pager() / BasePager()
- #5 一个聊天列表页面，每行有头像、昵称、最后一条消息和时间 — body() 返回 lambda 树

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
