# Quality Guidelines

> Code quality standards for Python backend development.

---

## Overview

This project follows Python best practices with focus on:
- Type hints for all public APIs
- Clear docstrings
- Small, focused functions

---

## Forbidden Patterns

### ❌ 全局变量
```python
# Bad - 全局可变状态
solutions = []

def solve():
    solutions.append(result)
```

### ✅ 更好的做法
```python
# Good - 返回结果
def solve(numbers: List[int]) -> List[str]:
    results = []
    # ... 计算
    return results
```

---

## Required Patterns

### 1. Type Hints (必须)
```python
# Good
def solve(self, numbers: List[int]) -> List[str]:
    ...

# Bad
def solve(self, numbers):
    ...
```

### 2. Docstrings (必须)
```python
# Good
def solve(self, numbers: List[int]) -> List[str]:
    """
    求解二十四点

    Args:
        numbers: 4个整数列表

    Returns:
        所有可能的解的表达式列表

    Raises:
        ValueError: 如果数字数量不是4
    """
```

### 3. 明确的异常
```python
# Good
if len(numbers) != 4:
    raise ValueError("必须提供4个数字")
```

---

## Testing Requirements

### 单元测试
```python
import pytest

def test_solve_basic():
    solver = Solver()
    solutions = solver.solve([1, 2, 3, 4])
    assert len(solutions) > 0
    assert "(1+2+3)*4" in solutions

def test_invalid_input():
    solver = Solver()
    with pytest.raises(ValueError):
        solver.solve([1, 2, 3])  # 不是4个数字
```

---

## Code Review Checklist

- [ ] 函数有类型注解
- [ ] 公共函数有 docstring
- [ ] 没有裸 `except:`
- [ ] 没有 print 调试代码
- [ ] 魔法数字用常量替代
- [ ] 列表推导式优先于循环

---

## Python Style Guide

| 规则 | 正确 | 错误 |
|------|------|------|
| 缩进 | 4空格 | Tab |
| 行长度 | ≤88字符 | >120字符 |
| 空行 | 2行分隔类/大函数 | 1行或3行 |
| 命名 | `snake_case` | `camelCase` |

使用 `black` 格式化：
```bash
pip install black
black .
```
