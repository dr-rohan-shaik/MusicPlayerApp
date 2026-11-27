package com.musicapp.core.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MusicTheme(
    darkTheme: Boolean = true, // Default to dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MusicTypography,
        shapes = MusicShapes,
        content = content
    )
}

// Convenience extensions
@Composable
fun MusicSurface(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    tonalElevation: Float = 0f,
    shadowElevation: Float = 0f,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        shape = shape,
        content = content
    )
}

// Card elevation helpers
@Composable
fun MusicCard(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    elevation: Float = 1f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) androidx.compose.foundation.clickable(interactionSource = interactionSource, onClick = onClick)
                else androidx.compose.ui.Modifier
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = elevation,
        shadowElevation = elevation,
        shape = MaterialTheme.shapes.medium,
        content = content
    )
}

@Composable
fun MusicElevatedCard(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    elevation: Float = 3f,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = elevation,
        shadowElevation = elevation,
        shape = MaterialTheme.shapes.medium,
        content = content
    )
}

// Text helpers with semantic styles
@Composable
fun ScreenTitle(text: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
fun SectionHeader(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    maxLines: Int = 1,
    overflow: androidx.compose.ui.text.TextOverflow = androidx.compose.ui.text.TextOverflow.Ellipsis
) {
    Text(
        text = text,
        style = MusicTextStyles.SectionHeader,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun CardTitle(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    maxLines: Int = 2,
    overflow: androidx.compose.ui.text.TextOverflow = androidx.compose.ui.text.TextOverflow.Ellipsis
) {
    Text(
        text = text,
        style = MusicTextStyles.CardTitle,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun CardSubtitle(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    maxLines: Int = 1,
    overflow: androidx.compose.ui.text.TextOverflow = androidx.compose.ui.text.TextOverflow.Ellipsis
) {
    Text(
        text = text,
        style = MusicTextStyles.CardSubtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun TrackTitle(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    maxLines: Int = 1,
    overflow: androidx.compose.ui.text.TextOverflow = androidx.compose.ui.text.TextOverflow.Ellipsis
) {
    Text(
        text = text,
        style = MusicTextStyles.TrackTitle,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun TrackArtist(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    maxLines: Int = 1,
    overflow: androidx.compose.ui.text.TextOverflow = androidx.compose.ui.text.TextOverflow.Ellipsis
) {
    Text(
        text = text,
        style = MusicTextStyles.TrackArtist,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun MetadataText(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text,
        style = MusicTextStyles.Metadata,
        color = color,
        modifier = modifier
    )
}

@Composable
fun DurationText(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text,
        style = MusicTextStyles.Duration,
        color = color,
        modifier = modifier
    )
}

@Composable
fun ButtonText(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimary
) {
    Text(
        text = text,
        style = MusicTextStyles.Button,
        color = color,
        modifier = modifier
    )
}

@Composable
fun ChipText(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text,
        style = MusicTextStyles.Chip,
        color = color,
        modifier = modifier
    )
}

@Composable
fun ExplicitBadge(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .background(MusicColors.ExplicitLabel.copy(alpha = 0.2f), MusicShapes.ExplicitBadge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "E",
            style = MusicTextStyles.ExplicitBadge,
            color = MusicColors.ExplicitLabel
        )
    }
}

@Composable
fun NowPlayingTitle(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    Text(
        text = text,
        style = MusicTextStyles.NowPlayingTitle,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun NowPlayingArtist(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    Text(
        text = text,
        style = MusicTextStyles.NowPlayingArtist,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun QueueNumber(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text,
        style = MusicTextStyles.QueueNumber,
        color = color,
        modifier = modifier
    )
}