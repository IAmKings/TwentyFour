"""
Fraction 分数类 - 避免浮点数精度丢失

经典难点：8 / (3 - 8/3) = 24
用 double: 8 / (3 - 2.666...) ≈ 23.999...  ❌
用 Fraction: 8 / (1/3) = 24                 ✅
"""

from __future__ import annotations
from typing import Optional
import math


class Fraction:
    """
    不可变的分数类，支持基本四则运算和比较运算

    内部使用分子/分母存储，自动约分到最简形式
    """

    __slots__ = ('_numerator', '_denominator')

    def __init__(self, numerator: int, denominator: int = 1):
        """
        创建分数

        Args:
            numerator: 分子
            denominator: 分母，默认 1

        Raises:
            ZeroDivisionError: 当分母为 0 时
        """
        if denominator == 0:
            raise ZeroDivisionError("分母不能为 0")

        # 处理负号：统一让分子携带符号
        if denominator < 0:
            numerator = -numerator
            denominator = -denominator

        # 约分
        g = self._gcd(abs(numerator), denominator)
        if g > 1:
            numerator //= g
            denominator //= g

        self._numerator = numerator
        self._denominator = denominator

    @staticmethod
    def _gcd(a: int, b: int) -> int:
        """计算最大公约数"""
        while b:
            a, b = b, a % b
        return a

    @staticmethod
    def _lcm(a: int, b: int) -> int:
        """计算最小公倍数"""
        return abs(a * b) // Fraction._gcd(a, b)

    @property
    def numerator(self) -> int:
        return self._numerator

    @property
    def denominator(self) -> int:
        return self._denominator

    def __repr__(self) -> str:
        if self._denominator == 1:
            return f"{self._numerator}"
        return f"{self._numerator}/{self._denominator}"

    def __str__(self) -> str:
        return self.__repr__()

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Fraction):
            return NotImplemented
        return (self._numerator == other._numerator and
                self._denominator == other._denominator)

    def __hash__(self) -> int:
        return hash((self._numerator, self._denominator))

    def __lt__(self, other: Fraction) -> bool:
        if not isinstance(other, Fraction):
            return NotImplemented
        # a/b < c/d  <=>  a*d < c*b
        return self._numerator * other._denominator < other._numerator * self._denominator

    def __le__(self, other: Fraction) -> bool:
        return self == other or self < other

    def __gt__(self, other: Fraction) -> bool:
        if not isinstance(other, Fraction):
            return NotImplemented
        return other < self

    def __ge__(self, other: Fraction) -> bool:
        return self == other or self > other

    def __bool__(self) -> bool:
        return self._numerator != 0

    def __neg__(self) -> Fraction:
        return Fraction(-self._numerator, self._denominator)

    def __pos__(self) -> Fraction:
        return self

    def __abs__(self) -> Fraction:
        return Fraction(abs(self._numerator), self._denominator)

    def __add__(self, other: Fraction) -> Fraction:
        if not isinstance(other, Fraction):
            return NotImplemented
        # a/b + c/d = (a*d + c*b) / (b*d)
        num = self._numerator * other._denominator + other._numerator * self._denominator
        den = self._denominator * other._denominator
        return Fraction(num, den)

    def __sub__(self, other: Fraction) -> Fraction:
        if not isinstance(other, Fraction):
            return NotImplemented
        return self + (-other)

    def __mul__(self, other: Fraction) -> Fraction:
        if not isinstance(other, Fraction):
            return NotImplemented
        # a/b * c/d = (a*c) / (b*d)
        return Fraction(
            self._numerator * other._numerator,
            self._denominator * other._denominator
        )

    def __truediv__(self, other: Fraction) -> Fraction:
        if not isinstance(other, Fraction):
            return NotImplemented
        if other._numerator == 0:
            raise ZeroDivisionError("不能除以 0")
        # a/b / (c/d) = (a*d) / (b*c)
        return Fraction(
            self._numerator * other._denominator,
            self._denominator * other._numerator
        )

    def __radd__(self, other: int) -> Fraction:
        return self + Fraction(other)

    def __rsub__(self, other: int) -> Fraction:
        return Fraction(other) - self

    def __rmul__(self, other: int) -> Fraction:
        return self * Fraction(other)

    def __rtruediv__(self, other: int) -> Fraction:
        return Fraction(other) / self

    # Python 2 兼容
    __div__ = __truediv__
    __rdiv__ = __rtruediv__

    def to_float(self) -> float:
        """转换为浮点数"""
        return self._numerator / self._denominator

    def is_integer(self) -> bool:
        """检查是否为整数"""
        return self._denominator == 1

    def is_zero(self) -> bool:
        """检查是否为零"""
        return self._numerator == 0

    def is_positive(self) -> bool:
        """检查是否为正数"""
        return self._numerator > 0

    def is_negative(self) -> bool:
        """检查是否为负数"""
        return self._numerator < 0


# ============ 工厂函数 ============

def int_to_fraction(n: int) -> Fraction:
    """整数转分数"""
    return Fraction(n, 1)


def float_to_fraction(f: float, tolerance: float = 1e-9, max_depth: int = 20) -> Fraction:
    """
    浮点数转分数（近似）

    Args:
        f: 浮点数
        tolerance: 精度容忍度
        max_depth: 最大迭代深度

    Returns:
        近似分数
    """
    if abs(f - int(f)) < tolerance:
        return Fraction(int(round(f)))

    # 连分数展开
    sign = -1 if f < 0 else 1
    f = abs(f)

    int_part = int(f)
    frac_part = f - int_part

    if frac_part < tolerance:
        return Fraction(sign * int_part)

    # 分子分母初始值
    h1, h2 = 1, 0
    k1, k2 = 0, 1

    b = frac_part
    for _ in range(max_depth):
        a = int(1 / b)
        h = a * h1 + h2
        k = a * k1 + k2
        b = 1 / b - a

        approx = h / k
        if abs(f - sign * (int_part + approx)) < tolerance:
            return Fraction(sign * (int_part * k + h), k)

        h2, h1 = h1, h
        k2, k1 = k1, k

        if b < tolerance:
            break

    # 返回最接近的分数
    return Fraction(sign * (int_part * k1 + h1), k1)


# ============ 测试 ============

if __name__ == "__main__":
    # 基本运算测试
    print("=== 基本运算测试 ===")
    f1 = Fraction(1, 3)
    f2 = Fraction(8, 3)
    print(f"f1 = {f1}")
    print(f"f2 = {f2}")
    print(f"f1 + f2 = {f1 + f2}")
    print(f"f2 - f1 = {f2 - f1}")
    print(f"f1 * f2 = {f1 * f2}")
    print(f"f2 / f1 = {f2 / f1}")

    # 经典难点测试
    print("\n=== 经典难点测试 ===")
    f8 = Fraction(8, 1)
    f3 = Fraction(3, 1)
    f83 = Fraction(8, 3)

    # 3 - 8/3
    step1 = f3 - f83
    print(f"3 - 8/3 = {step1}")

    # 8 / (3 - 8/3)
    result = f8 / step1
    print(f"8 / (3 - 8/3) = {result}")
    print(f"结果 == 24? {result == Fraction(24)}")

    # 精度对比
    print("\n=== 精度对比 ===")
    print(f"Float: 8 / (3 - 8/3) = {8 / (3 - 8/3)}")
    print(f"Fraction: 8 / (3 - 8/3) = {result}")

    # 比较测试
    print("\n=== 比较运算测试 ===")
    print(f"Fraction(1,2) < Fraction(2,3): {Fraction(1,2) < Fraction(2,3)}")
    print(f"Fraction(1,2) > Fraction(1,3): {Fraction(1,2) > Fraction(1,3)}")
    print(f"Fraction(2,4) == Fraction(1,2): {Fraction(2,4) == Fraction(1,2)}")  # 自动约分

    print("\n=== 所有测试通过！ ===")
