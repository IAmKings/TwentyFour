"""
预计算表 - 提前计算所有二十四点解法并缓存

使用方式：
1. 预计算（一次性）：precompute_and_save()
2. 加载预计算表：load("solutions.json")
3. 查询：get_solutions([1, 2, 3, 4])
"""

from typing import List, Dict, Tuple
import json
import os

# 全局预计算缓存
_PRECOMPUTED_CACHE: Dict[str, List[str]] = {}
_CACHE_LOADED = False


def _get_key(numbers: List[int]) -> str:
    """获取规范化的 key（排序后的元组字符串）"""
    return ','.join(map(str, sorted(numbers)))


def precompute_and_save(filepath: str = "solutions.json") -> Dict[str, List[str]]:
    """
    预计算所有 1-10 数字组合的解法

    Returns:
        预计算表字典
    """
    from solver import solve_24

    print("开始预计算...")
    table: Dict[str, List[str]] = {}

    total = 10 * 10 * 10 * 10
    current = 0

    for a in range(1, 11):
        for b in range(1, 11):
            for c in range(1, 11):
                for d in range(1, 11):
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


def load(filepath: str = "solutions.json") -> Dict[str, List[str]]:
    """
    加载预计算表

    Returns:
        预计算表字典
    """
    global _PRECOMPUTED_CACHE, _CACHE_LOADED

    if _CACHE_LOADED:
        return _PRECOMPUTED_CACHE

    if os.path.exists(filepath):
        print(f"加载预计算表 from {filepath}...")
        with open(filepath, 'r') as f:
            _PRECOMPUTED_CACHE = json.load(f)
        _CACHE_LOADED = True
        print(f"加载完成！共 {len(_PRECOMPUTED_CACHE)} 种组合")
    else:
        print(f"预计算表不存在，正在生成...")
        _PRECOMPUTED_CACHE = precompute_and_save(filepath)
        _CACHE_LOADED = True

    return _PRECOMPUTED_CACHE


def get_solutions(numbers: List[int]) -> List[str]:
    """
    获取指定数字的所有解法

    如果预计算表已加载，直接查表
    否则使用实时计算
    """
    global _CACHE_LOADED

    if _CACHE_LOADED:
        key = _get_key(numbers)
        return _PRECOMPUTED_CACHE.get(key, [])
    else:
        from solver import solve_24
        return solve_24(numbers)


def can_make_24(numbers: List[int]) -> bool:
    """快速检查是否有解"""
    return len(get_solutions(numbers)) > 0


def get_solution_count() -> int:
    """获取有解的组合数"""
    if _CACHE_LOADED:
        return sum(1 for v in _PRECOMPUTED_CACHE.values() if v)
    return 0


if __name__ == "__main__":
    import time

    # 首次使用：生成预计算表
    start = time.time()
    precompute_and_save()
    elapsed = time.time() - start
    print(f"\n总耗时: {elapsed:.3f}秒")

    # 测试查询
    print("\n=== 查询测试 ===")
    print(f"[1,2,3,4]: {get_solutions([1, 2, 3, 4])[:5]}...")
    print(f"[5,5,5,1]: {get_solutions([5, 5, 5, 1])}")
    print(f"[3,3,8,8]: {get_solutions([3, 3, 8, 8])}")
    print(f"[1,1,1,1]: {get_solutions([1, 1, 1, 1])}")
