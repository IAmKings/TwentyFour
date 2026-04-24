# Logging Guidelines

> How logging is done in this Python project.

---

## Overview

Simple projects use `print()`, serious projects use `logging`.
This project is simple - mainly uses `print()` for CLI output.

---

## Log Levels (Standard Python)

| Level | When to use | Example |
|-------|-------------|---------|
| DEBUG | 开发调试信息 | `print(f"DEBUG: numbers={numbers}")` |
| INFO | 正常流程信息 | `print(f"Found {len(solutions)} solutions")` |
| WARNING | 警告但不中断 | `print(f"WARNING: No solution found")` |
| ERROR | 错误信息 | `print(f"ERROR: {e}", file=sys.stderr)` |

---

## Patterns

### 1. CLI 输出
```python
# 正常信息
print("正在计算...")
print(f"找到 {len(solutions)} 个解")

# 错误信息
import sys
print(f"错误: {e}", file=sys.stderr)
```

### 2. 调试信息 (开发时)
```python
def solve(self, numbers: List[int]) -> List[str]:
    print(f"DEBUG: Solving {numbers}")  # 开发时删除

    # ... 求解逻辑
```

### 3. 使用 logging (可选)
```python
import logging

logger = logging.getLogger(__name__)

def solve(self, numbers: List[int]) -> List[str]:
    logger.debug(f"Input numbers: {numbers}")
    logger.info(f"Found {len(solutions)} solutions")
```

---

## Common Mistakes

### ❌ 不要：生产代码中的 print 调试
```python
# Bad - 遗留的调试代码
def solve(numbers):
    print(f"DEBUG: {numbers}")  # 忘记删除
    return result
```

### ✅ 应该：使用 logger 或删除
```python
# Good - 使用 logger
logger.debug(f"Input numbers: {numbers}")

# 或直接删除
def solve(numbers):
    # 没有调试输出
    return result
```

### ❌ 不要：敏感信息日志
```python
# Bad - 泄露信息
logger.info(f"User password: {password}")
```

### ✅ 应该：脱敏后记录
```python
# Good
logger.info(f"User login attempt: {username}")
```

---

## For New Code

1. **CLI 脚本**: 使用 `print()` 和 `sys.stderr`
2. **库/模块**: 使用 `logging` 模块
3. **调试代码**: 开发完成后删除或用 `if DEBUG:`
