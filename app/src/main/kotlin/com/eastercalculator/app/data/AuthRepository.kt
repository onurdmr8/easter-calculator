package com.eastercalculator.app.data

import android.content.Context
import android.content.SharedPreferences
import com.eastercalculator.app.model.AuthState
import com.eastercalculator.app.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("easter_calc_auth", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val uid = prefs.getString("user_uid", null)
        if (uid != null) {
            val displayName = prefs.getString("user_name", "Gizli Avcı") ?: "Gizli Avcı"
            val email = prefs.getString("user_email", null)
            val avatarEmoji = prefs.getString("user_avatar", "🐣") ?: "🐣"
            val isAnon = prefs.getBoolean("user_is_anon", false)

            _authState.value = AuthState.Authenticated(
                UserProfile(
                    uid = uid,
                    displayName = displayName,
                    email = email,
                    avatarEmoji = avatarEmoji,
                    isAnonymous = isAnon
                )
            )
        } else {
            signInAsGuest()
        }
    }

    fun signInAsGuest(): UserProfile {
        val guestUid = "guest_" + UUID.randomUUID().toString().take(8)
        val guestName = "Misafir Avcı #" + (1000..9999).random()
        val defaultAvatar = listOf("🐣", "🚀", "💎", "🤖", "🍕", "🧙‍♂️").random()

        prefs.edit()
            .putString("user_uid", guestUid)
            .putString("user_name", guestName)
            .putString("user_email", null)
            .putString("user_avatar", defaultAvatar)
            .putBoolean("user_is_anon", true)
            .apply()

        val profile = UserProfile(
            uid = guestUid,
            displayName = guestName,
            email = null,
            avatarEmoji = defaultAvatar,
            isAnonymous = true
        )
        _authState.value = AuthState.Authenticated(profile)
        return profile
    }

    fun signInWithGoogle(idTokenOrEmail: String, displayName: String): UserProfile {
        val uid = "google_" + UUID.nameUUIDFromBytes(idTokenOrEmail.toByteArray()).toString().take(10)
        val avatar = "🌟"

        prefs.edit()
            .putString("user_uid", uid)
            .putString("user_name", displayName)
            .putString("user_email", idTokenOrEmail)
            .putString("user_avatar", avatar)
            .putBoolean("user_is_anon", false)
            .apply()

        val profile = UserProfile(
            uid = uid,
            displayName = displayName,
            email = idTokenOrEmail,
            avatarEmoji = avatar,
            isAnonymous = false
        )
        _authState.value = AuthState.Authenticated(profile)
        return profile
    }

    fun registerWithEmail(name: String, email: String): UserProfile {
        val uid = "user_" + UUID.randomUUID().toString().take(10)
        val avatar = "🏆"

        prefs.edit()
            .putString("user_uid", uid)
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("user_avatar", avatar)
            .putBoolean("user_is_anon", false)
            .apply()

        val profile = UserProfile(
            uid = uid,
            displayName = name,
            email = email,
            avatarEmoji = avatar,
            isAnonymous = false
        )
        _authState.value = AuthState.Authenticated(profile)
        return profile
    }

    fun updateProfile(name: String, avatar: String) {
        val current = (_authState.value as? AuthState.Authenticated)?.user ?: return
        val updated = current.copy(displayName = name, avatarEmoji = avatar)

        prefs.edit()
            .putString("user_name", name)
            .putString("user_avatar", avatar)
            .apply()

        _authState.value = AuthState.Authenticated(updated)
    }

    fun signOut() {
        prefs.edit().clear().apply()
        signInAsGuest()
    }
}
