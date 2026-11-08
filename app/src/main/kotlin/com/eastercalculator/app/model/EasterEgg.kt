package com.eastercalculator.app.model

import androidx.compose.ui.graphics.Color

enum class Rarity(
    val title: String,
    val points: Int,
    val dropRateText: String,
    val color: Color,
    val glowColor: Color
) {
    COMMON("Yaygın", 100, "%50", Color(0xFF4CAF50), Color(0x664CAF50)),
    UNCOMMON("Seyrek", 250, "%25", Color(0xFF2196F3), Color(0x662196F3)),
    RARE("Nadir", 600, "%15", Color(0xFF9C27B0), Color(0x669C27B0)),
    EPIC("Epik", 1500, "%7", Color(0xFFFF9800), Color(0x66FF9800)),
    MYTHIC("Efsanevi", 5000, "%3", Color(0xFFFFD700), Color(0x88FFD700))
}

data class EasterEgg(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val rarity: Rarity,
    val hint: String,
    val triggerDescription: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val unlockedFormula: String? = null
)

data class Season(
    val id: String,
    val name: String,
    val year: Int,
    val month: Int,
    val daysRemaining: Int
)
