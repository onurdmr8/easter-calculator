package com.eastercalculator.app.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    @Test
    fun testSimpleAddition() {
        val result = engine.evaluate("2+3")
        assertEquals("5", result.resultString)
        assertEquals(5.0, result.numericResult!!, 0.0001)
    }

    @Test
    fun testOperatorPrecedence() {
        val result = engine.evaluate("2+3×4")
        assertEquals("14", result.resultString)
        assertEquals(14.0, result.numericResult!!, 0.0001)
    }

    @Test
    fun testParenthesis() {
        val result = engine.evaluate("(2+3)×4")
        assertEquals("20", result.resultString)
        assertEquals(20.0, result.numericResult!!, 0.0001)
    }

    @Test
    fun testDivisionByZeroTriggersInfinity() {
        val result = engine.evaluate("42÷0")
        assertTrue(result.isDivisionByZero)
        assertTrue(result.numericResult!!.isInfinite())
    }

    @Test
    fun testDecimalOperations() {
        val result = engine.evaluate("10÷4")
        assertEquals("2.5", result.resultString)
        assertEquals(2.5, result.numericResult!!, 0.0001)
    }
}
