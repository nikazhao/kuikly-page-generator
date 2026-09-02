# Kuikly Page Generator

基于 **LangGraph（单 Agent 图流水线）** 的 AI 辅助 Kuikly 页面生成器：自然语言需求 → 6 节点 Graph（解析→拆解→并行生成→组装→编译检查→自动修正）→ 可编译的 Kuikly Kotlin `.kt` 代码。

> 架构定位：本项目是 Kuikly 官方「开发 Agent」的迷你实践版。官方 2026 年 AI 工程化（Harness Engineering）已开源 `Tencent-TDS/KuiklyUI-AI`（Rules / Skills / 知识库，MCP 规划中），本项目的领域知识层即借力于此。

## 快速开始

```bash
cd Graph
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# 配置 LLM（三选一，推荐 CodeBuddy）
cp .env.example .env
# 编辑 .env 填入 CODEBUDDY_API_KEY / MODEL

# CLI 运行
python -m src.main "一个登录页面，有用户名密码输入框和登录按钮"
python -m src.main --trace "一个新闻列表页"        # 实时打印每步 state
python -m src.main --batch tests/test_cases.json   # 批量
python -m src.main --interactive                   # 交互
```

## 三种使用方式

### 1. CLI（默认）
见上方「快速开始」。

### 2. Web UI（Streamlit）
把工具包成网页，左侧输入需求，右侧展示 6 步 Graph 时间线 + 最终 `.kt` 代码（可下载）。

```bash
streamlit run webui/app.py
# 浏览器打开 http://localhost:8501
```

### 3. MCP Server（让其他 AI 工具调用）
暴露标准 MCP tool `generate_kuikly_page`，CodeBuddy / Cursor / Claude 等支持 MCP 的客户端可直接调用。

```bash
python mcp_server.py
```

客户端（stdio）配置示例：
```json
{
  "mcpServers": {
    "kuikly-page-generator": {
      "command": "python",
      "args": ["<Graph 项目绝对路径>/mcp_server.py"]
    }
  }
}
```

## 量化评估

```bash
python eval/run_eval.py                 # 跑全部用例，输出 eval/report.md
python eval/run_eval.py --limit 3       # 只跑前 3 条，快速验证
```

报告含通过率 / 平均修正次数 / 平均耗时 / 各用例明细，可作为接入官方 Kuikly MCP 前后的效果对比基线。

## 项目结构

```
Graph/
├── src/
│   ├── state.py         # Pydantic 强类型 State（GraphState BaseModel）
│   ├── nodes.py         # 6 个节点 + 路由 + kotlinc 真实语法校验 + 噪声过滤
│   ├── graph.py         # Graph 拓扑组装（add_node / add_edge）
│   ├── prompts.py       # Prompt 模板 + 官方规则注入
│   ├── llm.py           # LLM 调用封装（多后端 + 流式兼容）
│   ├── retriever.py     # 官方知识源检索（本地 .mdc / 可选 MCP）
│   ├── mcp_kuikly.py    # 官方 Kuikly MCP 知识源（条件实现，回退本地）
│   └── run_pipeline.py  # 流水线封装（Web UI / eval / MCP 共用）
├── webui/app.py         # Streamlit Web UI
├── mcp_server.py        # MCP Server（暴露 generate_kuikly_page）
├── eval/run_eval.py     # 量化评估
├── knowledge/KuiklyUI-AI/  # 克隆的官方规则（Phase 1.1）
├── examples/            # Kuikly 手写示例（Few-shot）
├── tests/               # 单元测试 + 测试用例
├── output/              # 生成的 .kt
├── 优化路线图.md          # 分阶段优化规划
├── 技术方案_借助开源项目升级.md  # 借力开源项目升级方案
└── IMPLEMENTATION_SUMMARY.md    # 实现总结（与代码同步）
```

## 知识源说明（Phase A）

| 来源 | 启用方式 | 说明 |
|------|---------|------|
| 本地克隆规则 | 默认 | 读 `knowledge/KuiklyUI-AI/rules/*.mdc` |
| 官方 Kuikly MCP | 设 `KUIKLY_MCP_URL` 环境变量 | 运行时实时获取官方文档；失败自动回退本地 |

上层（prompts.py / nodes.py）只依赖 `retriever.load_kuikly_rules()`，知识源切换对节点代码透明。
