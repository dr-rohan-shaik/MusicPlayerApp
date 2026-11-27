package com.musicapp.core.common.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val MusicShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

// Semantic shapes for music app
object MusicShapes {
    val Card = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val CardLarge = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val Image = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    val ImageCircle = androidx.compose.foundation.shape.CircleShape
    val Button = androidx.compose.foundation.shape.RoundedCornerShape(100.dp) // Pill shape
    val ButtonSquare = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val Chip = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val BottomSheet = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val Dialog = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
    val ProgressBar = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
    val PlayButton = androidx.compose.foundation.shape.CircleShape
    val ExplicitBadge = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
    val Tooltip = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
}