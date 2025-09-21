package com.example.aichaprototype110.ui.theme

import androidx.compose.ui.graphics.Brush
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aichaprototype110.R
import com.example.aichaprototype110.utils.AuthUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun WavingAnimation(
    modifier: Modifier = Modifier,
    frameResIds: List<Int>,
    frameDurationMillis: Long = 200L
) {
    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(frameDurationMillis)
            frameIndex = (frameIndex + 1) % frameResIds.size
        }
    }

    Image(
        painter = painterResource(id = frameResIds[frameIndex]),
        contentDescription = "Waving Animation",
        modifier = modifier
    )
}

@Composable
fun LoginScreen(navController: NavController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val token = account.idToken ?: ""
            AuthUtils.loginWithGoogle(
                auth = auth,
                googleAccountIdToken = token,
                context = context,
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onError = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Login Google gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFF9FD5E3)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val isDark = isDarkTheme

            Text("Hai! Sebelum memulai, login dulu yuk!", fontSize = 20.sp)

            WavingAnimation(
                frameResIds = listOf(
                    R.drawable.wave_1,
                    R.drawable.wave_2,
                    R.drawable.wave_3
                ),
                modifier = Modifier.size(120.dp)
            )

            OutlinedTextField(
                value = emailOrUsername,
                onValueChange = { emailOrUsername = it },
                label = { Text("Email atau Username") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )







            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
            }

            Button(
                onClick = {
                    errorMessage = ""
                    if (emailOrUsername.isBlank() || password.isBlank()) {
                        errorMessage = "Isi semua kolom!"
                        return@Button
                    }
                    isLoading = true
                    AuthUtils.login(
                        input = emailOrUsername,
                        password = password,
                        auth = auth,
                        db = db,
                        context = context,
                        onSuccess = {
                            isLoading = false
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = {
                            isLoading = false
                            errorMessage = it
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A4FCF)),
                interactionSource = remember { MutableInteractionSource() },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("LOGIN", color = Color.White)
                }
            }

            Text("Atau masuk dengan", color = Color.Gray)

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconButton(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.google_client_id))
                            .requestEmail()
                            .build()
                        googleLauncher.launch(GoogleSignIn.getClient(context, gso).signInIntent)
                    },
                    modifier = Modifier
                        .size(48.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_belajarid),
                        contentDescription = null
                    )
                }
            }

            Text("Belum punya akun?", color = Color.Gray)
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Daftar di sini", color = Color(0xFF5A4FCF))
            }
        }
    }
}
