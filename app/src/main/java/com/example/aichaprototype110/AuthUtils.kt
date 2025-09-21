package com.example.aichaprototype110.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

object AuthUtils {

    fun login(
        input: String,
        password: String,
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (input.contains("@")) {
            auth.signInWithEmailAndPassword(input, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Username or password incorrect")
                    }
                }
        } else {

            db.collection("usernames")
                .document(input)
                .get()
                .addOnSuccessListener { document ->
                    val email = document.getString("email")
                    if (!email.isNullOrEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onSuccess()
                                } else {
                                    onError("Username or password incorrect")
                                }
                            }
                    } else {
                        onError("Username or password incorrect")
                    }
                }
                .addOnFailureListener {
                    onError("Username or password incorrect")
                }
        }
    }

    fun loginWithGoogle(
        auth: FirebaseAuth,
        googleAccountIdToken: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(googleAccountIdToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val email = user?.email ?: return@addOnCompleteListener onError("Email Google tidak ditemukan")
                    val uid = user.uid

                    val db = FirebaseFirestore.getInstance()
                    val usernameRef = db.collection("usernames")
                    val userDocRef = usernameRef.document(uid)


                    usernameRef.whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {

                                val defaultUsername = email.substringBefore("@")
                                usernameRef.document(defaultUsername).set(mapOf("email" to email))
                            }
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onError("Gagal memverifikasi akun Google")
                        }
                } else {
                    val errorMsg = task.exception?.message ?: "Login dengan Google gagal"
                    onError(errorMsg)
                }
            }
    }

}
