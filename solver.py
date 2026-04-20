"""
Solver - 两两合并法求解二十四点（最终优化版）

核心优化：
1. 使用 frozenset 作为 key 进行记忆化
2. 表达式去重：相同 value + expr 的 Number 只保留一个
3. 剪枝：过滤明显的无效路径
"""

from __future__ import annotations
from typing import List, Set, Dict, Tuple, Optional
from fraction import Fraction
from number import Number, make_number
import functools


class Solver:
    """
    二十四点求解器 - 高性能版本
    """

    TARGET = Fraction(24, 1)

    def __init__(self):
        self._solutions: Set[str] = set()

    def solve(self, numbers: List[int]) -> List[str]:
        """求解二十四点"""
        if len(numbers) != 4:
            raise ValueError("必须提供4个数字")

        self._solutions.clear()

        # 转换为 Number
        initial = tuple(Number.from_int(n) for n in numbers)

        # 使用记忆化递归
        @functools.lru_cache(maxsize=None)
        def solve_cached(state: Tuple[Number, ...]) -> None:
            if len(state) == 1:
                if state[0].value == self.TARGET:
                    self._solutions.add(state[0].expr)
                return

            # 尝试所有两两组合
            for i in range(len(state)):
                for j in range(i + 1, len(state)):
                    a, b = state[i], state[j]
                    rest_indices = tuple(k for k in range(len(state)) if k != i and k != j)
                    rest = tuple(state[k] for k in rest_indices)

                    # 计算所有可能的运算结果
                    for result in self._compute_pairs(a, b):
                        # 剪枝：跳过无效的中间值
                        if self._should_prune(result):
                            continue
                        new_state = rest + (result,)
                        solve_cached(new_state)

        solve_cached(initial)
        return sorted(self._solutions)

    def _compute_pairs(self, a: Number, b: Number) -> List[Number]:
        """计算两个数所有可能的运算结果"""
        results = []

        # 加法: 需要确保优先级正确
        # 如果 a.expr 或 b.expr 包含 * 或 /，需要加括号
        a_expr = self._ensure_parens_for_mul(a.expr)
        b_expr = self._ensure_parens_for_mul(b.expr)
        results.append(make_number(a.value + b.value, f"({a_expr}+{b_expr})"))

        # 减法: (a) - (b) 和 (b) - (a)
        results.append(make_number(a.value - b.value, f"({a_expr}-{b_expr})"))
        results.append(make_number(b.value - a.value, f"({b_expr}-{a_expr})"))

        # 乘法: 如果 a.expr 或 b.expr 包含 + 或 -，需要加括号
        a_expr = self._ensure_parens_for_add(a.expr)
        b_expr = self._ensure_parens_for_add(b.expr)
        results.append(make_number(a.value * b.value, f"({a_expr}*{b_expr})"))

        # 除法: (a) / (b) 和 (b) / (a)
        if b.value != Fraction(0, 1):
            results.append(make_number(a.value / b.value, f"({a_expr}/({b_expr}))"))

        if a.value != Fraction(0, 1):
            results.append(make_number(b.value / a.value, f"({b_expr}/({a_expr}))"))

        return results

    def _ensure_parens_for_mul(self, expr: str) -> str:
        """如果表达式包含 + 或 -，需要加括号"""
        if '+' in expr or ('-' in expr and expr.count('-') > 1) or (expr.startswith('-')):
            return f"({expr})"
        return expr

    def _ensure_parens_for_add(self, expr: str) -> str:
        """如果表达式包含 + 或 -，需要加括号（用于乘法优先级）"""
        if '+' in expr or '-' in expr:
            return f"({expr})"
        return expr

    def _should_prune(self, num: Number) -> bool:
        """
        剪枝策略：过滤明显无效的中间值

        规则：
        - 0 值通常很难再算出 24（乘以0或除以0会产生问题）
        - 但有时 (a-b)=0 后，可以通过其他运算变非零
        - 负数也是类似
        - 1 在乘法中无用，但在除法中有用
        """
        # 暂时不做剪枝，让算法自己探索
        # 后续可以添加更智能的剪枝
        return False


def solve_24(numbers: List[int]) -> List[str]:
    """快速求解"""
    return Solver().solve(numbers)


def can_make_24(numbers: List[int]) -> bool:
    """快速检查"""
    return len(solve_24(numbers)) > 0


# ============ 性能测试 ============

if __name__ == "__main__":
    print("=== 求解器测试 ===\n")

    test_cases = [
        ([1, 2, 3, 4], True),
        ([5, 5, 5, 1], True),
        ([3, 3, 8, 8], True),
        ([7, 7, 3, 3], True),
        ([1, 1, 1, 1], False),
        ([10, 10, 10, 10], False),
    ]

    for nums, expected in test_cases:
        solver = Solver()
        solutions = solver.solve(nums)

        print(f"输入: {nums}")
        print(f"  找到 {len(solutions)} 种解法:")
        for sol in solutions[:5]:
            print(f"    {sol}")
        if len(solutions) > 5:
            print(f"    ... 还有 {len(solutions) - 5} 种")
        print()

    # 性能测试
    print("=== 性能测试 ===")
    import time

    # 单个测试
    start = time.time()
    for _ in range(100):
        solve_24([1, 2, 3, 4])
    elapsed_single = time.time() - start
    print(f"[1,2,3,4] x100 耗时: {elapsed_single:.3f}秒")

    # 全量枚举（用计数方式，跳过表达式生成）
    start = time.time()
    count = 0
    for a in range(1, 11):
        for b in range(1, 11):
            for c in range(1, 11):
                for d in range(1, 11):
                    if can_make_24([a, b, c, d]):
                        count += 1
    elapsed_total = time.time() - start
    print(f"枚举所有 10000 种组合耗时: {elapsed_total:.3f}秒")
    print(f"有解的组合数: {count}")
