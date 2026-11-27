package com.musicapp.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.MediaItemTransition
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.musicapp.core.data.PlaybackQueue
import com.musicapp.core.data.PlaybackState
import com.musicapp.core.data.RepeatMode
import com.musicapp.core.data.ShuffleMode
import com.musicapp.core.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class ExoPlayerController @Inject constructor(
    private val context: Context,
    private val mediaSession: MediaSession,
    private val mediaController: MediaController,
    private val notificationProvider: MediaNotificationProvider,
    private val scope: CoroutineScope
) : PlayerController, Player.Listener {

    private val exoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context))
        .build()
        .also {
            it.addListener(this)
            it.repeatMode = Player.REPEAT_MODE_OFF
            it.shuffleMode = Player.SHUFFLE_MODE_OFF
            it.setMediaItemTransition(MediaItemTransition.CROSSFADE)
        }

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack

    private val _queue = MutableStateFlow<PlaybackQueue>(PlaybackQueue(emptyList(), 0, ShuffleMode.OFF, RepeatMode.OFF))
    override val queue: StateFlow<PlaybackQueue> = _queue

    private val _position = MutableStateFlow(0L)
    override val position: StateFlow<Long> = _position

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration

    private val _bufferedPosition = MutableStateFlow(0L)
    override val bufferedPosition: StateFlow<Long> = _bufferedPosition

    init {
        // Observe position updates
        scope.launch {
            while (true) {
                kotlinx.coroutines.delay(500)
                _position.value = exoPlayer.currentPosition
                _duration.value = exoPlayer.duration
                _bufferedPosition.value = exoPlayer.bufferedPosition
            }
        }
    }

    override suspend fun play(track: Track, queue: List<Track> = emptyList(), index: Int = 0) {
        val mediaItems = (if (queue.isNotEmpty()) queue else listOf(track))
            .map { createMediaItem(it) }

        exoPlayer.setMediaItems(mediaItems, index)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        _queue.value = PlaybackQueue(
            tracks = if (queue.isNotEmpty()) queue else listOf(track),
            currentIndex = index,
            shuffleMode = ShuffleMode.OFF,
            repeatMode = RepeatMode.OFF
        )
        _currentTrack.value = (if (queue.isNotEmpty()) queue else listOf(track))[index]

        updateMediaSession()
        notificationProvider.updateNotification()
    }

    private fun createMediaItem(track: Track): MediaItem {
        val audioUri = track.audioUrl ?: track.localPath ?: return MediaItem.fromUri("file:///dev/null")

        return MediaItem.fromUri(audioUri)
            .buildUpon()
            .setMediaMetadata(MediaMetadata.Builder()
                .setMediaId(track.id)
                .setTitle(track.title)
                .setArtist(track.artist.name)
                .setAlbumTitle(track.album.title)
                .setArtworkUri(Uri.parse(track.artworkUrl ?: ""))
                .setDuration(track.duration)
                .build())
            .build()
    }

    override suspend fun playPause() {
        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
        updateMediaSession()
        notificationProvider.updateNotification()
    }

    override suspend fun pause() {
        exoPlayer.playWhenReady = false
        updateMediaSession()
        notificationProvider.updateNotification()
    }

    override suspend fun skipNext() {
        exoPlayer.seekToNext()
    }

    override suspend fun skipPrevious() {
        exoPlayer.seekToPrevious()
    }

    override suspend fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override suspend fun setShuffleMode(mode: ShuffleMode) {
        exoPlayer.shuffleMode = when (mode) {
            ShuffleMode.OFF -> Player.SHUFFLE_MODE_OFF
            ShuffleMode.ON -> Player.SHUFFLE_MODE_DEFAULT
        }
        val currentQueue = _queue.value
        _queue.value = currentQueue.copy(shuffleMode = mode)
        updateMediaSession()
    }

    override suspend fun setRepeatMode(mode: RepeatMode) {
        exoPlayer.repeatMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        val currentQueue = _queue.value
        _queue.value = currentQueue.copy(repeatMode = mode)
        updateMediaSession()
    }

    override suspend fun setVolume(volume: Float) {
        exoPlayer.volume = volume.coerceIn(0f, 1f)
    }

    override suspend fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = exoPlayer.playbackParameters.withSpeed(speed.coerceIn(0.5f, 2f))
    }

    override fun release() {
        exoPlayer.release()
        mediaSession.release()
    }

    // Player.Listener callbacks
    override fun onPlaybackStateChanged(state: Int) {
        _playbackState.value = when (state) {
            Player.STATE_IDLE -> PlaybackState.Idle
            Player.STATE_BUFFERING -> PlaybackState.Buffering
            Player.STATE_READY -> if (exoPlayer.playWhenReady) PlaybackState.Playing(exoPlayer.currentPosition) else PlaybackState.Paused(exoPlayer.currentPosition)
            Player.STATE_ENDED -> {
                val currentQueue = _queue.value
                val nextIndex = currentQueue.currentIndex + 1
                val nextTrack = if (nextIndex < currentQueue.tracks.size) currentQueue.tracks[nextIndex] else null
                PlaybackState.Completed(nextTrack)
            }
            else -> PlaybackState.Idle
        }
        updateMediaSession()
        notificationProvider.updateNotification()
    }

    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
        _playbackState.value = PlaybackState.Error(error.message ?: "Playback error")
        notificationProvider.updateNotification()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        updateMediaSession()
        notificationProvider.updateNotification()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (mediaItem != null) {
            val trackId = mediaItem.mediaId
            val currentQueue = _queue.value
            val track = currentQueue.tracks.find { it.id == trackId }
            track?.let {
                _currentTrack.value = it
                _queue.value = currentQueue.copy(currentIndex = currentQueue.tracks.indexOf(it))
            }
        }
        updateMediaSession()
        notificationProvider.updateNotification()
    }

    override fun onPositionDiscontinuity(reason: Int) {
        // Handle position changes (seek, skip, etc.)
    }

    private fun updateMediaSession() {
        val state = _playbackState.value
        val track = _currentTrack.value

        val playbackState = when (state) {
            is PlaybackState.Playing -> androidx.media3.session.PlaybackStateCompat.STATE_PLAYING
            is PlaybackState.Paused -> androidx.media3.session.PlaybackStateCompat.STATE_PAUSED
            is PlaybackState.Buffering -> androidx.media3.session.PlaybackStateCompat.STATE_BUFFERING
            is PlaybackState.Error -> androidx.media3.session.PlaybackStateCompat.STATE_ERROR
            else -> androidx.media3.session.PlaybackStateCompat.STATE_NONE
        }

        mediaController.setPlaybackState(
            playbackState,
            position = exoPlayer.currentPosition,
            speed = exoPlayer.playbackParameters.speed,
            bufferedPosition = exoPlayer.bufferedPosition
        )

        track?.let {
            mediaController.setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setMediaId(it.id)
                    .setTitle(it.title)
                    .setArtist(it.artist.name)
                    .setAlbumTitle(it.album.title)
                    .setArtworkUri(Uri.parse(it.artworkUrl ?: ""))
                    .setDuration(it.duration)
                    .build()
            )
        }
    }
}