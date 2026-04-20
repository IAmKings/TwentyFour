#!/usr/bin/env python3
"""
TwentyFour CLI - 二十四点游戏

用法:
    python cli.py                    # 交互模式（普通模式 1-10）
    python cli.py 1 2 3 4           # 查询指定数字（普通模式）
    python cli.py -H                 # 交互模式（高难度模式 1-13）
    python cli.py -H 1 2 3 4        # 查询指定数字（高难度模式）
    python cli.py --generate         # 重新生成预计算表（普通模式）
    python cli.py --generate -H      # 重新生成预计算表（高难度模式）
"""

import sys
import argparse
from typing import List

# 导入组件
from solver import Solver
from precompute import load, get_solutions, precompute_and_save


def parse_args():
    parser = argparse.ArgumentParser(description="TwentyFour - 二十四点求解器")
    parser.add_argument('numbers', nargs='*', type=int,
                        help='4个数字')
    parser.add_argument('--hard', '-H', action='store_true',
                        help='高难度模式 (范围 1-13)')
    parser.add_argument('--generate', '-g', action='store_true',
                        help='重新生成预计算表')
    parser.add_argument('--all', '-a', action='store_true',
                        help='显示所有解法（不截断）')
    return parser.parse_args()


def print_header(hard_mode: bool = False):
    mode = "高难度模式 (1-13)" if hard_mode else "普通模式 (1-10)"
    print("=" * 50)
    print("         二十四点求解器 (TwentyFour)")
    print(f"         [{mode}]")
    print("=" * 50)
    print()


def solve_and_display(numbers: List[int], range_max: int = 10, show_all: bool = False):
    """求解并显示结果"""
    if len(numbers) != 4:
        print(f"错误: 需要4个数字，得到 {len(numbers)} 个")
        return

    if any(n < 1 or n > range_max for n in numbers):
        print(f"错误: 数字必须在 1-{range_max} 之间")
        return

    print(f"输入: {numbers}")
    print("-" * 30)

    # 尝试加载预计算表
    try:
        solutions = get_solutions(numbers, range_max)
    except Exception:
        # 如果预计算表加载失败，使用实时计算
        print("(使用实时计算...)")
        solver = Solver()
        solutions = solver.solve(numbers)

    if solutions:
        print(f"✓ 能算出 24！共找到 {len(solutions)} 种解法:\n")
        display_solutions = solutions if show_all else solutions[:20]
        for i, sol in enumerate(display_solutions, 1):
            # 格式化表达式
            formatted = format_expression(sol)
            print(f"  {i:3d}. {formatted}")
        if not show_all and len(solutions) > 20:
            print(f"\n  ... 还有 {len(solutions) - 20} 种解法")
    else:
        print(f"✗ 不能算出 24")

    print()


def format_expression(expr: str) -> str:
    """
    格式化表达式，使其更易读

    例如: (5*(5-(1/(5)))) -> 5 * (5 - 1/5)
    """
    # 简单处理：添加空格
    result = expr
    result = result.replace('+', ' + ')
    result = result.replace('-', ' - ')
    result = result.replace('*', ' * ')
    result = result.replace('/', ' / ')
    # 清理多余空格
    result = ' '.join(result.split())
    return result


def interactive_mode(range_max: int = 10, hard_mode: bool = False):
    """交互模式"""
    print_header(hard_mode)
    range_name = "1-13" if hard_mode else "1-10"
    print(f"输入4个 {range_name} 的数字，用空格分隔")
    print("输入 'q' 退出")
    print()

    while True:
        try:
            user_input = input("> ").strip()

            if user_input.lower() in ('q', 'quit', 'exit'):
                print("拜拜！")
                break

            if not user_input:
                continue

            numbers = [int(x) for x in user_input.split()]
            solve_and_display(numbers, range_max)

        except ValueError:
            print(f"输入格式错误！请输入4个数字，用空格分隔\n")
        except EOFError:
            print("\n拜拜！")
            break


def main():
    args = parse_args()
    hard_mode = args.hard
    range_max = 13 if hard_mode else 10

    # 生成预计算表
    if args.generate:
        mode_name = "高难度" if hard_mode else "普通"
        print(f"正在重新生成预计算表 ({mode_name}模式 1-{range_max})...")
        precompute_and_save(range_max=range_max)
        print()
        return

    # 加载预计算表
    try:
        load(range_max=range_max)
    except Exception as e:
        print(f"警告: 无法加载预计算表 ({e})，将使用实时计算\n")

    # 查询模式
    if args.numbers:
        print_header(hard_mode)
        solve_and_display(args.numbers, range_max, show_all=args.all)
    else:
        # 交互模式
        interactive_mode(range_max, hard_mode)


if __name__ == "__main__":
    main()
