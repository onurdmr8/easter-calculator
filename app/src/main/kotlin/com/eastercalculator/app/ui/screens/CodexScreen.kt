package com.eastercalculator.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eastercalculator.app.model.EasterEgg
import com.eastercalculator.app.model.Rarity

@Composable
fun CodexScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val eggs by viewModel.eggRepository.eggs.collectAsStateWithLifecycle()
    val totalScore by viewModel.eggRepository.totalScore.collectAsStateWithLifecycle()
    val season by viewModel.eggRepository.season.collectAsStateWithLifecycle()

    var selectedRarity by remember { mutableStateOf<Rarity?>(null) }

    val unlockedCount = eggs.count { it.isUnlocked }
    val progressRatio = if (eggs.isNotEmpty()) unlockedCount.toFloat() / eggs.size else 0f

    val filteredEggs = if (selectedRarity == null) {
        eggs
    } else {
        eggs.filter { it.rarity == selectedRarity }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF13131F))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = season.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Ay Sonu Sıfırlanmasına: ${season.daysRemaining} Gün",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB0BEC5)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalScore Puan",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Keşif Başarısı",
                        color = Color(0xFFCCCCCC),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$unlockedCount / ${eggs.size} (${(progressRatio * 100).toInt()}%)",
                        color = Color(0xFF80D8FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFF9800),
                    trackColor = Color(0xFF2A2A3E),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            item {
                RarityFilterChip(
                    label = "Tümü (${eggs.size})",
                    color = Color.White,
                    isSelected = selectedRarity == null,
                    onClick = { selectedRarity = null }
                )
            }
            items(Rarity.entries.toTypedArray()) { rarity ->
                RarityFilterChip(
                    label = "${rarity.title} (${rarity.dropRateText})",
                    color = rarity.color,
                    isSelected = selectedRarity == rarity,
                    onClick = { selectedRarity = rarity }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredEggs, key = { it.id }) { egg ->
                CodexItemCard(egg = egg)
            }
        }
    }
}

@Composable
private fun RarityFilterChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) color.copy(alpha = 0.25f) else Color(0xFF1E1E2E))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) color else Color(0xFF333348),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) color else Color(0xFFAAAAAA),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CodexItemCard(egg: EasterEgg) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (egg.isUnlocked) 1.5.dp else 1.dp,
                color = if (egg.isUnlocked) egg.rarity.color.copy(alpha = 0.6f) else Color(0xFF262638),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (egg.isUnlocked) Color(0xFF1B1B2C) else Color(0xFF161622)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (egg.isUnlocked) egg.rarity.glowColor else Color(0xFF222233)),
                contentAlignment = Alignment.Center
            ) {
                if (egg.isUnlocked) {
                    Text(text = egg.emoji, fontSize = 28.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Kilitli",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayTitle = when {
                        egg.isUnlocked -> egg.title
                        egg.rarity == Rarity.GOD_PARTICLE -> "${egg.title} (Kilitli)"
                        else -> "??? (Kilitli)"
                    }
                    Text(
                        text = displayTitle,
                        fontWeight = FontWeight.Bold,
                        color = if (egg.isUnlocked) Color.White else Color(0xFF9E9E9E),
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(egg.rarity.color.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        val badgeLabel = if (egg.oneInChance != null) {
                            "${egg.oddsText} • +${egg.points}P"
                        } else {
                            "${egg.rarity.title} • +${egg.points}P"
                        }
                        Text(
                            text = badgeLabel,
                            color = egg.rarity.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (egg.isUnlocked) egg.description else "Gizem: ${egg.hint}",
                    color = if (egg.isUnlocked) Color(0xFFB0BEC5) else Color(0xFF78909C),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                if (egg.isUnlocked && egg.unlockedFormula != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Çözüm: ${egg.unlockedFormula}",
                        color = Color(0xFF80CBC4),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
