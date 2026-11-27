package com.musicapp.core.common.extensions

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Context extensions
fun Context.dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).roundToInt()
fun Context.pxToDp(px: Int): Float = px / resources.displayMetrics.density

// String extensions
fun String.toUri(): Uri = toUri()
fun String.isNullOrBlank(): Boolean = this.isNullOrEmpty() || this.trim().isEmpty()
fun String.capitalizeFirst(): String = if (isEmpty()) this else this[0].uppercase() + substring(1)

// Duration formatting
fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

fun Long.formatDurationLong(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) "%dh %dm".format(hours, minutes) else "%dm".format(minutes)
}

// Flow extensions
fun <T> kotlinx.coroutines.flow.Flow<T>.distinctUntilChangedBy(keySelector: (T) -> Any?): kotlinx.coroutines.flow.Flow<T> =
    map { keySelector(it) to it }
        .distinctUntilChanged { it.first }
        .map { it.second }

// CoroutineScope extensions
fun CoroutineScope.launchAndTrack(block: suspend CoroutineScope.() -> Unit): kotlinx.coroutines.Job {
    return launch { block() }
}

// AnnotatedString extensions
fun AnnotatedString.withExplicitMarker(
    explicit: Boolean,
    explicitStyle: SpanStyle = SpanStyle(
        color = com.musicapp.core.common.theme.MusicColors.ExplicitLabel,
        fontSize = 10.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    )
): AnnotatedString {
    if (!explicit) return this
    val builder = androidx.compose.ui.text.AnnotatedString.Builder(this)
    builder.addStyle(explicitStyle, length - 1, length)
    return builder.toAnnotatedString()
}

// Modifier extensions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.drawBehind

fun Modifier.shimmer(
    baseColor: Color = com.musicapp.core.common.theme.MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = com.musicapp.core.common.theme.MaterialTheme.colorScheme.surfaceContainerHighest
) = this.then(ShimmerModifier(baseColor, highlightColor))

private class ShimmerModifier(
    private val baseColor: Color,
    private val highlightColor: Color
) : LayoutModifier {
    @Override
    fun MeasureScope.measure(measurable: Measurable, constraints: androidx.compose.ui.layout.MeasureConstraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return MeasureScope.layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

    @Override
    fun androidx.compose.ui.draw.DrawScope.drawBehind() {
        // Shimmer effect implementation would go here
        // Using a simple gradient for now
    }
}

// Image loading helpers
fun String?.loadImage(
    imageLoader: coil.ImageLoader,
    placeholder: androidx.compose.ui.graphics.painter.Painter? = null,
    error: androidx.compose.ui.graphics.painter.Painter? = null,
    crossfade: Boolean = true
): coil.request.ImageRequest.Builder = coil.request.ImageRequest.Builder(imageLoader.context)
    .data(this)
    .placeholder(placeholder)
    .error(error)
    .crossfade(crossfade)
    .build()

// Number formatting
fun Int.formatWithCommas(): String = java.text.NumberFormat.getInstance().format(this)
fun Long.formatWithCommas(): String = java.text.NumberFormat.getInstance().format(this)

// List extensions
fun <T> List<T>.randomOrNull(): T? = if (isEmpty()) null else this[Random.nextInt(size)]

import kotlin.random.Random