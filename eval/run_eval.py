"""
量化评估：Kuikly 页面生成器
===========================
加载 tests/test_cases.json 的用例，逐条跑 run_pipeline，统计：
- 通过率（success 比例）
- 平均自动修正次数
- 平均耗时
- 各用例明细（需求 / 期望类型 / 成功 / 修正次数 / 错误数 / 耗时 / 代码长度）
输出 eval/report.md。

用法：
    python eval/run_eval.py
    python eval/run_eval.py --cases tests/test_cases.json --out eval/report.md
    python eval/run_eval.py --limit 3          # 只跑前 3 条，快速验证

设计借力：mayankysharma/langgraph-code-agent 的「生成后自动评估」思路（本项目用
自研统计，不引入 Judgeval 重依赖）。
"""

from __future__ import annotations

import argparse
import datetime
import json
import os
import sys
import time

_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

from src.run_pipeline import run_pipeline
from src.nodes import _kuikly_compliance


def run_eval(cases_path: str, out_path: str, limit: int | None = None, only: int | None = None) -> dict:
    with open(cases_path, "r", encoding="utf-8") as f:
        cases = json.load(f)
    if only:
        cases = [cases[only - 1]]  # 1-based，定位复现单条失败用例
    if limit:
        cases = cases[:limit]

    results = []
    for i, case in enumerate(cases):
        req = case.get("requirement", "")
        exp = case.get("expected_type", "")
        t0 = time.time()
        try:
            final, _ = run_pipeline(req, collect_steps=False)
            success = bool(final.get("success", False))
            fix = final.get("fix_attempts", 0) or 0
            elapsed = final.get("elapsed_seconds", round(time.time() - t0, 2))
            err = final.get("error_count", 0) or 0
            code = final.get("final_code", "") or ""
            code_len = len(code)
            compliance = _kuikly_compliance(code)
            # 落盘最终代码：编译错误只有 行:列 对着代码才能定位根因（无落盘则临时文件已删、无从复盘）
            code_dir = os.path.join(_PROJECT_ROOT, "eval", "code")
            os.makedirs(code_dir, exist_ok=True)
            _stem = os.path.splitext(os.path.basename(out_path))[0]
            with open(os.path.join(code_dir, f"{_stem}_case{i + 1}.kt"), "w", encoding="utf-8") as f:
                f.write(code)
            compile_errors = final.get("compile_errors", []) or []
            err_msg = ""
        except Exception as e:  # noqa: BLE001 — 单条失败不影响整体评估
            success, fix, err, code_len = False, 0, -1, 0
            compliance = {"score": 0.0, "hard_passed": 0, "hard_total": 0, "hard_failed": []}
            compile_errors = []
            elapsed = round(time.time() - t0, 2)
            err_msg = f"{type(e).__name__}: {e}"
        results.append({
            "idx": i + 1,
            "requirement": req,
            "expected_type": exp,
            "success": success,
            "fix_attempts": fix,
            "error_count": err,
            "elapsed": elapsed,
            "code_length": code_len,
            "compliance_score": compliance.get("score", 0.0),
            "compliance_hard": f"{compliance.get('hard_passed', 0)}/{compliance.get('hard_total', 0)}",
            "compliance_failed": compliance.get("hard_failed", []),
            "compile_errors": compile_errors,
            "error": err_msg,
        })
        print(f"[{i + 1}/{len(cases)}] {'✅' if success else '❌'} {req[:40]} | fix={fix} err={err} 符合度={compliance.get('score', 0):.0%} {elapsed}s")

    total = len(results)
    passed = sum(1 for r in results if r["success"])
    avg_fix = (sum(r["fix_attempts"] for r in results) / total) if total else 0
    avg_elapsed = (sum(r["elapsed"] for r in results) / total) if total else 0
    avg_compliance = (sum(r["compliance_score"] for r in results) / total) if total else 0

    summary = {
        "total": total,
        "passed": passed,
        "pass_rate": (passed / total * 100) if total else 0,
        "avg_fix_attempts": round(avg_fix, 2),
        "avg_elapsed": round(avg_elapsed, 2),
        "avg_compliance": round(avg_compliance, 4),
    }

    _write_report(out_path, summary, results, cases_path)
    print(f"\n通过率: {summary['pass_rate']:.0f}% ({passed}/{total}) | 平均修正 {summary['avg_fix_attempts']} | 平均耗时 {summary['avg_elapsed']}s | 平均规则符合度 {summary['avg_compliance']:.0%}")
    print(f"报告已写入: {out_path}")
    return summary


def _write_report(out_path: str, summary: dict, results: list[dict], cases_path: str) -> None:
    lines = []
    lines.append("# Kuikly 页面生成器 · 评估报告\n")
    lines.append(f"- 生成时间：{datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    lines.append(f"- 用例来源：`{os.path.relpath(cases_path, _PROJECT_ROOT)}`")
    lines.append(f"- 用例总数：**{summary['total']}**")
    lines.append(f"- 通过率：**{summary['pass_rate']:.0f}%** ({summary['passed']}/{summary['total']})")
    lines.append(f"- 平均自动修正次数：{summary['avg_fix_attempts']}")
    lines.append(f"- 平均耗时：{summary['avg_elapsed']}s")
    lines.append(f"- 平均规则符合度：**{summary['avg_compliance']:.0%}**（硬规则，确定性评分）\n")
    # 口径声明按实际情况输出：设了 KUIKLY_CLASSPATH 就是真编译校验，不能再写"未在真实 classpath 编译"
    if os.getenv("KUIKLY_CLASSPATH", "").strip():
        lines.append(
            "> ⚠️ **评估口径说明**：已配置 `KUIKLY_CLASSPATH`，`success` 由 **Kuikly 真实 classpath 编译校验**"
            "（unresolved reference / 类型不匹配等计为真错误）＋结构规则＋LLM 自审三层构成；"
            "「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。\n"
        )
    else:
        lines.append(
            "> ⚠️ **评估口径说明**：`success` 由 kotlinc 语法校验（已过滤 classpath 噪声）＋结构规则＋LLM 自审三层构成，"
            "**并未在真实 Kuikly classpath 下编译运行**；「规则符合度」为纯确定性评分（`_kuikly_compliance`），"
            "不依赖 LLM 自审，是可复现的硬指标。\n"
        )

    lines.append("## 用例明细\n")
    lines.append("| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |")
    lines.append("|---|------|---------|------|---------|-------|--------|---------|-----------|")
    for r in results:
        lines.append(
            f"| {r['idx']} | {r['requirement'][:30]} | {r['expected_type']} | "
            f"{'✅' if r['success'] else '❌'} | {r['fix_attempts']} | {r['error_count']} | "
            f"{r['elapsed']} | {r['code_length']} | {r['compliance_hard']} |"
        )

    failed = [r for r in results if not r["success"]]
    if failed:
        lines.append("\n## 未通过用例\n")
        for r in failed:
            detail = r["error"] or f"修正 {r['fix_attempts']} 次后仍剩 {r['error_count']} 个问题"
            lines.append(f"- #{r['idx']} {r['requirement'][:40]} — {detail}")
            for ce in r.get("compile_errors", []):
                lines.append(f"    - {ce}")

    low_compliance = [r for r in results if r["compliance_failed"]]
    if low_compliance:
        lines.append("\n## 硬规则未通过明细\n")
        for r in low_compliance:
            for desc in r["compliance_failed"]:
                lines.append(f"- #{r['idx']} {r['requirement'][:30]} — {desc}")

    lines.append("\n---\n*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*")

    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    with open(out_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def main() -> None:
    parser = argparse.ArgumentParser(description="Kuikly 页面生成器量化评估")
    parser.add_argument("--cases", default=os.path.join(_PROJECT_ROOT, "tests", "test_cases.json"))
    parser.add_argument("--out", default=os.path.join(_PROJECT_ROOT, "eval", "report.md"))
    parser.add_argument("--limit", type=int, default=None, help="只跑前 N 条用例")
    parser.add_argument("--only", type=int, default=None, help="只跑第 N 条用例（1-based，定位复现单条失败）")
    args = parser.parse_args()
    run_eval(args.cases, args.out, limit=args.limit, only=args.only)


if __name__ == "__main__":
    main()
