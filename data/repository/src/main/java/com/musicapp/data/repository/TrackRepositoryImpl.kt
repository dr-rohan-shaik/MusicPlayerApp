package com.musicapp.data.repository

import com.musicapp.core.data.PagedResult
import com.musicapp.core.data.TrackRepository
import com.musicapp.core.model.Track
import com.musicapp.core.model.TrackSource
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.TrackDao
import com.musicapp.data.local.entities.TrackEntity
import com.musicapp.data.remote.MusicApiService
import com.musicapp.data.remote.ApiResponse
import com.musicapp.data.remote.TrackDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao,
    private val apiService: MusicApiService
) : TrackRepository {

    override suspend fun getTrack(id: String): Result<Track> = try {
        // Try local first
        val localTrack = trackDao.getById(id)
        if (localTrack != null) {
            Result.success(localTrack.toDomain())
        } else {
            // Fetch from remote
            val response = apiService.getTrack(id)
            if (response.isSuccessful && response.body()?.isSuccess() == true) {
                val track = response.body()!!.data!!.toDomain()
                trackDao.insert(track.toEntity())
                Result.success(track)
            } else {
                Result.failure(Exception("Failed to fetch track: ${response.message()}"))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> = try {
        val localTracks = trackDao.getByIds(ids).map { it.toDomain() }
        val missingIds = ids.filter { id -> !localTracks.any { it.id == id } }

        if (missingIds.isNotEmpty()) {
            // Fetch missing from remote (batch if API supports)
            missingIds.forEach { id ->
                val response = apiService.getTrack(id)
                if (response.isSuccessful && response.body()?.isSuccess() == true) {
                    val track = response.body()!!.data!!.toDomain()
                    trackDao.insert(track.toEntity())
                    localTracks.add(track)
                }
            }
        }
        Result.success(localTracks)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchTracks(query: String, limit: Int, offset: Int): Result<PagedResult<Track>> = try {
        val response = apiService.searchTracks(query, limit, offset)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val dto = response.body()!!.data!!
            val tracks = dto.tracks?.items?.map { it.toDomain() } ?: emptyList()
            val total = dto.tracks?.total ?: 0L
            Result.success(PagedResult(tracks, total, offset, limit, offset + limit < total))
        } else {
            Result.failure(Exception("Search failed: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecommendedTracks(limit: Int): Result<List<Track>> = try {
        val response = apiService.getRecommendations(null, null, null, limit)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val tracks = response.body()!!.data!!.tracks.map { it.toDomain() }
            Result.success(tracks)
        } else {
            Result.failure(Exception("Failed to get recommendations: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentlyPlayed(limit: Int): Result<List<Track>> = try {
        val tracks = trackDao.getRecentlyPlayed(limit).map { it.toDomain() }
        Result.success(tracks)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getLikedTracks(limit: Int, offset: Int): Result<PagedResult<Track>> = try {
        val tracks = trackDao.getLikedTracks(limit, offset).map { it.toDomain() }
        // Note: Would need a count query for total
        Result.success(PagedResult(tracks, tracks.size.toLong(), offset, limit, tracks.size >= limit))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun likeTrack(trackId: String): Result<Unit> = try {
        val response = apiService.likeTrack(trackId)
        if (response.isSuccessful) {
            trackDao.setLiked(trackId, true)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to like track: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unlikeTrack(trackId: String): Result<Unit> = try {
        val response = apiService.unlikeTrack(trackId)
        if (response.isSuccessful) {
            trackDao.setLiked(trackId, false)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unlike track: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun incrementPlayCount(trackId: String): Result<Unit> = try {
        trackDao.incrementPlayCount(trackId, System.currentTimeMillis())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeRecentlyPlayed(limit: Int): Flow<List<Track>> =
        trackDao.observeRecentlyPlayed(limit).map { it.map { it.toDomain() } }

    override fun observeLikedTracks(): Flow<List<Track>> =
        trackDao.observeLikedTracks().map { it.map { it.toDomain() } }
}

// Extension functions for conversion
private fun TrackEntity.toDomain(): Track {
    return Track(
        id = this.id,
        title = this.title,
        artist = com.musicapp.core.model.ArtistRef(this.artistId, "", null), // Would need join
        album = com.musicapp.core.model.AlbumRef(this.albumId, "", null),
        duration = this.duration,
        artworkUrl = this.artworkUrl,
        audioUrl = this.audioUrl,
        localPath = this.localPath,
        genre = this.genre,
        releaseDate = this.releaseDate,
        trackNumber = this.trackNumber,
        discNumber = this.discNumber,
        isExplicit = this.isExplicit,
        isLiked = this.isLiked,
        playCount = this.playCount,
        addedAt = this.addedAt,
        source = if (this.source == 1) TrackSource.LOCAL_IMPORT else TrackSource.REMOTE
    )
}

private fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = this.id,
        title = this.title,
        artistId = this.artist.id,
        albumId = this.album.id,
        duration = this.duration,
        artworkUrl = this.artworkUrl,
        audioUrl = this.audioUrl,
        localPath = this.localPath,
        genre = this.genre,
        releaseDate = this.releaseDate,
        trackNumber = this.trackNumber,
        discNumber = this.discNumber,
        isExplicit = this.isExplicit,
        isLiked = this.isLiked,
        playCount = this.playCount,
        addedAt = this.addedAt,
        lastPlayedAt = null,
        source = if (this.source == TrackSource.LOCAL_IMPORT) 1 else 0
    )
}

private fun TrackDto.toDomain(): Track {
    val artist = artists.firstOrNull()?.toDomainRef() ?: com.musicapp.core.model.ArtistRef("", "Unknown", null)
    val album = album.toDomainRef()

    return Track(
        id = this.id,
        title = this.name,
        artist = artist,
        album = album,
        duration = this.duration_ms,
        artworkUrl = images.maxByOrNull { it.width ?: 0 }?.url,
        audioUrl = preview_url,
        localPath = null,
        genre = null,
        releaseDate = null,
        trackNumber = this.track_number,
        discNumber = this.disc_number,
        isExplicit = this.explicit,
        isLiked = false,
        playCount = 0,
        addedAt = System.currentTimeMillis(),
        source = TrackSource.REMOTE
    )
}

private fun ArtistRefDto.toDomainRef(): com.musicapp.core.model.ArtistRef {
    return com.musicapp.core.model.ArtistRef(
        id = this.id,
        name = this.name,
        artworkUrl = images?.maxByOrNull { it.width ?: 0 }?.url
    )
}

private fun AlbumRefDto.toDomainRef(): com.musicapp.core.model.AlbumRef {
    return com.musicapp.core.model.AlbumRef(
        id = this.id,
        title = this.name,
        artworkUrl = images?.maxByOrNull { it.width ?: 0 }?.url
    )
}