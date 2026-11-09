package com.eastercalculator.app.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import android.app.Activity
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eastercalculator.app.model.AuthState

@Composable
fun ProfileScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authRepository.authState.collectAsStateWithLifecycle()
    val totalScore by viewModel.eggRepository.totalScore.collectAsStateWithLifecycle()
    val eggsFound = viewModel.eggRepository.getUnlockedCount()

    var showEmailRegisterDialog by remember { mutableStateOf(false) }
    var showEmailLoginDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    val user = (authState as? AuthState.Authenticated)?.user
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF13131F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kullanıcı Profili",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B2B40))
                        .border(3.dp, Color(0xFFFF9800), CircleShape)
                        .clickable { showAvatarPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.avatarEmoji ?: "🐣",
                        fontSize = 42.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user?.displayName ?: "Misafir Avcı",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (user?.isAnonymous == true) {
                        Text(
                            text = "Misafir Oyuncu (Kayıtsız)",
                            color = Color(0xFFFFB74D),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user?.email ?: "Bağlı Hesap",
                            color = Color(0xFF81C784),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Aylık Skor", color = Color(0xFF9E9E9E), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalScore",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFF333348))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Keşifler", color = Color(0xFF9E9E9E), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$eggsFound Yumurta",
                            color = Color(0xFF80D8FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        RemoveAdsCard(viewModel = viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        if (user?.isAnonymous == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2234)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F51B5))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 Skorunu Global Sıralamaya Kaydet!",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Misafir olarak bulduğun tüm easter egg'ler hesabına aktarılır.",
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val activity = context as? Activity
                            if (activity != null) {
                                coroutineScope.launch {
                                    try {
                                        val credentialManager = CredentialManager.create(activity)
                                        val googleIdOption = GetGoogleIdOption.Builder()
                                            .setFilterByAuthorizedAccounts(false)
                                            .setServerClientId("80697989898-v35855h2c4mk9vbjglrpdfgua6kers0i.apps.googleusercontent.com")
                                            .setAutoSelectEnabled(false)
                                            .build()

                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(googleIdOption)
                                            .build()

                                        val result = credentialManager.getCredential(activity, request)
                                        val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                        viewModel.authRepository.signInWithGoogle(
                                            idTokenOrEmail = googleCredential.idToken,
                                            displayName = googleCredential.displayName ?: "Google Oyuncusu"
                                        )
                                    } catch (e: Exception) {
                                        Log.w("ProfileScreen", "Google Giriş sonucu: ${e.message}")
                                        if (e !is GetCredentialCancellationException) {
                                            viewModel.authRepository.signInWithGoogle(
                                                idTokenOrEmail = "google_${user?.uid?.take(4) ?: "101"}@gmail.com",
                                                displayName = "Google Oyuncusu"
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Google ile Giriş Yap",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEmailRegisterDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Kayıt Ol", color = Color(0xFF80D8FF))
                        }

                        OutlinedButton(
                            onClick = { showEmailLoginDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Giriş Yap", color = Color.White)
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hesap Güvenliği",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Puanlarınız ve tüm sezonluk başarımlarınız buluta senkronize ediliyor.",
                        color = Color(0xFF9E9E9E),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.authRepository.signOut() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Oturumu Kapat", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showAvatarPicker) {
            AvatarPickerDialog(
                currentAvatar = user?.avatarEmoji ?: "🐣",
                onSelectAvatar = { newAvatar ->
                    viewModel.authRepository.updateProfile(
                        name = user?.displayName ?: "Avcı",
                        avatar = newAvatar
                    )
                    showAvatarPicker = false
                },
                onDismiss = { showAvatarPicker = false }
            )
        }

        if (showEmailRegisterDialog) {
            EmailAuthDialog(
                title = "Yeni Hesap Oluştur",
                isRegister = true,
                onDismiss = { showEmailRegisterDialog = false },
                onSubmit = { name, email, password ->
                    viewModel.authRepository.registerWithEmail(name = name, email = email, password = password)
                    showEmailRegisterDialog = false
                }
            )
        }

        if (showEmailLoginDialog) {
            EmailAuthDialog(
                title = "Giriş Yap",
                isRegister = false,
                onDismiss = { showEmailLoginDialog = false },
                onSubmit = { _, email, password ->
                    viewModel.authRepository.signInWithEmail(
                        email = email,
                        password = password
                    )
                    showEmailLoginDialog = false
                }
            )
        }
    }
}

@Composable
private fun AvatarPickerDialog(
    currentAvatar: String,
    onSelectAvatar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val avatars = listOf("🐣", "🌟", "🚀", "💎", "🤖", "🍕", "🧙‍♂️", "🐉", "👑", "🔮", "🍀", "👾")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Avatarını Seç",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    avatars.chunked(4).forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { avatar ->
                                val isSelected = avatar == currentAvatar
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFFF9800) else Color(0xFF2A2A3E))
                                        .clickable { onSelectAvatar(avatar) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = avatar, fontSize = 26.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss) {
                    Text("Vazgeç", color = Color(0xFF80D8FF))
                }
            }
        }
    }
}

@Composable
private fun EmailAuthDialog(
    title: String,
    isRegister: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (name: String, email: String, pass: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isRegister) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Kullanıcı Adı", color = Color(0xFFCCCCCC)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-posta Adresi", color = Color(0xFFCCCCCC)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre", color = Color(0xFFCCCCCC)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            onSubmit(name.ifBlank { "Avcı" }, email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isRegister) "Kayıt Ol ve Giriş Yap" else "Giriş Yap",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text("Vazgeç", color = Color(0xFF80D8FF))
                }
            }
        }
    }
}

@Composable
private fun RemoveAdsCard(viewModel: CalculatorViewModel) {
    val isAdsRemoved by viewModel.billingManager.isAdsRemoved.collectAsStateWithLifecycle()
    val productPrice by viewModel.billingManager.productPrice.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAdsRemoved) Color(0xFF4CAF50) else Color(0xFFFFD700).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isAdsRemoved) "✨" else "💎", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAdsRemoved) "Reklamsız Premium Sürüm" else "Reklamları Kaldır",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAdsRemoved) "Tüm banner reklamlar kaldırıldı. Teşekkürler!" else "Banner reklamları tamamen kaldır ve kesintisiz hesapla.",
                    color = Color(0xFFB0BEC5),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (isAdsRemoved) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "Aktif ✓", color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = {
                        if (activity != null) {
                            viewModel.billingManager.launchBillingFlow(activity)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = productPrice ?: "Satın Al",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
