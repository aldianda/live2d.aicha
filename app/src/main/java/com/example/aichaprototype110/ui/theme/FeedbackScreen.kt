package com.example.aichaprototype110.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FeedbackScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var feedbackText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kirim Feedback", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            label = { Text("Masukkan feedback kamu di sini") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (feedbackText.isBlank()) {
                    Toast.makeText(context, "Feedback tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                val feedbackData = hashMapOf(
                    "message" to feedbackText,
                    "timestamp" to Timestamp.now(),
                    "email" to (auth.currentUser?.email ?: "Anonymous")
                )

                db.collection("feedback")
                    .add(feedbackData)
                    .addOnSuccessListener {
                        isLoading = false
                        Toast.makeText(context, "Feedback terkirim. Terima kasih!", Toast.LENGTH_SHORT).show()
                        feedbackText = ""
                    }
                    .addOnFailureListener {
                        isLoading = false
                        Toast.makeText(context, "Gagal mengirim feedback", Toast.LENGTH_SHORT).show()
                    }
            },
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            else Text("KIRIM")
        }
    }
}
