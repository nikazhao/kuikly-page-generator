"""
Few-shot Prompts
================
为每个节点定义精心调制的 Prompt，内含 Kuikly 官方代码示例作为 Few-shot。
这是补偿 LLM 训练数据中缺失 Kuikly API 知识的关键手段。
"""

from __future__ import annotations

import os

from .retriever import load_kuikly_rules


# ─── 加载 Few-shot 示例 ──────────────────────────────────────
_EXAMPLES_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "examples")


def _load_example(filename: str) -> str:
    """加载 examples/ 目录下的 Kuikly 代码示例"""
    path = os.path.join(_EXAMPLES_DIR, filename)
    try:
        with open(path, "r", encoding="utf-8") as f:
            return f.read().strip()
    except FileNotFoundError:
        return f"[示例 {filename} 未找到]"


# ─── Kuikly API 速查 ─────────────────────────────────────────
KUIKLY_API_REFERENCE = """
## Kuikly API 速查表

### 基础结构
```kotlin
@Page("页面名")
internal class XxxPage : Pager() {
    override fun body(): ViewBuilder {
        val ctx = this
        return {
            // 在此构建 UI 树
        }
    }
}
```

### 核心组件
| 组件 | 用途 | 示例 |
|------|------|------|
| View | 通用容器 | `View { attr { size(100f, 50f); backgroundColor(Color.RED) } }` |
| Text | 文本 | `Text { attr { text("hello"); fontSize(14f); color(Color.BLACK) } }` |
| Image | 图片 | `Image { attr { src("url"); size(40f, 40f) } }` |
| Center | 居中容器 | `Center { Text { attr { text("居中") } } }` |
| Scroller | 滚动容器 | `Scroller { attr { flex(1f) } /* 子View */ }` |
| List | 列表 | `List { attr { flex(1f) } /* items */ }` |

### 常用属性 (attr 块内)
- 尺寸: `size(w, h)`, `width(w)`, `height(h)`, `flex(1f)`
- 间距: `marginTop(v)`, `marginLeft(v)`, `paddingTop(v)`, `paddingLeft(v)`
- 颜色: `backgroundColor(Color(0xFF...))`, `color(Color(0xFF...))`
- 字体: `fontSize(14f)`, `fontWeightBold()`, `fontWeightSemiBold()`
- 圆角: `borderRadius(8f)`
- 对齐: `allCenter()`, `centerX()`, `centerY()`
- 边框: `border(Border(1f, color=Color.RED, lineStyle=BorderStyle.SOLID))`

### 响应式状态
```kotlin
private var title by observable("")
// 在事件中修改
event { click { title = "已点击" } }
```

### 事件
```kotlin
event {
    click { params ->
        // 点击处理
    }
}
```

### ⚠️ 常见幻觉 API 黑名单（严禁使用，真编译会失败）
- `events { }` → 正确是**单数** `event { }`
- `onClick { }` → 正确是 `click { }`（Kuikly 事件块内的事件名是 `click`，不是 Android 的 `onClick`）
- `textColor(...)` → 正确是 `color(...)`（设置文字颜色用 `color`，`textColor` 是 Android 概念）
- `pageWidth` / `pageHeight` → 正确是 `pagerData.pageViewWidth` / `pagerData.pageViewHeight`
- `ContentMode` / `FontWeight` / `JustifyContent` / `AlignItems` → 这些是 Android/Compose 概念，Kuikly 用 `flexDirectionRow()` / `justifyContentCenter()` / `alignItemsCenter()` / `fontWeightBold()` 等
- `ImageUri` 的 import 是 `com.tencent.kuikly.core.base.attr.ImageUri`（**base.attr 包**，不是 `views.ImageUri`）

### 导入
```kotlin
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.*      // View, Text, Image, Scroller...
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.layout.Center  // 如果用 Center
```
"""


# ─── Kuikly 系统能力 / Module 参考（路 B① 补充） ──────────────
# 背景：Kuikly 是跨端 UI 框架，本身不具备平台能力（音频、震动、存储等），
# 系统能力统一走 Module 机制。生成"电子木鱼"这类含音效/震动/持久化需求的页面时，
# 必须按本表写代码，禁止臆造不存在的 API（如直接猜 AudioManager / Vibrator 等）。
KUIKLY_MODULE_REFERENCE = """
## Kuikly 系统能力速查（Module 机制）

> Kuikly 是跨端 UI 框架，**本身不具备平台能力**（音频、震动、存储、网络、路由等），
> 系统能力统一通过 **Module 机制** 获取，分两类：
> - **内置模块**：直接 `acquireModule<T>(模块名)` 获取，无需自己实现；
> - **自定义模块**：音频/震动等 Kuikly 没有内置，需继承 `Module()` 自定义，
>   通过 `asyncToNativeMethod` 把方法名和参数发给 Native 端实现。

### 获取 Module 的两种方式
```kotlin
// ① 找不到会抛异常（用于确定存在的内置模块）
val sp = acquireModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME)
// ② 找不到返回 null（用于可能不存在的自定义模块，需判空）
val sp2 = getModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME)
// 必须在 created() 或之后调用（此时 Module 已初始化），不能在属性初始化时调用
```

### 内置模块：SharedPreferencesModule（磁盘键值持久化）
```kotlin
import com.tencent.kuikly.core.module.SharedPreferencesModule

// 写入（返回 Unit）
sp.setString("username", "kuikly")
sp.setInt("count", 42)
sp.setFloat("score", 98.5f)
sp.setObject("config", JSONObject().put("theme", "dark"))

// 读取（Int/Float/Object 返回可空类型，需 ?: 兜底）
val username: String = sp.getString("username")     // 非空，缺省返回 ""
val count: Int? = sp.getInt("count")                // 可空，建议 ?: 0
val score: Float? = sp.getFloat("score")
val config: JSONObject? = sp.getObject("config")
```
> 典型用法：`created()` 里读上次保存的值做初始化，`event { click {} }` 里写回。

### 自定义 Module：音频 / 震动（Kuikly 无内置，必须自定义）
Kuikly 侧定义接口（继承 `Module()`，实现 `moduleName()`，用 `asyncToNativeMethod`
把方法名 + JSONObject 参数发给 Native 端；真正播放/震动由各平台 Native 端实现）。

```kotlin
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

class AudioHapticsModule : Module() {
    override fun moduleName(): String = "KRAudioHapticsModule"

    companion object { const val MODULE_NAME = "KRAudioHapticsModule" }

    // 播放音效（异步，无返回值；Native 端用 MediaPlayer 等实现）
    fun playSound(soundName: String) {
        asyncToNativeMethod(
            "playSound",
            JSONObject().apply { put("soundName", soundName) },
            null
        )
    }

    // 触发震动（异步；Native 端用 Vibrator 等实现）
    fun vibrate(durationMs: Int) {
        asyncToNativeMethod(
            "vibrate",
            JSONObject().apply { put("durationMs", durationMs) },
            null
        )
    }
}
```

自定义 Module 必须在 Pager 的 `createExternalModules()` 中注册（名字必须与 `moduleName()` 一致）：
```kotlin
@Page("MuyuPage")
internal class MuyuPage : Pager() {
    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }
    // ...
}
```

### Module 通信方法（自定义模块内用）
| 方法 | 调用方式 | 参数 | 返回值 |
|------|---------|------|--------|
| `asyncToNativeMethod(methodName, JSONObject?, CallbackFn?)` | 异步 | JSONObject（序列化为 JSON 字符串） | Unit |
| `syncToNativeMethod(methodName, JSONObject?, CallbackFn?)` | 同步 | JSONObject | Any? |
| `asyncToNativeMethod(methodName, Array<Any>, AnyCallbackFn?)` | 异步 | 基本类型数组（传 ByteArray 用这个） | Unit |
| `toNative(keepCallbackAlive, methodName, param, callback, syncCall)` | 通用底层 | Any? | Any? |

> 音效/震动是"只发指令不关心返回值"的场景，用异步 `asyncToNativeMethod(..., null)`；
> 同步 `syncToNativeMethod` 在 Kuikly 线程执行，勿放耗时操作。

> ⚠️ **禁止臆造 `callNativeMethod`** —— 这不是 Kuikly 的 API（真编译会 unresolved reference）。
> 自定义 Module 与 Native 通信只有三种：`asyncToNativeMethod` / `syncToNativeMethod` / `toNative`。

### 其他内置模块（速查）
| 模块 | import | 用途 |
|------|--------|------|
| RouterModule | `com.tencent.kuikly.core.module.RouterModule` | 页面跳转 openPage/closePage |
| NotifyModule | `com.tencent.kuikly.core.module.NotifyModule` | 事件通知 addNotify/postNotify |
| MemoryCacheModule | `com.tencent.kuikly.core.module.MemoryCacheModule` | 内存缓存 setObject |
| NetworkModule | `com.tencent.kuikly.core.module.NetworkModule` | HTTP 请求 requestGet/requestPost |
"""


# ─── 组件白名单（路 B② 扩充） ────────────────────────────────
# 来源：官方 kuiklyDSL.mdc「组件使用」段 + ui-framework SKILL.md。
# 目的：给 parse_requirement 一个**真实存在**的组件池，并标注精确 import 路径，
# 避免 LLM 猜出不存在的组件或写错 import（木鱼实测暴露的正是这两类问题）。
# 注意：Button 在 compose 包、Center 在 layout 包，其余多在 views 包，均已标出。
KUIKLY_COMPONENT_WHITELIST = [
    # (组件名, 中文用途, 精确 import 路径)
    ("View", "容器", "com.tencent.kuikly.core.views.View"),
    ("Text", "文本", "com.tencent.kuikly.core.views.Text"),
    ("Image", "图片", "com.tencent.kuikly.core.views.Image"),
    ("Button", "按钮", "com.tencent.kuikly.core.views.compose.Button"),
    ("Center", "居中容器", "com.tencent.kuikly.core.views.layout.Center"),
    ("Scroller", "滚动容器", "com.tencent.kuikly.core.views.Scroller"),
    ("List", "列表", "com.tencent.kuikly.core.views.List"),
    ("Modal", "模态弹窗", "com.tencent.kuikly.core.views.Modal"),
    ("ActivityIndicator", "加载指示器", "com.tencent.kuikly.core.views.ActivityIndicator"),
    ("Input", "单行输入框", "com.tencent.kuikly.core.views.Input"),
    ("TextArea", "多行输入框", "com.tencent.kuikly.core.views.TextArea"),
    ("RichText", "富文本", "com.tencent.kuikly.core.views.RichText"),
    ("Switch", "开关", "com.tencent.kuikly.core.views.Switch"),
    ("CheckBox", "复选框", "com.tencent.kuikly.core.views.CheckBox"),
    ("Slider", "滑动器", "com.tencent.kuikly.core.views.Slider"),
    ("Tabs", "标签栏", "com.tencent.kuikly.core.views.Tabs"),
    ("AlertDialog", "提示对话框", "com.tencent.kuikly.core.views.AlertDialog"),
    ("ActionSheet", "底部操作表", "com.tencent.kuikly.core.views.ActionSheet"),
    ("DatePicker", "日期选择器", "com.tencent.kuikly.core.views.DatePicker"),
    ("ScrollPicker", "滚动选择器", "com.tencent.kuikly.core.views.ScrollPicker"),
    ("Refresh", "下拉刷新", "com.tencent.kuikly.core.views.Refresh"),
    ("FooterRefresh", "列表尾部刷新", "com.tencent.kuikly.core.views.FooterRefresh"),
    ("PageList", "分页列表", "com.tencent.kuikly.core.views.PageList"),
    ("SliderPage", "轮播图", "com.tencent.kuikly.core.views.SliderPage"),
    ("WaterFallList", "瀑布流", "com.tencent.kuikly.core.views.WaterFallList"),
    ("Video", "视频播放器", "com.tencent.kuikly.core.views.Video"),
    ("Canvas", "自绘画布", "com.tencent.kuikly.core.views.Canvas"),
    ("Mask", "遮罩", "com.tencent.kuikly.core.views.Mask"),
]


def _component_whitelist_text() -> str:
    """把组件白名单渲染成 prompt 里可读的文本（含 import）。"""
    lines = []
    for name, usage, imp in KUIKLY_COMPONENT_WHITELIST:
        lines.append(f"- {name}（{usage}）→ `{imp}`")
    return "\n".join(lines)


def get_kuikly_api_reference(dsl_type: str = "dsl") -> str:
    """动态获取 Kuikly 官方知识，替代手写静态 API 表。

    优先读取已克隆的 Tencent-TDS/KuiklyUI-AI 官方规则；
    若仓库未克隆，降级回退到本文件手写的 KUIKLY_API_REFERENCE。
    """
    official = load_kuikly_rules(dsl_type)
    return official if official else KUIKLY_API_REFERENCE


# ─── 节点①：需求解析 Prompt ─────────────────────────────────
PROMPT_PARSE_REQUIREMENT = """你是一个 Kuikly 页面需求分析专家。

用户需求：{user_requirement}

请分析并输出以下信息（JSON 格式）：

```json
{{
  "page_name": "PascalCase 页面名称，以 Page 结尾",
  "page_type": "login|list|detail|form|settings|profile|dashboard|other",
  "components_needed": ["Text", "View", "Image", "Button", ...],
  "description": "一句话总结页面功能"
}}
```

注意：
- page_name 必须以 Page 结尾，使用 PascalCase
- components_needed 只能从下面这份**官方组件白名单**里选（不要臆造不存在的组件）：

{component_whitelist}

- 根据需求推断合理的组件组合
- 如果需求涉及「音效 / 声音 / 震动 / 播放」，说明需要自定义 Module（Audio/Haptics），
  在 description 里注明"含音效/震动，需自定义 Module"
- 如果需求涉及「保存 / 记住 / 持久化 / 计数累计」，说明需要 SharedPreferencesModule 持久化，
  在 description 里注明"含持久化，需 SharedPreferencesModule"
"""


# ─── 节点②：页面拆解 Prompt ─────────────────────────────────
PROMPT_DECOMPOSE_PAGE = """你是一个 Kuikly 页面架构师。基于以下信息，将页面拆解为三个独立的代码模块。

用户原始需求：{user_requirement}
需求解析结论：{parsed_intent}
页面名称：{page_name}
页面类型：{page_type}
需要的组件：{components_needed}

### Kuikly API 参考
{api_reference}

### Kuikly 系统能力参考（音效/震动/持久化走 Module）
{module_reference}

### 示例代码（边框测试页 — 展示布局嵌套）
```kotlin
{example_border}
```

### 示例代码（事件处理页 — 展示交互逻辑）
```kotlin
{example_event}
```

请输出三个模块的代码描述（各用一个 JSON 对象）：

**布局模块**：页面的视图树结构（Container → 子View 嵌套关系）
```json
{{
  "layout_plan": "描述视图树层次结构，从最外层 View 到内层组件",
  "layout_key_components": ["View", "Center", "Text", "Image", ...]
}}
```

**交互模块**：页面的状态变量和事件处理
```json
{{
  "input_plan": "列出需要的状态变量(observable)和事件处理(click等)",
  "input_state_vars": [{{"name": "title", "type": "String", "default": "\"\""}}]
}}
```

> 关键：交互模块的 `input_plan` 必须**忠实传达**用户需求里涉及的系统能力信号，逐字写清：
> - 若需求提到「保存/记住/持久化/累计/历史记录」→ input_plan 必须写明「需 SharedPreferencesModule 持久化（getInt/setInt 读回计数）」；
> - 若需求提到「音效/声音/播放」→ 写明「需自定义 AudioHapticsModule 播放音效」；
> - 若需求提到「震动/触感」→ 写明「需自定义 AudioHapticsModule 触发震动」。
> 这些信号是下游交互代码生成节点判断「要不要走 Module」的唯一依据，漏写会导致能力被随机丢弃。

**样式模块**：页面的颜色、字体、间距等样式规范
```json
{{
  "style_plan": "配色方案、字体大小、间距规范",
  "style_colors": ["0xFFF5F5F5", "0xFF07C160", ...]
}}
```

将三个 JSON 合并输出：
```json
{{
  "layout_plan": "...",
  "input_plan": "...",
  "style_plan": "..."
}}
```
"""


# ─── 节点③a：布局代码生成 Prompt ────────────────────────────
PROMPT_GEN_LAYOUT = """你是 Kuikly Kotlin 布局代码生成器。只生成 `body()` 方法内的视图树代码，不要生成 class 和 import。

页面名称：{page_name}
布局计划：{layout_plan}

### Kuikly API 参考
{api_reference}

### 布局示例
```kotlin
{example_border}
```

要求：
1. 从 `return {` 开始，到对应的 `}` 结束
2. 最外层通常是 `View { attr { size(pagerData.pageViewWidth, pagerData.pageViewHeight) ... } }`
3. 合理使用 Center 进行居中
4. 所有 View 都要有 attr 块设置尺寸
5. 使用 `val ctx = this` 模式捕获上下文

只输出 Kotlin 代码，不加任何解释：
"""


# ─── 节点③b：交互代码生成 Prompt ────────────────────────────
PROMPT_GEN_INPUT = """你是 Kuikly Kotlin 交互逻辑代码生成器。只生成状态变量声明和事件处理代码。

用户原始需求：{user_requirement}
需求解析结论：{parsed_intent}
页面名称：{page_name}
交互计划：{input_plan}

### Kuikly 系统能力参考（音效/震动/持久化走 Module）
{module_reference}

### 响应式状态示例
```kotlin
private var username by observable("")
private var errorMsg by observable("")

// 在 event 块中处理
event {
    click { params ->
        if (ctx.username.isEmpty()) {
            ctx.errorMsg = "请输入用户名"
        }
    }
}
```

### 持久化 + 音效/震动示例（木鱼点击计数）
```kotlin
private var meritCount by observable(0)
private lateinit var audioModule: AudioHapticsModule
private lateinit var spModule: SharedPreferencesModule

override fun created() {
    super.created()
    audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
    spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
    meritCount = spModule.getInt("meritCount") ?: 0
}

// 点击事件里：改状态 + 写回持久化 + 触发音效/震动
event {
    click {
        ctx.meritCount += 1
        ctx.spModule.setInt("meritCount", ctx.meritCount)
        ctx.audioModule.playSound("muyu_knock")
        ctx.audioModule.vibrate(30)
    }
}
```

要求：
1. 输出状态变量声明（private var xxx by observable(...)）
2. 输出事件处理代码片段（event { click { } }）
3. 事件中用 `ctx.` 访问状态变量
4. 若交互计划提到音效/震动/持久化，必须按上面的 Module 写法生成（created 里 acquireModule、事件里调用），不要臆造 AudioManager/Vibrator 等平台 API
5. 只输出代码片段，不要 class 定义和 import

只输出 Kotlin 代码片段，不加任何解释：
"""


# ─── 节点③c：样式代码生成 Prompt ────────────────────────────
PROMPT_GEN_STYLE = """你是 Kuikly Kotlin 样式代码生成器。基于样式计划，生成 attr 块内的样式属性。

页面名称：{page_name}
样式计划：{style_plan}

### 样式属性参考
- 尺寸: size(w, h), width(w), height(h), flex(1f)
- 间距: marginTop(v), marginLeft(v), paddingTop(v), paddingLeft(v)
- 颜色: backgroundColor(Color(0xFF...)), color(Color(0xFF...))
- 字体: fontSize(14f), fontWeightBold(), fontWeightSemiBold()
- 圆角: borderRadius(8f)
- 对齐: allCenter(), centerX(), centerY()

要求：
1. 输出样式常量定义和内联 attr 样式片段
2. 使用 Color(0xFF...) 格式定义颜色
3. 合理的间距（通常 8f/12f/16f/24f/40f）
4. 只输出代码片段

只输出 Kotlin 代码片段，不加任何解释：
"""


# ─── 节点④：代码组装 Prompt ─────────────────────────────────
PROMPT_ASSEMBLE = """你是 Kuikly Kotlin 代码组装器。将三个模块的代码片段组装为一个完整的、可编译的 Kuikly Page。

页面名称：{page_name}（@Page 注解名：{page_name}）
页面类型：{page_type}

### 布局代码
```kotlin
{layout_code}
```

### 交互代码
```kotlin
{input_code}
```

### 样式代码
```kotlin
{style_code}
```

### 完整页面示例（登录页）
```kotlin
{example_login}
```

### 完整页面示例（音频+震动+持久化，木鱼）
```kotlin
{example_audio}
```

### Kuikly 系统能力参考（音效/震动/持久化走 Module）
{module_reference}

### 组装规则
1. 以 `package com.tencent.kuikly.demo.pages` 开头
2. 导入所有用到的类（参考示例；组件 import 必须用白名单里的精确路径，Button 在 compose 包）
3. `@Page("{page_name}")` 注解
4. `internal class {page_name} : Pager()` 声明
5. 状态变量放在 class 顶部
6. `override fun body(): ViewBuilder` 内组装布局树
7. 交互逻辑嵌入布局的事件块中
8. 样式嵌入各组件的 attr 块中
9. 若用到音效/震动/持久化：必须写 `createExternalModules()` 注册自定义 Module、`created()` 里 acquireModule、以及 Module 类定义（参考木鱼示例），不要臆造平台 API
10. 代码必须结构完整、缩进正确

输出完整的 .kt 文件代码，不加任何解释：
"""


# ─── 节点⑤：编译检查 Prompt ─────────────────────────────────
PROMPT_COMPILE_CHECK = """你是 Kuikly Kotlin 编译检查器。审查以下代码，检查是否能在 Kuikly 框架中正确编译运行。

```kotlin
{code}
```

### Kuikly 编译规则
1. 必须有 @Page 注解
2. 必须继承 Pager()（或 BasePager）
3. 必须重写 body(): ViewBuilder
4. body() 返回的 lambda 内只能有组件调用（View/Text/Image/Center...）
5. 组件必须有 attr { } 块
6. 使用 observable 的变量必须以 `private var xxx by observable(default)` 声明
7. 事件块为 `event { click { params -> } }`
8. 颜色必须用 Color(0xFF...) 格式
9. package 声明和 import 语句必须正确
10. 括号必须匹配

### 检查项
- [ ] @Page 注解存在
- [ ] class 声明正确
- [ ] import 语句完整
- [ ] body() 方法正确重写
- [ ] 括号完全匹配
- [ ] observable 声明格式正确
- [ ] 组件 API 使用正确
- [ ] 颜色格式正确

输出 JSON：
```json
{{
  "passed": true/false,
  "errors": ["错误描述1", "错误描述2"],
  "warnings": ["警告描述"],
  "error_count": 0
}}
```

如果通过，errors 为空数组。只输出 JSON：
"""


# ─── 节点⑥：自动修正 Prompt ─────────────────────────────────
PROMPT_AUTO_FIX = """你是 Kuikly Kotlin 代码修正器。以下代码有编译问题，请修正。

### 原始代码
```kotlin
{code}
```

### 检测到的问题
{errors}

### 已尝试修正次数：{fix_attempts}（最多 3 次）

### Kuikly API 参考
{api_reference}

### 修正规则
1. 只修正检测到的问题，不要大幅改动结构
2. 保持页面功能不变
3. 确保括号匹配
4. 确保所有 import 都存在
5. 确保 observable 声明格式正确
6. 确保 Color() 格式正确

输出修正后的完整 .kt 文件代码，不加任何解释：
"""


# ─── Prompt 组装函数 ─────────────────────────────────────────
def get_prompt(node_name: str) -> str:
    """根据节点名获取对应 Prompt 模板"""
    prompts = {
        "parse_requirement": PROMPT_PARSE_REQUIREMENT,
        "decompose_page": PROMPT_DECOMPOSE_PAGE,
        "gen_layout": PROMPT_GEN_LAYOUT,
        "gen_input": PROMPT_GEN_INPUT,
        "gen_style": PROMPT_GEN_STYLE,
        "assemble": PROMPT_ASSEMBLE,
        "compile_check": PROMPT_COMPILE_CHECK,
        "auto_fix": PROMPT_AUTO_FIX,
    }
    return prompts[node_name]


def fill_prompt(template: str, **kwargs) -> str:
    """填充 Prompt 模板的变量（安全替换，不使用 str.format 避免花括号冲突）"""
    for key, val in kwargs.items():
        template = template.replace("{" + key + "}", str(val))
    return template
