package com.musicapp.data.repository

import com.musicapp.core.data.PagedResult
import com.musicapp.core.data.PlaylistRepository
import com.musicapp.core.model.Playlist
import com.musicapp.core.model.PlaylistSource
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.PlaylistDao
import com.musicapp.data.local.entities.PlaylistEntity
import com.musicapp.data.local.entities.PlaylistTrackEntity
import com.musicapp.data.remote.MusicApiService
import com.musicapp.data.remote.ApiResponse
import com.musicapp.data.remote.PlaylistDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val apiService: MusicApiService
) : PlaylistRepository {

    override suspend fun getPlaylist(id: String): Result<Playlist> = try {
        val localPlaylist = playlistDao.getById(id)
        if (localPlaylist != null) {
            val tracks = playlistDao.getPlaylistTracks(id).map { it.trackId }
            // Would need to fetch track details
            Result.success(localPlaylist.toDomain(tracks))
        } else {
            val response = apiService.getPlaylist(id)
            if (response.isSuccessful && response.body()?.isSuccess() == true) {
                val playlist = response.body()!!.data!!.toDomain()
                playlistDao.insert(playlist.toEntity())
                Result.success(playlist)
            } else {
                Result.failure(Exception("Failed to fetch playlist: ${response.message()}"))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUserPlaylists(limit: Int, offset: Int): Result<PagedResult<Playlist>> = try {
        // Would need current user ID
        val playlists = playlistDao.getUserPlaylists("current_user", limit, offset).map { it.toDomain(emptyList()) }
        Result.success(PagedResult(playlists, playlists.size.toLong(), offset, limit, playlists.size >= limit))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFollowedPlaylists(limit: Int, offset: Int): Result<PagedResult<Playlist>> = try {
        val playlists = playlistDao.getFollowedPlaylists(limit, offset).map { it.toDomain(emptyList()) }
        Result.success(PagedResult(playlists, playlists.size.toLong(), offset, limit, playlists.size >= limit))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMadeForYouPlaylists(limit: Int): Result<List<Playlist>> = try {
        val playlists = playlistDao.getMadeForYouPlaylists(limit).map { it.toDomain(emptyList()) }
        Result.success(playlists)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPopularNearYou(limit: Int): Result<List<Playlist>> = try {
        val playlists = playlistDao.getPopularNearYou(limit).map { it.toDomain(emptyList()) }
        Result.success(playlists)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createPlaylist(request: com.musicapp.core.data.CreatePlaylistRequest): Result<Playlist> = try {
        val dto = com.musicapp.data.remote.CreatePlaylistRequestDto(
            name = request.name,
            description = request.description,
            public = request.isPublic,
            collaborative = request.isCollaborative
        )
        val response = apiService.createPlaylist(dto)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val playlist = response.body()!!.data!!.toDomain()
            playlistDao.insert(playlist.toEntity())
            Result.success(playlist)
        } else {
            Result.failure(Exception("Failed to create playlist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updatePlaylist(playlistId: String, request: com.musicapp.core.data.UpdatePlaylistRequest): Result<Playlist> = try {
        val dto = com.musicapp.data.remote.UpdatePlaylistRequestDto(
            name = request.name,
            description = request.description,
            public = request.isPublic,
            collaborative = request.isCollaborative
        )
        val response = apiService.updatePlaylist(playlistId, dto)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val playlist = response.body()!!.data!!.toDomain()
            playlistDao.update(playlist.toEntity())
            Result.success(playlist)
        } else {
            Result.failure(Exception("Failed to update playlist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deletePlaylist(playlistId: String): Result<Unit> = try {
        val response = apiService.deletePlaylist(playlistId)
        if (response.isSuccessful) {
            playlistDao.clearPlaylistTracks(playlistId)
            // Would need deleteById in DAO
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete playlist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>, position: Int?): Result<Unit> = try {
        val uris = trackIds.map { "spotify:track:$it" }
        val dto = com.musicapp.data.remote.AddTracksRequestDto(uris = uris, position = position)
        val response = apiService.addTracksToPlaylist(playlistId, dto)
        if (response.isSuccessful) {
            playlistDao.addTracksToPlaylist(playlistId, trackIds, position)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to add tracks: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun removeTracksFromPlaylist(playlistId: String, trackIds: List<String>): Result<Unit> = try {
        val trackUris = trackIds.map { com.musicapp.data.remote.TrackUriDto(uri = "spotify:track:$it") }
        val dto = com.musicapp.data.remote.RemoveTracksRequestDto(tracks = trackUris)
        val response = apiService.removeTracksFromPlaylist(playlistId, dto)
        if (response.isSuccessful) {
            playlistDao.removeTracksFromPlaylist(playlistId, trackIds)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to remove tracks: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun reorderPlaylistTracks(playlistId: String, fromIndex: Int, toIndex: Int): Result<Unit> = try {
        playlistDao.reorderTrack(playlistId, fromIndex, toIndex)
        // Would also need API call for remote sync
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun followPlaylist(playlistId: String): Result<Unit> = try {
        val response = apiService.followPlaylist(playlistId)
        if (response.isSuccessful) {
            // Update local source to FOLLOWED
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to follow playlist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unfollowPlaylist(playlistId: String): Result<Unit> = try {
        val response = apiService.unfollowPlaylist(playlistId)
        if (response.isSuccessful) {
            // Remove from local
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unfollow playlist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeUserPlaylists(): Flow<List<Playlist>> =
        playlistDao.observeUserPlaylists("current_user").map { it.map { it.toDomain(emptyList()) } }

    override fun observePlaylist(id: String): Flow<Playlist> =
        playlistDao.getPlaylistTracks(id).map { tracks ->
            playlistDao.getById(id)!!.toDomain(tracks.map { it.trackId })
        }.distinctUntilChanged()
}

// Conversion extensions
private fun PlaylistEntity.toDomain(trackIds: List<String>): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        artworkUrl = this.artworkUrl,
        owner = com.musicapp.core.model.UserRef(this.ownerId, "", null),
        isPublic = this.isPublic,
        isCollaborative = this.isCollaborative,
        trackCount = this.trackCount,
        duration = this.duration,
        followerCount = this.followerCount,
        tracks = trackIds.map { com.musicapp.core.model.TrackRef(it, "", com.musicapp.core.model.ArtistRef("", "", null), 0) },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        source = if (this.source == 1) PlaylistSource.FOLLOWED else PlaylistSource.USER_CREATED
    )
}

private fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        artworkUrl = this.artworkUrl,
        ownerId = this.owner.id,
        isPublic = this.isPublic,
        isCollaborative = this.isCollaborative,
        trackCount = this.trackCount,
        duration = this.duration,
        followerCount = this.followerCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        source = if (this.source == PlaylistSource.FOLLOWED) 1 else 0
    )
}

private fun PlaylistDto.toDomain(): Playlist {
    val trackIds = tracks?.items?.map { it.id } ?: emptyList()
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        artworkUrl = images?.maxByOrNull { it.width ?: 0 }?.url,
        owner = owner.toDomainRef(),
        isPublic = this.public,
        isCollaborative = this.collaborative,
        trackCount = tracks?.total ?: 0,
        duration = 0L, // Would need to calculate
        followerCount = followers?.total ?: 0,
        tracks = trackIds.map { com.musicapp.core.model.TrackRef(it, "", com.musicapp.core.model.ArtistRef("", "", null), 0) },
        createdAt = 0L, // Not in DTO
        updatedAt = 0L,
        source = if (this.owner.id == "current_user") PlaylistSource.USER_CREATED else PlaylistSource.FOLLOWED
    )
}

private fun UserRefDto.toDomainRef(): com.musicapp.core.model.UserRef {
    return com.musicapp.core.model.UserRef(
        id = this.id,
        name = this.display_name ?: "Unknown",
        avatarUrl = images?.maxByOrNull { it.width ?: 0 }?.url
    )
}