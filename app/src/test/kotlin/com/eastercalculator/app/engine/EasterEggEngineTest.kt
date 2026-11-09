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

    @Test
    fun testGodParticlesHaveCorrectOddsAndPoints() {
        val godParticles = easterEggEngine.getAllEasterEggs()
            .filter { it.rarity == Rarity.GOD_PARTICLE }

        assertEquals(7, godParticles.size)

        val higgs = godParticles.first { it.id == "god_higgs_boson" }
        assertEquals(100000, higgs.points)
        assertEquals(100000, higgs.oneInChance)
        assertEquals("100.000/1 Oran", higgs.oddsText)

        val quantum = godParticles.first { it.id == "god_quantum_fluctuation" }
        assertEquals(50, quantum.points)
        assertEquals(20, quantum.oneInChance)
        assertEquals("20/1 Oran", quantum.oddsText)
    }

    @Test
    fun testGodParticleRollTriggersWhenRandomHitsZero() {
        val alwaysZeroRandom = object : kotlin.random.Random() {
            override fun nextBits(bitCount: Int): Int = 0
            override fun nextInt(until: Int): Int = 0
        }
        val deterministicGodEngine = EasterEggEngine(random = alwaysZeroRandom)

        // First roll hits the highest-point locked God Particle (Higgs Boson: 100,000 points)
        val firstRoll = deterministicGodEngine.checkEasterEgg(
            expression = "12+34",
            numericResult = 46.0,
            isDivisionByZero = false,
            unlockedEggIds = emptySet()
        )
        assertNotNull(firstRoll)
        assertEquals("god_higgs_boson", firstRoll?.id)
        assertEquals(Rarity.GOD_PARTICLE, firstRoll?.rarity)
        assertEquals(100000, firstRoll?.points)

        // When Higgs Boson is already unlocked, next roll hits the next tier (Singularity Core: 50,000 points)
        val secondRoll = deterministicGodEngine.checkEasterEgg(
            expression = "12+34",
            numericResult = 46.0,
            isDivisionByZero = false,
            unlockedEggIds = setOf("god_higgs_boson")
        )
        assertNotNull(secondRoll)
        assertEquals("god_singularity_core", secondRoll?.id)
        assertEquals(50000, secondRoll?.points)
    }
}
