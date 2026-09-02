# Kuikly Page Generator — 实现总结文档

> **项目名称**：基于 Graph Engineering（LangGraph）的 AI 辅助 Kuikly 页面生成器
> **作者**：nikazhao（腾讯 Kuikly 客户端开发实习生）
> **技术栈**：Python 3.10+ / LangGraph 1.2+ / Pydantic 2.x / CodeBuddy API (deepseek-v3) / kotlinc 2.4
> **日期**：2026-08-24（本文档已同步至代码最新状态）

---

## 一、项目概述

### 1.1 这个项目解决什么问题

Kuikly 是腾讯自研的跨平台 UI 框架，使用 Kotlin DSL 编写页面。但 LLM 的训练数据里几乎没有 Kuikly API 知识，直接让模型生成 Kuikly 代码质量很差。

本项目的核心思路：**用 LangGraph 图编排（单 Agent + 多角色步骤流水线）+ Few-shot Prompt 注入 Kuikly 知识 + kotlinc 真实语法校验**，让 LLM 分步骤、分角色地生成可用的 Kuikly 页面代码。

> **架构定位说明**：本项目是「**单 Agent 图流水线**」（single-agent agentic workflow），即一个 LLM 实例（deepseek-v3）通过 6 个图节点分饰不同角色（解析/拆解/布局/交互/样式/组装/检查/修正）协作完成页面生成。
> 这与「多 Agent 系统」（multiple independent agents，每个有独立模型/记忆/工具，通过 Supervisor/Hand-off/Swarm 协调）不同。当前阶段选择单 Agent 图流水线是因为它足够覆盖需求且实现成本低；路线图 Phase 2.1 预留了 Supervisor 多 Agent 扩展入口。

### 1.2 核心流程

```
用户输入自然语言（如"一个登录页面"）
        ↓
  6 个图节点协作（解析→拆解→并行生成→组装→检查→修正）
        ↓
  输出可编译的 Kuikly Kotlin .kt 文件
```

### 1.3 已验证成果

- ✅ 6 节点 Graph 全链路跑通（线性 / Fan-out 并行 / Fan-in 汇总 / 条件循环）
- ✅ **15/15 单元测试通过**（含 Pydantic 强类型校验、kotlinc 噪声过滤等新增测试）
- ✅ CodeBuddy API（deepseek-v3）作为 LLM 后端稳定可用
- ✅ State 从 TypedDict 升级为 **Pydantic BaseModel**（Phase 2.2，节点交接自动校验类型）
- ✅ 编译检查从「括号匹配 + LLM 模拟」升级为 **kotlinc 真实语法校验 + classpath 噪声过滤**（Phase 3 ①）
- ✅ 领域知识从手写静态 API 表升级为 **官方 `KuiklyUI-AI` 规则动态检索**（Phase 1.1）
- ✅ 新增 `--trace` 可观测模式：实时打印每步 state 快照
- ✅ 实测：登录页生成耗时 ~48s，自动修正 1 次后编译检查通过

---

## 二、Graph 拓扑设计

### 2.1 拓扑全景图

```
  ┌─────────────────────────────────────────────────────┐
  │  ① parse_requirement（需求解析）                      │
  │      自然语言 → 结构化 JSON                            │
  │      ↓                                               │
  │  ② decompose_page（页面拆解）                          │
  │      拆为布局/交互/样式三模块计划                        │
  │      ↓                                               │
  │  ┌─────── Fan-out（并行）──────┐                     │
  │  ↓              ↓              ↓                     │
  │  ③a gen_layout  ③b gen_input  ③c gen_style          │
  │  布局代码生成    交互代码生成    样式代码生成             │
  │  └─────── Fan-in（汇总）──────┘                      │
  │      ↓                                               │
  │  ④ assemble（代码组装）                                │
  │      三段代码片段 → 完整 .kt 文件                       │
  │      ↓                                               │
  │  ⑤ compile_check（编译检查）                           │
  │      kotlinc 真语法校验 + 规则检查 + LLM 深度检查        │
  │      ↓                                               │
  │  ┌─── route（条件路由）───┐                           │
  │  ↓ PASS                ↓ FAIL                         │
  │  END（输出）         ⑥ auto_fix（自动修正）            │
  │                         ↓                             │
  │                      回到 ⑤（重新检查，最多 3 次）       │
  └─────────────────────────────────────────────────────┘
```

### 2.2 六个节点详解

| 节点 | 名称 | 职责 | LLM 调用 | 输入 | 输出 |
|------|------|------|---------|------|------|
| ① | parse_requirement | 需求解析 | ✅ JSON | `user_requirement` | `page_name`, `page_type`, `components_needed`, `parsed_intent` |
| ② | decompose_page | 页面拆解 | ✅ JSON | ①的输出 | `layout_plan`, `input_plan`, `style_plan` |
| ③a | gen_layout | 布局代码生成 | ✅ 文本 | `layout_plan` | `layout_code` |
| ③b | gen_input | 交互代码生成 | ✅ 文本 | `input_plan` | `input_code` |
| ③c | gen_style | 样式代码生成 | ✅ 文本 | `style_plan` | `style_code` |
| ④ | assemble | 代码组装 | ✅ 文本 | ③a/③b/③c 的输出 | `assembled_code`（完整 .kt）, `imports_needed` |
| ⑤ | compile_check | 编译检查 | ✅ JSON（LLM 部分） | `assembled_code` | `compile_passed`, `compile_errors`, `error_count`, `final_code` |
| ⑥ | auto_fix | 自动修正 | ✅ 文本 | 代码 + 错误列表 | 修正后的 `assembled_code`, `fix_attempts`, `fix_applied` |

### 2.3 用到的 4 种 Graph 编排模式

| 模式 | 用在哪 | 怎么实现 |
|------|--------|---------|
| **线性** | ①→② | `graph.add_edge(A, B)` |
| **Fan-out（并行）** | ②→③a/③b/③c | 一个节点向多个节点连 edge |
| **Fan-in（汇总）** | ③a/③b/③c→④ | 多个节点向一个节点连 edge |
| **条件循环** | ⑤→⑥→⑤ | `add_conditional_edges` + 路由函数 |

---

## 三、LangGraph 核心原理

### 3.1 LangGraph 的 4 个核心 API

整个项目只用了这 4 个 API：

```python
from langgraph.graph import StateGraph, END, START

# 1. 创建图，绑定状态类型（Pydantic BaseModel）
graph = StateGraph(GraphState)

# 2. 注册节点（每个节点是一个 state→state 的纯函数）
graph.add_node("parse_requirement", node_parse_requirement)
graph.add_node("decompose_page", node_decompose_page)
# ... 共 8 个节点（6 业务 + 路由函数不占节点）

# 3. 连线（A 执行完自动执行 B）
graph.add_edge(START, "parse_requirement")       # 入口
graph.add_edge("parse_requirement", "decompose_page")  # 线性

# Fan-out：一个节点连到多个节点 = 并行执行
graph.add_edge("decompose_page", "gen_layout")
graph.add_edge("decompose_page", "gen_input")
graph.add_edge("decompose_page", "gen_style")

# Fan-in：多个节点连到同一个节点 = 等全部完成后汇总
graph.add_edge("gen_layout", "assemble")
graph.add_edge("gen_input", "assemble")
graph.add_edge("gen_style", "assemble")

# 4. 条件路由（根据 state 决定下一步去哪）
graph.add_conditional_edges(
    "compile_check",         # 从哪个节点出发
    route_after_check,       # 路由函数：返回 "end" 或 "fix"
    {
        "end": END,          # "end" → 结束
        "fix": "auto_fix",   # "fix" → 去修正节点
    },
)

# 循环：修正后回到检查
graph.add_edge("auto_fix", "compile_check")

# 编译（变成可执行图）
compiled = graph.compile()
```

### 3.2 State 的流转机制（Pydantic 模式）

LangGraph 的核心是**状态在节点间流转**。本项目使用 **Pydantic BaseModel** 定义 State（Phase 2.2 升级），带来两个关键行为变化：

1. **写回时强类型校验**：节点返回 partial state 时，Pydantic 自动校验字段类型，传参出错早暴露
2. **传参时是模型实例**：LangGraph 在 Pydantic 模式下把 state 作为 `GraphState` 模型实例（非 dict）传给节点函数

每个节点的标准结构：

```python
def node_xxx(state: Dict[str, Any]) -> Dict[str, Any]:
    # Step 0: 统一转 dict（兼容 Pydantic 模型实例和普通 dict）
    state = _state_values(state)

    # Step 1: 接收完整的 state（只读）
    # Step 2: 构造 prompt 并调用 LLM
    # Step 3: 返回 partial state（只需返回自己修改的字段）
    return {
        "field_a": value_a,
        "field_b": value_b,
        # 其他字段保持不变，LangGraph 自动 merge
    }
```

其中 `_state_values()` shim 函数：

```python
def _state_values(state):
    """把节点收到的 state 统一成 dict"""
    if isinstance(state, dict):
        return state
    return state.model_dump()  # Pydantic 模型 → dict
```

### 3.3 Fan-out / Fan-in 的并行原理

当多个 edge 从同一个节点出发时，LangGraph **自动并行执行**目标节点：

```
decompose_page → gen_layout   （并行）
decompose_page → gen_input    （并行）
decompose_page → gen_style    （并行）
```

三个生成节点同时跑，互不依赖。每个节点只写自己的字段（`layout_code` / `input_code` / `style_code`），不会冲突。

当三个都完成后，Fan-in 节点 `assemble` 自动被触发，它可以从 state 里读到三个节点各自的输出。

### 3.4 条件循环的实现

```python
def route_after_check(state: dict) -> str:
    """编译检查后的路由函数"""
    if state.get("compile_passed", False):
        return "end"         # 通过 → 结束
    if state.get("fix_attempts", 0) >= 3:
        return "end"         # 超过 3 次 → 放弃，结束
    return "fix"             # 没通过 → 去修正
```

修正节点 `auto_fix` 执行完后，edge 连回 `compile_check`，形成循环。这个循环会在通过检查或超过 3 次后自动终止。

---

## 四、State 数据结构（Pydantic 强类型）

```python
from pydantic import BaseModel, ConfigDict, Field
from typing import Annotated, Optional

class GraphState(BaseModel):
    """Kuikly Page Generator 的全局状态（Pydantic 强类型）"""

    model_config = ConfigDict(extra="allow", arbitrary_types_allowed=True)

    # ── 输入 ──
    user_requirement: str = ""     # 用户自然语言描述
    page_name: str = ""            # 生成的 @Page 注解名

    # ── 节点①输出 ──
    parsed_intent: str = ""        # 解析后的结构化意图
    page_type: str = ""            # 页面类型 (login/list/detail/form/settings/profile...)
    components_needed: Annotated[list[str], take_last] = Field(default_factory=list)

    # ── 节点②输出 ──
    layout_plan: str = ""          # 布局结构描述 (JSON string)
    input_plan: str = ""           # 交互/输入元素描述
    style_plan: str = ""           # 样式/主题描述

    # ── 节点③a/b/c 输出（并行）──
    layout_code: str = ""          # ③a 布局代码片段
    input_code: str = ""           # ③b 交互代码片段
    style_code: str = ""           # ③c 样式代码片段

    # ── 节点④输出 ──
    assembled_code: str = ""       # 组装后的完整 .kt 代码
    imports_needed: list[str] = Field(default_factory=list)

    # ── 节点⑤输出 ──
    compile_passed: Optional[bool] = None   # 是否通过编译检查（None = 尚未检查）
    compile_errors: list[str] = Field(default_factory=list)
    error_count: int = 0

    # ── 节点⑥输出 ──
    fix_attempts: int = 0          # 已尝试修正次数
    fix_applied: str = ""          # 本次修正描述

    # ── 最终输出 ──
    final_code: str = ""           # 最终代码
    success: bool = False
    elapsed_seconds: float = 0.0   # 耗时
```

**设计要点**：
- 用 **Pydantic `BaseModel`** 替代 `TypedDict`（Phase 2.2 升级），获得写回时自动类型校验
- `extra="allow"` → 节点返回 schema 外的字段也不报错（兜底兼容）
- `arbitrary_types_allowed=True` → 允许任何 Python 类型作为通道值
- `components_needed` 用 `take_last` reducer，保证并行/循环覆盖语义确定
- 所有字段给默认值，节点只回传"自己负责的字段"，LangGraph 按通道增量合并

---

## 五、Prompt 工程 + 领域知识检索

### 5.1 两层知识注入策略

本项目采用 **静态 Few-shot + 动态官方规则** 双层注入：

| 层 | 来源 | 内容 | 加载方式 |
|----|------|------|---------|
| 静态 Few-shot | `examples/*.kt`（5 个手写示例） | 完整 Kuikly 页面代码 | `_load_example()` 读文件 |
| 动态规则 | `knowledge/KuiklyUI-AI/rules/*.mdc`（官方仓库） | Kuikly DSL 开发规范 | `retriever.py` → `load_kuikly_rules()` |

当官方仓库未克隆时，`retriever.py` 返回空字符串，`prompts.py` 自动降级到手写的 `KUIKLY_API_REFERENCE` 静态表（零依赖兜底）。

### 5.2 Prompt 组成结构

每个节点的 Prompt 包含三部分：

```
角色定义  →  "你是 Kuikly Kotlin 布局代码生成器"
上下文    →  页面名、计划、API 速查表（或官方规则）、Few-shot 示例代码
输出要求  →  "只输出 Kotlin 代码，不加任何解释"
```

### 5.3 Kuikly API 速查表（降级方案）

当 `retriever.py` 找不到官方规则时，使用内置静态速查表：

```
### 核心组件
| 组件 | 用途 | 示例 |
| View | 通用容器 | View { attr { size(100f, 50f) } } |
| Text | 文本 | Text { attr { text("hello"); fontSize(14f) } } |
| Center | 居中容器 | Center { Text { ... } } |

### 常用属性
- 尺寸: size(w, h), width(w), flex(1f)
- 颜色: backgroundColor(Color(0xFF...))
- 对齐: allCenter(), centerX()
```

### 5.4 Few-shot 示例

`examples/` 目录下有 5 个手写的 Kuikly 页面代码：
- `01_border_test.kt` — 布局嵌套 + 边框
- `02_event_and_module.kt` — 事件处理 + 模块化
- `03_state_display.kt` — 响应式状态
- `04_settings_page.kt` — 设置页
- `05_login_page.kt` — 登录页（完整参考）

这些在 Prompt 里通过 `_load_example()` 加载，作为 Few-shot 注入。

---

## 六、LLM 调用封装

### 6.1 后端自动切换

```python
def get_llm() -> ChatOpenAI:
    # 优先 CodeBuddy（腾讯内部）
    if os.getenv("CODEBUDDY_API_KEY"):
        return ChatOpenAI(
            model="deepseek-v3",
            api_key=codebuddy_key,
            base_url="https://copilot.tencent.com/v2",
        )
    # 回退到 SiliconFlow / OpenAI 兼容接口
    if os.getenv("OPENAI_API_KEY"):
        return ChatOpenAI(
            base_url=os.getenv("OPENAI_BASE_URL"),
            ...
        )
```

### 6.2 流式 / 非流式兼容

CodeBuddy `/v2` 端点**只支持流式请求**，而 SiliconFlow/OpenAI 支持非流式。封装了自动回退逻辑：

```python
def call_llm(llm, prompt) -> str:
    try:
        return llm.invoke(prompt).content       # 先试非流式
    except:
        # 回退到流式（CodeBuddy 走这里）
        chunks = []
        for chunk in llm.stream(prompt):
            chunks.append(chunk.content)
        return "".join(chunks)
```

### 6.3 JSON 响应解析

部分节点需要 LLM 返回 JSON（如需求解析）。封装了三层容错：
1. 直接 `json.loads(raw)`
2. 提取 ` ```json ... ``` ` 代码块
3. 正则提取第一个 `{ ... }` 块

---

## 七、编译检查机制（三层递进）

### 7.1 第一层：kotlinc 真实语法校验（Phase 3 ①）

**这是当前版本的核心升级**——不再用假检查，而是真的调用 `/opt/homebrew/bin/kotlinc` 二进制对生成的代码做语法解析：

```python
def _kotlinc_syntax_errors(code: str) -> list[str]:
    # 1. 写临时 .kt 文件
    # 2. subprocess.run([kotlinc, path], timeout=60)
    # 3. 解析 stderr 中 ": error:" 行
    # 4. 过滤 classpath 噪声（见 7.2）
    # 5. 返回真实语法错误列表
```

由于本地没有 Kuikly classpath，kotlinc 会把「未解析的引用」「lambda 形参类型推断失败」等也报成 error。这些不是真语法错误，必须过滤。

### 7.2 Classpath 噪声过滤

```python
def _is_classpath_noise(line: str) -> bool:
    _CLASSPATH_NOISE = (
        "unresolved reference",          # 引用的类不在 classpath
        "cannot infer type",             # lambda 形参推断失败
        "type mismatch",                 # 类型比对失败
        "no value passed for parameter", # 形参缺失
        "overload resolution ambiguity", # 重载歧义
        "unresolved type",               # 未解析的类型名
    )
    low = line.lower()
    return any(n in low for n in _CLASSPATH_NOISE)
```

**效果**：真语法错误（如 `syntax error: Expecting '}'`）被保留；classpath 缺失导致的误报全部过滤掉。

### 7.3 第二层：结构规则检查（始终生效）

与 classpath 无关的结构性规则，即使 kotlinc 不可用也照常检查：

```
✓ @Page 注解存在
✓ 继承 Pager() 或 BasePager()
✓ override fun body() 存在
✓ ViewBuilder 返回类型
✓ package 声明
```

当 kotlinc 不可用时，降级为括号匹配（`{`/`}` 和 `(`/`)` 数量一致）。

### 7.4 第三层：LLM 深度语义检查

通过 Prompt 让 LLM 审查代码：
- import 语句是否完整
- observable 声明格式是否正确
- 组件 API 使用是否正确
- 颜色格式是否正确

### 7.5 自动修正循环

```
检查 → 发现 5 个问题（kotlinc 语法 + 规则 + LLM）
  ↓
修正 → LLM 根据错误列表修复代码
  ↓
重新检查 → 0 问题 → ✅ 通过
```

最多循环 3 次，超过则停止并标记为失败。

---

## 八、可观测性（--trace 模式）

### 8.1 实时查看 Graph 状态流转

新增 `--trace` CLI 参数，使用 LangGraph 原生 `stream_mode="values"` 实时打印每一步的完整 state 快照：

```bash
python -m src.main --trace "一个登录页面"
```

输出示例：
```
STATE @ step 0  （共 11 个字段）
  user_requirement: '一个登录页面'
  page_name: 'LoginPage'
  success: False
  ...

STATE @ step 1  （共 14 个字段）
  parsed_intent: '...'
  page_type: 'login'
  components_needed: ['View', 'Text', ...]
  ...
```

长代码字段（`assembled_code`、`final_code`）自动截断显示前 70 字符，避免刷屏。

### 8.2 stream_mode="values" 的含义

LangGraph 的 `stream(mode="values")` 每次产出的是**当前时刻全局 state 的完整快照**（不是增量 diff）。这意味着每一步可以看到所有字段累积到什么程度——非常适合调试和理解 Graph 执行过程。

---

## 九、文件结构

```
Graph/
├── README.md                     # 项目说明
├── architecture.md               # 技术方案文档（3 天冲刺计划）
├── HANDOFF.md                    # 交接文档
├── 优化路线图.md                  # 分阶段优化规划 + 进度追踪
├── IMPLEMENTATION_SUMMARY.md     # ← 本文档
├── requirements.txt              # Python 依赖（langgraph, langchain-openai, pydantic, openai）
├── .env.example                  # 环境变量模板
├── .env                          # 实际 API Key 配置（不提交 git）
│
├── src/                          # 核心源码
│   ├── __init__.py
│   ├── main.py                   # CLI 入口（单条/批量/交互/trace 模式）
│   ├── graph.py                  # Graph 拓扑组装（add_node + add_edge）
│   ├── state.py                  # State 数据结构定义（Pydantic BaseModel）
│   ├── nodes.py                  # 8 个节点函数 + 路由 + kotlinc 校验 + 噪声过滤
│   ├── prompts.py                # 各节点 Prompt 模板 + API 速查表 + Few-shot 加载
│   ├── llm.py                    # LLM 调用封装（多后端 + 流式兼容 + JSON 解析）
│   └── retriever.py              # 官方 KuiklyUI-AI 规则检索（Phase 1.1 新增）
│
├── knowledge/                    # 外部知识源（gitignore）
│   └── KuiklyUI-AI/             # Tencent-TDS/KuiklyUI-AI 仓库（clone 自 GitHub）
│       └── rules/               # .mdc 规则文件
│           ├── kuiklyDSL.mdc
│           └── kuiklyComposeDSL.mdc
│
├── examples/                     # Kuikly 代码示例（Few-shot 用）
│   ├── 01_border_test.kt
│   ├── 02_event_and_module.kt
│   ├── 03_state_display.kt
│   ├── 04_settings_page.kt
│   └── 05_login_page.kt
│
├── tests/                        # 测试
│   ├── test_graph.py             # 单元测试（15 个）
│   ├── test_cases.json           # 标准测试用例（10 个）
│   └── test_cases_extra.json     # 扩展测试用例
│
└── output/                       # 生成的代码输出
    ├── LoginPage.kt              # 生成的 Kuikly 代码
    ├── LoginPage.meta.json       # 生成元数据
    ├── NewsListPage.kt           # 另一次生成的示例
    ├── DetailPage.kt             # trace 模式生成示例
    └── batch_report.json         # 批量测试报告
```

---

## 十、本地搭建步骤（5 分钟）

### 10.1 环境要求

- Python 3.10+
- 一个 LLM API Key（推荐 CodeBuddy）
- kotlinc 2.4+（可选，用于真实语法校验；没有则降级为括号匹配）

### 10.2 搭建步骤

```bash
# 1. 解压项目
unzip Graph_project.zip
cd Graph

# 2. 创建虚拟环境
python3 -m venv venv
source venv/bin/activate    # Windows: venv\Scripts\activate

# 3. 安装依赖
pip install -r requirements.txt
# 安装：langgraph, langchain-openai, pydantic, openai

# 4. 配置 API Key
cp .env.example .env
```

编辑 `.env` 文件，填入你的 Key（三选一）：

```bash
# 方案 1（推荐·腾讯内部）— CodeBuddy API
CODEBUDDY_API_KEY=***
MODEL=deepseek-v3

# 方案 2（外部免费）— 硅基流动 SiliconFlow
OPENAI_API_KEY=sk-your-key
OPENAI_BASE_URL=https://api.siliconflow.cn/v1
MODEL=deepseek-ai/DeepSeek-V3

# 方案 3 — DeepSeek 官方
OPENAI_API_KEY=sk-your-key
OPENAI_BASE_URL=https://api.deepseek.com/v1
MODEL=deepseek-chat
```

### 10.3 运行

```bash
# 单条需求生成
python -m src.main "一个登录页面，有用户名密码输入框和登录按钮"

# 带 trace 可观测模式（实时看每步 state）
python -m src.main --trace "一个新闻列表页"

# 批量测试（跑 tests/test_cases.json 里的所有用例）
python -m src.main --batch tests/test_cases.json

# 交互模式（反复输入需求）
python -m src.main --interactive

# 跑单元测试
pytest tests/ -v
```

### 10.4 运行输出示例

```
============================================================
🚀 Kuikly Page Generator
============================================================
📝 需求: 一个登录页面，有用户名密码输入框和登录按钮

① 需求解析
  输入: 一个登录页面，有用户名密码输入框和登录按钮

② 页面拆解
  页面: LoginPage (login)
  ├─ ③b 交互代码生成...
  ├─ ③a 布局代码生成...
  └─ ③c 样式代码生成...

④ 代码组装
  组装完成: 6422 chars, 9 imports

⑤ 编译检查
  kotlinc 语法: 0 错误
  规则检查: 0 问题
  LLM 检查: PASS
  总计: 0 问题, ✓ 通过

============================================================
✅ 成功
⏱ 耗时: 47.85s
🔄 修正次数: 0
📦 代码长度: 6391 chars
============================================================
💾 已保存: output/LoginPage.kt
```

---

## 十一、如何修改和扩展

### 11.1 核心文件职责对照表

| 想做什么 | 改哪个文件 | 怎么改 |
|---------|-----------|--------|
| 加一个新节点 | `graph.py` + `nodes.py` | 写一个 `node_xxx(state)→dict` 函数，`add_node` + `add_edge` 连线 |
| 删一个节点 | `graph.py` | 去掉对应的 `add_node` 和 `add_edge` |
| 改节点逻辑 | `nodes.py` | 找到对应函数，修改内部逻辑 |
| 改 Prompt | `prompts.py` | 找到对应 `PROMPT_XXX` 模板修改 |
| 加新的 state 字段 | `state.py` | 在 `GraphState` 里加字段（Pydantic field） |
| 换 LLM 后端 | `llm.py` 或 `.env` | 改 `.env` 里的 Key 和 base_url |
| 改编译检查规则 | `nodes.py` 的 `_rule_check()` | 加/删 if 条件 |
| 改 kotlinc 噪声过滤 | `nodes.py` 的 `_is_classpath_noise()` | 加/删噪声关键词 |
| 改最大修正次数 | `nodes.py` 的 `node_auto_fix()` | 改 `if fix_attempts > 3` |
| 改路由逻辑 | `nodes.py` 的 `route_after_check()` | 改条件判断 |
| 加官方知识源 | `retriever.py` | 在 `_RULE_FILES` 映射加新条目 |

### 11.2 加新节点示例

比如想加一个"代码美化"节点：

**Step 1**：在 `state.py` 加字段
```python
beautified_code: str  # 美化后的代码
```

**Step 2**：在 `nodes.py` 写节点函数
```python
def node_beautify(state: Dict[str, Any]) -> Dict[str, Any]:
    state = _state_values(state)
    code = state.get("assembled_code", "")
    prompt = f"美化以下 Kotlin 代码的缩进和格式:\n{code}"
    beautified = call_llm(_get_llm(), prompt)
    return {"beautified_code": beautified}
```

**Step 3**：在 `graph.py` 注册并连线
```python
graph.add_node("beautify", node_beautify)
graph.add_edge("assemble", "beautify")    # 组装 → 美化
graph.add_edge("beautify", "compile_check")  # 美化 → 检查
```

### 11.3 调试技巧

```python
# 方式 1：--trace 模式（推荐）
python -m src.main --trace "一个登录页面"

# 方式 2：手动 stream（在 main.py 的 generate_page 里）
for step, values in enumerate(graph.stream(state, {"recursion_limit": 30}, stream_mode="values")):
    print(f"Step {step}: {list(values.keys())}")

# 方式 3：查看最终状态
final_state = graph.invoke(state, {"recursion_limit": 30})
print(json.dumps(final_state, ensure_ascii=False, indent=2))
```

---

## 十二、LangGraph 学习要点

### 12.1 与普通函数链调用的区别

**普通方式**（线性、难扩展）:
```python
result = node_a(input)
result = node_b(result)
result = node_c(result)
# 想加并行？想加循环？得自己写一堆控制逻辑
```

**LangGraph**（声明式、天生支持并行和循环）:
```python
graph = StateGraph(GraphState)  # 注意：这里传的是 Pydantic 模型类
graph.add_node("a", node_a)
graph.add_node("b", node_b)
graph.add_node("c", node_c)
graph.add_edge("a", "b")
graph.add_edge("a", "c")  # 自动并行
graph.add_edge("b", "d")
graph.add_edge("c", "d")  # 自动汇总
compiled = graph.compile()
result = compiled.invoke(initial_state)
# LangGraph 负责调度、并行、状态合并
```

### 12.2 关键概念

| 概念 | 说明 |
|------|------|
| **State** | 在所有节点间共享的全局数据，本项目用 Pydantic `BaseModel` 定义（支持强类型校验） |
| **Node** | 一个纯函数 `state → partial state`，只返回修改的字段 |
| **Edge** | 节点间的连线，A 完成后自动执行 B |
| **Fan-out** | 一个节点连多个 edge = 并行执行 |
| **Fan-in** | 多个 edge 连同一节点 = 等全部完成后再执行 |
| **Conditional Edge** | 根据 state 动态选择下一个节点 |
| **Reducer** | 定义多个节点同时写同一字段时的合并策略（本项目用 `take_last`） |
| **Pydantic Mode** | LangGraph 支持 Pydantic 模型作为 State，写回时自动校验类型，但传参时是模型实例需 shim 转 dict |

---

## 十三、关键设计决策与权衡

| 决策 | 选择 | 理由 |
|------|------|------|
| Graph 框架 | LangGraph | Python 生态唯一成熟的多步骤编排框架，API 简洁 |
| State 类型 | **Pydantic BaseModel**（Phase 2.2 升级） | 写回时自动类型校验，传参出错早暴露；比 TypedDict 更安全 |
| 知识注入 | **Few-shot + 官方规则双保险**（Phase 1.1） | 手写示例保证基础可用；官方规则保证权威性和时效性 |
| LLM 后端 | CodeBuddy deepseek-v3 | 腾讯内网免费、速度快、中文理解好 |
| 节点数量 | 8 个（6 业务 + 路由函数） | 覆盖 4 种编排模式（线性/并行/汇总/循环），又不过度复杂 |
| 编译检查 | **kotlinc 真语法 + 规则 + LLM 三层**（Phase 3 ①） | kotlinc 抓真语法错误；规则抓结构缺失；LLM 抓语义问题 |
| 噪声过滤 | `_is_classpath_noise()` 6 类关键词 | 本地无 Kuikly classpath 时的必要妥协，不过滤会大量误报 |
| 修正循环 | 最多 3 次 | 防止死循环，3 次通常能修好大部分问题 |
| State 访问 | `_state_values()` shim | 兼容 Pydantic 模型实例传参和 dict 访问习惯两全 |
| 可观测性 | `--trace` + `stream_mode="values"` | 调试和理解 Graph 执行过程的最佳方式 |

---

## 十四、测试体系

### 14.1 测试清单（15 个）

| # | 测试名 | 验证内容 | 关联 Phase |
|---|--------|---------|-----------|
| 1 | `test_state_initialization` | 初始状态字段默认值 | 基础 |
| 2 | `test_state_auto_page_name` | 自动生成 Page 名 | 基础 |
| 3 | `test_state_pydantic_model` | GraphState 是 Pydantic 模型 + 强类型拦截 | **Phase 2.2** |
| 4 | `test_state_values_shim` | `_state_values()` 兼容模型和 dict | **Phase 2.2** |
| 5 | `test_graph_builds` | Graph 能成功 compile | 基础 |
| 6 | `test_rule_check_valid_code` | 正确代码 0 错误 | 基础 |
| 7 | `test_rule_check_missing_annotation` | 缺少 @Page 被检出 | 基础 |
| 8 | `test_rule_check_bracket_mismatch` | 括号不匹配被检出（兼容 kotlinc 和降级两种报文） | **Phase 3 ①** |
| 9 | `test_infer_imports` | import 推断准确 | 基础 |
| 10 | `test_route_after_check_pass` | 通过→end | 基础 |
| 11 | `test_route_after_check_fail` | 失败→fix | 基础 |
| 12 | `test_route_after_check_max_attempts` | 超限→end | 基础 |
| 13 | `test_examples_exist` | Few-shot 文件存在 | 基础 |
| 14 | `test_classpath_noise_filter` | 6 类噪声被过滤 + 真语法错误保留 | **Phase 3 ①** |
| 15 | `test_test_cases_json` | 测试用例 JSON 格式 | 基础 |

### 14.2 运行测试

```bash
pytest tests/ -v
```

---

## 十五、后续扩展方向（按路线图）

| 优先级 | 方向 | 说明 | 路线图位置 |
|--------|------|------|-----------|
| P0 | **Chroma 向量库 RAG** | 把 Kuikly 官方文档切块建索引，按查询动态检索最相关片段注入 Prompt | Phase 1.2 |
| P1 | **Supervisor 路由节点** | 在 ① 前加 supervisor，按需求复杂度决定直走还是拆子任务（此时升级为真·多 Agent） | Phase 2.1 |
| P1 | **Human-in-the-loop** | `compile_check` 后 interrupt 等 人确认才进 auto_fix | Phase 2.3 |
| P2 | **kotlinc + Kuikly classpath** | 给 kotlinc 加上 Kuikly core/annotations jar，验证 import/注解/继承真实存在 | Phase 3 ② |
| P2 | **Gradle CI 真编译** | clone KuiklyUI 源码跑 `compileKotlin`，CI 流水线调用 | Phase 3 ③ |
| P3 | **LangGraph Studio 可视化** | 接入 LangGraph Studio，Web UI 查看 Graph 拓扑和执行状态 | — |
| P3 | **Prompt 自动优化** | 基于测试反馈自动迭代 Prompt（如 DSPy） | — |
