package com.example.aichaprototype110.ui.theme

import android.Manifest
import android.app.Activity
import android.media.MediaRecorder
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aichaprototype110.R
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aichaprototype110.voice.GoogleCloudSpeechHelper
import kotlinx.coroutines.delay
import java.io.File


class AudioRecorder(private val outputFile: File) {
    private var recorder: MediaRecorder? = null

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
    }

    fun stopRecording(): File {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
        return outputFile
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val spokenTextState = remember { mutableStateOf("") }

    var audioRecorder by remember { mutableStateOf<AudioRecorder?>(null) }
    val speechHelper = remember { GoogleCloudSpeechHelper(activity) }

    // Fungsi upload audio (mock)
    fun uploadAudioFile(audioFile: File, callback: (String?) -> Unit) {
        // TODO: Implementasi upload sesungguhnya di sini
        callback("File audio berhasil di-upload!")
    }

    // Listener hasil transkripsi
    speechHelper.setOnFinalResultListener { result ->
        spokenTextState.value = "Transkripsi: $result"
        val audioFile = audioRecorder?.stopRecording()
        audioFile?.let { file ->
            uploadAudioFile(file) { serverResult ->
                spokenTextState.value = serverResult ?: "Gagal mengirim suara"
            }
        }
    }

    // Cek izin dan mulai proses suara
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.RECORD_AUDIO
        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 100)
        } else {
            val outputFile = File(context.cacheDir, "recorded_audio.3gp")
            audioRecorder = AudioRecorder(outputFile)

            audioRecorder?.startRecording()
            speechHelper.startListening()

            delay(5000)

            val audioFile = audioRecorder?.stopRecording()
            audioFile?.let { file ->
                speechHelper.sendAudioToFirebase(file)
            }
        }
    }

    // UI utama
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                NavigationDrawerItem(
                    label = { Text("Pengaturan") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("setting")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Feedback") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("feedback")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_main),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                content = { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = spokenTextState.value,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            )
        }
    }
}
