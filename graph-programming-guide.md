# Graph 编程读法（Kuikly Page Generator）

> 配套 `IMPLEMENTATION_SUMMARY.md` 的「学习者视角」笔记。
> 那份文档是项目总结；这份讲**怎么读这套代码、graph 编程的心智模型**，方便你边看源码边学。

## 0. 一句话心智模型

把"生成一个 Kuikly 页面"拆成一串**有角色、有顺序、还能并行和循环**的节点；所有节点共享一块**状态（State）**；LangGraph 负责按你画的连线调度。

你写的不是 `a(); b(); c()` 这种函数调用链，而是：

1. 一张**流程图**（谁连谁、谁并行、谁循环）
2. 每个框里**具体干什么**（一个 `state → 部分 state` 的函数）

## 1. 四个核心概念

| 概念 | 代码里的样子 | 一句话记住 |
|------|------------|-----------|
| **Node（节点）** | `graph.add_node("名字", 函数)` | 一个框：输入是整个 state，输出只回自己改的字段 |
| **Edge（边）** | `graph.add_edge(A, B)` | A 跑完自动跑 B |
| **State（状态）** | `GraphState(TypedDict)`（`state.py`） | 所有节点唯一共享的"黑板"，谁都能读、谁只写自己那份 |
| **路由（Router）** | `add_conditional_edges(出发节点, 路由函数, {分支: 目标})` | 根据 state 决定下一步去哪 |

## 2. 四种编排模式（对照 `graph.py` 真实行号）

| 模式 | 发生位置 | 代码 | 一句话 |
|------|---------|------|--------|
| **线性** | ①→② | `graph.py:65-66` | 一个接一个 |
| **Fan-out 并行** | ②→③a/③b/③c | `graph.py:69-71` | 一个节点连出多条边 = 同时跑 |
| **Fan-in 汇总** | ③a/③b/③c→④ | `graph.py:74-76` | 多条边汇入同一节点 = 等全部完成才触发 |
| **条件循环** | ⑤→⑥→⑤（最多 3 次） | `graph.py:82-92` + `nodes.py:308` `route_after_check` | 检查不通过就修，修完再检查 |

## 3. State 怎么流（最关键的一点）

每个节点是一个**纯函数**：`state -> 部分 state`。

1. 只读入完整的 `state`（dict）
2. 只 `return` 自己负责的字段（`nodes.py` 里每个 `node_xxx` 返回的 dict 都只有自己那几个键）
3. LangGraph 自动把返回值 **merge** 回全局 state

所以并行节点 ③a/③b/③c 各写 `layout_code` / `input_code` / `style_code` 三个**不同字段**，天然不冲突、不会互相覆盖。
（对比：如果它们都写同一个字段，就需要用 reducer 指定合并策略，见 `state.py` 里的 `take_last` / `merge_str`。）

## 4. 一次完整请求经过什么（数据视角）

```
需求文字
  → ① 需求解析：变结构化 JSON（page_name / page_type / components_needed）
  → ② 页面拆解：拆成 layout_plan / input_plan / style_plan 三份计划
  → ③a/③b/③c：各生成一段代码（布局 / 交互 / 样式）—— 并行
  → ④ 组装：三段拼成完整 .kt 文件
  → ⑤ 编译检查：规则检查（括号/@Page/继承…）+ LLM 语义检查
       ├─ 通过 → END（输出 final_code）
       └─ 不通过 → ⑥ 自动修正：拿着错误列表让 LLM 修，再回到 ⑤
```

## 5. 你本地怎么跑（环境已装好）

```bash
cd /Users/zhaozining/WorkBuddy/gragh/Graph
source venv/bin/activate

# 必须：把你的 CodeBuddy Key 写进 .env
#   CODEBUDDY_API_KEY=ck_你的key
#   获取地址：https://copilot.tencent.com/profile/keys

# 单条需求生成
python -m src.main "一个登录页，有用户名密码输入框和登录按钮"

# 批量测试（跑 tests/test_cases.json 的 10 个用例）
python -m src.main --batch tests/test_cases.json

# 结构测试（不需要 Key，验证 Graph 拓扑和规则检查）
pytest tests/ -v
```

> ⚠️ 一个坑：`README.md` 里写的是 `python -m src.graph`，那是**错的**——`graph.py` 没有 `__main__`，真正的入口是 `src.main`。照上面跑。

## 6. 版本小提醒

当前装的是 **langgraph 1.2.11**（文档写的是 0.2）。四个核心 API
（`StateGraph` / `START` / `END` / `add_conditional_edges`）用法**完全没变**，
可以放心照这份笔记和文档学，不必因为版本号不同而怀疑代码。

## 7. 想改它，从哪下手

| 想做什么 | 改哪个文件 |
|---------|-----------|
| 加/删一个节点 | `graph.py`（add_node + add_edge） |
| 改某个节点逻辑 | `nodes.py`（对应 `node_xxx`） |
| 改提示词 | `prompts.py`（对应 `PROMPT_XXX`） |
| 加 state 字段 | `state.py`（`GraphState`） |
| 换 LLM 后端 | `llm.py` 或 `.env` |
| 改最大修正次数 | `nodes.py` 的 `node_auto_fix`（`> 3`） |
