package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index("artistId"),
        Index("albumId"),
        Index("isLiked"),
        Index("addedAt"),
        Index("source")
    ]
)
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistId: String,
    val albumId: String,
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
    val lastPlayedAt: Long?,
    val source: Int // 0 = REMOTE, 1 = LOCAL_IMPORT
)