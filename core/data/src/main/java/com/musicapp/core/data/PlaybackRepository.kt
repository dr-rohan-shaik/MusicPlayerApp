package com.musicapp.core.data

import com.musicapp.core.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    // Queue management
    suspend fun getQueue(): Result<PlaybackQueue>
    suspend fun setQueue(queue: PlaybackQueue): Result<Unit>
    suspend fun addToQueue(track: Track, position: Int?): Result<Unit>
    suspend fun removeFromQueue(trackId: String): Result<Unit>
    suspend fun clearQueue(): Result<Unit>
    suspend fun reorderQueue(fromIndex: Int, toIndex: Int): Result<Unit>

    // Playback state
    suspend fun getPlaybackState(): Result<PlaybackState>
    suspend fun savePlaybackState(state: PlaybackState): Result<Unit>

    // History
    suspend fun addToHistory(track: Track): Result<Unit>
    suspend fun getHistory(limit: Int): Result<List<Track>>
    fun observeHistory(): Flow<List<Track>>

    // Current playback
    fun observeQueue(): Flow<PlaybackQueue>
    fun observePlaybackState(): Flow<PlaybackState>
}

@Parcelize
data class PlaybackQueue(
    val tracks: List<Track>,
    val currentIndex: Int,
    val shuffleMode: ShuffleMode,
    val repeatMode: RepeatMode
) : Parcelable

sealed interface PlaybackState {
    object Idle : PlaybackState
    object Buffering : PlaybackState
    data class Playing(val position: Long) : PlaybackState
    data class Paused(val position: Long) : PlaybackState
    data class Completed(val nextTrack: Track?) : PlaybackState
    data class Error(val message: String) : PlaybackState
}

enum class ShuffleMode { OFF, ON }
enum class RepeatMode { OFF, ALL, ONE }