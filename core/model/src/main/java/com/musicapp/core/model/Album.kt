package com.musicapp.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    val id: String,
    val title: String,
    val artist: ArtistRef,
    val artworkUrl: String?,
    val releaseDate: Long,
    val trackCount: Int,
    val duration: Long,
    val type: AlbumType,
    val isLiked: Boolean,
    val tracks: List<TrackRef>
) : Parcelable {

    val formattedReleaseDate: String
        get() = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(releaseDate))

    val formattedDuration: String
        get() = formatDuration(duration)

    companion object {
        private fun formatDuration(ms: Long): String {
            val totalSeconds = ms / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            return if (hours > 0) "%dh %dm".format(hours, minutes) else "%dm".format(minutes)
        }
    }
}

enum class AlbumType { ALBUM, SINGLE, EP, COMPILATION }