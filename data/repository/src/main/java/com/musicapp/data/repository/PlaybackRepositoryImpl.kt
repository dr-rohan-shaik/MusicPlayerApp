package com.musicapp.data.repository

import com.musicapp.core.data.PagedResult
import com.musicapp.core.data.PlaybackRepository
import com.musicapp.core.data.PlaybackQueue
import com.musicapp.core.data.PlaybackState
import com.musicapp.core.model.Track
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.HistoryDao
import com.musicapp.data.local.dao.PlaybackStateDao
import com.musicapp.data.local.entities.HistoryEntity
import com.musicapp.data.local.entities.PlaybackStateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    private val playbackStateDao: PlaybackStateDao,
    private val historyDao: HistoryDao
) : PlaybackRepository {

    override suspend fun getQueue(): Result<PlaybackQueue> = try {
        val state = playbackStateDao.getState()
        state?.let {
            val queue = it.queueJson?.let { com.google.gson.Gson().fromJson(it, PlaybackQueue::class.java) }
            queue?.let { Result.success(it) } ?: Result.failure(Exception("No queue saved"))
        } ?: Result.failure(Exception("No playback state"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun setQueue(queue: PlaybackQueue): Result<Unit> = try {
        val json = com.google.gson.Gson().toJson(queue)
        val entity = PlaybackStateEntity(
            queueJson = json,
            currentIndex = queue.currentIndex,
            shuffleMode = queue.shuffleMode.ordinal,
            repeatMode = queue.repeatMode.ordinal,
            position = 0,
            volume = 1f,
            updatedAt = System.currentTimeMillis()
        )
        playbackStateDao.insert(entity)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addToQueue(track: Track, position: Int?): Result<Unit> = try {
        val currentQueue = getQueue().getOrNull() ?: PlaybackQueue(emptyList(), 0, com.musicapp.core.data.ShuffleMode.OFF, com.musicapp.core.data.RepeatMode.OFF)
        var newTracks = currentQueue.tracks.toMutableList()
        val insertPosition = position ?: newTracks.size
        newTracks.add(insertPosition, track)

        val newQueue = currentQueue.copy(tracks = newTracks)
        setQueue(newQueue).getOrThrow()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun removeFromQueue(trackId: String): Result<Unit> = try {
        val currentQueue = getQueue().getOrNull() ?: return Result.failure(Exception("No queue"))
        val newTracks = currentQueue.tracks.filter { it.id != trackId }
        var newIndex = currentQueue.currentIndex
        if (newIndex >= newTracks.size) newIndex = newTracks.size - 1
        if (newIndex < 0) newIndex = 0

        val newQueue = currentQueue.copy(tracks = newTracks, currentIndex = newIndex)
        setQueue(newQueue).getOrThrow()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun clearQueue(): Result<Unit> = try {
        val emptyQueue = PlaybackQueue(emptyList(), 0, com.musicapp.core.data.ShuffleMode.OFF, com.musicapp.core.data.RepeatMode.OFF)
        setQueue(emptyQueue).getOrThrow()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun reorderQueue(fromIndex: Int, toIndex: Int): Result<Unit> = try {
        val currentQueue = getQueue().getOrNull() ?: return Result.failure(Exception("No queue"))
        val tracks = currentQueue.tracks.toMutableList()
        val item = tracks.removeAt(fromIndex)
        tracks.add(toIndex, item)

        var newIndex = currentQueue.currentIndex
        if (currentQueue.currentIndex == fromIndex) newIndex = toIndex
        else if (currentQueue.currentIndex > fromIndex && currentQueue.currentIndex <= toIndex) newIndex -= 1
        else if (currentQueue.currentIndex < fromIndex && currentQueue.currentIndex >= toIndex) newIndex += 1

        val newQueue = currentQueue.copy(tracks = tracks, currentIndex = newIndex)
        setQueue(newQueue).getOrThrow()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPlaybackState(): Result<PlaybackState> = try {
        // Would be provided by PlayerController
        Result.success(PlaybackState.Idle)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun savePlaybackState(state: PlaybackState): Result<Unit> = try {
        // Persist state if needed
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addToHistory(track: Track): Result<Unit> = try {
        val entity = HistoryEntity(
            trackId = track.id,
            trackTitle = track.title,
            artistName = track.artist.name,
            albumTitle = track.album.title,
            artworkUrl = track.artworkUrl,
            duration = track.duration,
            playedAt = System.currentTimeMillis(),
            source = if (track.source == com.musicapp.core.model.TrackSource.LOCAL_IMPORT) 1 else 0
        )
        historyDao.insert(entity)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getHistory(limit: Int): Result<List<Track>> = try {
        val history = historyDao.getHistory(limit)
        // Would need to convert to Track objects
        Result.success(emptyList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeHistory(): Flow<List<Track>> =
        historyDao.observeHistory(50).map { it.map { entity ->
            Track(
                id = entity.trackId,
                title = entity.trackTitle,
                artist = com.musicapp.core.model.ArtistRef("", entity.artistName, null),
                album = com.musicapp.core.model.AlbumRef("", entity.albumTitle, null),
                duration = entity.duration,
                artworkUrl = entity.artworkUrl,
                audioUrl = null,
                localPath = null,
                genre = null,
                releaseDate = null,
                trackNumber = 0,
                discNumber = 0,
                isExplicit = false,
                isLiked = false,
                playCount = 0,
                addedAt = entity.playedAt,
                source = if (entity.source == 1) com.musicapp.core.model.TrackSource.LOCAL_IMPORT else com.musicapp.core.model.TrackSource.REMOTE
            )
        } }

    override fun observeQueue(): Flow<PlaybackQueue> =
        playbackStateDao.observeState().map { state ->
            state?.queueJson?.let { com.google.gson.Gson().fromJson(it, PlaybackQueue::class.java) }
                ?: PlaybackQueue(emptyList(), 0, com.musicapp.core.data.ShuffleMode.OFF, com.musicapp.core.data.RepeatMode.OFF)
        }.distinctUntilChanged()

    override fun observePlaybackState(): Flow<PlaybackState> =
        playbackStateDao.observeState().map { _ -> PlaybackState.Idle }.distinctUntilChanged()
}