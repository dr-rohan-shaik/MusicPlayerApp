package com.musicapp.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val id: String,
    val title: String,
    val artist: ArtistRef,
    val album: AlbumRef,
    val duration: Long,
    val artworkUrl: String?,
    val audioUrl: String?,
    val localPath: String?,
    val genre: String?,
    val releaseDate: Long?,
    val trackNumber: Int,
    val discNumber: Int,
    val isExplicit: Boolean,
    val isLiked: Boolean,
    val playCount: Long,
    val addedAt: Long,
    val source: TrackSource = TrackSource.REMOTE
) : Parcelable {

    val formattedDuration: String
        get() = formatDuration(duration)

    companion object {
        private fun formatDuration(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
    }
}

enum class TrackSource { REMOTE, LOCAL_IMPORT }

@Parcelize
data class ArtistRef(
    val id: String,
    val name: String,
    val artworkUrl: String?
) : Parcelable

@Parcelize
data class AlbumRef(
    val id: String,
    val title: String,
    val artworkUrl: String?
) : Parcelable

@Parcelize
data class TrackRef(
    val id: String,
    val title: String,
    val artist: ArtistRef,
    val duration: Long
) : Parcelable