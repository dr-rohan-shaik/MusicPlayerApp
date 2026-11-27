package com.musicapp.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicapp.core.common.theme.CardSubtitle
import com.musicapp.core.common.theme.MetadataText
import com.musicapp.core.common.theme.SectionHeader
import com.musicapp.core.common.theme.ScreenTitle
import com.musicapp.core.common.ui.card.AlbumCard
import com.musicapp.core.common.ui.card.ArtistCard
import com.musicapp.core.common.ui.card.PlaylistCard
import com.musicapp.core.common.ui.card.TrackCard
import com.musicapp.core.common.ui.list.HorizontalAlbumList
import com.musicapp.core.common.ui.list.HorizontalArtistList
import com.musicapp.core.common.ui.list.HorizontalPlaylistList
import com.musicapp.core.common.ui.list.HorizontalTrackList
import com.musicapp.core.common.ui.navigation.TopAppBar
import com.musicapp.core.model.Album
import com.musicapp.core.model.Artist
import com.musicapp.core.model.Playlist
import com.musicapp.core.model.Track
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.width

@Composable
fun LibraryScreen(
    onNavigate: (String, String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.PLAYLISTS) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Your Library",
                showProfile = true,
                onProfileClick = { onNavigate("profile", "") },
                actions = listOf(
                    com.musicapp.core.common.ui.navigation.TopAppBarAction(
                        icon = androidx.compose.material.icons.filled.Sort,
                        contentDescription = "Sort",
                        onClick = { /* Show sort bottom sheet */ }
                    ),
                    com.musicapp.core.common.ui.navigation.TopAppBarAction(
                        icon = androidx.compose.material.icons.filled.FilterList,
                        contentDescription = "Filter",
                        onClick = { /* Show filter bottom sheet */ }
                    ),
                    com.musicapp.core.common.ui.navigation.TopAppBarAction(
                        icon = androidx.compose.material.icons.filled.GridView,
                        contentDescription = "Toggle view",
                        onClick = { /* Toggle grid/list */ }
                    )
                )
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab row
                TabRow(
                    selectedTab = selectedTab,
                    onTabClick = { selectedTab = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Content
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        LibraryTab.PLAYLISTS -> PlaylistsContent(
                            playlists = (uiState as? LibraryUiState.Success)?.playlists ?: emptyList(),
                            onItemClick = { playlist -> onNavigate("playlistDetail", playlist.id) },
                            onPlayTrack = onPlayTrack,
                            onCreateClick = { onNavigate("playlistCreate", "") },
                            emptyMessage = "You don't have any playlists yet"
                        )
                        LibraryTab.ALBUMS -> AlbumsContent(
                            albums = (uiState as? LibraryUiState.Success)?.albums ?: emptyList(),
                            onItemClick = { album -> onNavigate("albumDetail", album.id) },
                            onPlayTrack = onPlayTrack,
                            emptyMessage = "You haven't saved any albums yet"
                        )
                        LibraryTab.ARTISTS -> ArtistsContent(
                            artists = (uiState as? LibraryUiState.Success)?.artists ?: emptyList(),
                            onItemClick = { artist -> onNavigate("artistDetail", artist.id) },
                            onPlayTrack = onPlayTrack,
                            emptyMessage = "You aren't following any artists yet"
                        )
                        LibraryTab.LIKED_SONGS -> LikedSongsContent(
                            tracks = (uiState as? LibraryUiState.Success)?.likedSongs ?: emptyList(),
                            onItemClick = { track, queue -> onPlayTrack(track, queue) },
                            onPlayTrack = onPlayTrack,
                            emptyMessage = "No liked songs yet"
                        )
                        LibraryTab.RECENTLY_PLAYED -> RecentlyPlayedContent(
                            tracks = (uiState as? LibraryUiState.Success)?.recentlyPlayed ?: emptyList(),
                            onItemClick = { track, queue -> onPlayTrack(track, queue) },
                            onPlayTrack = onPlayTrack,
                            emptyMessage = "No recent plays"
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun TabRow(
    selectedTab: LibraryTab,
    onTabClick: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(LibraryTab.values()) { tab ->
            androidx.compose.material3.Chip(
                selected = selectedTab == tab,
                onClick = { onTabClick(tab) },
                modifier = Modifier.padding(vertical = 4.dp),
                label = { Text(text = tab.label) },
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun PlaylistsContent(
    playlists: List<Playlist>,
    onItemClick: (Playlist) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onCreateClick: () -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier
) {
    if (playlists.isEmpty()) {
        EmptyState(
            icon = androidx.compose.material.icons.filled.PlaylistPlay,
            title = "No Playlists",
            message = emptyMessage,
            actionText = "Create Playlist",
            onActionClick = onCreateClick,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(playlist) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) },
                    showOwner = true
                )
            }
        }
    }
}

@Composable
fun AlbumsContent(
    albums: List<Album>,
    onItemClick: (Album) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier
) {
    if (albums.isEmpty()) {
        EmptyState(
            icon = androidx.compose.material.icons.filled.Album,
            title = "No Albums",
            message = emptyMessage,
            modifier = modifier
        )
    } else {
        LazyHorizontalGrid(
            modifier = modifier.fillMaxSize(),
            rows = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(albums) { album ->
                AlbumCard(
                    album = album,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(album) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }
    }
}

@Composable
fun ArtistsContent(
    artists: List<Artist>,
    onItemClick: (Artist) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier
) {
    if (artists.isEmpty()) {
        EmptyState(
            icon = androidx.compose.material.icons.filled.Person,
            title = "No Artists",
            message = emptyMessage,
            modifier = modifier
        )
    } else {
        LazyHorizontalGrid(
            modifier = modifier.fillMaxSize(),
            rows = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(artists) { artist ->
                ArtistCard(
                    artist = artist,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(artist) },
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }
    }
}

@Composable
fun LikedSongsContent(
    tracks: List<Track>,
    onItemClick: (Track, List<Track>) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier
) {
    if (tracks.isEmpty()) {
        EmptyState(
            icon = androidx.compose.material.icons.filled.Favorite,
            title = "No Liked Songs",
            message = emptyMessage,
            modifier = modifier
        )
    } else {
        // Play all button at top
        Column(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                PlayAllButton(
                    onClick = { onPlayTrack(tracks.first(), tracks) },
                    trackCount = tracks.size
                )
            }

            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(tracks) { track ->
                    TrackCard(
                        track = track,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onItemClick(track, tracks) },
                        onPlayClick = { onPlayTrack(track, tracks) },
                        imageLoader = remember { coil.ImageLoader(LocalContext.current) },
                        showAlbum = true,
                        showArtist = true,
                        showTrackNumber = true,
                        trackNumber = tracks.indexOf(track) + 1
                    )
                    androidx.compose.foundation.layout.Divider(
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedContent(
    tracks: List<Track>,
    onItemClick: (Track, List<Track>) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier
) {
    if (tracks.isEmpty()) {
        EmptyState(
            icon = androidx.compose.material.icons.filled.History,
            title = "No Recent Plays",
            message = emptyMessage,
            modifier = modifier
        )
    } else {
        HorizontalTrackList(
            tracks = tracks,
            modifier = modifier.fillMaxSize(),
            onItemClick = onItemClick,
            onPlayClick = onPlayTrack,
            imageLoader = remember { coil.ImageLoader(LocalContext.current) },
            showAlbum = true,
            showArtist = true
        )
    }
}

@Composable
fun PlayAllButton(
    onClick: () -> Unit,
    trackCount: Int,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.FilledButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.ButtonDefaults.filledButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.filled.PlayArrow,
                contentDescription = "Play all",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Play all ($trackCount songs)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
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
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.TextAlign.Center
            )
            actionText?.let { text ->
                onActionClick?.let { click ->
                    androidx.compose.material3.FilledButton(
                        onClick = click,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = text)
                    }
                }
            }
        }
    }
}

enum class LibraryTab(val label: String) {
    PLAYLISTS("Playlists"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    LIKED_SONGS("Liked Songs"),
    RECENTLY_PLAYED("Recently Played")
}

@AndroidEntryPoint
class LibraryViewModel @Inject constructor(
    private val playlistRepository: com.musicapp.core.data.PlaylistRepository,
    private val albumRepository: com.musicapp.core.data.AlbumRepository,
    private val artistRepository: com.musicapp.core.data.ArtistRepository,
    private val trackRepository: com.musicapp.core.data.TrackRepository
) : ViewModel() {
    private val _uiState = mutableStateOf<LibraryUiState>(LibraryUiState.Loading)
    val uiState: androidx.compose.runtime.State<LibraryUiState> = _uiState

    init {
        loadLibrary()
    }

    fun loadLibrary() {
        _uiState.value = LibraryUiState.Loading
        lifecycleScope.launch {
            val playlistsResult = playlistRepository.getUserPlaylists(50, 0)
            val albumsResult = albumRepository.getSavedAlbums(50, 0)
            val artistsResult = artistRepository.getFollowedArtists(50, 0)
            val likedSongsResult = trackRepository.getLikedTracks(50, 0)
            val recentlyPlayedResult = trackRepository.getRecentlyPlayed(50)

            val playlists = playlistsResult.getOrNull()?.items ?: emptyList()
            val albums = albumsResult.getOrNull()?.items ?: emptyList()
            val artists = artistsResult.getOrNull()?.items ?: emptyList()
            val likedSongs = likedSongsResult.getOrNull()?.items ?: emptyList()
            val recentlyPlayed = recentlyPlayedResult.getOrNull() ?: emptyList()

            _uiState.value = LibraryUiState.Success(
                playlists = playlists,
                albums = albums,
                artists = artists,
                likedSongs = likedSongs,
                recentlyPlayed = recentlyPlayed
            )
        }
    }
}

sealed interface LibraryUiState {
    object Loading : LibraryUiState
    data class Success(
        val playlists: List<Playlist>,
        val albums: List<Album>,
        val artists: List<Artist>,
        val likedSongs: List<Track>,
        val recentlyPlayed: List<Track>
    ) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}