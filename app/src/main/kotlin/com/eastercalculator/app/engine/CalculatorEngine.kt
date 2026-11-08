package com.eastercalculator.app.engine

import java.util.Stack
import kotlin.math.abs

data class CalculationResult(
    val expression: String,
    val resultString: String,
    val numericResult: Double?,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isDivisionByZero: Boolean = false
)

class CalculatorEngine {

    fun evaluate(expression: String): CalculationResult {
        if (expression.isBlank()) {
            return CalculationResult(expression = "", resultString = "0", numericResult = 0.0)
        }

        // Clean up expression symbols (× -> *, ÷ -> /)
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace(" ", "")

        return try {
            val result = evaluateExpression(sanitized)
            if (result.isInfinite()) {
                CalculationResult(
                    expression = expression,
                    resultString = "Sonsuz (∞)",
                    numericResult = Double.POSITIVE_INFINITY,
                    isDivisionByZero = true
                )
            } else if (result.isNaN()) {
                CalculationResult(
                    expression = expression,
                    resultString = "Hata",
                    numericResult = null,
                    isError = true,
                    errorMessage = "Tanımsız İşlem"
                )
            } else {
                val formatted = formatNumber(result)
                CalculationResult(
                    expression = expression,
                    resultString = formatted,
                    numericResult = result
                )
            }
        } catch (e: ArithmeticException) {
            CalculationResult(
                expression = expression,
                resultString = "Sonsuz (∞)",
                numericResult = Double.POSITIVE_INFINITY,
                isDivisionByZero = true
            )
        } catch (e: Exception) {
            CalculationResult(
                expression = expression,
                resultString = "Hata",
                numericResult = null,
                isError = true,
                errorMessage = e.message ?: "Geçersiz İfade"
            )
        }
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble() && abs(value) < 1e12) {
            value.toLong().toString()
        } else {
            // Trim unnecessary trailing zeros
            val str = String.format(java.util.Locale.US, "%.6f", value).trimEnd('0')
            if (str.endsWith(".")) str.dropLast(1) else str
        }
    }

    private fun evaluateExpression(expr: String): Double {
        val tokens = tokenize(expr)
        val rpn = toPostfix(tokens)
        return evaluateRpn(rpn)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }
                c == '+' || c == '*' || c == '/' || c == '%' || c == '(' || c == ')' -> {
                    tokens.add(c.toString())
                    i++
                }
                c == '-' -> {
                    // Check if it's unary minus (at start or after another operator / '(')
                    val isUnary = tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "%", "(")
                    if (isUnary) {
                        // Unary minus: read the following number or negate
                        i++
                        val sb = StringBuilder("-")
                        while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                            sb.append(expr[i])
                            i++
                        }
                        if (sb.length > 1) {
                            tokens.add(sb.toString())
                            continue
                        } else {
                            // Standalone '-' token if followed by parenthesis
                            tokens.add("-1")
                            tokens.add("*")
                            continue
                        }
                    } else {
                        tokens.add("-")
                        i++
                    }
                }
                else -> i++
            }
        }
        return tokens
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            else -> 0
        }
    }

    private fun toPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val ops = Stack<String>()

        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                output.add(token)
            } else if (token == "(") {
                ops.push(token)
            } else if (token == ")") {
                while (ops.isNotEmpty() && ops.peek() != "(") {
                    output.add(ops.pop())
                }
                if (ops.isNotEmpty() && ops.peek() == "(") {
                    ops.pop()
                }
            } else {
                while (ops.isNotEmpty() && precedence(ops.peek()) >= precedence(token)) {
                    output.add(ops.pop())
                }
                ops.push(token)
            }
        }
        while (ops.isNotEmpty()) {
            output.add(ops.pop())
        }
        return output
    }

    private fun evaluateRpn(rpn: List<String>): Double {
        val stack = Stack<Double>()
        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
            } else {
                if (stack.size < 2) throw IllegalArgumentException("Eksik terim")
                val b = stack.pop()
                val a = stack.pop()
                val res = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) Double.POSITIVE_INFINITY else a / b
                    }
                    "%" -> a % b
                    else -> throw UnsupportedOperationException("Bilinmeyen operatör: $token")
                }
                stack.push(res)
            }
        }
        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }
}
