# TwentyFour 去重优化与分数类重构

## 1. 概述

基于 `twenty_four.py` 进行重构，解决当前实现的去重问题和浮点精度问题，并增加预计算表功能。

**当前问题：**
- 去重不完善，242种解法中存在大量等价表达式
- 使用 float 存在精度丢失风险
- 无预计算表，每次都重新计算

---

## 2. 功能需求

### 2.1 核心算法重构 - 两两合并法

**算法思路：**
1. 从当前集合中任选两个数 a 和 b
2. 对它们进行所有允许的运算（+, -, *, /），得到结果 c
3. 将 a 和 b 从集合中移除，放入 c，现在集合剩下 3 个数字
4. 递归执行上述过程，直到集合中只剩 1 个数字
5. 如果最后这个数字等于 24，则记录该路径

**优势：**
- 自然处理括号问题
- 比穷举所有括号组合更简洁
- 更容易实现去重

### 2.2 数字对象设计

每个数字封装为一个对象，记录：
- `value`: 数值（使用分数避免精度丢失）
- `expr`: 字符串表达式

```python
class Number:
    def __init__(self, value: Fraction, expr: str):
        self.value = value
        self.expr = expr
```

### 2.3 去重机制

#### 数值去重
交换律导致的重复（如 `1+2` 和 `2+1`）：
- 对两个 Number 执行运算时，将结果按数值排序
- 使用 `value`（分数）进行比较，而非字符串

#### 结构去重
算式表达上的重复：
- 使用 `expr` 字符串进行去重
- 不同的 expr 即使数值相同也保留

**去重策略：**
- 每次生成新的 Number 时，检查是否已存在相同 value + expr 的对象
- 可选：只保留"最简洁"的表达式形式

### 2.4 分数类 (Fraction)

避免浮点数精度丢失：

```python
class Fraction:
    def __init__(self, numerator: int, denominator: int):
        self.numerator = numerator
        self.denominator = denominator

    # 支持 +, -, *, / 运算
    # 支持比较运算 (==, <, >)
    # 支持转换为 float / string
```

**经典难点验证：**
```
8 / (3 - 8/3) = 24
用 float: 8 / (3 - 2.666...) = 8 / 0.333... ≈ 23.999...
用 Fraction: 8 / (3 - 8/3) = 8 / (1/3) = 24 ✓
```

### 2.5 位运算优化（可选）

使用 4 位位掩码表示数字使用状态：
- bit 0-3 分别代表数字 0-3 是否已被使用
- 避免频繁创建和销毁 List
- 提升递归效率

```python
USED_MASK = 0b1111  # 4 个数字都可用
# 移除数字 1: USED_MASK & ~ (1 << 1)
```

### 2.6 预计算表

**背景：**
- 4 个 1-10 的数字组合总数：10^4 = 10,000 种
- 去重后更少，可提前计算

**功能：**
- 启动时加载预计算表（JSON 格式）
- 用户输入时直接查表，毫秒级响应
- 后台任务：更新预计算表

**JSON 格式：**
```json
{
  "1,2,3,4": {
    "solvable": true,
    "solutions": ["((1+2)+3)*4", "1*(2*3*4)", ...]
  },
  "1,1,1,1": {
    "solvable": false,
    "solutions": []
  }
}
```

---

## 3. 技术方案

### 3.1 项目结构

```
TwentyFour/
├── twenty_four.py          # 主算法实现
├── fraction.py             # 分数类
├── number.py               # 数字对象类
├── solver.py               # 两两合并法求解器
├── deduplicator.py         # 去重逻辑
├── precompute.py           # 预计算表生成/加载
├── solutions.json          # 预计算表数据
└── cli.py                  # 命令行界面
```

### 3.2 核心接口

```python
# 分数运算
f1 = Fraction(1, 3)       # 1/3
f2 = Fraction(8, 3)       # 8/3
f3 = f1 + f2               # 3/3 = 1
f4 = Fraction(8, 1) / f1  # 8 / (1/3) = 24

# 数字对象
num = Number(Fraction(24, 1), "24")

# 求解
solver = Solver()
solutions = solver.solve([5, 5, 5, 1])
# 返回: [{"expr": "5*(5-1/5)", "value": Fraction(24,1)}, ...]

# 预计算
precomputed = PrecomputeTable()
precomputed.load("solutions.json")
result = precomputed.get([1, 2, 3, 4])
```

---

## 4. 验收标准

### 4.1 功能验收

| 用例 | 输入 | 预期输出 |
|------|------|----------|
| 基本求解 | 1 2 3 4 | 能求出24，显示所有解 |
| 经典难点 | 3 3 8 8 | 8/(3-8/3)=24，无精度问题 |
| 无解情况 | 1 1 1 1 | 正确判定无解 |
| 预计算表 | 任意组合 | 毫秒级查表响应 |

### 4.2 去重验收

| 输入 | 去重前 | 去重后（目标） |
|------|--------|----------------|
| 1 2 3 4 | ~242种 | < 20 种 |
| 5 5 5 1 | 2种 | 1-2 种 |
| 3 3 8 8 | 1种 | 1 种 |

### 4.3 精度验收

```python
# 经典难点必须精确为 24
result = Fraction(8, 1) / (Fraction(3, 1) - Fraction(8, 3))
assert result == Fraction(24, 1)  # 不能用 float 比较
```

---

## 5. 实现顺序

1. **Fraction 分数类** - 基础组件
2. **Number 数字对象** - 封装 value + expr
3. **Solver 两两合并法** - 核心算法
4. **Deduplicator 去重逻辑** - 数值 + 结构去重
5. **PrecomputeTable 预计算表** - JSON 存储与加载
6. **CLI 界面** - 交互式求解

---

## 6. 参考资料

- 算法原理：`.trellis/TwentyFour.md`
- 当前实现：`twenty_four.py`
