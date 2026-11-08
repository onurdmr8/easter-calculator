package com.eastercalculator.app.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.eastercalculator.app.data.AuthRepository
import com.eastercalculator.app.data.EasterEggRepository
import com.eastercalculator.app.data.LeaderboardRepository
import com.eastercalculator.app.engine.CalculationResult
import com.eastercalculator.app.engine.CalculatorEngine
import com.eastercalculator.app.model.AuthState
import com.eastercalculator.app.model.EasterEgg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalculatorUiState(
    val expression: String = "",
    val resultPreview: String = "0",
    val isEvaluated: Boolean = false,
    val activeRainEmoji: String? = null,
    val triggeredEgg: EasterEgg? = null,
    val isNewDiscovery: Boolean = false,
    val triggeredFormula: String = "",
    val dailyHint: String = ""
)

class CalculatorViewModel(
    application: Application,
    val eggRepository: EasterEggRepository = EasterEggRepository(application),
    val authRepository: AuthRepository = AuthRepository(application),
    val leaderboardRepository: LeaderboardRepository = LeaderboardRepository()
) : AndroidViewModel(application) {

    private val engine = CalculatorEngine()

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            dailyHint = "Günün Gizli Sayısı: ${eggRepository.engine.currentDailySecret}"
        )
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        syncLeaderboard()
    }

    private fun syncLeaderboard() {
        val auth = authRepository.authState.value
        if (auth is AuthState.Authenticated) {
            leaderboardRepository.updateLeaderboardWithUser(
                currentUser = auth.user,
                currentScore = eggRepository.totalScore.value,
                foundCount = eggRepository.getUnlockedCount()
            )
        }
    }

    fun onDigit(digit: String) {
        val currentExpr = if (_uiState.value.isEvaluated) "" else _uiState.value.expression
        val newExpr = if (currentExpr == "0" && digit != ".") digit else currentExpr + digit
        val preview = previewResult(newExpr)

        _uiState.value = _uiState.value.copy(
            expression = newExpr,
            resultPreview = preview,
            isEvaluated = false
        )
    }

    fun onOperator(op: String) {
        val currentExpr = _uiState.value.expression
        if (currentExpr.isEmpty()) {
            if (op == "-") {
                _uiState.value = _uiState.value.copy(expression = "-", isEvaluated = false)
            }
            return
        }

        val lastChar = currentExpr.last().toString()
        val newExpr = if (lastChar in listOf("+", "-", "×", "÷", "%")) {
            currentExpr.dropLast(1) + op
        } else {
            currentExpr + op
        }

        _uiState.value = _uiState.value.copy(
            expression = newExpr,
            isEvaluated = false
        )
    }

    fun onClear() {
        _uiState.value = _uiState.value.copy(
            expression = "",
            resultPreview = "0",
            isEvaluated = false
        )
    }

    fun onDelete() {
        val currentExpr = _uiState.value.expression
        if (currentExpr.isNotEmpty()) {
            val newExpr = currentExpr.dropLast(1)
            val preview = previewResult(newExpr)
            _uiState.value = _uiState.value.copy(
                expression = newExpr,
                resultPreview = preview,
                isEvaluated = false
            )
        }
    }

    fun onToggleSign() {
        val currentExpr = _uiState.value.expression
        if (currentExpr.isEmpty()) return

        val newExpr = if (currentExpr.startsWith("-")) {
            currentExpr.drop(1)
        } else {
            "-$currentExpr"
        }
        val preview = previewResult(newExpr)
        _uiState.value = _uiState.value.copy(
            expression = newExpr,
            resultPreview = preview
        )
    }

    fun onPercent() {
        onOperator("%")
    }

    fun onCalculate() {
        val expr = _uiState.value.expression
        if (expr.isEmpty()) return

        val res = engine.evaluate(expr)
        _uiState.value = _uiState.value.copy(
            expression = expr,
            resultPreview = res.resultString,
            isEvaluated = true
        )

        checkEasterEggTrigger(expr, res)
    }

    private fun checkEasterEggTrigger(expression: String, result: CalculationResult) {
        val egg = eggRepository.engine.checkEasterEgg(
            expression = expression,
            numericResult = result.numericResult,
            isDivisionByZero = result.isDivisionByZero
        )

        if (egg != null) {
            val formula = "$expression = ${result.resultString}"
            val wasNewlyUnlocked = eggRepository.unlockEgg(egg.id, formula)

            _uiState.value = _uiState.value.copy(
                activeRainEmoji = egg.emoji,
                triggeredEgg = egg,
                isNewDiscovery = wasNewlyUnlocked,
                triggeredFormula = formula
            )

            syncLeaderboard()
        }
    }

    fun dismissCelebration() {
        _uiState.value = _uiState.value.copy(
            triggeredEgg = null
        )
    }

    fun stopRain() {
        _uiState.value = _uiState.value.copy(
            activeRainEmoji = null
        )
    }

    private fun previewResult(expr: String): String {
        if (expr.isEmpty()) return "0"
        return try {
            val res = engine.evaluate(expr)
            if (!res.isError) res.resultString else _uiState.value.resultPreview
        } catch (_: Exception) {
            _uiState.value.resultPreview
        }
    }
}
