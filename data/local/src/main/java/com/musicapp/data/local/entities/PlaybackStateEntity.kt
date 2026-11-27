package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state")
data class PlaybackStateEntity(
    @PrimaryKey val id: Int = 1, // Singleton
    val queueJson: String?, // JSON serialized PlaybackQueue
    val currentIndex: Int,
    val shuffleMode: Int,
    val repeatMode: Int,
    val position: Long,
    val volume: Float,
    val updatedAt: Long
)