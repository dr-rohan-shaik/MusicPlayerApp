package com.musicapp.feature.playback.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.dragDetected
import androidx.compose.foundation.gestures.tap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.musicapp.core.common.theme.DurationText
import com.musicapp.core.common.theme.MusicColors
import com.musicapp.core.common.theme.MusicShapes
import com.musicapp.core.common.theme.NowPlayingArtist
import com.musicapp.core.common.theme.NowPlayingTitle
import com.musicapp.core.common.theme.QueueNumber
import com.musicapp.core.model.Track
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Scaffold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring

@Composable
fun NowPlayingScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaybackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val position by viewModel.position.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val shuffleMode by viewModel.shuffleMode.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val isLiked by viewModel.isLiked.collectAsStateWithLifecycle()

    // Drag to dismiss
    var dragOffset by remember { mutableStateOf(0f) }
    val animatedOffset = animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationY = animatedOffset.value },
        topBar = {
            androidx.compose.material3.TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = "Now Playing", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Queue */ }) {
                        Icon(QueueMusic, contentDescription = "Queue", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { /* Lyrics */ }) {
                        Icon(Lyrics, contentDescription = "Lyrics", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { },
                        onDrag = { change, dragAmount ->
                            if (dragAmount.y > 0) {
                                dragOffset += dragAmount.y
                                change.consume()
                            }
                        },
                        onDragEnd = {
                            if (dragOffset > 200) {
                                onDismiss()
                            } else {
                                dragOffset = 0f
                            }
                        }
                    )
                },
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Album artwork
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(MusicShapes.CardLarge)
                    .background(MusicColors.CardBackgroundEnd)
            ) {
                currentTrack?.let { track ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current as ImageLoader)
                            .data(track.artworkUrl)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier.fillMaxSize().clip(MusicShapes.CardLarge),
                        contentScale = ContentScale.Crop,
                        contentDescription = track.title
                    )
                }
            }

            // Track info
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentTrack?.let { track ->
                    NowPlayingTitle(text = track.title)
                    NowPlayingArtist(text = track.artist.name)
                }
            }

            // Progress bar
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DurationText(text = position.formatDuration())
                    DurationText(text = duration.formatDuration())
                }

                Slider(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    value = if (duration > 0) position.toFloat() / duration else 0f,
                    onValueChange = { viewModel.seekTo((it * duration).toLong()) },
                    valueRange = 0f..1f,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MusicColors.ProgressBackground
                    )
                )
            }

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = { viewModel.setShuffleMode(if (shuffleMode == com.musicapp.core.data.ShuffleMode.OFF) com.musicapp.core.data.ShuffleMode.ON else com.musicapp.core.data.ShuffleMode.OFF) }
                ) {
                    Icon(
                        imageVector = Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (shuffleMode == com.musicapp.core.data.ShuffleMode.ON) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Previous
                IconButton(onClick = { viewModel.skipPrevious() }) {
                    Icon(SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                }

                // Play/Pause
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { viewModel.togglePlayPause() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState is com.musicapp.core.data.PlaybackState.Playing) Pause else PlayArrow,
                        contentDescription = if (playbackState is com.musicapp.core.data.PlaybackState.Playing) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Next
                IconButton(onClick = { viewModel.skipNext() }) {
                    Icon(SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                }

                // Repeat
                IconButton(
                    onClick = {
                        viewModel.setRepeatMode(
                            when (repeatMode) {
                                com.musicapp.core.data.RepeatMode.OFF -> com.musicapp.core.data.RepeatMode.ALL
                                com.musicapp.core.data.RepeatMode.ALL -> com.musicapp.core.data.RepeatMode.ONE
                                else -> com.musicapp.core.data.RepeatMode.OFF
                            }
                        )
                    }
                ) {
                    Icon(
                        imageVector = when (repeatMode) {
                            com.musicapp.core.data.RepeatMode.OFF -> Repeat
                            com.musicapp.core.data.RepeatMode.ONE -> RepeatOne
                            else -> Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (repeatMode != com.musicapp.core.data.RepeatMode.OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Secondary controls
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like
                IconButton(
                    onClick = {
                        currentTrack?.let { track ->
                            viewModel.toggleLike(track)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Favorite else FavoriteBorder,
                        contentDescription = if (isLiked) "Liked" else "Like",
                        tint = if (isLiked) com.musicapp.core.common.theme.MusicColors.LikedRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Devices
                IconButton(onClick = { /* Show devices */ }) {
                    Icon(Cast, contentDescription = "Devices", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Share
                IconButton(onClick = { /* Share */ }) {
                    Icon(Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // More
                IconButton(onClick = { /* More options */ }) {
                    Icon(MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun QueueScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaybackViewModel = hiltViewModel()
) {
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Queue", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(androidx.compose.material.icons.filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearQueue() }) {
                        Icon(androidx.compose.material.icons.filled.ClearAll, contentDescription = "Clear queue")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Now Playing section
            Text(text = "Now Playing", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))

            queue.getOrNull(currentIndex)?.let { currentTrack ->
                TrackCard(
                    track = currentTrack,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = { },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) },
                    isPlaying = true,
                    isCurrentQueue = true
                )
            }

            androidx.compose.foundation.layout.Divider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Up Next
            Text(text = "Up Next", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            if (currentIndex + 1 < queue.size) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(queue.drop(currentIndex + 1)) { track ->
                        TrackCard(
                            track = track,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.playFromQueue(queue.indexOf(track)) },
                            imageLoader = remember { coil.ImageLoader(LocalContext.current) },
                            isCurrentQueue = true
                        )
                    }
                }
            } else {
                Text(text = "Queue is empty", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(16.dp))
            }

            // History
            if (history.isNotEmpty()) {
                androidx.compose.foundation.layout.Divider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(text = "History", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 16.dp))

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(history.reversed()) { track ->
                        TrackCard(
                            track = track,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.playTrack(track, queue + history) },
                            imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LyricsScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaybackViewModel = hiltViewModel()
) {
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Lyrics", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(androidx.compose.material.icons.filled.Close, contentDescription = "Close")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            currentTrack?.let { track ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Lyrics for ${track.title}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Lyrics feature coming soon", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    }
}

import androidx.compose.ui.platform.LocalContext
import com.musicapp.core.common.theme.MusicShapes

@AndroidEntryPoint
class PlaybackViewModel @Inject constructor(
    private val playbackUseCase: com.musicapp.core.domain.PlaybackUseCase,
    private val playbackRepository: com.musicapp.core.data.PlaybackRepository,
    private val playerController: com.musicapp.media.PlayerController
) : ViewModel() {
    private val _uiState = mutableStateOf<PlaybackUiState>(PlaybackUiState.Idle)
    val uiState: androidx.compose.runtime.State<PlaybackUiState> = _uiState

    val playbackState = playerController.playbackState
    val currentTrack = playerController.currentTrack
    val queue = playerController.queue
    val position = playerController.position
    val duration = playerController.duration
    val bufferedPosition = playerController.bufferedPosition

    // Derived state
    val shuffleMode = queue.map { it.shuffleMode }
    val repeatMode = queue.map { it.repeatMode }
    val currentIndex = queue.map { it.currentIndex }
    val history = playbackRepository.observeHistory().stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val isLiked = currentTrack.map { it?.isLiked == true }

    init {
        observePlayback()
    }

    private fun observePlayback() {
        viewModelScope.launch {
            playbackState.collect { state ->
                _uiState.value = when (state) {
                    is com.musicapp.core.data.PlaybackState.Playing -> PlaybackUiState.Playing
                    is com.musicapp.core.data.PlaybackState.Paused -> PlaybackUiState.Paused
                    is com.musicapp.core.data.PlaybackState.Buffering -> PlaybackUiState.Buffering
                    is com.musicapp.core.data.PlaybackState.Error -> PlaybackUiState.Error(state.message)
                    else -> PlaybackUiState.Idle
                }
            }
        }
    }

    fun playTrack(track: Track, queue: List<Track>, startIndex: Int) {
        viewModelScope.launch {
            playbackUseCase.playTrack(track, queue, startIndex)
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            playbackUseCase.togglePlayPause()
        }
    }

    fun skipNext() {
        viewModelScope.launch {
            playbackUseCase.skipNext()
        }
    }

    fun skipPrevious() {
        viewModelScope.launch {
            playbackUseCase.skipPrevious()
        }
    }

    fun seekTo(position: Long) {
        viewModelScope.launch {
            playbackUseCase.seekTo(position)
        }
    }

    fun setShuffleMode(mode: com.musicapp.core.data.ShuffleMode) {
        viewModelScope.launch {
            playbackUseCase.setShuffleMode(mode)
        }
    }

    fun setRepeatMode(mode: com.musicapp.core.data.RepeatMode) {
        viewModelScope.launch {
            playbackUseCase.setRepeatMode(mode)
        }
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch {
            playbackUseCase.setVolume(volume)
        }
    }

    fun playFromQueue(index: Int) {
        viewModelScope.launch {
            val q = queue.value
            if (index < q.size) {
                playbackUseCase.playTrack(q[index], q, index)
            }
        }
    }

    fun toggleLike(track: Track) {
        viewModelScope.launch {
            if (track.isLiked) {
                playbackUseCase.unlikeTrack(track.id) // Would need to add to use case
            } else {
                playbackUseCase.likeTrack(track.id)
            }
        }
    }

    fun clearQueue() {
        viewModelScope.launch {
            playbackUseCase.clearQueue()
        }
    }
}

sealed interface PlaybackUiState {
    object Idle : PlaybackUiState
    object Playing : PlaybackUiState
    object Paused : PlaybackUiState
    object Buffering : PlaybackUiState
    data class Error(val message: String) : PlaybackUiState
}