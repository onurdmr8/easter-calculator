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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val activity = context as? android.app.Activity

                    // Kozmik Şans Katlayıcı (Rewarded Ad Boost)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (uiState.luckBoostRemaining > 0) Color(0xFF00E5FF).copy(alpha = 0.2f)
                                else Color(0xFF1E1E2E)
                            )
                            .border(
                                1.dp,
                                if (uiState.luckBoostRemaining > 0) Color(0xFF00E5FF) else Color(0xFF333348),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (activity != null) {
                                    com.eastercalculator.app.ads.AdManager.showRewardedAd(
                                        activity = activity,
                                        onRewardEarned = {
                                            viewModel.activateLuckBoost(charges = 15, multiplier = 5)
                                        }
                                    )
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.luckBoostRemaining > 0) "5x Şans (${uiState.luckBoostRemaining})" else "5x Şans (İzle)",
                                color = if (uiState.luckBoostRemaining > 0) Color(0xFF00E5FF) else Color(0xFF80D8FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E1E2E))
                            .clickable { showHintDialog = !showHintDialog }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Günün İpucu",
                                tint = Color(0xFF80D8FF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "İpucu",
                                color = Color(0xFF80D8FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = showHintDialog) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val activity = context as? android.app.Activity

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
                            text = "💡 Günlük Gizemler & Tanrı Parçacıkları",
                            color = Color(0xFF80D8FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bugünün Gizli Sayısı: ${viewModel.eggRepository.engine.currentDailySecret}\n⚛️ Tanrı Parçacıkları: Her '=' işleminde 20/1 (+50P) ile 100.000/1 (+100.000P) arasında tamamen rastgele oranlarla düşer! '5x Şans' ile olasılığı katlayabilirsin.",
                            color = Color(0xFFEEEEEE),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFF9800).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(8.dp))
                                .clickable {
                                    if (activity != null) {
                                        com.eastercalculator.app.ads.AdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                viewModel.unlockHintWithReward()
                                            }
                                        )
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🎬 Video İzle: Gizli Bir Easter Egg Formülü Öğren!",
                                color = Color(0xFFFFB74D),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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

        uiState.unlockedHintEgg?.let { egg ->
            androidx.compose.ui.window.Dialog(onDismissRequest = viewModel::dismissHintDialog) {
                androidx.compose.material3.Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = egg.emoji, fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🔓 Gizli Formül İpucu!",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = egg.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nasıl Bulunur: ${egg.triggerDescription}",
                            color = Color(0xFF80D8FF),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.Button(
                            onClick = viewModel::dismissHintDialog,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Anladım!", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
