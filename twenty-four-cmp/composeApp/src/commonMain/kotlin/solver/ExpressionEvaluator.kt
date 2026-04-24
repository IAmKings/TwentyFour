package solver

/**
 * 表达式求值和验证
 */
object ExpressionEvaluator {

    sealed class EvalResult {
        data class Success(val value: Double) : EvalResult()
        data class Error(val message: String) : EvalResult()
    }

    /**
     * 验证并求值用户输入的表达式
     */
    fun evaluate(expression: String, numbers: List<Int>): EvalResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return EvalResult.Error("请输入算式")
        }

        // 检查非法字符
        if (!Regex("^[\\d+\\-*/().\\sAJQKajqk]+$").matches(trimmed)) {
            return EvalResult.Error("包含非法字符")
        }

        // 替换 A/J/Q/K
        val normalized = Solver24.parseInput(trimmed)

        // 验证使用了正确的4张牌
        val usedNumbers = Regex("\\d+").findAll(normalized)
            .map { it.value.toInt() }
            .sorted()
            .toList()

        val sortedNumbers = numbers.sorted()

        if (usedNumbers.size != 4 || usedNumbers != sortedNumbers) {
            return EvalResult.Error("必须使用给出的四张牌")
        }

        // 安全求值
        return try {
            val result = safeEvaluate(normalized)
            if (result.isFinite()) {
                EvalResult.Success(result)
            } else {
                EvalResult.Error("算式无效")
            }
        } catch (e: Exception) {
            EvalResult.Error("算式格式错误")
        }
    }

    /**
     * 安全求值数学表达式（只支持 + - * / 和括号）
     */
    private fun safeEvaluate(expr: String): Double {
        val tokens = tokenize(expr.replace(" ", ""))
        val parser = ExprParser(tokens)
        val result = parser.parseExpression()
        if (parser.hasMore()) {
            throw IllegalArgumentException("表达式解析不完整")
        }
        return result
    }

    /**
     * 表达式解析器
     */
    private class ExprParser(private val tokens: List<String>) {
        private var pos = 0

        fun hasMore(): Boolean = pos < tokens.size

        private fun peek(): String? = tokens.getOrNull(pos)
        private fun consume(): String = tokens[pos++]

        fun parseExpression(): Double {
            var value = parseTerm()
            while (peek() == "+" || peek() == "-") {
                val op = consume()
                val right = parseTerm()
                value = if (op == "+") value + right else value - right
            }
            return value
        }

        fun parseTerm(): Double {
            var value = parseFactor()
            while (peek() == "*" || peek() == "/") {
                val op = consume()
                val right = parseFactor()
                if (op == "*") {
                    value *= right
                } else {
                    if (right == 0.0) throw ArithmeticException("除零")
                    value /= right
                }
            }
            return value
        }

        fun parseFactor(): Double {
            return when (val token = peek()) {
                "(" -> {
                    consume() // (
                    val value = parseExpression()
                    if (peek() != ")") throw IllegalArgumentException("缺少右括号")
                    consume() // )
                    value
                }
                null -> throw IllegalArgumentException("意外的表达式结束")
                else -> {
                    if (token.all { it.isDigit() }) {
                        consume().toDouble()
                    } else {
                        throw IllegalArgumentException("意外的token: $token")
                    }
                }
            }
        }
    }

    /**
     * 将表达式字符串分词
     */
    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && expr[i].isDigit()) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                c in "+-*/()" -> {
                    tokens.add(c.toString())
                    i++
                }
                else -> throw IllegalArgumentException("非法字符: $c")
            }
        }
        return tokens
    }
}
