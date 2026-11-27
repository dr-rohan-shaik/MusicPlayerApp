package com.musicapp.core.common.ui.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Original
import com.musicapp.core.common.theme.CardTitle
import com.musicapp.core.common.theme.CardSubtitle
import com.musicapp.core.common.theme.MusicColors
import com.musicapp.core.common.theme.MusicShapes
import com.musicapp.core.common.theme.MetadataText
import com.musicapp.core.model.Playlist
import com.musicapp.core.model.TrackRef

@Composable
fun PlaylistCard(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageLoader: ImageLoader,
    showOwner: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Playlist artwork (4-image collage or single)
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(MusicColors.CardBackgroundEnd, MusicShapes.Card),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.tracks.size >= 4) {
                // 2x2 collage
                CollageArtwork(
                    tracks = playlist.tracks.take(4),
                    modifier = Modifier.fillMaxSize(),
                    imageLoader = imageLoader
                )
            } else {
                // Single image or placeholder
                AsyncImage(
                    model = ImageRequest.Builder(imageLoader.context)
                        .data(playlist.artworkUrl)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MusicShapes.Card),
                    contentScale = ContentScale.Crop,
                    contentDescription = playlist.name
                )
            }

            // Play indicator overlay
            PlayOverlay()
        }

        // Playlist name
        CardTitle(
            text = playlist.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, horizontal = 4.dp),
            maxLines = 2
        )

        // Owner name
        if (showOwner && playlist.owner.name.isNotBlank()) {
            CardSubtitle(
                text = "By ${playlist.owner.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, bottom = 4.dp),
                maxLines = 1
            )
        }

        // Track count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            MetadataText(text = "${playlist.trackCount} songs, ${playlist.formattedDuration}")
        }
    }
}

@Composable
fun PlaylistCardSmall(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageLoader: ImageLoader
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MusicShapes.Image)
                .background(MusicColors.CardBackgroundEnd)
        ) {
            if (playlist.tracks.size >= 4) {
                CollageArtwork(
                    tracks = playlist.tracks.take(4),
                    modifier = Modifier.fillMaxSize(),
                    imageLoader = imageLoader
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(imageLoader.context)
                        .data(playlist.artworkUrl)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier.fillMaxSize().clip(MusicShapes.Image),
                    contentScale = ContentScale.Crop,
                    contentDescription = playlist.name
                )
            }
        }

        // Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp)
                .weight(1f)
        ) {
            CardTitle(text = playlist.name, maxLines = 1, modifier = Modifier.fillMaxWidth())
            if (playlist.owner.name.isNotBlank()) {
                CardSubtitle(text = "By ${playlist.owner.name}", maxLines = 1, modifier = Modifier.fillMaxWidth())
            } else {
                MetadataText(text = "${playlist.trackCount} songs", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun PlayOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f), MusicShapes.Card),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material.Icon(
            imageVector = androidx.compose.material.icons.filled.PlayArrow,
            contentDescription = "Play",
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, androidx.compose.foundation.shape.CircleShape)
                .padding(12.dp)
        )
    }
}

@Composable
fun CollageArtwork(
    tracks: List<TrackRef>,
    modifier: Modifier,
    imageLoader: ImageLoader
) {
    // Simplified - would need actual artwork URLs from tracks
    // For now, show placeholder
    Box(
        modifier = modifier
            .clip(MusicShapes.Card)
    ) {
        // 2x2 grid of images would go here
        // Using a single placeholder for now
        AsyncImage(
            model = ImageRequest.Builder(imageLoader.context)
                .data(tracks.firstOrNull()?.let { "placeholder" } ?: "placeholder")
                .crossfade(true)
                .build(),
            modifier = Modifier.fillMaxSize().clip(MusicShapes.Card),
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )
    }
}