package com.musicapp.core.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark theme (primary) - Spotify-inspired
val DarkColorScheme = darkColorScheme(
    // Primary - Spotify Green
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1ED760),
    onPrimaryContainer = Color.Black,

    // Secondary
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color.White,

    // Tertiary
    tertiary = Color(0xFFFFFFFF),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2A2A2A),
    onTertiaryContainer = Color.White,

    // Surface
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFB3B3B3),

    // Background
    background = Color(0xFF000000),
    onBackground = Color.White,

    // Error
    error = Color(0xFFE22134),
    onError = Color.White,
    errorContainer = Color(0xFF4C1D1D),
    onErrorContainer = Color(0xFFFFDAD6),

    // Outline
    outline = Color(0xFF404040),
    outlineVariant = Color(0xFF2A2A2A),

    // Scrim
    scrim = Color.Black,

    // Inverse
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF1DB954),

    // Shadow
    shadow = Color.Black,

    // Surface tint
    surfaceTint = Color(0xFF1DB954)
)

// Light theme (alternative)
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1ED760),
    onPrimaryContainer = Color.Black,

    secondary = Color(0xFF121212),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color.Black,

    tertiary = Color(0xFF121212),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E0E0),
    onTertiaryContainer = Color.Black,

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121212),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF121212),

    error = Color(0xFFE22134),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF4C1D1D),

    outline = Color(0xFFC0C0C0),
    outlineVariant = Color(0xFFE0E0E0),

    scrim = Color.Black,

    inverseSurface = Color(0xFF121212),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF1DB954),

    shadow = Color.Black,

    surfaceTint = Color(0xFF1DB954)
)

// Custom semantic colors for music app
object MusicColors {
    val LikedRed = Color(0xFFE22134)
    val ExplicitLabel = Color(0xFFB3B3B3)
    val OfflineGray = Color(0xFF666666)
    val PlayButtonGreen = Color(0xFF1DB954)
    val PlayButtonHover = Color(0xFF1ED760)

    // Artist/Album card backgrounds (subtle gradients)
    val CardBackgroundStart = Color(0xFF1A1A1A)
    val CardBackgroundEnd = Color(0xFF121212)

    // Progress bar
    val ProgressActive = Color(0xFF1DB954)
    val ProgressBuffered = Color(0xFF404040)
    val ProgressBackground = Color(0xFF2A2A2A)

    // Queue/History
    val CurrentlyPlaying = Color(0xFF1DB954)
    val QueueBackground = Color(0xFF181818)

    // Miniplayer
    val MiniplayerBackground = Color(0xFF181818)
    val MiniplayerDivider = Color(0xFF2A2A2A)
}

@Composable
fun isDarkTheme(): Boolean = MaterialTheme.colorScheme.isDark