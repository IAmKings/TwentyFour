"""
预计算表 - 提前计算所有二十四点解法并缓存

使用方式：
1. 预计算：precompute_and_save(range_max=10)  # 普通模式
2. 预计算：precompute_and_save(range_max=13)  # 高难度模式
3. 加载：load(range_max=10)
4. 查询：get_solutions([1, 2, 3, 4], range_max=10)
"""

from typing import List, Dict
import json
import os

# 全局预计算缓存
_PRECOMPUTED_CACHE: Dict[str, List[str]] = {}
_CACHE_LOADED = False
_CACHE_RANGE = None


def _get_filename(range_max: int) -> str:
    """根据范围获取文件名"""
    if range_max == 10:
        return "solutions.json"
    else:
        return f"solutions_{range_max}.json"


def _get_key(numbers: List[int]) -> str:
    """获取规范化的 key（排序后的元组字符串）"""
    return ','.join(map(str, sorted(numbers)))


def precompute_and_save(range_max: int = 10) -> Dict[str, List[str]]:
    """
    预计算指定范围数字组合的解法

    Args:
        range_max: 数字范围上限 (1-N)

    Returns:
        预计算表字典
    """
    from solver import solve_24

    filepath = _get_filename(range_max)

    print(f"开始预计算 (范围 1-{range_max})...")
    table: Dict[str, List[str]] = {}

    total = range_max ** 4
    current = 0

    for a in range(1, range_max + 1):
        for b in range(1, range_max + 1):
            for c in range(1, range_max + 1):
                for d in range(1, range_max + 1):
                    current += 1
                    if current % 1000 == 0:
                        print(f"进度: {current}/{total}")

                    key = _get_key([a, b, c, d])
                    if key not in table:  # 避免重复计算
                        solutions = solve_24([a, b, c, d])
                        table[key] = solutions

    # 保存到文件
    print(f"保存预计算表到 {filepath}...")
    with open(filepath, 'w') as f:
        json.dump(table, f, indent=2)

    print(f"预计算完成！共 {len(table)} 种组合，有解 {sum(1 for v in table.values() if v)} 种")

    return table


def load(range_max: int = 10) -> Dict[str, List[str]]:
    """
    加载预计算表

    Args:
        range_max: 数字范围上限 (1-N)

    Returns:
        预计算表字典
    """
    global _PRECOMPUTED_CACHE, _CACHE_LOADED, _CACHE_RANGE

    filepath = _get_filename(range_max)

    if _CACHE_LOADED and _CACHE_RANGE == range_max:
        return _PRECOMPUTED_CACHE

    if os.path.exists(filepath):
        print(f"加载预计算表 from {filepath}...")
        with open(filepath, 'r') as f:
            _PRECOMPUTED_CACHE = json.load(f)
        _CACHE_LOADED = True
        _CACHE_RANGE = range_max
        print(f"加载完成！共 {len(_PRECOMPUTED_CACHE)} 种组合")
    else:
        print(f"预计算表不存在，正在生成...")
        _PRECOMPUTED_CACHE = precompute_and_save(range_max=range_max)
        _CACHE_LOADED = True
        _CACHE_RANGE = range_max

    return _PRECOMPUTED_CACHE


def get_solutions(numbers: List[int], range_max: int = 10) -> List[str]:
    """
    获取指定数字的所有解法

    Args:
        numbers: 4个数字
        range_max: 数字范围上限 (1-N)

    Returns:
        解法列表
    """
    global _CACHE_LOADED, _CACHE_RANGE

    # 如果缓存的 range 匹配，直接查表
    if _CACHE_LOADED and _CACHE_RANGE == range_max:
        key = _get_key(numbers)
        return _PRECOMPUTED_CACHE.get(key, [])

    # 尝试加载预计算表
    try:
        cache = load(range_max)
        key = _get_key(numbers)
        return cache.get(key, [])
    except Exception:
        # 如果预计算表加载失败，使用实时计算
        from solver import solve_24
        return solve_24(numbers)


def can_make_24(numbers: List[int], range_max: int = 10) -> bool:
    """快速检查是否有解"""
    return len(get_solutions(numbers, range_max)) > 0


def get_solution_count(range_max: int = 10) -> int:
    """获取有解的组合数"""
    global _CACHE_LOADED, _CACHE_RANGE

    if _CACHE_LOADED and _CACHE_RANGE == range_max:
        return sum(1 for v in _PRECOMPUTED_CACHE.values() if v)
    return 0


if __name__ == "__main__":
    import time

    # 生成普通模式预计算表
    start = time.time()
    precompute_and_save(range_max=10)
    elapsed = time.time() - start
    print(f"\n普通模式总耗时: {elapsed:.3f}秒")

    # 测试查询
    print("\n=== 查询测试 ===")
    print(f"[1,2,3,4]: {get_solutions([1, 2, 3, 4], 10)[:3]}...")
    print(f"[5,5,5,1]: {get_solutions([5, 5, 5, 1], 10)}")
    print(f"[3,3,8,8]: {get_solutions([3, 3, 8, 8], 10)}")
    print(f"[1,1,1,1]: {get_solutions([1, 1, 1, 1], 10)}")
