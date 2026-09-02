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
- components_needed 只能包含 Kuikly 支持的组件：View, Text, Image, Center, Scroller, List, CheckBox, Switch, Input
- 根据需求推断合理的组件组合
"""


# ─── 节点②：页面拆解 Prompt ─────────────────────────────────
PROMPT_DECOMPOSE_PAGE = """你是一个 Kuikly 页面架构师。基于以下信息，将页面拆解为三个独立的代码模块。

页面名称：{page_name}
页面类型：{page_type}
需要的组件：{components_needed}

### Kuikly API 参考
{api_reference}

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

页面名称：{page_name}
交互计划：{input_plan}

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

要求：
1. 输出状态变量声明（private var xxx by observable(...)）
2. 输出事件处理代码片段（event { click { } }）
3. 事件中用 `ctx.` 访问状态变量
4. 只输出代码片段，不要 class 定义和 import

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

### 组装规则
1. 以 `package com.tencent.kuikly.demo.pages` 开头
2. 导入所有用到的类（参考示例）
3. `@Page("{page_name}")` 注解
4. `internal class {page_name} : Pager()` 声明
5. 状态变量放在 class 顶部
6. `override fun body(): ViewBuilder` 内组装布局树
7. 交互逻辑嵌入布局的事件块中
8. 样式嵌入各组件的 attr 块中
9. 代码必须结构完整、缩进正确

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
