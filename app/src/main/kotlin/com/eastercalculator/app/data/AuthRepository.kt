package com.eastercalculator.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.eastercalculator.app.model.AuthState
import com.eastercalculator.app.model.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthRepository(private val context: Context) {

    companion object {
        private const val TAG = "AuthRepository"
        private const val PREFS_NAME = "easter_calc_auth"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        try {
            val currentFbUser = auth.currentUser
            if (currentFbUser != null) {
                emitUserFromFirebase(currentFbUser)
            } else {
                signInAsGuest()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth başlatılamadı, yerel misafir modu devrede: ${e.message}")
            loadLocalUserFallback()
        }
    }

    private fun emitUserFromFirebase(fbUser: com.google.firebase.auth.FirebaseUser) {
        val savedAvatar = prefs.getString("user_avatar", null) ?: "🐣"
        val savedName = prefs.getString("user_name", null)
            ?: fbUser.displayName
            ?: if (fbUser.isAnonymous) "Misafir Avcı #${fbUser.uid.take(4)}" else "Avcı"

        val profile = UserProfile(
            uid = fbUser.uid,
            displayName = savedName,
            email = fbUser.email,
            avatarEmoji = savedAvatar,
            isAnonymous = fbUser.isAnonymous
        )

        prefs.edit()
            .putString("user_uid", fbUser.uid)
            .putString("user_name", savedName)
            .putString("user_email", fbUser.email)
            .putString("user_avatar", savedAvatar)
            .putBoolean("user_is_anon", fbUser.isAnonymous)
            .apply()

        _authState.value = AuthState.Authenticated(profile)
    }

    fun signInAsGuest(): UserProfile {
        try {
            auth.signInAnonymously().addOnSuccessListener { authResult ->
                authResult.user?.let { emitUserFromFirebase(it) }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Firebase anonim giriş hatası: ${e.message}")
                loadLocalUserFallback()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase signInAnonymously çağrılamadı: ${e.message}")
            loadLocalUserFallback()
        }

        return (_authState.value as? AuthState.Authenticated)?.user ?: loadLocalUserFallback()
    }

    private fun loadLocalUserFallback(): UserProfile {
        val uid = prefs.getString("user_uid", null) ?: ("guest_" + UUID.randomUUID().toString().take(8))
        val name = prefs.getString("user_name", "Misafir Avcı #" + (1000..9999).random()) ?: "Misafir Avcı"
        val avatar = prefs.getString("user_avatar", "🐣") ?: "🐣"
        val isAnon = prefs.getBoolean("user_is_anon", true)

        val profile = UserProfile(
            uid = uid,
            displayName = name,
            email = null,
            avatarEmoji = avatar,
            isAnonymous = isAnon
        )
        prefs.edit()
            .putString("user_uid", uid)
            .putString("user_name", name)
            .putString("user_avatar", avatar)
            .putBoolean("user_is_anon", isAnon)
            .apply()

        _authState.value = AuthState.Authenticated(profile)
        return profile
    }

    fun signInWithGoogle(idTokenOrEmail: String, displayName: String, onResult: ((Boolean, String?) -> Unit)? = null) {
        val currentUser = try { auth.currentUser } catch (_: Exception) { null }

        if (idTokenOrEmail.contains("@") && !idTokenOrEmail.startsWith("eyJ")) {
            // Simulated token or email fallback
            handleMockGoogleLogin(idTokenOrEmail, displayName)
            onResult?.invoke(true, null)
            return
        }

        try {
            val credential = GoogleAuthProvider.getCredential(idTokenOrEmail, null)
            if (currentUser != null && currentUser.isAnonymous) {
                // Link anonymous user so no discovery data is lost!
                currentUser.linkWithCredential(credential)
                    .addOnSuccessListener { result ->
                        result.user?.let { emitUserFromFirebase(it) }
                        onResult?.invoke(true, null)
                    }
                    .addOnFailureListener {
                        // If link failed (e.g. account already exists), sign in directly
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener { res ->
                                res.user?.let { emitUserFromFirebase(it) }
                                onResult?.invoke(true, null)
                            }
                            .addOnFailureListener { err ->
                                onResult?.invoke(false, err.localizedMessage)
                            }
                    }
            } else {
                auth.signInWithCredential(credential)
                    .addOnSuccessListener { res ->
                        res.user?.let { emitUserFromFirebase(it) }
                        onResult?.invoke(true, null)
                    }
                    .addOnFailureListener { err ->
                        onResult?.invoke(false, err.localizedMessage)
                    }
            }
        } catch (e: Exception) {
            handleMockGoogleLogin(idTokenOrEmail, displayName)
            onResult?.invoke(true, null)
        }
    }

    private fun handleMockGoogleLogin(email: String, displayName: String) {
        val uid = "google_" + UUID.nameUUIDFromBytes(email.toByteArray()).toString().take(10)
        val avatar = "🌟"
        val profile = UserProfile(
            uid = uid,
            displayName = displayName,
            email = email,
            avatarEmoji = avatar,
            isAnonymous = false
        )
        prefs.edit()
            .putString("user_uid", uid)
            .putString("user_name", displayName)
            .putString("user_email", email)
            .putString("user_avatar", avatar)
            .putBoolean("user_is_anon", false)
            .apply()

        _authState.value = AuthState.Authenticated(profile)
    }

    fun registerWithEmail(name: String, email: String, password: String = "EasterCalc123!", onResult: ((Boolean, String?) -> Unit)? = null) {
        val currentUser = try { auth.currentUser } catch (_: Exception) { null }
        val credential = EmailAuthProvider.getCredential(email, password)

        try {
            if (currentUser != null && currentUser.isAnonymous) {
                // Link current anonymous account so score and eggs are preserved
                currentUser.linkWithCredential(credential)
                    .addOnSuccessListener { result ->
                        val user = result.user
                        user?.updateProfile(
                            UserProfileChangeRequest.Builder().setDisplayName(name).build()
                        )
                        prefs.edit().putString("user_name", name).putString("user_avatar", "🏆").apply()
                        user?.let { emitUserFromFirebase(it) }
                        onResult?.invoke(true, null)
                    }
                    .addOnFailureListener {
                        // If linking failed, create directly
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { res ->
                                res.user?.updateProfile(
                                    UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                )
                                prefs.edit().putString("user_name", name).putString("user_avatar", "🏆").apply()
                                res.user?.let { emitUserFromFirebase(it) }
                                onResult?.invoke(true, null)
                            }
                            .addOnFailureListener { err ->
                                onResult?.invoke(false, err.localizedMessage)
                            }
                    }
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { res ->
                        res.user?.updateProfile(
                            UserProfileChangeRequest.Builder().setDisplayName(name).build()
                        )
                        prefs.edit().putString("user_name", name).putString("user_avatar", "🏆").apply()
                        res.user?.let { emitUserFromFirebase(it) }
                        onResult?.invoke(true, null)
                    }
                    .addOnFailureListener { err ->
                        onResult?.invoke(false, err.localizedMessage)
                    }
            }
        } catch (_: Exception) {
            registerWithEmailFallback(name, email)
            onResult?.invoke(true, null)
        }
    }

    fun signInWithEmail(email: String, password: String, onResult: ((Boolean, String?) -> Unit)? = null) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { res ->
                    res.user?.let { emitUserFromFirebase(it) }
                    onResult?.invoke(true, null)
                }
                .addOnFailureListener { err ->
                    onResult?.invoke(false, err.localizedMessage)
                }
        } catch (_: Exception) {
            registerWithEmailFallback(email.substringBefore("@"), email)
            onResult?.invoke(true, null)
        }
    }

    private fun registerWithEmailFallback(name: String, email: String): UserProfile {
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

        try {
            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )
        } catch (_: Exception) {}

        _authState.value = AuthState.Authenticated(updated)
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
        prefs.edit().clear().apply()
        signInAsGuest()
    }
}
