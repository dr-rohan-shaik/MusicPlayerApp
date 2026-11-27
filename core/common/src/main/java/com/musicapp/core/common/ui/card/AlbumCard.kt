package com.musicapp.core.common.ui.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.musicapp.core.model.Album
import com.musicapp.core.model.AlbumRef

@Composable
fun AlbumCard(
    album: Album,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageLoader: ImageLoader,
    showExplicit: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Album artwork
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(MusicColors.CardBackgroundEnd, MusicShapes.Card)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(imageLoader.context)
                    .data(album.artworkUrl)
                    .crossfade(true)
                    .placeholder(R.color.placeholder)
                    .error(R.color.error)
                    .build(),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MusicShapes.Card),
                contentScale = ContentScale.Crop,
                contentDescription = album.title
            )
        }

        // Album title
        CardTitle(
            text = album.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, horizontal = 4.dp),
            maxLines = 2
        )

        // Artist name
        if (album.artist.name.isNotBlank()) {
            CardSubtitle(
                text = album.artist.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, bottom = 4.dp),
                maxLines = 1
            )
        }

        // Release year + explicit badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            MetadataText(text = album.releaseDate.formatYear())
            if (showExplicit && album.tracks.any { it.isExplicit }) {
                ExplicitBadge(modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun AlbumCardSmall(
    album: Album,
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
        AsyncImage(
            model = ImageRequest.Builder(imageLoader.context)
                .data(album.artworkUrl)
                .crossfade(true)
                .build(),
            modifier = Modifier
                .size(56.dp)
                .clip(MusicShapes.Image),
            contentScale = ContentScale.Crop,
            contentDescription = album.title
        )

        // Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp)
                .weight(1f)
        ) {
            CardTitle(text = album.title, maxLines = 1, modifier = Modifier.fillMaxWidth())
            CardSubtitle(text = album.artist.name, maxLines = 1, modifier = Modifier.fillMaxWidth())
        }
    }
}

// Extension for year formatting
fun Long.formatYear(): String {
    return java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault()).format(java.util.Date(this))
}