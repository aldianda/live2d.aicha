package com.example.aichaprototype110

import android.Manifest
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.plusAssign
import com.example.aichaprototype110.datastore.ThemePreference
import com.example.aichaprototype110.renderer.Live2DGLView
import com.example.aichaprototype110.ui.theme.AIchaNavGraph
import com.example.aichaprototype110.ui.theme.AIchaPrototype110Theme
import com.example.aichaprototype110.voice.GoogleCloudSpeechHelper
import com.example.aichaprototype110.voice.VoiceResponder
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("aichaprototype110")
        }
    }

    private lateinit var live2DView: Live2DGLView

    external fun stringFromJNI(): String

    external fun nativeSetAssetManager(assetManager: AssetManager)

    private lateinit var themePreference: ThemePreference
    private lateinit var googleCloudSpeechHelper: GoogleCloudSpeechHelper
    private lateinit var voiceResponder: VoiceResponder

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            mulaiPengakuanSuara()
        } else {
            Log.e("MainActivity", "Izin mikrofon ditolak")
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nativeSetAssetManager(assets)

        themePreference = ThemePreference(this)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val navController = rememberAnimatedNavController()
            val currentUser = remember { FirebaseAuth.getInstance().currentUser }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                themePreference.isDarkMode.collect { isDarkTheme = it }
            }

            AIchaPrototype110Theme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Live2D background
                    live2DView = Live2DGLView(this)
                    setContentView(live2DView)

                    // Navigation
                    AIchaNavGraph(
                        navController = navController,
                        startDestination = if (currentUser != null) "home" else "login",
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDark ->
                            scope.launch {
                                themePreference.saveDarkMode(isDark)
                                isDarkTheme = isDark
                            }
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            scope.launch {
                                themePreference.saveDarkMode(false)
                            }
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        Log.d("JNI", stringFromJNI())

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    @Composable
    fun Live2DView() {
        AndroidView(factory = { context -> Live2DGLView(context) }, modifier = Modifier.fillMaxSize())
    }

    private fun mulaiPengakuanSuara() {
        googleCloudSpeechHelper = GoogleCloudSpeechHelper(this)
        voiceResponder = VoiceResponder(this, googleCloudSpeechHelper)
        voiceResponder.startVoiceInteraction()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::voiceResponder.isInitialized) {
            voiceResponder.destroy()
        }
    }
}
