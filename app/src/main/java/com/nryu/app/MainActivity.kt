package com.nryu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nryu.app.data.AudioVisualizer
import com.nryu.app.ui.MusicViewModel
import com.nryu.app.ui.components.SongList
import com.nryu.app.ui.components.VisualizerCore
import com.nryu.app.ui.theme.NryuTheme

class MainActivity : ComponentActivity() {
    private val visualizer = AudioVisualizer()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permissions result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.RECORD_AUDIO
            )
        )

        setContent {
            NryuTheme {
                val musicViewModel: MusicViewModel = viewModel()
                val songs by musicViewModel.songs.collectAsState()
                val currentSong by musicViewModel.currentSong.collectAsState()
                val isPlaying by musicViewModel.isPlaying.collectAsState()
                
                var amplitudes by remember { mutableStateOf(FloatArray(0)) }

                LaunchedEffect(Unit) {
                    visualizer.start(0) 
                    visualizer.amplitude.collect {
                        amplitudes = it
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Library Layer
                        SongList(
                            songs = songs,
                            currentSong = currentSong,
                            onSongClick = { song ->
                                musicViewModel.playSong(song)
                            }
                        )

                        // Reactive Visuals Layer (Only when playing)
                        AnimatedVisibility(
                            visible = isPlaying,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            VisualizerCore(amplitude = amplitudes)
                        }

                        // Floating Controls
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { musicViewModel.togglePlayback() },
                                modifier = Modifier.size(80.dp)
                            ) {
                                Text(if (isPlaying) "⏸" else "▶")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        visualizer.stop()
    }
}
