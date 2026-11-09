package com.eastercalculator.app.data

import android.content.Context
import android.content.SharedPreferences
import com.eastercalculator.app.engine.EasterEggEngine
import com.eastercalculator.app.model.EasterEgg
import com.eastercalculator.app.model.Season
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class EasterEggRepository(
    private val context: Context,
    val engine: EasterEggEngine = EasterEggEngine()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("easter_calc_prefs", Context.MODE_PRIVATE)

    private val _eggs = MutableStateFlow<List<EasterEgg>>(emptyList())
    val eggs: StateFlow<List<EasterEgg>> = _eggs.asStateFlow()

    private val _season = MutableStateFlow(calculateCurrentSeason())
    val season: StateFlow<Season> = _season.asStateFlow()

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    init {
        loadEggs()
    }

    private fun calculateCurrentSeason(): Season {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val daysLeft = (lastDay - currentDay).coerceAtLeast(1)

        val monthNames = arrayOf(
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )
        val name = "${monthNames[month - 1]} $year Sezonu"
        val id = String.format(java.util.Locale.US, "%04d-%02d", year, month)

        return Season(
            id = id,
            name = name,
            year = year,
            month = month,
            daysRemaining = daysLeft
        )
    }

    private fun loadEggs() {
        val baseEggs = engine.getAllEasterEggs()
        val seasonPrefix = "season_${_season.value.id}_"

        var scoreAccumulator = 0
        val updatedList = baseEggs.map { egg ->
            val isUnlocked = prefs.getBoolean("${seasonPrefix}egg_${egg.id}_unlocked", false)
            val unlockedAt = if (isUnlocked) prefs.getLong("${seasonPrefix}egg_${egg.id}_time", 0L) else null
            val formula = if (isUnlocked) prefs.getString("${seasonPrefix}egg_${egg.id}_formula", null) else null

            if (isUnlocked) {
                scoreAccumulator += egg.points
            }

            egg.copy(
                isUnlocked = isUnlocked,
                unlockedAt = unlockedAt,
                unlockedFormula = formula
            )
        }

        _eggs.value = updatedList
        _totalScore.value = scoreAccumulator
    }

    fun unlockEgg(eggId: String, formula: String): Boolean {
        val currentEggs = _eggs.value.toMutableList()
        val index = currentEggs.indexOfFirst { it.id == eggId }
        if (index == -1) return false

        val existing = currentEggs[index]
        if (existing.isUnlocked) {
            return false
        }

        val now = System.currentTimeMillis()
        val seasonPrefix = "season_${_season.value.id}_"

        prefs.edit()
            .putBoolean("${seasonPrefix}egg_${eggId}_unlocked", true)
            .putLong("${seasonPrefix}egg_${eggId}_time", now)
            .putString("${seasonPrefix}egg_${eggId}_formula", formula)
            .apply()

        val updatedEgg = existing.copy(
            isUnlocked = true,
            unlockedAt = now,
            unlockedFormula = formula
        )
        currentEggs[index] = updatedEgg
        _eggs.value = currentEggs

        val newScore = _totalScore.value + updatedEgg.points
        _totalScore.value = newScore

        return true
    }

    fun getUnlockedCount(): Int {
        return _eggs.value.count { it.isUnlocked }
    }

    fun getUnlockedEggIds(): Set<String> {
        return _eggs.value.filter { it.isUnlocked }.map { it.id }.toSet()
    }
}
