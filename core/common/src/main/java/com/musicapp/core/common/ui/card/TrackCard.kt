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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.musicapp.core.common.theme.CardTitle
import com.musicapp.core.common.theme.CardSubtitle
import com.musicapp.core.common.theme.DurationText
import com.musicapp.core.common.theme.MusicColors
import com.musicapp.core.common.theme.MusicShapes
import com.musicapp.core.common.theme.TrackTitle
import com.musicapp.core.common.theme.TrackArtist
import com.musicapp.core.model.Track
import com.musicapp.core.model.TrackRef
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.foundation.layout.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.dp

@Composable
fun TrackCard(
    track: Track,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onPlayClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null,
    imageLoader: ImageLoader,
    showAlbum: Boolean = true,
    showArtist: Boolean = true,
    showDuration: Boolean = true,
    showTrackNumber: Boolean = false,
    trackNumber: Int? = null,
    isPlaying: Boolean = false,
    isCurrentQueue: Boolean = false
) {
    val showActions = onPlayClick != null || onMoreClick != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track number or play indicator
        if (showTrackNumber && trackNumber != null) {
            TrackNumber(
                number = trackNumber,
                isPlaying = isPlaying,
                isCurrentQueue = isCurrentQueue,
                modifier = Modifier
                    .width(48.dp)
                    .padding(end = 8.dp)
            )
        } else if (isPlaying && isCurrentQueue) {
            PlayingIndicator(modifier = Modifier.width(48.dp).padding(end = 8.dp))
        } else {
            androidx.compose.foundation.layout.Box(modifier = Modifier.width(48.dp).padding(end = 8.dp))
        }

        // Artwork
        AsyncImage(
            model = ImageRequest.Builder(imageLoader.context)
                .data(track.artworkUrl)
                .crossfade(true)
                .build(),
            modifier = Modifier
                .size(56.dp)
                .clip(MusicShapes.Image),
            contentScale = ContentScale.Crop,
            contentDescription = track.title
        )

        // Track info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp)
                .weight(1f)
        ) {
            TrackTitle(text = track.title, maxLines = 1, modifier = Modifier.fillMaxWidth())

            if (showArtist && track.artist.name.isNotBlank()) {
                TrackArtist(text = track.artist.name, maxLines = 1, modifier = Modifier.fillMaxWidth())
            }

            if (showAlbum && track.album.title.isNotBlank()) {
                MetadataText(
                    text = track.album.title,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Duration
        if (showDuration) {
            DurationText(
                text = track.formattedDuration,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // Actions
        if (showActions) {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                onPlayClick?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) androidx.compose.material.icons.filled.Pause else androidx.compose.material.icons.filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }
                }

                onMoreClick?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = MoreVert,
                            contentDescription = "More options"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackCardCompact(
    track: TrackRef,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onPlayClick: (() -> Unit)? = null,
    trackNumber: Int? = null,
    isPlaying: Boolean = false,
    isCurrentQueue: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track number/playing indicator
        if (trackNumber != null) {
            TrackNumber(
                number = trackNumber,
                isPlaying = isPlaying,
                isCurrentQueue = isCurrentQueue,
                modifier = Modifier
                    .width(32.dp)
                    .padding(end = 8.dp)
            )
        } else if (isPlaying && isCurrentQueue) {
            PlayingIndicator(modifier = Modifier.width(32.dp).padding(end = 8.dp))
        } else {
            androidx.compose.foundation.layout.Box(modifier = Modifier.width(32.dp).padding(end = 8.dp))
        }

        // Track info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .weight(1f)
        ) {
            TrackTitle(text = track.title, maxLines = 1, modifier = Modifier.fillMaxWidth())
            TrackArtist(text = track.artist.name, maxLines = 1, modifier = Modifier.fillMaxWidth())
        }

        // Duration
        DurationText(text = track.formattedDuration, modifier = Modifier.padding(end = 8.dp))

        // Play button
        onPlayClick?.let {
            IconButton(onClick = it) {
                Icon(
                    imageVector = if (isPlaying) androidx.compose.material.icons.filled.Pause else androidx.compose.material.icons.filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
    }
}

@Composable
fun TrackNumber(
    number: Int,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    isCurrentQueue: Boolean = false
) {
    if (isPlaying && isCurrentQueue) {
        PlayingIndicator(modifier)
    } else {
        com.musicapp.core.common.theme.QueueNumber(
            text = number.toString(),
            modifier = modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun PlayingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(16.dp)
        ) {
            // Animated equalizer bars
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(MusicColors.PlayButtonGreen, RoundedCornerShape(1.5.dp))
                            .graphicsLayer {
                                // Animation would be added here
                                scaleY = 0.3f + (i * 0.2f)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun ExplicitIndicator(
    modifier: Modifier = Modifier,
    isExplicit: Boolean
) {
    if (isExplicit) {
        com.musicapp.core.common.theme.ExplicitBadge(modifier)
    }
}