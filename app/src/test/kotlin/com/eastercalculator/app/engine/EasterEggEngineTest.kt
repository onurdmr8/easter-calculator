package com.eastercalculator.app.engine

import com.eastercalculator.app.model.Rarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class EasterEggEngineTest {

    private lateinit var easterEggEngine: EasterEggEngine

    @Before
    fun setUp() {
        easterEggEngine = EasterEggEngine()
    }

    @Test
    fun testJackpotTrigger() {
        val egg = easterEggEngine.checkEasterEgg(
            expression = "700+77",
            numericResult = 777.0,
            isDivisionByZero = false
        )
        assertNotNull(egg)
        assertEquals("common_jackpot", egg?.id)
        assertEquals("🎰", egg?.emoji)
        assertEquals(Rarity.COMMON, egg?.rarity)
    }

    @Test
    fun testDivisionByZeroTriggersBlackHole() {
        val egg = easterEggEngine.checkEasterEgg(
            expression = "42÷0",
            numericResult = Double.POSITIVE_INFINITY,
            isDivisionByZero = true
        )
        assertNotNull(egg)
        assertEquals("epic_black_hole", egg?.id)
        assertEquals("🌌", egg?.emoji)
        assertEquals(Rarity.EPIC, egg?.rarity)
    }

    @Test
    fun testLeetHackerTrigger() {
        val egg = easterEggEngine.checkEasterEgg(
            expression = "1300+37",
            numericResult = 1337.0,
            isDivisionByZero = false
        )
        assertNotNull(egg)
        assertEquals("rare_leet_hacker", egg?.id)
        assertEquals("🤖", egg?.emoji)
    }

    @Test
    fun testDailySecretTrigger() {
        val dailyTarget = easterEggEngine.currentDailySecret.toDouble()
        val egg = easterEggEngine.checkEasterEgg(
            expression = "$dailyTarget",
            numericResult = dailyTarget,
            isDivisionByZero = false
        )
        assertNotNull(egg)
        assertEquals("rare_daily_secret", egg?.id)
        assertEquals("🎯", egg?.emoji)
    }

    @Test
    fun testGoldenEggTrigger() {
        val egg = easterEggEngine.checkEasterEgg(
            expression = "1000×1000",
            numericResult = 1000000.0,
            isDivisionByZero = false
        )
        assertNotNull(egg)
        assertEquals("mythic_golden_egg", egg?.id)
        assertEquals("🐣", egg?.emoji)
        assertEquals(Rarity.MYTHIC, egg?.rarity)
    }
}
