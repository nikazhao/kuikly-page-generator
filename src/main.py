"""
Kuikly Page Generator — CLI 入口
=================================
自然语言描述 → Graph 多 Agent 协作 → 生成 Kuikly Kotlin 代码

用法：
  python -m src.main "一个登录页面，有用户名密码输入框和登录按钮"
  python -m src.main --batch tests/test_cases.json
  python -m src.main --interactive
"""

from __future__ import annotations

import sys
import os
import time
import json

# 确保项目根目录在 path 中
_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

from src.state import initial_state
from src.graph import get_graph


def _print_trace_state(values: dict, step: int):
    """把每一步的 state 打成紧凑可读的形式（长代码字段截断，避免刷屏）"""
    print(f"\n{'─' * 50}")
    print(f"STATE @ step {step}  （共 {len(values)} 个字段）")
    print(f"{'─' * 50}")
    skip = {"assembled_code", "final_code"}  # 代码体太长，单独处理
    for k, v in values.items():
        if k in skip:
            if isinstance(v, str) and v:
                print(f"  {k}: <代码 {len(v)} chars> 开头 {v[:70]!r}…")
            else:
                print(f"  {k}: {v!r}")
        else:
            print(f"  {k}: {v!r}")


def generate_page(user_requirement: str, page_name: str = "", trace: bool = False) -> dict:
    """
    端到端生成 Kuikly 页面

    Args:
        user_requirement: 自然语言描述
        page_name: 可选，指定页面名
        trace: 为 True 时用 LangGraph stream 实时打印每一步 state（看"过程/状态"用）

    Returns:
        最终状态 dict，含 final_code, success 等
    """
    print("\n" + "=" * 60)
    print("Kuikly Page Generator")
    print("=" * 60)
    print(f"需求: {user_requirement}")

    start_time = time.time()

    # 初始化状态
    state = initial_state(user_requirement, page_name)

    # 执行 Graph
    graph = get_graph()

    if trace:
        # LangGraph 原生 stream：stream_mode="values" 每步产出"完整 state 快照"，
        # 也就是节点之间流动的那块共享黑板在每一步结束时长什么样。
        final_state = None
        for step, values in enumerate(
            graph.stream(state, {"recursion_limit": 30}, stream_mode="values")
        ):
            _print_trace_state(values, step)
            final_state = values
    else:
        final_state = graph.invoke(state, {"recursion_limit": 30})

    elapsed = time.time() - start_time
    final_state["elapsed_seconds"] = round(elapsed, 2)

    # 确定成功状态
    if final_state.get("compile_passed"):
        final_state["success"] = True
    elif final_state.get("fix_attempts", 0) >= 3:
        final_state["success"] = False
    else:
        final_state["success"] = final_state.get("compile_passed", False)

    # 输出结果
    print("\n" + "=" * 60)
    print(f"{'成功' if final_state['success'] else '部分完成'}")
    print(f"耗时: {final_state['elapsed_seconds']}s")
    print(f"修正次数: {final_state.get('fix_attempts', 0)}")
    print(f"代码长度: {len(final_state.get('final_code', ''))} chars")
    print("=" * 60)

    # 保存到 output
    save_output(final_state)

    return final_state


def save_output(state: dict):
    """保存生成的代码到 output/ 目录"""
    output_dir = os.path.join(_PROJECT_ROOT, "output")
    os.makedirs(output_dir, exist_ok=True)

    page_name = state.get("page_name", "GeneratedPage")
    code = state.get("final_code", "")

    if code:
        filename = os.path.join(output_dir, f"{page_name}.kt")
        with open(filename, "w", encoding="utf-8") as f:
            f.write(code)
        print(f"💾 已保存: {filename}")

    # 保存元数据
    meta = {
        "page_name": page_name,
        "success": state.get("success"),
        "elapsed_seconds": state.get("elapsed_seconds"),
        "fix_attempts": state.get("fix_attempts"),
        "page_type": state.get("page_type"),
        "components_needed": state.get("components_needed"),
        "code_length": len(code),
    }
    meta_path = os.path.join(output_dir, f"{page_name}.meta.json")
    with open(meta_path, "w", encoding="utf-8") as f:
        json.dump(meta, f, ensure_ascii=False, indent=2)


def batch_generate(test_file: str):
    """批量生成，读取 JSON 测试用例"""
    with open(test_file, "r", encoding="utf-8") as f:
        cases = json.load(f)

    results = []
    for i, case in enumerate(cases):
        print(f"\n{'#' * 60}")
        print(f"# 测试用例 {i+1}/{len(cases)}")
        print(f"{'#' * 60}")
        result = generate_page(case["requirement"])
        result["requirement"] = case["requirement"]
        result["expected_type"] = case.get("expected_type")
        results.append({
            "requirement": case["requirement"],
            "page_name": result.get("page_name"),
            "success": result.get("success"),
            "elapsed": result.get("elapsed_seconds"),
            "fix_attempts": result.get("fix_attempts"),
            "code_length": len(result.get("final_code", "")),
        })

    # 汇总
    print("\n" + "=" * 60)
    print("📊 批量测试汇总")
    print("=" * 60)
    total = len(results)
    success = sum(1 for r in results if r["success"])
    avg_time = sum(r["elapsed"] for r in results) / total if total else 0
    print(f"  总数: {total}")
    print(f"  成功: {success} ({success/total*100:.0f}%)")
    print(f"  平均耗时: {avg_time:.1f}s")
    print(f"  修正使用: {sum(r['fix_attempts'] for r in results)}")

    report_path = os.path.join(_PROJECT_ROOT, "output", "batch_report.json")
    with open(report_path, "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    print(f"📁 报告已保存: {report_path}")


def interactive_mode():
    """交互模式"""
    print("\n🎨 Kuikly Page Generator — 交互模式")
    print("输入自然语言描述页面，输入 'quit' 退出。\n")

    while True:
        try:
            req = input("📝 描述你想要的页面: ").strip()
        except (EOFError, KeyboardInterrupt):
            break
        if not req or req.lower() in ("quit", "exit", "q"):
            break
        generate_page(req)
        print()


def main():
    raw_args = sys.argv[1:]
    if not raw_args:
        print(__doc__)
        print("\n示例:")
        print('  python -m src.main "一个登录页面"')
        print('  python -m src.main --trace "一个登录页面"      # 实时打印每一步 state')
        print('  python -m src.main --batch tests/test_cases.json')
        print('  python -m src.main --interactive')
        return

    trace = "--trace" in raw_args
    args = [a for a in raw_args if a != "--trace"]

    arg = args[0]
    if arg == "--batch":
        test_file = args[1] if len(args) > 1 else "tests/test_cases.json"
        batch_generate(test_file)
    elif arg == "--interactive":
        interactive_mode()
    else:
        generate_page(arg, trace=trace)


if __name__ == "__main__":
    try:
        main()
    except ValueError as e:
        print(f"\n❌ 配置错误: {e}", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ 运行错误: {e}", file=sys.stderr)
        sys.exit(1)
