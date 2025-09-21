package com.example.aichaprototype110.tts

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType

object TTSUtils {
    private const val API_KEY = "YOUR_ELEVEN_LABS_API_KEY"
    private const val BASE_URL = "https://api.elevenlabs.io/v1/text-to-speech/generate"

    private val client = OkHttpClient()

    fun speak(context: Context, text: String, onFinish: () -> Unit) {
        getVoiceResponse(text, { audioData ->
            try {
                val tempFile = kotlin.io.path.createTempFile(suffix = ".mp3").toFile()
                tempFile.writeBytes(audioData)

                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    prepare()
                    setOnCompletionListener {
                        release()
                        tempFile.delete()
                        onFinish()
                    }
                    start()
                }
            } catch (e: Exception) {
                Log.e("TTSUtils", "Playback error: ${e.message}")
                onFinish()
            }
        }, { error ->
            Log.e("TTSUtils", "TTS error: $error")
            onFinish()
        })
    }

    private fun getVoiceResponse(text: String, onSuccess: (ByteArray) -> Unit, onError: (String) -> Unit) {
        val json = JSONObject()
        json.put("text", text)
        json.put("voice", "en_us_male")
        json.put("model_id", "eleven_monolingual_v1")

        val body = RequestBody.create(
            "application/json".toMediaType(), json.toString()
        )

        val request = Request.Builder()
            .url(BASE_URL)
            .post(body)
            .header("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("TTS request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val audioData = response.body?.bytes()
                    if (audioData != null) {
                        onSuccess(audioData)
                    } else {
                        onError("Failed to receive audio data")
                    }
                } else {
                    onError("Failed to get TTS response: ${response.message}")
                }
            }
        })
    }
}
