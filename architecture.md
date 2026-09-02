# Kuikly Graph Engineering 探索项目 — 3 天冲刺方案

> **项目名称**：Kuikly Page Generator — 基于 Graph Engineering 的 AI 辅助 Kuikly 页面生成
> **作者**：nikazhao（腾讯 Kuikly 客户端开发实习生）
> **日期**：2026-08-23
> **周期**：3 天
> **版本**：v1.0

---

## 一、3 天能做什么（说实话）

3 天不可能做完完整产品。但 3 天**足够验证核心假设 + 跑通完整 Graph 拓扑**，产出一个**能演示的 Demo**。

### 三天目标

| 天 | 目标 | 产出 |
|----|------|------|
| **Day 1** | 跑通 LangGraph + 验证模型能否生成 Kuikly 代码 | 一个 Python 脚本：输入文本 → 输出 Kotlin 代码 |
| **Day 2** | 实现完整 Graph 拓扑（全部节点） | 多 Agent 协作的完整 Graph，终端可运行 |
| ** Day 3** | 打磨 Prompt + 批量测试 + 录制 Demo | 测试报告 + 可演示的完整流程 |

---

## 二、Graph 拓扑设计（精简版）

### 2.1 核心拓扑

```
              ┌─────────────────────┐
              │  ① 需求解析 Agent    │
              │ 自然语言 → 结构化JSON │
              └─────────┬───────────┘
                        │
         ┌──────────────┼──────────────┐
         │ Fan-out       │              │
  ┌──────▼──────┐ ┌─────▼───────┐ ┌────▼────────┐
  │ ②a 布局生成  │ │ ②b 组件生成  │ │ ②c 样式生成  │
  │   Agent     │ │   Agent     │ │   Agent     │
  └──────┬──────┘ └─────┬───────┘ └────┬────────┘
         │               │              │
         └───────────────┼──────────────┘
                         │ Fan-in
              ┌──────────▼───────────┐
              │  ③ 代码组装 Agent     │
              │  三段代码 → 完整.kt   │
              └──────────┬───────────┘
                         │
              ┌──────────▼───────────┐
              │  ④ 编译检查 + 自修正  │
              │  最多重试 3 次        │
              └──────────┬───────────┘
                         │
              ┌──────────▼───────────┐
              │  ⑤ 输出最终代码       │
              └──────────────────────┘
```

### 2.2 节点定义

| 节点 | 职责 | LLM? | 说明 |
|------|------|------|------|
| ① 需求解析 | 自然语言 → 结构化 JSON | ✅ | 输出 `{page_type, components, layout, interactions}` |
| ②a 布局生成 | 生成布局容器代码 | ✅ | Column / Row / Stack / ScrollView |
| ②b 组件生成 | 生成内容组件代码 | ✅ | Text / Button / TextField / Image / ListView |
| ②c 样式生成 | 生成样式修饰代码 | ✅ | padding / color / fontSize / cornerRadius |
| ③ 代码组装 | 三段拼接为完整 .kt | ✅ | 加 @Page 注解、import、类定义 |
| ④ 编译检查+修正 | 编译 → 失败则自动修正 → 重试 | ✅ | 最多 3 次循环 |

### 2.3 为什么这个拓扑适合 3 天冲刺

1. **只用了 6 个节点**——不是 8 个，去掉了人工审核和独立修正节点（合并到④里）
2. **用了全部 4 种核心编排模式**——线性、Fan-out、Fan-in、循环，叙事完整
4. **每个节点都是一次 LLM 调用**——没有复杂的工程依赖
5. **不需要前端**——Day 1-2 全在终端跑，Day 3 录屏时加个简单展示

---

## 三、技术栈（极简）

| 组件 | 选型 | 理由 |
|------|------|------|
| **Graph 编排** | LangGraph (Python) | 3 天内唯一可行的成熟方案 |
| **LLM** | CodeBuddy 网关 | 免费，模型多，内网快 |
| **Kuikly 知识注入** | Few-shot Prompt（不用 RAG） | 3 天没时间建 ChromaDB |
| **运行界面** | 终端 + LangGraph Studio | 不写前端 |
| **编译检查** | 先手动看，Day 3 尝试自动 | 不强求自动化 |

### 3.1 为什么不用 RAG

3 天时间建不好 RAG 知识库（收集文档、分块、向量化、调检索参数）。**用 Few-shot Prompt 替代**——直接在 Prompt 里塞 2-3 个完整的 Kuikly 页面代码示例，效果够用。

### 3.2 LLM 调用配置

```python
from openai import OpenAI

client = OpenAI(
    base_url="https://copilot.tencent.com/v2",
    api_key="<你的内部token>"
)
```

---

## 四、State 数据结构

```python
from typing import TypedDict, Optional
from langgraph.graph import StateGraph, END

class GraphState(TypedDict):
    # 输入
    user_requirement: str

    # 节点①输出
    parsed_requirement: str    # 结构化 JSON 字符串

    # 节点②输出（并行）
    layout_code: str           # ②a
    component_code: str        # ②b
    style_code: str            # ②c

    # 节点③输出
    full_code: str             # 完整 .kt 文件

    # 节点④输出
    compile_result: Optional[str]  # 编译输出或错误信息
    retry_count: int               # 当前重试次数
    max_retries: int               # 最大重试 = 3

    # 最终输出
    final_code: Optional[str]
    status: str                    # success / failed / running
```

---

## 五、三天详细计划

### Day 1：跑通骨架 + 验证核心假设

> **核心命题：模型能不能生成像样的 Kuikly Kotlin 代码？**

| 时段 | 任务 | 具体 |
|------|------|------|
| **上午** | 环境搭建 | ① 创建 Python venv，装 LangGraph + OpenAI SDK<br>② 配置 CodeBuddy 网关连接，跑通第一次 LLM 调用<br>③ 准备 2-3 个手写的 Kuikly 页面代码作为 Few-shot 示例 |
| **下午** | 最简 Graph 骨架 | ④ 写 State 定义<br>⑤ 实现 2 个节点：需求解析 + 代码生成（合二为一）<br>⑥ 用 LangGraph 组装成最简 Graph（A→B→END）<br>⑦ 终端跑通：输入"我要一个登录页" → 输出 Kotlin 代码 |
| **晚上** | 质量验证 | ⑧ 拿 5 个不同的页面描述测试生成质量<br>⑨ 看生成的代码能不能编译<br>⑩ 记录：哪些通过了？哪些不行？问题在哪？ |

**Day 1 成功标准：至少 1 个生成的 Kuikly 代码片段能编译通过（或人工判断"接近可用"）。**

**如果 Day 1 失败**：说明 Prompt 工程需要加大投入——更多 few-shot 示例、更精细的指令。先解决质量再继续。

---

### Day 2：实现完整 Graph 拓扑

> **核心命题：多 Agent 分工协作的完整流程能否跑通？**

| 时段 | 任务 | 具体 |
|------|------|------|
| **上午** | 拆解节点 | ① 把 Day 1 的单节点拆成 6 个节点<br>② 为每个节点编写专门的 Prompt 模板<br>③ 实现 Fan-out（②a/②b/②c 并行） |
| **下午** | 组装 Graph | ④ 实现 Fan-in（③ 汇总）<br>⑤ 实现条件路由 + 循环（④编译检查→修正→重试）<br>⑥ 用 StateGraph 组装完整拓扑<br>⑦ 端到端跑通 |
| **晚上** | 调试 + 优化 | ⑧ 跑 3-5 个测试用例<br>⑨ 调整 Prompt，解决常见错误<br>⑩ 用 LangGraph Studio 可视化 Graph 执行过程 |

**Day 2 成功标准：完整 6 节点 Graph 端到端跑通，能在 LangGraph Studio 里看到节点状态变化。**

---

### Day 3：打磨 + 测试 + 演示

> **核心命题：生成质量怎么样？能不能讲清楚？**

| 时段 | 任务 | 具体 |
|------|------|------|
| **上午** | 批量测试 | ① 用 10 种标准页面类型测试（见下表）<br>② 记录编译通过率、代码质量<br>③ 挑选出最好和最差的案例 |
| **下午** | 打磨 + 包装 | ④ 针对最差案例优化 Prompt<br>⑤ 撰写 README.md<br>⑥ 编写一键运行脚本 |
| **晚上** | 录制 Demo | ⑦ 录制完整演示：从输入需求到生成代码<br>⑧ 截图 LangGraph Studio 的拓扑可视化<br>⑨ 整理最终交付物 |

**Day 3 成功标准：10 种页面测试有结果，至少 3 个生成质量可接受，有可演示的完整录屏。**

---

### 标准测试用例（10 种页面）

| # | 页面描述 | 复杂度 | 核心组件 |
|---|---------|--------|---------|
| 1 | 登录页：手机号+验证码+登录按钮 | 低 | TextField, Button, Column |
| 2 | 注册页：用户名+密码+确认+注册 | 低 | TextField, Button, Column |
| 3 | 商品列表：图片+标题+价格列表 | 中 | ListView, Image, Text |
| 4 | 商品详情：大图+描述+购买按钮 | 中 | ScrollView, Image, Text, Button |
| 5 | 设置页：开关项+跳转项列表 | 中 | ListView, Switch, Text |
| 6 | 搜索页：搜索框+搜索结果列表 | 中 | TextField, ListView, Text |
| 7 | 个人中心：头像+信息+功能菜单 | 中 | Image, Text, ListView |
| 8 | 对话列表：头像+昵称+最新消息 | 中 | ListView, Image, Text |
| 9 | 标签页：底部Tab+多个子页面 | 高 | TabLayout, 多页面 |
| 10 | 表单页：多个输入项+提交 | 高 | TextField, Dropdown, Button |

---

## 六、文件结构

```
Desktop/Graph/
├── README.md                    # 项目说明
├── architecture.md              # 技术方案（本文档）
├── examples/                    # 手写的 Kuikly 标准示例
│   ├── login_page.kt            # 登录页示例
│   ├── list_page.kt             # 列表页示例
│   └── detail_page.kt           # 详情页示例
├── src/
│   ├── state.py                 # State 定义
│   ├── nodes.py                 # 6 个节点的实现
│   ├── graph.py                 # Graph 拓扑组装
│   ├── prompts.py               # 各节点 Prompt 模板
│   ├── llm.py                   # LLM 调用封装
│   └── config.py                # 配置
├── tests/
│   └── test_cases.py            # 10 种页面测试用例
├── output/                      # 生成的代码输出
└── requirements.txt             # Python 依赖
```

---

## 七、风险与应对

| 风险 | 应对 |
|------|------|
| **Day 1 模型生成质量太差** | 加大 few-shot 示例数量和质量；尝试不同模型（CodeBuddy 有 32 个） |
| **Kuikly DSL 太特殊模型不认识** | 在 Prompt 里加完整的 Kuikly DSL 语法说明 + API 参考 |
| **LangGraph 学习曲线** | LangGraph Quickstart 2 小时能跑通；核心 API 就 4 个：StateGraph、add_node、add_edge、add_conditional_edges |
| **编译环境不通** | 先手动检查代码质量，不强求自动编译 |
| **3 天时间不够** | 砍到最简：只保留①②b③④四个节点，去掉并行和样式节点 |

---

## 八、交付物清单

| # | 交付物 | 形式 | Day |
|---|--------|------|-----|
| 1 | **Graph 引擎源码** | Python 代码 | Day 2 |
| 2 | **Kuikly 示例代码** | 3 个手写 .kt 文件 | Day 1 |
| 3 | **Prompt 模板集** | 6 个节点的 Prompt | Day 2 |
|4 | **测试报告** | 10 种页面的生成结果 | Day 3 |
| 5 | **技术方案文档** | 本文档 | 已完成 |
| 6 | **演示 Demo** | 录屏 + LangGraph Studio 截图 | Day 3 |
| 7 | **README.md** | 项目说明 | Day 3 |
```
