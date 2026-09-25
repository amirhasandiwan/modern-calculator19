package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBasicCalculations() {
        assertEquals("4", CalculatorEngine.evaluate("2 + 2"))
        assertEquals("6", CalculatorEngine.evaluate("10 − 4"))
        assertEquals("24", CalculatorEngine.evaluate("6 × 4"))
        assertEquals("5", CalculatorEngine.evaluate("20 ÷ 4"))
    }

    @Test
    fun testOperatorPrecedence() {
        // 2 + 3 * 4 should be 14, not 20
        assertEquals("14", CalculatorEngine.evaluate("2 + 3 × 4"))
        // 10 - 6 / 2 should be 7
        assertEquals("7", CalculatorEngine.evaluate("10 − 6 ÷ 2"))
    }

    @Test
    fun testDivisionByZero() {
        assertEquals("Error", CalculatorEngine.evaluate("5 ÷ 0"))
        assertEquals("Error", CalculatorEngine.evaluate("100 ÷ 0"))
    }

    @Test
    fun testDecimalValidation() {
        assertTrue(CalculatorEngine.canAppendDecimal("12"))
        assertFalse(CalculatorEngine.canAppendDecimal("12.5"))
        assertTrue(CalculatorEngine.canAppendDecimal("12.5 + 3"))
        assertFalse(CalculatorEngine.canAppendDecimal("12.5 + 3.1"))
    }

    @Test
    fun testPercentage() {
        assertEquals("0.5", CalculatorEngine.evaluate("50%"))
        assertEquals("10", CalculatorEngine.evaluate("20 × 50%"))
    }

    @Test
    fun testBillingInitialItems() {
        val items = BillingDefaults.initialItems()
        assertEquals(10, items.size)
        val names = items.map { it.name }
        assertTrue(names.contains("Doodh"))
        assertTrue(names.contains("Cheeni"))
        assertTrue(names.contains("Chawal"))
        assertTrue(names.contains("Aata"))
        assertTrue(names.contains("Tel"))
        assertTrue(names.contains("Chai Patti"))
        assertTrue(names.contains("Aalu"))
        assertTrue(names.contains("Pyaz"))
        assertTrue(names.contains("Sabun"))
        assertTrue(names.contains("Namak"))

        val total = items.sumOf { it.price }
        assertTrue(total > 0)
        // 66 + 45 + 90 + 380 + 150 + 120 + 35 + 40 + 30 + 25 = 981.0
        assertEquals(981.0, total, 0.01)
    }
}
