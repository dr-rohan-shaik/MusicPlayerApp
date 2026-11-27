package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "albums",
    indices = [Index("artistId"), Index("isLiked"), Index("addedAt")]
)
data class AlbumEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistId: String,
    val artworkUrl: String?,
    val releaseDate: Long,
    val trackCount: Int,
    val duration: Long,
    val type: Int, // 0=ALBUM, 1=SINGLE, 2=EP, 3=COMPILATION
    val isLiked: Boolean,
    val addedAt: Long
)