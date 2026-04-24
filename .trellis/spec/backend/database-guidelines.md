# Data Storage Guidelines

> How data is stored and persisted in this project.

---

## Overview

This project uses **JSON files** for data persistence, not a traditional database.
No ORM or migration system needed.

---

## Data Files

| File | Purpose | Format |
|------|---------|--------|
| `solutions.json` | 预计算的所有解 | JSON dict |
| `solutions_13.json` | 扩展到1-13的解 | JSON dict |
| `unsolvable_10.txt` | 无解组合列表 | Plain text |
| `unsolvable_13.txt` | 扩展无解列表 | Plain text |

---

## Storage Patterns

### 1. JSON 文件读写
```python
import json
from pathlib import Path

def load_solutions(path: str) -> dict:
    """加载预计算的解"""
    with open(path, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_solutions(path: str, data: dict) -> None:
    """保存解到文件"""
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
```

### 2. 纯文本列表
```python
def load_unsolvable(path: str) -> Set[Tuple[int, ...]]:
    """加载无解组合"""
    unsolvable = set()
    with open(path, 'r') as f:
        for line in f:
            line = line.strip()
            if line:
                nums = tuple(map(int, line.split(',')))
                unsolvable.add(nums)
    return unsolvable
```

### 3. File Paths
```python
# 使用 pathlib
from pathlib import Path

DATA_DIR = Path(__file__).parent
SOLUTIONS_FILE = DATA_DIR / "solutions.json"
```

---

## Naming Conventions

| 类型 | 规则 | 示例 |
|------|------|------|
| JSON数据文件 | `snake_case.json` | `solutions_13.json` |
| 文本数据文件 | `snake_case.txt` | `unsolvable_10.txt` |

---

## Common Mistakes

### ❌ 不要：硬编码路径
```python
# Bad
with open('/Users/kings/data/solutions.json') as f:
```

### ✅ 应该：相对路径或配置
```python
# Good
DATA_DIR = Path(__file__).parent
with open(DATA_DIR / "solutions.json") as f:
```

### ❌ 不要：写入时无备份
```python
# Bad - 直接覆盖
with open(path, 'w') as f:
    f.write(new_content)
```

### ✅ 应该：先写临时文件再移动
```python
# Good - 原子写入
temp = path.with_suffix('.tmp')
temp.write_text(new_content)
temp.replace(path)
```
