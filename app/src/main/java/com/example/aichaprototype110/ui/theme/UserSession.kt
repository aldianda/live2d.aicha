package com.example.aichaprototype110.ui.theme

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Menyimpan status login/logout
    fun saveUserLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    // Mengecek status login/logout
    fun isUserLoggedIn(): Boolean {
        return preferences.getBoolean("is_logged_in", false)
    }
}
