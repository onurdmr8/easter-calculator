package com.eastercalculator.app.data

import android.util.Log
import com.eastercalculator.app.model.LeaderboardEntry
import com.eastercalculator.app.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardRepository {

    companion object {
        private const val TAG = "LeaderboardRepo"
        private const val COLLECTION_LEADERBOARD = "leaderboard"
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

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

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(baseGlobalPlayers)
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private var isFirestoreListenerAttached = false

    fun updateLeaderboardWithUser(
        currentUser: UserProfile,
        currentScore: Int,
        foundCount: Int,
        seasonId: String = "2026-11"
    ) {
        val userEntry = LeaderboardEntry(
            rank = 0,
            uid = currentUser.uid,
            username = currentUser.displayName + " (Sen)",
            avatarEmoji = currentUser.avatarEmoji,
            monthlyScore = currentScore,
            eggsFound = foundCount,
            isCurrentUser = true,
            badgeTitle = getBadgeTitle(currentScore, foundCount)
        )

        // 1. Instant local update
        val allEntries = (baseGlobalPlayers.filter { it.uid != currentUser.uid } + userEntry)
            .sortedByDescending { it.monthlyScore }
        _leaderboard.value = allEntries.mapIndexed { index, entry -> entry.copy(rank = index + 1) }

        // 2. Sync to Firestore (asynchronous cloud write)
        try {
            val docId = "${seasonId}_${currentUser.uid}"
            val data = hashMapOf(
                "uid" to currentUser.uid,
                "username" to currentUser.displayName,
                "avatarEmoji" to currentUser.avatarEmoji,
                "monthlyScore" to currentScore,
                "eggsFound" to foundCount,
                "isAnonymous" to currentUser.isAnonymous,
                "seasonId" to seasonId,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            firestore.collection(COLLECTION_LEADERBOARD).document(docId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Skor Firestore'a senkronize edildi: $currentScore")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Firestore senkronizasyon uyarısı: ${e.message}")
                }

            attachFirestoreListener(currentUser.uid, seasonId)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore updateLeaderboard çağrılamadı: ${e.message}")
        }
    }

    private fun attachFirestoreListener(currentUid: String, seasonId: String) {
        if (isFirestoreListenerAttached) return
        isFirestoreListenerAttached = true

        try {
            firestore.collection(COLLECTION_LEADERBOARD)
                .whereEqualTo("seasonId", seasonId)
                .orderBy("monthlyScore", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        Log.w(TAG, "Firestore leaderboard dinleyici uyarısı: ${error?.message}")
                        return@addSnapshotListener
                    }

                    val firestoreEntries = snapshot.documents.mapNotNull { doc ->
                        val uid = doc.getString("uid") ?: return@mapNotNull null
                        val username = doc.getString("username") ?: "Oyuncu"
                        val avatar = doc.getString("avatarEmoji") ?: "🐣"
                        val score = doc.getLong("monthlyScore")?.toInt() ?: 0
                        val count = doc.getLong("eggsFound")?.toInt() ?: 0
                        val isCurrent = (uid == currentUid)

                        LeaderboardEntry(
                            rank = 0,
                            uid = uid,
                            username = if (isCurrent) "$username (Sen)" else username,
                            avatarEmoji = avatar,
                            monthlyScore = score,
                            eggsFound = count,
                            isCurrentUser = isCurrent,
                            badgeTitle = getBadgeTitle(score, count)
                        )
                    }

                    val merged = (baseGlobalPlayers.filter { b -> firestoreEntries.none { f -> f.uid == b.uid } } + firestoreEntries)
                        .sortedByDescending { it.monthlyScore }
                        .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

                    _leaderboard.value = merged
                }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore dinleyici kurulamadı: ${e.message}")
        }
    }

    fun getBadgeTitle(score: Int, count: Int): String {
        return when {
            score >= 100000 -> "Tanrı Parçacığı Efendisi 🧬"
            score >= 30000 -> "Kuantum Hükümdarı ⚛️"
            score >= 15000 -> "Kozmik Büyükusta 🏆"
            score >= 10000 -> "Efsanevi Yumurta Avcısı 🐣"
            score >= 5000 -> "Kıdemli Sayı Kaşifi 🌟"
            score >= 2000 -> "Uzman Hesapçı 🚀"
            score >= 500 -> "Yetenekli Avcı 🔍"
            else -> "Çırak Matematikçi 🌱"
        }
    }
}
