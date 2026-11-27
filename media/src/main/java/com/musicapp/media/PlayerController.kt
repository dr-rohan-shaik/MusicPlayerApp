package com.musicapp.media

import com.musicapp.core.data.PlaybackState
import com.musicapp.core.data.PlaybackQueue
import com.musicapp.core.data.ShuffleMode
import com.musicapp.core.data.RepeatMode
import com.musicapp.core.model.Track
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface PlayerController {
    val playbackState: StateFlow<PlaybackState>
    val currentTrack: StateFlow<Track?>
    val queue: StateFlow<PlaybackQueue>
    val position: StateFlow<Long>
    val duration: StateFlow<Long>
    val bufferedPosition: StateFlow<Long>

    suspend fun play(track: Track, queue: List<Track> = emptyList(), index: Int = 0)
    suspend fun playPause()
    suspend fun pause()
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun seekTo(position: Long)
    suspend fun setShuffleMode(mode: ShuffleMode)
    suspend fun setRepeatMode(mode: RepeatMode)
    suspend fun setVolume(volume: Float)
    suspend fun setPlaybackSpeed(speed: Float)
    fun release()
}