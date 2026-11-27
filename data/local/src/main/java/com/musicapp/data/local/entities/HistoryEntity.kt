package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playback_history",
    indices = [Index("playedAt")]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackId: String,
    val trackTitle: String,
    val artistName: String,
    val albumTitle: String,
    val artworkUrl: String?,
    val duration: Long,
    val playedAt: Long,
    val source: Int // 0 = REMOTE, 1 = LOCAL_IMPORT
)