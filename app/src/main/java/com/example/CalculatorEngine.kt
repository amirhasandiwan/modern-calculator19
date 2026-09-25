package com.example

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object CalculatorEngine {

    /**
     * Evaluates a math expression string.
     * Supports operators +, -, × (*), ÷ (/), %
     * Handles division by zero gracefully by returning "Error".
     */
    fun evaluate(rawExpression: String): String {
        if (rawExpression.isBlank()) return ""

        // Normalize operators
        var sanitized = rawExpression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")

        // If expression ends with an operator other than %, strip trailing operator for evaluation
        while (sanitized.isNotEmpty() && isTrailingStrippableOperator(sanitized.last())) {
            sanitized = sanitized.dropLast(1)
        }

        if (sanitized.isEmpty()) return ""

        return try {
            val result = evaluateExpression(sanitized)
            formatNumber(result)
        } catch (e: ArithmeticException) {
            "Error"
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun isOperatorChar(c: Char): Boolean = c == '+' || c == '-' || c == '*' || c == '/' || c == '%'

    private fun isTrailingStrippableOperator(c: Char): Boolean = c == '+' || c == '-' || c == '*' || c == '/'

    /**
     * Tokenizes and evaluates expression respecting operator precedence:
     * 1. Unary % (if followed by operator or end) or modulo
     * 2. *, /
     * 3. +, -
     */
    private fun evaluateExpression(expr: String): BigDecimal {
        val tokens = tokenize(expr)
        if (tokens.isEmpty()) return BigDecimal.ZERO

        // Pass 1: Handle unary % (convert X% to X / 100)
        val afterPercent = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "%") {
                if (afterPercent.isNotEmpty() && !isOperator(afterPercent.last())) {
                    val prev = BigDecimal(afterPercent.removeAt(afterPercent.size - 1))
                    val percentVal = prev.divide(BigDecimal("100"), MathContext.DECIMAL128)
                    afterPercent.add(percentVal.toPlainString())
                }
            } else {
                afterPercent.add(token)
            }
            i++
        }

        if (afterPercent.isEmpty()) return BigDecimal.ZERO

        // Pass 2: Handle * and /
        val afterMulDiv = mutableListOf<String>()
        var j = 0
        while (j < afterPercent.size) {
            val token = afterPercent[j]
            if (token == "*" || token == "/") {
                if (afterMulDiv.isEmpty() || j + 1 >= afterPercent.size) {
                    throw IllegalArgumentException("Malformed expression")
                }
                val left = BigDecimal(afterMulDiv.removeAt(afterMulDiv.size - 1))
                val right = BigDecimal(afterPercent[j + 1])
                val res = if (token == "*") {
                    left.multiply(right, MathContext.DECIMAL128)
                } else {
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        throw ArithmeticException("Division by zero")
                    }
                    left.divide(right, 10, RoundingMode.HALF_UP)
                }
                afterMulDiv.add(res.toPlainString())
                j += 2
            } else {
                afterMulDiv.add(token)
                j++
            }
        }

        // Pass 3: Handle + and -
        if (afterMulDiv.isEmpty()) return BigDecimal.ZERO
        var total = BigDecimal(afterMulDiv[0])
        var k = 1
        while (k < afterMulDiv.size) {
            val op = afterMulDiv[k]
            if (k + 1 >= afterMulDiv.size) break
            val nextVal = BigDecimal(afterMulDiv[k + 1])
            total = when (op) {
                "+" -> total.add(nextVal, MathContext.DECIMAL128)
                "-" -> total.subtract(nextVal, MathContext.DECIMAL128)
                else -> total
            }
            k += 2
        }

        return total
    }

    private fun isOperator(token: String): Boolean =
        token == "+" || token == "-" || token == "*" || token == "/" || token == "%"

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if (c.isDigit() || c == '.') {
                currentNumber.append(c)
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                // Check for leading or unary minus
                if (c == '-' && currentNumber.isEmpty() && (tokens.isEmpty() || isOperator(tokens.last()))) {
                    currentNumber.append('-')
                } else {
                    if (currentNumber.isNotEmpty()) {
                        tokens.add(currentNumber.toString())
                        currentNumber = StringBuilder()
                    }
                    tokens.add(c.toString())
                }
            }
            i++
        }

        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }

        return tokens
    }

    fun formatNumber(number: BigDecimal): String {
        // Strip trailing zeros after decimal point
        val stripped = number.stripTrailingZeros()
        return stripped.toPlainString()
    }

    /**
     * Checks whether a decimal point can be safely appended to the current expression.
     * Looks at the last number token in the expression.
     */
    fun canAppendDecimal(expression: String): Boolean {
        if (expression.isEmpty()) return true
        val lastChar = expression.last()
        if (isOperatorChar(lastChar)) return true

        // Find current number segment going backward
        var index = expression.length - 1
        while (index >= 0 && !isOperatorChar(expression[index])) {
            if (expression[index] == '.') return false
            index--
        }
        return true
    }
}
