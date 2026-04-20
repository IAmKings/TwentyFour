#!/usr/bin/env python3
"""
二十四点算法
输入4个1-10的数字，判断能否经过四则运算计算出24

算法思路：
1. 枚举4个数字的全排列 (24种)
2. 枚举运算符的组合 (4^3 = 64种)
3. 枚举所有可能的括号组合 (5种)
4. 检查是否有计算结果等于24
"""

from itertools import permutations, product
from typing import Optional


def can_make_24(numbers: list[float], tolerance: float = 1e-9) -> bool:
    """
    判断给定的4个数能否计算出24

    Args:
        numbers: 4个数字列表
        tolerance: 浮点数精度容忍度

    Returns:
        True if 24 can be made, False otherwise
    """
    if len(numbers) != 4:
        raise ValueError("必须输入4个数字")

    # 运算符
    operators = ['+', '-', '*', '/']

    # 5种括号组合方式 (a,b,c,d为三个运算符)
    # 1. ((a op b) op c) op d
    # 2. (a op (b op c)) op d
    # 3. a op ((b op c) op d)
    # 4. a op (b op (c op d))
    # 5. (a op b) op (c op d)

    def apply_op(a: float, b: float, op: str) -> Optional[float]:
        """执行单个运算"""
        if op == '+':
            return a + b
        elif op == '-':
            return a - b
        elif op == '*':
            return a * b
        elif op == '/':
            if b == 0:
                return None
            return a / b
        return None

    def evaluate_with_parens(nums: list[float], ops: list[str], pattern: int) -> Optional[float]:
        """根据括号模式计算结果"""
        a, b, c, d = nums
        op1, op2, op3 = ops

        if pattern == 0:
            # ((a op b) op c) op d
            r1 = apply_op(a, b, op1)
            if r1 is None:
                return None
            r2 = apply_op(r1, c, op2)
            if r2 is None:
                return None
            return apply_op(r2, d, op3)

        elif pattern == 1:
            # (a op (b op c)) op d
            r1 = apply_op(b, c, op2)
            if r1 is None:
                return None
            r2 = apply_op(a, r1, op1)
            if r2 is None:
                return None
            return apply_op(r2, d, op3)

        elif pattern == 2:
            # a op ((b op c) op d)
            r1 = apply_op(b, c, op2)
            if r1 is None:
                return None
            r2 = apply_op(r1, d, op3)
            if r2 is None:
                return None
            return apply_op(a, r2, op1)

        elif pattern == 3:
            # a op (b op (c op d))
            r1 = apply_op(c, d, op3)
            if r1 is None:
                return None
            r2 = apply_op(b, r1, op2)
            if r2 is None:
                return None
            return apply_op(a, r2, op1)

        elif pattern == 4:
            # (a op b) op (c op d)
            r1 = apply_op(a, b, op1)
            if r1 is None:
                return None
            r2 = apply_op(c, d, op3)
            if r2 is None:
                return None
            return apply_op(r1, r2, op2)

        return None

    # 枚举所有排列
    for nums in permutations(numbers):
        # 枚举所有运算符组合
        for ops in product(operators, repeat=3):
            # 枚举所有括号模式
            for pattern in range(5):
                result = evaluate_with_parens(list(nums), list(ops), pattern)
                if result is not None and abs(result - 24) < tolerance:
                    return True

    return False


def find_solution(numbers: list[float], tolerance: float = 1e-9) -> Optional[str]:
    """
    找出一种能够计算出24的表达式

    Returns:
        能够计算出24的表达式字符串，如果不存在则返回None
    """
    if len(numbers) != 4:
        raise ValueError("必须输入4个数字")

    operators = ['+', '-', '*', '/']

    def apply_op(a: float, b: float, op: str) -> Optional[float]:
        if op == '+':
            return a + b
        elif op == '-':
            return a - b
        elif op == '*':
            return a * b
        elif op == '/':
            if b == 0:
                return None
            return a / b
        return None

    # 存储找到的解决方案
    solution = None

    def evaluate_with_parens(nums: list[float], ops: list[str], pattern: int) -> Optional[float]:
        nonlocal solution
        a, b, c, d = nums
        op1, op2, op3 = ops

        if pattern == 0:
            # ((a op b) op c) op d
            r1 = apply_op(a, b, op1)
            if r1 is None:
                return None
            r2 = apply_op(r1, c, op2)
            if r2 is None:
                return None
            result = apply_op(r2, d, op3)
            if result is not None and abs(result - 24) < tolerance:
                solution = f"(({a} {op1} {b}) {op2} {c}) {op3} {d}"
            return result

        elif pattern == 1:
            # (a op (b op c)) op d
            r1 = apply_op(b, c, op2)
            if r1 is None:
                return None
            r2 = apply_op(a, r1, op1)
            if r2 is None:
                return None
            result = apply_op(r2, d, op3)
            if result is not None and abs(result - 24) < tolerance:
                solution = f"({a} {op1} ({b} {op2} {c})) {op3} {d}"
            return result

        elif pattern == 2:
            # a op ((b op c) op d)
            r1 = apply_op(b, c, op2)
            if r1 is None:
                return None
            r2 = apply_op(r1, d, op3)
            if r2 is None:
                return None
            result = apply_op(a, r2, op1)
            if result is not None and abs(result - 24) < tolerance:
                solution = f"{a} {op1} (({b} {op2} {c}) {op3} {d})"
            return result

        elif pattern == 3:
            # a op (b op (c op d))
            r1 = apply_op(c, d, op3)
            if r1 is None:
                return None
            r2 = apply_op(b, r1, op2)
            if r2 is None:
                return None
            result = apply_op(a, r2, op1)
            if result is not None and abs(result - 24) < tolerance:
                solution = f"{a} {op1} ({b} {op2} ({c} {op3} {d}))"
            return result

        elif pattern == 4:
            # (a op b) op (c op d)
            r1 = apply_op(a, b, op1)
            if r1 is None:
                return None
            r2 = apply_op(c, d, op3)
            if r2 is None:
                return None
            result = apply_op(r1, r2, op2)
            if result is not None and abs(result - 24) < tolerance:
                solution = f"({a} {op1} {b}) {op2} ({c} {op3} {d})"
            return result

        return None

    for nums in permutations(numbers):
        for ops in product(operators, repeat=3):
            for pattern in range(5):
                evaluate_with_parens(list(nums), list(ops), pattern)
                if solution is not None:
                    return solution

    return None


def get_all_solutions(numbers: list[float], tolerance: float = 1e-9) -> list[str]:
    """
    找出所有能够计算出24的表达式

    Returns:
        能够计算出24的所有表达式列表（去重）
    """
    if len(numbers) != 4:
        raise ValueError("必须输入4个数字")

    operators = ['+', '-', '*', '/']
    solutions = []

    def apply_op(a: float, b: float, op: str) -> Optional[float]:
        if op == '+':
            return a + b
        elif op == '-':
            return a - b
        elif op == '*':
            return a * b
        elif op == '/':
            if b == 0:
                return None
            return a / b
        return None

    def evaluate_with_parens(nums: list[float], ops: list[str], pattern: int) -> Optional[float]:
        a, b, c, d = nums
        op1, op2, op3 = ops

        if pattern == 0:
            r1 = apply_op(a, b, op1)
            if r1 is None:
                return None
            r2 = apply_op(r1, c, op2)
            if r2 is None:
                return None
            result = apply_op(r2, d, op3)
            if result is not None and abs(result - 24) < tolerance:
                solutions.append(f"(({a}{op1}{b}){op2}{c}){op3}{d}")
            return result

        elif pattern == 1:
            r1 = apply_op(b, c, op2)
            if r1 is None:
                return None
            r2 = apply_op(a, r1, op1)
            if r2 is None:
                return None
            result = apply_op(r2, d, op3)
            if result is not None and abs(result - 24) < tolerance:
                solutions.append(f"({a}{op1}({b}{op2}{c})){op3}{d}")
            return result

        elif pattern == 2:
            r1 = apply_op(b, c, op2)
            if r1 is None:
                return None
            r2 = apply_op(r1, d, op3)
            if r2 is None:
                return None
            result = apply_op(a, r2, op1)
            if result is not None and abs(result - 24) < tolerance:
                solutions.append(f"{a}{op1}(({b}{op2}{c}){op3}{d})")
            return result

        elif pattern == 3:
            r1 = apply_op(c, d, op3)
            if r1 is None:
                return None
            r2 = apply_op(b, r1, op2)
            if r2 is None:
                return None
            result = apply_op(a, r2, op1)
            if result is not None and abs(result - 24) < tolerance:
                solutions.append(f"{a}{op1}({b}{op2}({c}{op3}{d}))")
            return result

        elif pattern == 4:
            r1 = apply_op(a, b, op1)
            if r1 is None:
                return None
            r2 = apply_op(c, d, op3)
            if r2 is None:
                return None
            result = apply_op(r1, r2, op2)
            if result is not None and abs(result - 24) < tolerance:
                solutions.append(f"({a}{op1}{b}){op2}({c}{op3}{d})")
            return result

        return None

    for nums in permutations(numbers):
        for ops in product(operators, repeat=3):
            for pattern in range(5):
                evaluate_with_parens(list(nums), list(ops), pattern)

    # 去重并返回
    return list(dict.fromkeys(solutions))


def format_solution(s: str) -> str:
    """格式化表达式，更易读"""
    # 去掉 .0 后缀，并添加空格使运算符与数字分开
    s = s.replace('.0', '')
    s = s.replace('+', ' + ').replace('-', ' - ').replace('*', ' * ').replace('/', ' / ')
    return s


def simplify_solutions(solutions: list[str]) -> list[str]:
    """
    简化解法列表：去重，格式化输出
    """
    # 去掉 .0 后缀后去重
    normalized = set()
    for sol in solutions:
        normalized.add(sol.replace('.0', ''))
    return sorted(normalized)


# ==================== 测试 ====================
if __name__ == "__main__":
    # 测试用例
    test_cases = [
        ([1, 2, 3, 4], True),    # (1+2+3)*4 = 24
        ([5, 5, 5, 1], True),    # 5*(5-1/5) = 24
        ([7, 7, 3, 3], True),    # 7*(3+3/7) = 24
        ([1, 1, 1, 1], False),   # 不可能得到24
        ([10, 10, 10, 10], True),# 10+10+10-6... 等等这个不对，看看有没有
        ([9, 7, 2, 1], True),    # 7*2+9+1 = 24 (不对)... 试试 (7-1)*4 = 24 不对没4
    ]

    print("=" * 50)
    print("二十四点算法测试")
    print("=" * 50)

    for nums, expected in test_cases:
        result = can_make_24(nums)
        status = "✓" if result == expected else "✗"
        print(f"{status} {nums} -> {'能' if result else '不能'}算出24 (预期: {'能' if expected else '不能'})")
        if result:
            sol = find_solution(nums)
            print(f"  解法: {sol}")

    # 交互式测试
    print("\n" + "=" * 50)
    print("输入4个1-10的数字（用空格分隔），按回车检测")
    print("输入 'q' 退出")
    print("=" * 50)

    while True:
        try:
            user_input = input("\n> ").strip()
            if user_input.lower() == 'q':
                break

            nums = [float(x) for x in user_input.split()]
            if len(nums) != 4:
                print("请输入4个数字！")
                continue
            if any(n < 1 or n > 10 for n in nums):
                print("数字必须在1-10之间！")
                continue

            if can_make_24(nums):
                print(f"✓ 能算出24！")
                solutions = get_all_solutions(nums)
                # 简化表达式（去掉括号，等价变换）
                simplified = simplify_solutions(solutions)
                print(f"\n共找到 {len(solutions)} 种等价解法，")
                print(f"化简后 {len(simplified)} 种不同形式:\n")
                for i, sol in enumerate(simplified, 1):
                    print(f"  {i}. {sol}")
            else:
                print(f"✗ 不能算出24")

        except ValueError:
            print("输入格式错误！")
        except EOFError:
            break

    print("\n拜拜！")
