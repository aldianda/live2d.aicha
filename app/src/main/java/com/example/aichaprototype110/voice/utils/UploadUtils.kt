package com.example.aichaprototype110.voice

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

fun uploadAudioFile(file: File, onResult: (String?) -> Unit) {
    val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

    val call = RetrofitClient.instance.uploadAudio(body)
    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                onResult(response.body()?.string())
            } else {
                onResult(null)
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            onResult(null)
        }
    })
}
