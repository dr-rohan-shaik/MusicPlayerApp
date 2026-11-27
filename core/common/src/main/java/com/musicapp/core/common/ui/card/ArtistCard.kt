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
import androidx.compose.foundation.shape.CircleShape
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
import com.musicapp.core.common.theme.MusicColors
import com.musicapp.core.common.theme.MusicShapes
import com.musicapp.core.common.theme.MetadataText
import com.musicapp.core.model.Artist
import com.musicapp.core.model.ArtistRef
import androidx.compose.material.icons.filled.Verified

@Composable
fun ArtistCard(
    artist: Artist,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageLoader: ImageLoader,
    showFollowers: Boolean = true,
    showVerified: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular artist image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MusicColors.CardBackgroundEnd)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(imageLoader.context)
                    .data(artist.artworkUrl)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = artist.name
            )
        }

        // Artist name with verification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CardTitle(
                text = artist.name,
                modifier = Modifier.weight(1f),
                maxLines = 2
            )
            if (showVerified && artist.isVerified) {
                Icon(
                    imageVector = Verified,
                    contentDescription = "Verified artist",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp, top = 2.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Followers count
        if (showFollowers) {
            MetadataText(
                text = "${formatFollowerCount(artist.followerCount)} followers",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, bottom = 4.dp)
            )
        }

        // Genres
        if (artist.genres.isNotEmpty()) {
            MetadataText(
                text = artist.genres.take(3).joinToString(", "),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun ArtistCardSmall(
    artist: ArtistRef,
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
        // Circular avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MusicColors.CardBackgroundEnd)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(imageLoader.context)
                    .data(artist.artworkUrl)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = artist.name
            )
        }

        // Name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp)
                .weight(1f)
        ) {
            CardTitle(text = artist.name, maxLines = 1, modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun formatFollowerCount(count: Long): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0)
        count >= 1_000 -> "%.1fK".format(count / 1_000.0)
        else -> count.toString()
    }
}