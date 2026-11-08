package com.eastercalculator.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eastercalculator.app.ui.components.CalculatorKeypad
import com.eastercalculator.app.ui.components.EggUnlockedDialog
import com.eastercalculator.app.ui.components.EmojiRainCanvas

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToCodex: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val totalScore by viewModel.eggRepository.totalScore.collectAsStateWithLifecycle()

    var showHintDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF13131F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E2E))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🐣", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalScore Puan",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E2E))
                        .clickable { showHintDialog = !showHintDialog }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Günün İpucu",
                            tint = Color(0xFF80D8FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Günün İpucu",
                            color = Color(0xFF80D8FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showHintDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E2838))
                        .border(1.dp, Color(0xFF80D8FF).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 Günlük ve Aylık Rastgele Gizemler",
                            color = Color(0xFF80D8FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bugünün Gizli Sayısı: ${viewModel.eggRepository.engine.currentDailySecret}\nBu sayıyı veya 42÷0 gibi gizli kuralları hesapla, emojileri dök!",
                            color = Color(0xFFEEEEEE),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = uiState.expression.ifEmpty { "0" },
                    color = Color(0xFF9E9E9E),
                    fontSize = if (uiState.expression.length > 14) 22.sp else 28.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiState.resultPreview,
                    color = if (uiState.resultPreview.startsWith("Hata")) Color(0xFFFF5252) else Color.White,
                    fontSize = if (uiState.resultPreview.length > 10) 36.sp else 48.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            CalculatorKeypad(
                onDigit = viewModel::onDigit,
                onOperator = viewModel::onOperator,
                onClear = viewModel::onClear,
                onDelete = viewModel::onDelete,
                onCalculate = viewModel::onCalculate,
                onToggleSign = viewModel::onToggleSign,
                onPercent = viewModel::onPercent,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        AnimatedVisibility(
            visible = uiState.activeRainEmoji != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            uiState.activeRainEmoji?.let { emoji ->
                EmojiRainCanvas(
                    emoji = emoji,
                    onFinished = viewModel::stopRain
                )
            }
        }

        uiState.triggeredEgg?.let { egg ->
            EggUnlockedDialog(
                egg = egg,
                isNewDiscovery = uiState.isNewDiscovery,
                unlockedFormula = uiState.triggeredFormula,
                onDismiss = viewModel::dismissCelebration,
                onViewCodex = {
                    viewModel.dismissCelebration()
                    onNavigateToCodex()
                }
            )
        }
    }
}
