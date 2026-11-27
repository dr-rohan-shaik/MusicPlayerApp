package com.musicapp.core.domain

import com.musicapp.core.data.PlaybackRepository
import com.musicapp.core.data.PlaybackQueue
import com.musicapp.core.data.PlaybackState
import com.musicapp.core.data.ShuffleMode
import com.musicapp.core.data.RepeatMode
import com.musicapp.core.model.Track
import com.musicapp.media.PlayerController
import javax.inject.Inject

class PlaybackUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val playerController: PlayerController
) {
    suspend fun playTrack(track: Track, queue: List<Track> = emptyList(), startIndex: Int = 0) {
        val newQueue = if (queue.isNotEmpty()) queue else listOf(track)
        val index = if (queue.isNotEmpty()) startIndex else 0

        playbackRepository.setQueue(PlaybackQueue(newQueue, index, ShuffleMode.OFF, RepeatMode.OFF)).getOrThrow()
        playerController.play(newQueue[index])
        playbackRepository.addToHistory(track).getOrThrow()
    }

    suspend fun playPlaylist(playlist: com.musicapp.core.model.Playlist, shuffle: Boolean = false) {
        val tracks = playlist.tracks.map { it.toTrack() }
        val queue = if (shuffle) tracks.shuffled() else tracks
        playbackRepository.setQueue(PlaybackQueue(queue, 0, if (shuffle) ShuffleMode.ON else ShuffleMode.OFF, RepeatMode.OFF)).getOrThrow()
        playerController.play(queue[0])
        tracks.forEach { playbackRepository.addToHistory(it).getOrThrow() }
    }

    suspend fun playAlbum(album: com.musicapp.core.model.Album, shuffle: Boolean = false) {
        val tracks = album.tracks.map { it.toTrack() }
        val queue = if (shuffle) tracks.shuffled() else tracks
        playbackRepository.setQueue(PlaybackQueue(queue, 0, if (shuffle) ShuffleMode.ON else ShuffleMode.OFF, RepeatMode.OFF)).getOrThrow()
        playerController.play(queue[0])
        tracks.forEach { playbackRepository.addToHistory(it).getOrThrow() }
    }

    // Controls
    suspend fun togglePlayPause() = playerController.togglePlayPause()
    suspend fun pause() = playerController.pause()
    suspend fun skipNext() = playerController.skipNext()
    suspend fun skipPrevious() = playerController.skipPrevious()
    suspend fun seekTo(position: Long) = playerController.seekTo(position)
    suspend fun setShuffleMode(mode: ShuffleMode) = playerController.setShuffleMode(mode)
    suspend fun setRepeatMode(mode: RepeatMode) = playerController.setRepeatMode(mode)
    suspend fun setVolume(volume: Float) = playerController.setVolume(volume)
    suspend fun setPlaybackSpeed(speed: Float) = playerController.setPlaybackSpeed(speed)

    // Queue management
    suspend fun addToQueue(track: Track, position: Int? = null) = playbackRepository.addToQueue(track, position).getOrThrow()
    suspend fun removeFromQueue(trackId: String) = playbackRepository.removeFromQueue(trackId).getOrThrow()
    suspend fun clearQueue() = playbackRepository.clearQueue().getOrThrow()
    suspend fun reorderQueue(fromIndex: Int, toIndex: Int) = playbackRepository.reorderQueue(fromIndex, toIndex).getOrThrow()
}

private fun com.musicapp.core.model.TrackRef.toTrack(): Track {
    return Track(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = AlbumRef("", "", null), // Will be filled from context
        duration = this.duration,
        artworkUrl = null,
        audioUrl = null,
        localPath = null,
        genre = null,
        releaseDate = null,
        trackNumber = 0,
        discNumber = 0,
        isExplicit = false,
        isLiked = false,
        playCount = 0,
        addedAt = 0
    )
}