package com.example.aichaprototype110

import com.example.aichaprototype110.MainActivity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(2000)  // Tunda 2 detik untuk splashscreen

            val user = FirebaseAuth.getInstance().currentUser

            // Jika ada pengguna yang login, arahkan ke Home, jika tidak, ke Login
            val intent = Intent(this@SplashScreen, MainActivity::class.java).apply {
                putExtra("startDestination", if (user != null) "home" else "login")
            }
            startActivity(intent)
            finish()  // Menutup SplashScreen
        }
    }
}
