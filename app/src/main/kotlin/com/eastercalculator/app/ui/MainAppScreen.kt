package com.eastercalculator.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eastercalculator.app.ui.screens.CalculatorScreen
import com.eastercalculator.app.ui.screens.CalculatorViewModel
import com.eastercalculator.app.ui.screens.CodexScreen
import com.eastercalculator.app.ui.screens.LeaderboardScreen
import com.eastercalculator.app.ui.screens.ProfileScreen

enum class NavTab(val title: String, val icon: ImageVector) {
    CALCULATOR("Hesapla", Icons.Default.Calculate),
    CODEX("Koleksiyon", Icons.Default.MilitaryTech),
    LEADERBOARD("Sıralama", Icons.Default.EmojiEvents),
    PROFILE("Profil", Icons.Default.Person)
}

@Composable
fun MainAppScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(NavTab.CALCULATOR) }
    val eggs by viewModel.eggRepository.eggs.collectAsStateWithLifecycle()
    val unlockedCount = eggs.count { it.isUnlocked }

    val isAdsRemoved by viewModel.billingManager.isAdsRemoved.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            androidx.compose.foundation.layout.Column {
                if (!isAdsRemoved) {
                    com.eastercalculator.app.ui.components.AdBannerView()
                }
                NavigationBar(
                    containerColor = Color(0xFF161624),
                    tonalElevation = 8.dp
                ) {
                    NavTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                if (tab == NavTab.CODEX && unlockedCount > 0) {
                                    BadgedBox(badge = {
                                        Badge(
                                            containerColor = Color(0xFFFF9800),
                                            contentColor = Color.Black
                                        ) {
                                            Text("$unlockedCount", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }) {
                                        Icon(tab.icon, contentDescription = tab.title)
                                    }
                                } else {
                                    Icon(tab.icon, contentDescription = tab.title)
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF9800),
                                selectedTextColor = Color(0xFFFF9800),
                                unselectedIconColor = Color(0xFF90A4AE),
                                unselectedTextColor = Color(0xFF90A4AE),
                                indicatorColor = Color(0xFF232338)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF13131F))
        ) {
            Crossfade(targetState = selectedTab, label = "TabSwitch") { tab ->
                when (tab) {
                    NavTab.CALCULATOR -> CalculatorScreen(
                        viewModel = viewModel,
                        onNavigateToCodex = { selectedTab = NavTab.CODEX }
                    )
                    NavTab.CODEX -> CodexScreen(
                        viewModel = viewModel
                    )
                    NavTab.LEADERBOARD -> LeaderboardScreen(
                        viewModel = viewModel
                    )
                    NavTab.PROFILE -> ProfileScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
