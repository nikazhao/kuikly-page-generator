# Kuikly 页面生成器 · 评估报告

- 生成时间：2026-09-02 19:40:27
- 用例来源：`tests/test_cases.json`
- 用例总数：**1**
- 通过率：**0%** (0/1)
- 平均自动修正次数：3.0
- 平均耗时：98.33s
- 平均规则符合度：**100%**（硬规则，确定性评分）

> ⚠️ **评估口径说明**：`success` 由 kotlinc 语法校验（已过滤 classpath 噪声）＋结构规则＋LLM 自审三层构成，**并未在真实 Kuikly classpath 下编译运行**；「规则符合度」为纯确定性评分（`_kuikly_compliance`），不依赖 LLM 自审，是可复现的硬指标。

## 用例明细

| # | 需求 | 期望类型 | 成功 | 修正次数 | 错误数 | 耗时(s) | 代码长度 | 规则符合度 |
|---|------|---------|------|---------|-------|--------|---------|-----------|
| 1 | 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击 | login | ❌ | 3 | 30 | 98.33 | 7605 | 7/7 |

## 未通过用例

- #1 一个登录页面，有用户名输入框、密码输入框和登录按钮，按钮点击后验证输入 — 修正 3 次后仍剩 30 个问题
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:4:37: error: unresolved reference 'AlignItems'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:8:37: error: unresolved reference 'FlexDirection'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:9:37: error: unresolved reference 'FontWeight'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:10:37: error: unresolved reference 'JustifyContent'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:31:9: error: unresolved reference 'callNativeMethod'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:39:9: error: unresolved reference 'callNativeMethod'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:62:23: error: type mismatch: inferred type is 'AudioHapticsModule?', but 'AudioHapticsModule' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:70:36: error: unresolved reference 'pageWidth'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:70:57: error: unresolved reference 'pageHeight'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:71:35: error: unresolved reference 'FlexDirection'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:77:41: error: unresolved reference 'pageWidth'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:78:40: error: unresolved reference 'JustifyContent'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:78:40: error: argument type mismatch: actual type is 'Unit', but 'FlexJustifyContent' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:79:36: error: unresolved reference 'AlignItems'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:79:36: error: argument type mismatch: actual type is 'Unit', but 'FlexAlign' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:85:29: error: unresolved reference 'fontWeight'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:85:51: error: unresolved reference 'Bold'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:94:39: error: unresolved reference 'FlexDirection'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:95:36: error: unresolved reference 'AlignItems'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:95:36: error: argument type mismatch: actual type is 'Unit', but 'FlexAlign' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:110:29: error: unresolved reference 'onTextChange'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:126:29: error: unresolved reference 'secureText'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:129:29: error: unresolved reference 'onTextChange'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:150:44: error: unresolved reference 'JustifyContent'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:150:44: error: argument type mismatch: actual type is 'Unit', but 'FlexJustifyContent' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:151:40: error: unresolved reference 'AlignItems'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:151:40: error: argument type mismatch: actual type is 'Unit', but 'FlexAlign' was expected.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:190:33: error: unresolved reference 'fontWeight'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:190:55: error: unresolved reference 'Bold'.
    - /var/folders/tq/1vf6rlgx5_sf10wmxkj27g7c0000gn/T/tmpi6mlxqb7.kt:199:41: error: unresolved reference 'pageWidth'.

---
*本报告由 `eval/run_eval.py` 自动生成，可作为接入官方 Kuikly MCP 前后的效果对比基线。*
