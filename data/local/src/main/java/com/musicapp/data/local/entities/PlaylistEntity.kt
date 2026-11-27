package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists",
    indices = [Index("ownerId"), Index("updatedAt"), Index("source")]
)
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val artworkUrl: String?,
    val ownerId: String,
    val isPublic: Boolean,
    val isCollaborative: Boolean,
    val trackCount: Int,
    val duration: Long,
    val followerCount: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val source: Int // 0 = USER_CREATED, 1 = FOLLOWED
)