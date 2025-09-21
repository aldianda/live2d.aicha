// RetrofitClient.kt
package com.example.aichaprototype110.voice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://<project-id>.cloudfunctions.net/"

    val instance: SpeechApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpeechApiService::class.java)
    }
}
