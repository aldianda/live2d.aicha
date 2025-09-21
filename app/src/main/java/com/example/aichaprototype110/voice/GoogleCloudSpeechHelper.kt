package com.example.aichaprototype110.voice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Base64
import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class GoogleCloudSpeechHelper(private val activity: Activity) {

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(activity)
    private var isListening = false
    private var isSpeaking = false
    private val functions = FirebaseFunctions.getInstance()

    private var onFinalResultListener: ((String) -> Unit)? = null

    init {
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechHelper", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                Log.e("SpeechHelper", "Error: $error")
                if (!isSpeaking) {
                    restartListeningWithDelay()
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    stopListening()
                    isSpeaking = true
                    onFinalResultListener?.invoke(spokenText)
                } else {
                    if (!isSpeaking) {
                        restartListeningWithDelay()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun setOnFinalResultListener(listener: (String) -> Unit) {
        onFinalResultListener = listener
    }

    fun setSpeaking(speaking: Boolean) {
        isSpeaking = speaking
    }

    fun startListening() {
        if (!isListening) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            }
            recognizer.startListening(intent)
            isListening = true
            Log.d("SpeechHelper", "Started Listening")
        }
    }

    fun stopListening() {
        if (isListening) {
            recognizer.stopListening()
            isListening = false
            Log.d("SpeechHelper", "Stopped Listening")
        }
    }

    private fun restartListeningWithDelay(delayMillis: Long = 500) {
        Handler(Looper.getMainLooper()).postDelayed({
            startListening()
        }, delayMillis)
    }

    fun destroy() {
        try {
            recognizer.destroy()
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error destroying recognizer: ${e.message}")
        }
    }

    fun sendAudioToFirebase(audioFile: File) {
        val audioBytes = readFile(audioFile)
        if (audioBytes.isEmpty()) {
            Log.e("SpeechHelper", "Audio file kosong atau gagal dibaca.")
            return
        }

        val data = hashMapOf("audio" to audioBytes)

        functions
            .getHttpsCallable("speechToText")
            .call(data)
            .addOnSuccessListener { result ->
                val transcription = (result.data as? Map<*, *>)?.get("transcription") as? String
                transcription?.let {
                    Log.d("SpeechHelper", "Transcription from Firebase: $it")
                    onFinalResultListener?.invoke(it)
                }
            }
            .addOnFailureListener { e ->
                Log.e("SpeechHelper", "Firebase speechToText failed: ${e.message}")
            }
    }

    private fun readFile(file: File): String {
        return try {
            val inputStream = FileInputStream(file)
            val byteArray = inputStream.readBytes()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: IOException) {
            Log.e("SpeechHelper", "Gagal membaca file audio: ${e.message}")
            ""
        }
    }
}
