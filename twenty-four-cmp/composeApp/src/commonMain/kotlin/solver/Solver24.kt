package solver

/**
 * 24点求解器 - 回溯算法
 * 从4个数字中通过 + - * / 和括号组合得到24
 */
object Solver24 {

    private const val TARGET = 24.0
    private const val EPSILON = 1e-9

    private data class ExprItem(
        val value: Double,
        val expression: String
    )

    /**
     * 求解24点，返回一个可行表达式（如 "(A+5)*(7-4)"）
     * 如果无解返回 null
     */
    fun solve(numbers: List<Int>): String? {
        val items = numbers.map { n ->
            ExprItem(
                value = n.toDouble(),
                expression = displayLabel(n)
            )
        }
        return solveRecursive(items)?.expression
    }

    /**
     * 生成一个保证有解的4个数字组合
     */
    fun generateSolvable(maxNumber: Int = 13): List<Int> {
        repeat(8000) {
            val ns = List(4) { (1..maxNumber).random() }
            if (hasSolution(ns)) return ns
        }
        // 保底
        return listOf(1, 2, 3, 4)
    }

    /**
     * 判断是否有解
     */
    fun hasSolution(numbers: List<Int>): Boolean {
        val items = numbers.map { n ->
            ExprItem(value = n.toDouble(), expression = displayLabel(n))
        }
        return solveRecursive(items) != null
    }

    private fun solveRecursive(items: List<ExprItem>): ExprItem? {
        if (items.size == 1) {
            return if (kotlin.math.abs(items[0].value - TARGET) < EPSILON) {
                items[0]
            } else {
                null
            }
        }

        for (i in items.indices) {
            for (j in items.indices) {
                if (i == j) continue

                val rest = items.filterIndexed { index, _ -> index != i && index != j }
                val a = items[i]
                val b = items[j]

                val operations = listOf('+', '-', '*')
                val allOps = if (kotlin.math.abs(b.value) > EPSILON) {
                    operations + '/'
                } else {
                    operations
                }

                for (op in allOps) {
                    val newValue = when (op) {
                        '+' -> a.value + b.value
                        '-' -> a.value - b.value
                        '*' -> a.value * b.value
                        '/' -> a.value / b.value
                        else -> throw IllegalStateException()
                    }

                    val needParenA = needParentheses(a, op, isLeft = true)
                    val needParenB = needParentheses(b, op, isLeft = false)

                    val exprA = if (needParenA) "(${a.expression})" else a.expression
                    val exprB = if (needParenB) "(${b.expression})" else b.expression

                    val newExpr = "$exprA$op$exprB"
                    val result = solveRecursive(rest + ExprItem(newValue, newExpr))
                    if (result != null) return result
                }
            }
        }
        return null
    }

    /**
     * 判断是否需要加括号
     */
    private fun needParentheses(item: ExprItem, op: Char, isLeft: Boolean): Boolean {
        // 高优先级运算符内部有低优先级运算时需要括号
        // 例如: (a+b)*c 中 a+b 需要括号
        if (op == '*' || op == '/') {
            // 检查表达式中是否包含 + 或 - （且不是被括号包围的整体）
            val expr = item.expression
            if (containsLowPrecedenceOutsideParens(expr)) {
                return true
            }
        }
        // 减法/除法时右边需要括号（因为不满足交换律）
        if (!isLeft && (op == '-' || op == '/')) {
            val expr = item.expression
            if (containsOperatorOutsideParens(expr)) {
                return true
            }
        }
        return false
    }

    private fun containsLowPrecedenceOutsideParens(expr: String): Boolean {
        var depth = 0
        for (c in expr) {
            when (c) {
                '(' -> depth++
                ')' -> depth--
                '+', '-' -> if (depth == 0) return true
            }
        }
        return false
    }

    private fun containsOperatorOutsideParens(expr: String): Boolean {
        var depth = 0
        for (c in expr) {
            when (c) {
                '(' -> depth++
                ')' -> depth--
                '+', '-', '*', '/' -> if (depth == 0) return true
            }
        }
        return false
    }

    /**
     * 数字显示标签：1->A, 11->J, 12->Q, 13->K
     */
    fun displayLabel(n: Int): String = when (n) {
        1 -> "A"
        11 -> "J"
        12 -> "Q"
        13 -> "K"
        else -> n.toString()
    }

    /**
     * 解析输入中的 A/J/Q/K 为数字
     */
    fun parseInput(input: String): String {
        return input
            .replace(Regex("[Aa]"), "1")
            .replace(Regex("[Jj]"), "11")
            .replace(Regex("[Qq]"), "12")
            .replace(Regex("[Kk]"), "13")
    }
}
