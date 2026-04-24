# Directory Structure

> How Python backend code is organized in this project.

---

## Overview

This is a **Python-based 24 game solver** project with two parts:
1. **Python backend**: Core algorithms (solver, fraction, number types)
2. **Compose Multiplatform frontend**: UI application

---

## Directory Layout

```
TwentyFour/
├── solver.py           # 24点求解器 - 两两合并法
├── fraction.py         # 分数计算，避免浮点精度问题
├── number.py           # Number类型封装，包含值和表达式
├── precompute.py       # 预计算无解组合
├── twenty_four.py      # 主逻辑/CLI入口
├── cli.py              # 命令行界面
│
├── solutions.json       # 预计算的解 (1-10)
├── solutions_13.json    # 预计算的解 (1-13)
├── unsolvable_10.txt    # 无解组合 (1-10)
├── unsolvable_13.txt    # 无解组合 (1-13)
│
└── twenty-four-cmp/    # Compose Multiplatform UI
    └── composeApp/src/
        ├── commonMain/ # 跨平台代码
        ├── androidMain/ # Android特定
        └── desktopMain/ # Desktop特定
```

---

## Module Organization

| File | Purpose |
|------|---------|
| `solver.py` | 核心求解算法 |
| `fraction.py` | 精确分数计算 |
| `number.py` | 数字类型封装 |
| `precompute.py` | 预计算无解组合 |
| `twenty_four.py` | 游戏主逻辑 |
| `cli.py` | 命令行入口 |

---

## Naming Conventions

| 类型 | 规则 | 示例 |
|------|------|------|
| 模块文件 | `snake_case.py` | `solver.py`, `fraction.py` |
| 类名 | `PascalCase` | `Solver`, `Fraction` |
| 函数 | `snake_case` | `solve()`, `make_number()` |
| 常量 | `UPPER_SNAKE_CASE` | `TARGET = Fraction(24, 1)` |
| 类型别名 | `PascalCase` | `NumberList = List[Number]` |

---

## Examples

### Good: Module with clear responsibility
```python
# fraction.py
class Fraction:
    """精确分数计算，避免浮点精度问题"""
    def __init__(self, numerator: int, denominator: int = 1):
        ...
```

### Good: Functions with type hints
```python
def solve(self, numbers: List[int]) -> List[str]:
    """求解二十四点"""
    ...
```

### Good: Constants at module level
```python
TARGET = Fraction(24, 1)
MAX_NUMBER = 13
```
