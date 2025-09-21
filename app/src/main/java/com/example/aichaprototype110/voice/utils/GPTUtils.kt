package com.example.aichaprototype110.voice.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

object GPTUtils {
    private const val API_KEY = "sk-proj-ftOICKXOTaA29Vec5wc4UAz10bkuus-HIas35wpdyz7NqZ2_Wxg5AcPLyApKelznhizQ-H53vkT3BlbkFJwy5b7DI9oieh_NE9kPbeOkz3YtMw8hUTFfAHisqmU5z_PF0ajrJe-oFMZX12aGxGMW3kFZlGwAY"
    private const val ENDPOINT = "https://api.openai.com/v1/chat/completions"
    private const val MODEL = "gpt-4o-mini"

    fun getResponseFromGPT(userMessage: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                val json = JSONObject().apply {
                    put("model", MODEL)
                    put("messages", listOf(
                        mapOf("role" to "user", "content" to userMessage)
                    ))
                    put("temperature", 0.7)
                }

                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )

                val request = Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onError(e.message ?: "Unknown error")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val message = JSONObject(responseBody)
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            onSuccess(message.trim())
                        } else {
                            onError("Error: ${response.code}")
                        }
                    }
                })
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error")
            }
        }
    }
}
