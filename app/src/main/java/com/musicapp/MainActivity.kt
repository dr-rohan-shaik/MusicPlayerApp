package com.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltNavController
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.musicapp.core.common.theme.MusicTheme
import com.musicapp.navigation.AppNavHost
import com.musicapp.core.model.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MusicTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val playbackViewModel = hiltViewModel<com.musicapp.feature.playback.viewmodel.PlaybackViewModel>()

                    // Observe playback state to show/hide miniplayer
                    lifecycleScope.launch {
                        playbackViewModel.uiState.collect { state =>
                            // Miniplayer visibility handled by UI
                        }
                    }

                    AppNavHost(
                        navController = navController,
                        onPlayTrack = { track, queue, startIndex ->
                            playbackViewModel.playTrack(track, queue, startIndex)
                            // Show now playing
                            navController.navigate("nowPlaying") {
                                // Avoid multiple now playing dialogs
                                popUpTo("nowPlaying", inclusive = true)
                            }
                        }
                    )
                }
            }
        }
    }
}