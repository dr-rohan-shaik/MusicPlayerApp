package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "artists",
    indices = [Index("isFollowed"), Index("followerCount")]
)
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val artworkUrl: String?,
    val genres: String, // JSON array stored as string
    val followerCount: Long,
    val isVerified: Boolean,
    val biography: String?,
    val isFollowed: Boolean
)