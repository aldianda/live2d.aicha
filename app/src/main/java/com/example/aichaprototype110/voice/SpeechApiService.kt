package com.example.aichaprototype110.voice

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call

interface SpeechApiService {
    @Multipart
    @POST("speechToText") // sesuai dengan endpoint function kamu
    fun uploadAudio(
        @Part audio: MultipartBody.Part
    ): Call<ResponseBody>
}
