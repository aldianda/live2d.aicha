package com.example.aichaprototype110.voice

import android.app.Activity
import android.util.Log
import com.example.aichaprototype110.tts.TTSUtils
import com.example.aichaprototype110.voice.utils.GPTUtils
import java.io.File

class VoiceResponder(
    private val activity: Activity,
    private val helper: GoogleCloudSpeechHelper
) {
    init {
        helper.setOnFinalResultListener { text ->
            Log.d("VoiceResponder", "Transcribed: $text")
            if (text.isNotBlank()) {
                handleVoiceCommand(text)
            } else {
                Log.d("VoiceResponder", "Empty transcription, ignoring.")
                helper.startListening()
            }
        }
    }

    fun startVoiceInteraction() {
        helper.startListening()
    }

    private fun handleVoiceCommand(transcribedText: String) {
        GPTUtils.getResponseFromGPT(transcribedText, { response ->
            Log.d("VoiceResponder", "GPT Response: $response")
            TTSUtils.speak(activity, response) {
                helper.startListening()
            }
        }, { error ->
            Log.e("VoiceResponder", "Error from GPT: $error")
            TTSUtils.speak(activity, "Maaf, terjadi kesalahan: $error") {
                helper.startListening()
            }
        })
    }

    fun sendAudioFileToFirebase(audioFile: File) {
        helper.sendAudioToFirebase(audioFile)
    }

    fun destroy() {
        helper.destroy()
    }
}
