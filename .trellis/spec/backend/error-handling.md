# Error Handling

> How errors are handled in this Python project.

---

## Overview

This project uses **Python's built-in exception system** with clear error messages.
No external error handling libraries needed - keep it simple.

---

## Error Types

### ValueError
用于参数校验失败：
```python
def solve(self, numbers: List[int]) -> List[str]:
    if len(numbers) != 4:
        raise ValueError("必须提供4个数字")
```

### 自定义异常 (可选)
```python
class SolverError(Exception):
    """求解器基础异常"""
    pass

class UnsolvableError(SolverError):
    """无解异常"""
    pass
```

---

## Error Handling Patterns

### 1. 函数级别验证
```python
def solve(self, numbers: List[int]) -> List[str]:
    # 输入验证
    if len(numbers) != 4:
        raise ValueError("必须提供4个数字")

    for n in numbers:
        if n < 1 or n > 13:
            raise ValueError(f"数字必须在1-13范围内: {n}")
```

### 2. 使用 Optional 处理可能的 None
```python
from typing import Optional

def find_solution(numbers: List[int]) -> Optional[str]:
    """返回解或None"""
    solutions = self.solve(numbers)
    return solutions[0] if solutions else None
```

### 3. 防御性编程
```python
# 检查除零
if denominator == 0:
    return None

# 使用 tolerance 比较浮点数
if abs(result - 24.0) < 1e-9:
    return True
```

---

## Common Mistakes

### ❌ 不要：裸 except
```python
# Bad
try:
    do_something()
except:
    pass
```

### ✅ 应该：具体异常 + 明确消息
```python
# Good
try:
    result = divide(a, b)
except ZeroDivisionError:
    return None
```

### ❌ 不要：吞掉异常不告知
```python
# Bad
except Exception:
    pass
```

### ✅ 应该：至少记录
```python
import sys
except ValueError as e:
    print(f"Error: {e}", file=sys.stderr)
    raise
```

---

## Anti-Patterns

1. **不要用 print 代替日志**
   - 正式代码用 `logging` 模块

2. **不要 bare except**
   - 永远指定异常类型

3. **不要隐藏错误**
   - 让错误传播给调用者
