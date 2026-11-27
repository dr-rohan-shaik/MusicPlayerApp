package com.musicapp.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicapp.core.common.theme.MusicTheme
import com.musicapp.core.common.theme.ScreenTitle
import com.musicapp.core.common.theme.SectionHeader
import com.musicapp.core.common.ui.card.AlbumCard
import com.musicapp.core.common.ui.card.PlaylistCard
import com.musicapp.core.common.ui.card.TrackCard
import com.musicapp.core.common.ui.list.HorizontalAlbumList
import com.musicapp.core.common.ui.list.HorizontalPlaylistList
import com.musicapp.core.common.ui.list.HorizontalTrackList
import com.musicapp.core.common.ui.navigation.TopAppBar
import com.musicapp.core.model.HomeContent
import com.musicapp.core.model.Track
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

@Composable
fun HomeScreen(
    onNavigate: (String, String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Home",
                showProfile = true,
                onProfileClick = { onNavigate("profile", "") }
            )
        },
        content = { innerPadding ->
            when (uiState) {
                is HomeUiState.Loading -> HomeSkeleton(innerPadding)
                is HomeUiState.Error -> ErrorScreen(message = uiState.message, onRetry = { viewModel.loadContent() })
                is HomeUiState.Success -> HomeContentScreen(
                    content = uiState.content,
                    padding = innerPadding,
                    onItemClick = { track, queue -> onPlayTrack(track, queue) },
                    onNavigate = onNavigate
                )
            }
        }
    ) { innerPadding ->
        // Bottom bar handled by main activity
    }
}

@Composable
fun HomeContentScreen(
    content: HomeContent,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onItemClick: (Track, List<Track>) -> Unit,
    onNavigate: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Recently Played
        if (content.recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Jump Back In",
                    onSeeAllClick = { onNavigate("recentlyPlayedDetail", "") },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                HorizontalPlaylistList(
                    playlists = content.recentlyPlayed.map { it.toPlaylistRef() },
                    onItemClick = { playlist -> onNavigate("playlistDetail", playlist.id) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }

        // Made For You
        if (content.madeForYou.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Made For You",
                    onSeeAllClick = { /* navigate to made for you */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                HorizontalPlaylistList(
                    playlists = content.madeForYou,
                    onItemClick = { playlist -> onNavigate("playlistDetail", playlist.id) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }

        // New Releases
        if (content.newReleases.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "New Releases",
                    onSeeAllClick = { /* navigate to new releases */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                HorizontalAlbumList(
                    albums = content.newReleases,
                    onItemClick = { album -> onNavigate("albumDetail", album.id) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }

        // Recommended Tracks
        if (content.recommended.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recommended For You",
                    onSeeAllClick = { /* navigate to recommendations */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                HorizontalTrackList(
                    tracks = content.recommended,
                    onItemClick = { track, queue -> onItemClick(track, queue) },
                    onPlayClick = { track, queue -> onItemClick(track, queue) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) },
                    showAlbum = true,
                    showArtist = true
                )
            }
        }

        // Popular Near You
        if (content.popularNearYou.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Popular Near You",
                    onSeeAllClick = { /* navigate */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                HorizontalPlaylistList(
                    playlists = content.popularNearYou,
                    onItemClick = { playlist -> onNavigate("playlistDetail", playlist.id) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }
    }
}

@Composable
fun HomeSkeleton(padding: androidx.compose.foundation.layout.PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(5) { index ->
            item {
                SectionHeaderSkeleton(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                HorizontalSkeletonList(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun SectionHeaderSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, com.musicapp.core.common.theme.MusicShapes.Card)
            .shimmer()
    )
}

@Composable
fun HorizontalSkeletonList(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            items(4) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, com.musicapp.core.common.theme.MusicShapes.Card)
                        .shimmer()
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.filled.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            com.musicapp.core.common.theme.ScreenTitle(text = "Something went wrong")
            com.musicapp.core.common.theme.CardSubtitle(text = message, modifier = Modifier.fillMaxWidth())
            androidx.compose.material3.Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

// Extension to convert Track to PlaylistRef for recently played
private fun Track.toPlaylistRef(): com.musicapp.core.model.Playlist {
    // This is a simplified conversion - in real app would fetch actual playlist
    return com.musicapp.core.model.Playlist(
        id = this.id,
        name = this.title,
        description = this.artist.name,
        artworkUrl = this.artworkUrl,
        owner = com.musicapp.core.model.UserRef("", "", null),
        isPublic = true,
        isCollaborative = false,
        trackCount = 1,
        duration = this.duration,
        followerCount = 0,
        tracks = emptyList(),
        createdAt = this.addedAt,
        updatedAt = this.addedAt
    )
}

import androidx.compose.ui.platform.LocalContext

@AndroidEntryPoint
class HomeViewModel @Inject constructor(
    private val getHomeContent: com.musicapp.core.domain.GetHomeContentUseCase
) : ViewModel() {
    private val _uiState = mutableStateOf<HomeUiState>(HomeUiState.Loading)
    val uiState: androidx.compose.runtime.State<HomeUiState> = _uiState

    init {
        loadContent()
    }

    fun loadContent() {
        _uiState.value = HomeUiState.Loading
        androidx.lifecycle.lifecycleScope.launch {
            val result = getHomeContent()
            _uiState.value = when {
                result.isSuccess -> HomeUiState.Success(result.getOrThrow())
                else -> HomeUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load home content")
            }
        }
    }
}

sealed interface HomeUiState {
    data class Success(val content: HomeContent) : HomeUiState
    object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
}