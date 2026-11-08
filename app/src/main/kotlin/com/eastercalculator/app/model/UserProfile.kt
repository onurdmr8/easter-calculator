package com.eastercalculator.app.model

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String?,
    val avatarEmoji: String,
    val isAnonymous: Boolean = false,
    val monthlyScore: Int = 0,
    val totalEggsFound: Int = 0,
    val rank: Int = 0
)

data class LeaderboardEntry(
    val rank: Int,
    val uid: String,
    val username: String,
    val avatarEmoji: String,
    val monthlyScore: Int,
    val eggsFound: Int,
    val isCurrentUser: Boolean = false,
    val badgeTitle: String = "Çırak Matematikçi"
)

sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(val user: UserProfile) : AuthState
    data object Unauthenticated : AuthState
}
