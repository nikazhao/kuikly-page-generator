# HANDOFF：Kuikly Graph Engineering 项目交接文档

> **致**：WorkBuddy / 接手同学
> **来自**：nikazhao
> **日期**：2026-08-23
> **项目路径**：`/root/Desktop/Graph/`
> **状态**：编码 100% 完成，单元测试 12/12 通过，等待 LLM API Key 后跑全链路验证

---

## 一、项目是什么

**一句话**：自然语言描述页面需求 → LangGraph 多 Agent 拓扑协作 → 生成 Kuikly Kotlin DSL 代码 → 输出 `.kt` 文件。

**项目定位**：AI 工程能力展示 / 学习实践项目（非生产系统）。卖点 = **Graph Engineering 本身**（Fan-out/Fan-in + 条件循环的多 Agent 编排模式）。

**时间周期**：3 天冲刺方案（见 `architecture.md`）。

---

## 二、代码完成度

| 模块 | 文件 | 行数 | 状态 |
|------|------|------|------|
| State 定义 | `src/state.py` | 114 | ✅ 完成 |
| LLM 封装（多后端） | `src/llm.py` | 110 | ✅ 完成 |
| 6 节点实现 | `src/nodes.py` | 302 | ✅ 完成 |
| Prompt 模板 | `src/prompts.py` | 381 | ✅ 完成 |
| Graph 拓扑组装 | `src/graph.py` | 106 | ✅ 完成 |
| CLI 入口 | `src/main.py` | 190 | ✅ 完成 |
| 单元测试 | `tests/test_graph.py` | 126 | ✅ **12/12 全通过** |
| 测试用例 | `tests/test_cases.json` | 43 | ✅ 10 用例 |
| Few-shot 示例 | `examples/*.kt` × 5 | — | ✅ 完成 |
| 架构文档 | `architecture.md` | 251 | ✅ 完成 |

**总计**：7 个 Python 模块 + 5 个 Kotlin 示例 + 12 个测试，约 1,331 行代码。

---

## 三、Graph 拓扑（6 节点）

```
① 需求解析 (parse_requirement)
    │  自然语言 → 结构化 JSON {page_type, components, page_name}
    ▼
② 页面拆解 (decompose_page)
    │  → layout_plan / input_plan / style_plan
    ▼
  ┌─────────────┬─────────────┐         ← Fan-out（并行）
  │             │             │
③a 布局生成   ③b 交互生成   ③c 样式生成
(gen_layout)  (gen_input)   (gen_style)
  │             │             │
  └─────────────┴─────────────┘         ← Fan-in（汇合）
    │
    ▼
④ 代码组装 (assemble)
    │  三段拼接 → 完整 .kt + 自动推断 import
    ▼
⑤ 编译检查 (compile_check)  ◄────────┐
    │  规则检查 + LLM 双重验证          │
    ├─→ PASS → END                     │ 条件循环
    └─→ FAIL → ⑥ 自动修正 ─────────────┘  (最多 3 轮)
                  (auto_fix)
```

**4 种编排模式全覆盖**：线性流转、Fan-out 并行、Fan-in 汇合、条件循环重试。

---

## 四、技术栈

| 组件 | 选型 | 说明 |
|------|------|------|
| **Graph 编排** | LangGraph ≥ 0.2.0 | Python，StateGraph + add_conditional_edges |
| **LLM 调用** | langchain-openai ≥ 0.1.0 | ChatOpenAI，OpenAI 兼容协议 |
| **LLM 后端** | ⚠️ 待定 | 见下方「API Key 问题」 |
| **Kuikly 知识注入** | Few-shot Prompt | 不用 RAG，直接在 Prompt 里塞完整 .kt 示例 |
| **编译检查** | 规则检查 + LLM 双重 | 先快规则（括号/注解/继承），再 LLM 深检 |
| **运行界面** | 终端 CLI | `python3 -m src.main "需求描述"` |

---

## 五、Kuikly DSL 关键模式

项目生成的代码遵循 Kuikly Kotlin DSL 模式（基于 `github.com/Tencent-TDS/KuiklyUI` 的 321 个 demo 页面提炼）：

```kotlin
package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.views.*

@Page("LoginPage")
internal class LoginPage : Pager() {
    override fun body(): ViewBuilder {
        return {
            // DSL 视图树
        }
    }
}
```

**核心组件**：`View`、`Text`、`Button`、`Image`、`Column`、`Row`、`Center`、`Scroller`
**状态管理**：`Module` + `observable()` delegate
**事件绑定**：`attr { onClick { ... } }`

---

## 六、当前唯一阻塞点：API Key

项目代码完全就绪，**只差一个 LLM API Key 就能跑全链路**。

### 已尝试的方案及结果

| # | 方案 | 结果 | 原因 |
|---|------|------|------|
| 1 | TokenHub (`tokenhub.tencentmaas.com`) | ❌ 权限不足 | 实习生无 DevCloud 权限 |
| 2 | IOPS 内部网关 | ❌ 不可达 | 需内网环境 |
| 3 | CodeBuddy CLI（本机） | ⚠️ 未登录 | CLI v2.137.1 已安装但无登录会话 |
| 4 | Venus AI (`ai.woa.com`) | 🔜 **推荐尝试** | 有「体验应用组」，员工免费试用 |

### 推荐下一步（优先级排序）

1. **Venus AI 平台** (`ai.woa.com`) — 腾讯内部，有免费体验额度，OpenAI 兼容协议，登录即用
2. **光子 LLM 平台** — 免费，OpenAI 兼容，KM 有接入教程
3. **硅基流动 SiliconFlow** — 外部方案，手机号注册送 14 元额度（纯外部，非腾讯）

### 获取 Key 后的配置步骤

在 `/root/Desktop/Graph/` 下创建 `.env` 文件：

```bash
# 以 Venus AI 为例（确认 Base URL 后替换）
OPENAI_API_KEY=<你的key>
OPENAI_BASE_URL=https://ai.woa.com/v1    # 确认实际地址
MODEL=deepseek-v3                          # 或 hunyuan-pro，取决于平台支持的模型名
```

然后运行全链路：

```bash
cd /root/Desktop/Graph
python3 -m src.main "一个包含用户名密码输入框和登录按钮的登录页面"
```

---

## 七、如何运行

### 环境

```bash
# Python 环境
python3 = /hermes-app/venv/bin/python3

# 已安装依赖
# langgraph, langchain-openai, openai, pytest — 均已安装验证通过

# 如需重装
uv pip install -r requirements.txt --python /hermes-app/venv/bin/python3
```

### 单元测试（不依赖 LLM）

```bash
cd /root/Desktop/Graph
python3 -m pytest tests/test_graph.py -v
# 预期：12 passed
```

测试覆盖：
- State 初始化 + 自动 Page 名生成
- Graph 编译成功
- 规则检查：正确代码通过 / 缺 @Page 报错 / 括号不匹配报错
- import 推断逻辑
- 条件路由：通过→END / 失败→修正 / 达上限→END
- Few-shot 示例文件存在性
- 测试用例 JSON 格式

### 全链路运行（需要 API Key）

```bash
cd /root/Desktop/Graph

# 单条需求
python3 -m src.main "一个包含用户名密码输入框和登录按钮的登录页面"

# 批量测试（10 个标准用例）
python3 -m src.main --batch tests/test_cases.json

# 结果输出到 output/ 目录
```

---

## 八、文件清单

```
Graph/
├── HANDOFF.md                      ← 本文档
├── README.md                       项目说明（需更新运行指令）
├── architecture.md                 3 天冲刺技术方案（251 行）
├── requirements.txt                langgraph / langchain-openai / openai
├── .env.example                    配置模板（4 种后端方案）
│
├── examples/                       Kuikly 手写 Few-shot 示例
│   ├── 01_border_test.kt           基础边框 + 文本
│   ├── 02_event_and_module.kt      事件绑定 + Module 状态
│   ├── 03_state_display.kt         状态驱动 UI
│   ├── 04_settings_page.kt         设置页（开关 + 列表）
│   └── 05_login_page.kt            登录页（完整示例）
│
├── src/
│   ├── __init__.py
│   ├── state.py                    GraphState (TypedDict) + reducer + initial_state()
│   ├── llm.py                      get_llm() — 多后端，ChatOpenAI 封装
│   ├── prompts.py                  7 个 Prompt 模板 + Kuikly API 参考 + Few-shot 加载
│   ├── nodes.py                    6 节点实现 + _rule_check + _infer_imports + 路由
│   ├── graph.py                    build_graph() — StateGraph 拓扑组装
│   └── main.py                     CLI 入口 + 批量运行 + 输出保存
│
├── tests/
│   ├── __init__.py
│   ├── test_graph.py               12 个单元测试
│   ├── test_cases.json             10 个标准测试用例
│   └── test_cases_extra.json       2 个额外高难度用例
│
└── output/                         生成结果输出目录（当前为空）
```

---

## 九、已知限制与改进方向

| 问题 | 严重度 | 说明 |
|------|--------|------|
| 编译检查是模拟的 | 中 | 没有接入真实 Kotlin 编译器，用规则+LLM 双重检查替代 |
| import 推断靠关键词匹配 | 低 | 简单但够用，可扩展为 AST 解析 |
| Prompt 可能需要调优 | 中 | 模型对 Kuikly DSL 的理解取决于 Few-shot 质量，可能需要加更多示例 |
| 没有前端界面 | 低 | 设计上用终端 + LangGraph Studio 展示，非必要不写前端 |
| 并行节点可能不真正并行 | 低 | LangGraph 的 Fan-out 在某些模式下可能是顺序执行，需验证 |

---

## 十、后续路线（Day 2-3）

| 阶段 | 任务 | 预计 |
|------|------|------|
| **Day 2** | 配置 API Key → 全链路跑通 → 调试 6 节点协作 → LangGraph Studio 可视化 | 1 天 |
| **Day 3** | 10 用例批量测试 → 优化 Prompt → 记录通过率 → 录制 Demo | 1 天 |

---

## 十一、关键设计决策记录

1. **为什么不用 RAG？** 3 天时间不够建知识库（收集文档→分块→向量化→调参），Few-shot Prompt 足够验证假设。
2. **为什么 6 节点不是 8 节点？** 砍掉了人工审核节点和独立修正节点（合并到编译检查+自动修正循环里），保留核心拓扑模式。
3. **为什么编译检查分两层？** 规则检查（括号匹配、注解、继承）秒完成且零成本；LLM 检查更深入但有延迟和费用。先快后慢，省 Token。
4. **为什么 llm.py 支持多后端？** API Key 获取受阻于权限问题，保留 CodeBuddy / SiliconFlow / DeepSeek / OpenAI 四种后端灵活切换。
5. **Few-shot 示例从哪来？** 从 `github.com/Tencent-TDS/KuiklyUI` 的 321 个 demo 中精选 5 个代表性页面。

---

*如有疑问，请联系 nikazhao。*
