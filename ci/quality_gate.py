"""
CI 质量门禁（本地脚本，P1③）
===========================
本地一键质量门禁：单元测试 → 量化评估 → 确定性规则符合度阈值 → 编译档位报告。

设计定位（区别于 CI 平台流水线）：
- 本脚本**在本地跑**，作为提交前的质量自检（用户当前选择"先本地脚本就绪"）；
- 未来接 CI 平台时，把同一套判定逻辑搬进流水线即可（脚本保持平台无关）。

三道门禁（任一不达标即退出非 0）：
1. **单元测试**：pytest tests/ 全绿（20 个用例，覆盖重试/符合度/路由/规则）；
2. **量化评估**：跑 eval，平均规则符合度 ≥ 阈值（默认 0.7）；
3. **编译档位报告**：打印当前编译校验档位（full/syntax/unavailable），
   若配置了 KUIKLY_CLASSPATH 则走真编译（unresolved reference 视为真错误）。

用法：
    python ci/quality_gate.py                      # 全量：单测 + eval 全量 + 阈值
    python ci/quality_gate.py --eval-limit 2       # 快速冒烟：eval 只跑 2 条
    python ci/quality_gate.py --skip-eval          # 只跑单元测试（不调 LLM，省钱）

退出码：
    0   全部通过
    1   单测失败
    2   eval 失败 / 阈值不达标
"""

from __future__ import annotations

import argparse
import os
import subprocess
import sys

_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

DEFAULT_COMPLIANCE_THRESHOLD = 0.7


def _run_pytest(venv_python: str) -> bool:
    """跑单元测试，返回是否全绿。"""
    print("\n" + "=" * 60)
    print("门禁① 单元测试（pytest tests/）")
    print("=" * 60)
    proc = subprocess.run(
        [venv_python, "-m", "pytest", "tests/", "-q"],
        cwd=_PROJECT_ROOT,
    )
    ok = proc.returncode == 0
    print(f"  单测结果: {'✓ 全绿' if ok else '✗ 有失败'}")
    return ok


def _run_eval(venv_python: str, eval_limit: int | None, threshold: float) -> bool:
    """跑量化评估并检查符合度阈值。"""
    print("\n" + "=" * 60)
    print(f"门禁② 量化评估（符合度阈值 ≥ {threshold:.0%}）")
    print("=" * 60)

    from eval.run_eval import run_eval

    cases_path = os.path.join(_PROJECT_ROOT, "tests", "test_cases.json")
    out_path = os.path.join(_PROJECT_ROOT, "eval", "report.md")

    try:
        summary = run_eval(cases_path, out_path, limit=eval_limit)
    except Exception as e:  # noqa: BLE001 — 任何评估异常都视为门禁失败
        print(f"  eval 异常: {type(e).__name__}: {e}")
        return False

    avg = summary.get("avg_compliance", 0.0)
    ok = avg >= threshold
    print(f"  平均规则符合度: {avg:.0%}（阈值 {threshold:.0%}）→ {'✓ 达标' if ok else '✗ 不达标'}")
    return ok


def _report_compile_tier() -> None:
    """打印当前编译校验档位（不设门禁，仅报告，供人判断可信度）。"""
    from src.nodes import _find_kuikly_classpath, _kotlinc_path

    print("\n" + "=" * 60)
    print("门禁③ 编译校验档位报告")
    print("=" * 60)
    kotlinc = _kotlinc_path()
    classpath = _find_kuikly_classpath()
    if not kotlinc:
        print("  档位: unavailable（未找到 kotlinc，编译校验不可用）")
    elif classpath:
        print("  档位: full（配置了 KUIKLY_CLASSPATH，真编译，unresolved reference 为真错误）")
        print(f"  classpath: {classpath}")
    else:
        print("  档位: syntax（无 classpath，仅纯语法校验，过滤缺库噪声）")
        print("  提示: 设置环境变量 KUIKLY_CLASSPATH 指向 Kuikly core 产物，可升级为真编译")


def main() -> int:
    parser = argparse.ArgumentParser(description="Kuikly 页面生成器 · 本地质量门禁")
    parser.add_argument("--eval-limit", type=int, default=None, help="eval 只跑前 N 条（快速冒烟）")
    parser.add_argument("--skip-eval", action="store_true", help="跳过 eval，只跑单元测试")
    parser.add_argument("--threshold", type=float, default=DEFAULT_COMPLIANCE_THRESHOLD,
                        help=f"符合度阈值（默认 {DEFAULT_COMPLIANCE_THRESHOLD}）")
    parser.add_argument("--python", default=None, help="指定 Python 解释器（默认用项目 venv）")
    args = parser.parse_args()

    # 定位项目 venv 的 Python
    if args.python:
        venv_python = args.python
    else:
        venv_python = os.path.join(_PROJECT_ROOT, "venv", "bin", "python")
        if not os.path.exists(venv_python):
            venv_python = sys.executable  # 兜底：用当前解释器

    print(f"Kuikly 页面生成器 · 本地质量门禁")
    print(f"项目根目录: {_PROJECT_ROOT}")
    print(f"Python: {venv_python}")

    # 门禁① 单元测试
    if not _run_pytest(venv_python):
        print("\n✗ 门禁未通过：单元测试失败")
        return 1

    # 门禁② 量化评估（可跳过）
    if not args.skip_eval:
        if not _run_eval(venv_python, args.eval_limit, args.threshold):
            print("\n✗ 门禁未通过：eval 失败或符合度不达标")
            return 2
    else:
        print("\n（已跳过门禁② 量化评估）")

    # 门禁③ 编译档位报告（仅报告）
    _report_compile_tier()

    print("\n" + "=" * 60)
    print("✓ 全部门禁通过")
    print("=" * 60)
    return 0


if __name__ == "__main__":
    sys.exit(main())
