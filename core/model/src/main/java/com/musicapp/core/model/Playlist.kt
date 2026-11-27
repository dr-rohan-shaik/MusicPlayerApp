package com.musicapp.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val artworkUrl: String?,
    val owner: UserRef,
    val isPublic: Boolean,
    val isCollaborative: Boolean,
    val trackCount: Int,
    val duration: Long,
    val followerCount: Long,
    val tracks: List<TrackRef>,
    val createdAt: Long,
    val updatedAt: Long,
    val source: PlaylistSource = PlaylistSource.USER_CREATED
) : Parcelable {

    val formattedDuration: String
        get() = formatDuration(duration)

    val formattedCreatedAt: String
        get() = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(createdAt))

    companion object {
        private fun formatDuration(ms: Long): String {
            val totalSeconds = ms / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            return if (hours > 0) "%dh %dm".format(hours, minutes) else "%dm".format(minutes)
        }
    }
}

enum class PlaylistSource { USER_CREATED, FOLLOWED }

@Parcelize
data class UserRef(
    val id: String,
    val name: String,
    val avatarUrl: String?
) : Parcelable