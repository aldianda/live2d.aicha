package com.example.aichaprototype110.ui.theme

import androidx.compose.ui.graphics.Brush
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichaprototype110.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAFAFA),
                        Color(0xFF9FD5E3)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_white),
                contentDescription = "Logo Aicha",
                modifier = Modifier.size(100.dp)
            )

            Text("Buat akun baru", fontSize = 20.sp)


            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )


            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(50)
            )


            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                ,
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(50)
            )



            var confirmPasswordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                ,
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(50)
            )


            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
            }

            Button(
                onClick = {
                    errorMessage = ""

                    if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Isi semua kolom!"
                        return@Button
                    }

                    if (password.length < 6) {
                        errorMessage = "Password minimal 6 karakter"
                        return@Button
                    }

                    if (password != confirmPassword) {
                        errorMessage = "Konfirmasi password tidak cocok"
                        return@Button
                    }

                    isLoading = true

                    db.collection("usernames").document(username).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                isLoading = false
                                errorMessage = "Username sudah digunakan"
                            } else {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            db.collection("usernames").document(username)
                                                .set(mapOf("email" to email))
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Akun berhasil dibuat!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    onSignUpSuccess() // langsung ke home
                                                }
                                                .addOnFailureListener {
                                                    errorMessage = "Gagal menyimpan username"
                                                    isLoading = false
                                                }
                                        } else {
                                            errorMessage = task.exception?.message ?: "Gagal mendaftar"
                                            isLoading = false
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = "Gagal memeriksa username"
                            isLoading = false
                        }
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
                    Text("DAFTAR", color = Color.White)
                }
            }

            Text("Sudah punya akun?", color = Color.Gray)

            TextButton(onClick = { onBackToLogin() }) {
                Text("Login di sini", color = Color(0xFF5A4FCF))
            }
        }
    }
}
