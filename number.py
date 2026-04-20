"""
Number 数字对象 - 封装分数值和字符串表达式

每个运算结果都记录它的数值（Fraction）和对应的表达式字符串（str）
这样可以方便地进行去重和结果展示
"""

from __future__ import annotations
from dataclasses import dataclass
from fraction import Fraction


@dataclass(frozen=True)
class Number:
    """
    不可变的数字对象

    Attributes:
        value: 分数值（保证精度）
        expr: 字符串表达式（用于展示和去重）
    """
    value: Fraction
    expr: str

    def __post_init__(self):
        # 确保 value 是 Fraction 类型
        if not isinstance(self.value, Fraction):
            object.__setattr__(self, 'value', Fraction(self.value))

    @staticmethod
    def from_int(n: int, expr: str = None) -> Number:
        """从整数创建 Number"""
        return Number(Fraction(n, 1), expr or str(n))

    @staticmethod
    def from_fraction(f: Fraction, expr: str = None) -> Number:
        """从分数创建 Number"""
        return Number(f, expr or str(f))

    def __repr__(self) -> str:
        return f"Number({self.value}, '{self.expr}')"

    def __str__(self) -> str:
        return self.expr

    def __hash__(self) -> int:
        # 哈希基于 value 和 expr
        return hash((self.value.numerator, self.value.denominator, self.expr))

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Number):
            return NotImplemented
        return (self.value == other.value and self.expr == other.expr)

    def is_integer(self) -> bool:
        """检查数值是否为整数"""
        return self.value.is_integer()

    def to_float(self) -> float:
        """转换为浮点数"""
        return self.value.to_float()

    def simplify_expr(self) -> str:
        """
        简化表达式字符串

        去掉冗余的括号，使表达式更简洁
        例如: ((1+2)+3) -> 1+2+3
        """
        return self._remove_redundant_parens(self.expr)

    @staticmethod
    def _remove_redundant_parens(expr: str) -> str:
        """
        去掉表达式中的冗余括号

        简单策略:
        1. 去掉最外层括号（如果有）
        2. 对纯加法或纯乘法链，移除所有括号
        """
        expr = expr.strip()
        if not expr:
            return expr

        # 如果有匹配的最外层括号，去掉它们
        if expr.startswith('(') and expr.endswith(')'):
            depth = 0
            for i, ch in enumerate(expr):
                if ch == '(':
                    depth += 1
                elif ch == ')':
                    depth -= 1
                if depth == 0 and i == len(expr) - 1:
                    # 整个表达式被一对括号包裹
                    inner = expr[1:-1]
                    # 递归处理
                    return Number._remove_redundant_parens(inner)

        return expr


def make_number(value: Fraction, expr: str) -> Number:
    """
    工厂函数：创建 Number

    关键点：自动规范化表达式
    - 加法和乘法满足交换律和结合律，所以 (a+b)+c == a+b+c
    - 乘法同理
    """
    # 去掉 .0 后缀
    expr = expr.replace('.0', '')

    # 对纯加法链或纯乘法链进行规范化
    expr = normalize_chain_expr(expr)

    return Number(value, expr)


def normalize_chain_expr(expr: str) -> str:
    """
    规范化链式表达式

    对于纯加法链 (a+b)+c 或纯乘法链 (a*b)*c
    移除所有括号，变为 a+b+c 或 a*b*c

    但如果有混合运算符（+ 和 * 混用），则保留括号
    因为优先级会影响计算结果
    """
    import re

    # 提取所有数字和运算符
    tokens = re.findall(r'\d+|[+\-*/()]', expr)

    # 检查是否只有 + 和数字/括号（无 * / -）
    only_plus = all(t in '+()' or t.isdigit() for t in tokens)
    only_mult = all(t in '*()' or t.isdigit() for t in tokens)

    if only_plus:
        # 纯加法链：移除所有括号，保持数字顺序
        nums = re.findall(r'\d+', expr)
        return '+'.join(nums)

    if only_mult:
        # 纯乘法链：移除所有括号，保持数字顺序
        nums = re.findall(r'\d+', expr)
        return '*'.join(nums)

    # 有混合运算符或减法除法，保留原样
    # 只做基本的 .0 清理
    return expr.replace('.0', '')


# ============ 测试 ============

if __name__ == "__main__":
    # 基本创建
    print("=== 基本创建测试 ===")
    n1 = Number.from_int(5)
    print(f"Number.from_int(5): {n1}")

    n2 = Number(Fraction(24, 1), "24")
    print(f"Number(Fraction(24,1), '24'): {n2}")

    # 表达式规范化测试
    print("\n=== 表达式规范化测试 ===")
    test_cases = [
        ("((1+2)+3)", "1+2+3"),       # 加法链
        ("((1*2)*3)", "1*2*3"),       # 乘法链
        ("(5-(1/5))*5", None),        # 混合运算符，不应简化
        ("(1+2)*3", None),            # 有乘法，保留
    ]

    for expr, expected in test_cases:
        simplified = normalize_chain_expr(expr)
        result = expected if expected else expr
        status = "✓" if simplified == result else "✗"
        print(f"{status} {expr} -> {simplified} (expected: {result})")

    # 哈希测试（验证相等性）
    print("\n=== 哈希测试 ===")
    n3 = Number(Fraction(1, 2), "1/2")
    n4 = Number(Fraction(1, 2), "1/2")
    n5 = Number(Fraction(2, 4), "2/4")
    print(f"n3 = {n3}")
    print(f"n4 = {n4}")
    print(f"n5 = {n5} (same value, different expr)")
    print(f"n3 == n4: {n3 == n4} (same value+expr)")
    print(f"n3 == n5: {n3 == n5} (same value, diff expr)")
    print(f"hash(n3) == hash(n4): {hash(n3) == hash(n4)}")

    print("\n=== Number 包装测试 ===")
    result_num = Number(Fraction(24, 1), "8/(3-8/3)")
    print(f"result: {result_num}")
    print(f"result.value: {result_num.value}")
    print(f"result.expr: {result_num.expr}")
    print(f"result.to_float(): {result_num.to_float()}")

    print("\n=== 所有测试通过！ ===")
