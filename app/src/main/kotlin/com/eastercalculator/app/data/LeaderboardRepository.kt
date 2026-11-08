package com.eastercalculator.app.data

import com.eastercalculator.app.model.LeaderboardEntry
import com.eastercalculator.app.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardRepository {

    private val baseGlobalPlayers = listOf(
        LeaderboardEntry(1, "p_1", "MatrixMaster", "🐣", 16850, 19, false, "Kozmik Büyükusta"),
        LeaderboardEntry(2, "p_2", "AstroMath", "🚀", 14300, 16, false, "Galaksi Kaşifi"),
        LeaderboardEntry(3, "p_3", "Pythagoras_77", "🧙‍♂️", 12750, 15, false, "Sayı Büyücüsü"),
        LeaderboardEntry(4, "p_4", "CyberFox", "🤖", 9800, 13, false, "Siber Dedektif"),
        LeaderboardEntry(5, "p_5", "QuantumGamer", "🌌", 8650, 12, false, "Kuantum Yolcusu"),
        LeaderboardEntry(6, "p_6", "NebulaKnight", "⚔️", 7400, 10, false, "Nebula Muhafızı"),
        LeaderboardEntry(7, "p_7", "LunaSecret", "🌙", 6200, 9, false, "Ay Dedektifi"),
        LeaderboardEntry(8, "p_8", "LuckyClover99", "🍀", 5100, 8, false, "Şanslı Matematikçi"),
        LeaderboardEntry(9, "p_9", "PixelDragon", "🐉", 4350, 7, false, "Piksel Avcısı"),
        LeaderboardEntry(10, "p_10", "ZeroGravity", "🛸", 3800, 6, false, "Yerçekimsiz Avcı"),
        LeaderboardEntry(11, "p_11", "EchoAlpha", "⚡", 2900, 5, false, "Şimşek Çırağı"),
        LeaderboardEntry(12, "p_12", "NovaSeeker", "🌟", 1950, 4, false, "Yıldız Arayıcısı")
    )

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    fun updateLeaderboardWithUser(currentUser: UserProfile, currentScore: Int, foundCount: Int) {
        val userEntry = LeaderboardEntry(
            rank = 0,
            uid = currentUser.uid,
            username = currentUser.displayName + if (currentUser.isAnonymous) " (Sen)" else " (Sen)",
            avatarEmoji = currentUser.avatarEmoji,
            monthlyScore = currentScore,
            eggsFound = foundCount,
            isCurrentUser = true,
            badgeTitle = getBadgeTitle(currentScore, foundCount)
        )

        val allEntries = (baseGlobalPlayers.filter { it.uid != currentUser.uid } + userEntry)
            .sortedByDescending { it.monthlyScore }

        val ranked = allEntries.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }

        _leaderboard.value = ranked
    }

    private fun getBadgeTitle(score: Int, count: Int): String {
        return when {
            score >= 15000 -> "Kozmik Büyükusta 🏆"
            score >= 10000 -> "Efsanevi Yumurta Avcısı 🐣"
            score >= 5000 -> "Kıdemli Sayı Kaşifi 🌟"
            score >= 2000 -> "Uzman Hesapçı 🚀"
            score >= 500 -> "Yetenekli Avcı 🔍"
            else -> "Çırak Matematikçi 🌱"
        }
    }
}
