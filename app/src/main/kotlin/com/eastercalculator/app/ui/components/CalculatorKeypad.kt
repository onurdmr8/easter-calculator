package com.eastercalculator.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    DIGIT,
    OPERATOR,
    ACTION,
    EQUALS
}

data class CalcButtonSpec(
    val label: String,
    val type: ButtonType,
    val onClick: () -> Unit
)

@Composable
fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onCalculate: () -> Unit,
    onToggleSign: () -> Unit,
    onPercent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonRows: List<List<CalcButtonSpec>> = listOf(
        listOf(
            CalcButtonSpec("AC", ButtonType.ACTION, onClear),
            CalcButtonSpec("±", ButtonType.ACTION, onToggleSign),
            CalcButtonSpec("%", ButtonType.ACTION, onPercent),
            CalcButtonSpec("÷", ButtonType.OPERATOR) { onOperator("÷") }
        ),
        listOf(
            CalcButtonSpec("7", ButtonType.DIGIT) { onDigit("7") },
            CalcButtonSpec("8", ButtonType.DIGIT) { onDigit("8") },
            CalcButtonSpec("9", ButtonType.DIGIT) { onDigit("9") },
            CalcButtonSpec("×", ButtonType.OPERATOR) { onOperator("×") }
        ),
        listOf(
            CalcButtonSpec("4", ButtonType.DIGIT) { onDigit("4") },
            CalcButtonSpec("5", ButtonType.DIGIT) { onDigit("5") },
            CalcButtonSpec("6", ButtonType.DIGIT) { onDigit("6") },
            CalcButtonSpec("-", ButtonType.OPERATOR) { onOperator("-") }
        ),
        listOf(
            CalcButtonSpec("1", ButtonType.DIGIT) { onDigit("1") },
            CalcButtonSpec("2", ButtonType.DIGIT) { onDigit("2") },
            CalcButtonSpec("3", ButtonType.DIGIT) { onDigit("3") },
            CalcButtonSpec("+", ButtonType.OPERATOR) { onOperator("+") }
        ),
        listOf(
            CalcButtonSpec(".", ButtonType.DIGIT) { onDigit(".") },
            CalcButtonSpec("0", ButtonType.DIGIT) { onDigit("0") },
            CalcButtonSpec("⌫", ButtonType.ACTION, onDelete),
            CalcButtonSpec("=", ButtonType.EQUALS, onCalculate)
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { spec ->
                    CalcKey(
                        spec = spec,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalcKey(
    spec: CalcButtonSpec,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, fontSize) = when (spec.type) {
        ButtonType.DIGIT -> Triple(Color(0xFF262636), Color.White, 24.sp)
        ButtonType.OPERATOR -> Triple(Color(0xFF3F51B5), Color(0xFF80D8FF), 26.sp)
        ButtonType.ACTION -> Triple(Color(0xFF323246), Color(0xFFFFB74D), 20.sp)
        ButtonType.EQUALS -> Triple(Color(0xFFFF9800), Color.White, 28.sp)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = spec.onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = spec.label,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (spec.type == ButtonType.EQUALS || spec.type == ButtonType.OPERATOR) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}
