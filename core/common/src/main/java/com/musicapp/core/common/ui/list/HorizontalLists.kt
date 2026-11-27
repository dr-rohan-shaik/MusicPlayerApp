package com.musicapp.core.common.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicapp.core.common.theme.CardSubtitle
import com.musicapp.core.common.theme.MetadataText
import com.musicapp.core.common.theme.SectionHeader
import com.musicapp.core.model.Album
import com.musicapp.core.model.Artist
import com.musicapp.core.model.Playlist
import com.musicapp.core.model.Track
import com.musicapp.core.common.ui.card.AlbumCard
import com.musicapp.core.common.ui.card.ArtistCard
import com.musicapp.core.common.ui.card.PlaylistCard
import com.musicapp.core.common.ui.card.TrackCard
import coil.ImageLoader

@Composable
fun HorizontalAlbumList(
    albums: List<Album>,
    modifier: Modifier = Modifier,
    onItemClick: (Album) -> Unit,
    imageLoader: ImageLoader,
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (onSeeAllClick != null) {
            SectionHeader(
                title = "Albums",
                onSeeAllClick = onSeeAllClick,
                modifier = Modifier.padding(horizontal = 16.dp, bottom = 4.dp)
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            flingBehavior = androidx.compose.foundation.gestures.FlingBehavior(
                rememberScrollState().flingBehavior
            )
        ) {
            items(albums) { album ->
                AlbumCard(
                    album = album,
                    modifier = Modifier.width(160.dp),
                    onClick = { onItemClick(album) },
                    imageLoader = imageLoader
                )
            }
        }
    }
}

@Composable
fun HorizontalPlaylistList(
    playlists: List<Playlist>,
    modifier: Modifier = Modifier,
    onItemClick: (Playlist) -> Unit,
    imageLoader: ImageLoader,
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (onSeeAllClick != null) {
            SectionHeader(
                title = "Playlists",
                onSeeAllClick = onSeeAllClick,
                modifier = Modifier.padding(horizontal = 16.dp, bottom = 4.dp)
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    modifier = Modifier.width(160.dp),
                    onClick = { onItemClick(playlist) },
                    imageLoader = imageLoader
                )
            }
        }
    }
}

@Composable
fun HorizontalArtistList(
    artists: List<Artist>,
    modifier: Modifier = Modifier,
    onItemClick: (Artist) -> Unit,
    imageLoader: ImageLoader,
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (onSeeAllClick != null) {
            SectionHeader(
                title = "Artists",
                onSeeAllClick = onSeeAllClick,
                modifier = Modifier.padding(horizontal = 16.dp, bottom = 4.dp)
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            items(artists) { artist ->
                ArtistCard(
                    artist = artist,
                    modifier = Modifier.width(120.dp),
                    onClick = { onItemClick(artist) },
                    imageLoader = imageLoader
                )
            }
        }
    }
}

@Composable
fun HorizontalTrackList(
    tracks: List<Track>,
    modifier: Modifier = Modifier,
    onItemClick: (Track) -> Unit,
    onPlayClick: ((Track) -> Unit)? = null,
    imageLoader: ImageLoader,
    showAlbum: Boolean = true,
    showArtist: Boolean = true
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(tracks) { track ->
            TrackCard(
                track = track,
                modifier = Modifier.width(200.dp),
                onClick = { onItemClick(track) },
                onPlayClick = onPlayClick?.let { { it(track) } },
                imageLoader = imageLoader,
                showAlbum = showAlbum,
                showArtist = showArtist
            )
        }
    }
}

@Composable
fun VerticalTrackList(
    tracks: List<Track>,
    modifier: Modifier = Modifier,
    onItemClick: (Track) -> Unit,
    onPlayClick: ((Track) -> Unit)? = null,
    imageLoader: ImageLoader,
    showAlbum: Boolean = true,
    showArtist: Boolean = true,
    showTrackNumbers: Boolean = false
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(tracks) { track ->
            TrackCard(
                track = track,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onItemClick(track) },
                onPlayClick = onPlayClick?.let { { it(track) } },
                imageLoader = imageLoader,
                showAlbum = showAlbum,
                showArtist = showArtist,
                showTrackNumber = showTrackNumbers,
                trackNumber = if (showTrackNumbers) tracks.indexOf(track) + 1 else null
            )
            androidx.compose.foundation.layout.Divider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun CompactTrackList(
    tracks: List<Track>,
    modifier: Modifier = Modifier,
    onItemClick: (Track) -> Unit,
    onPlayClick: ((Track) -> Unit)? = null,
    imageLoader: ImageLoader,
    showAlbum: Boolean = true,
    showArtist: Boolean = true
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(tracks) { track ->
            TrackCard(
                track = track,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onItemClick(track) },
                onPlayClick = onPlayClick?.let { { it(track) } },
                imageLoader = imageLoader,
                showAlbum = showAlbum,
                showArtist = showArtist
            )
        }
    }
}