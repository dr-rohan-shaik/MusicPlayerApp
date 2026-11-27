package com.musicapp.core.data

import com.musicapp.core.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun getPlaylist(id: String): Result<Playlist>
    suspend fun getUserPlaylists(limit: Int, offset: Int): Result<PagedResult<Playlist>>
    suspend fun getFollowedPlaylists(limit: Int, offset: Int): Result<PagedResult<Playlist>>
    suspend fun getMadeForYouPlaylists(limit: Int): Result<List<Playlist>>
    suspend fun getPopularNearYou(limit: Int): Result<List<Playlist>>
    suspend fun createPlaylist(request: CreatePlaylistRequest): Result<Playlist>
    suspend fun updatePlaylist(playlistId: String, request: UpdatePlaylistRequest): Result<Playlist>
    suspend fun deletePlaylist(playlistId: String): Result<Unit>
    suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>, position: Int?): Result<Unit>
    suspend fun removeTracksFromPlaylist(playlistId: String, trackIds: List<String>): Result<Unit>
    suspend fun reorderPlaylistTracks(playlistId: String, fromIndex: Int, toIndex: Int): Result<Unit>
    suspend fun followPlaylist(playlistId: String): Result<Unit>
    suspend fun unfollowPlaylist(playlistId: String): Result<Unit>
    fun observeUserPlaylists(): Flow<List<Playlist>>
    fun observePlaylist(id: String): Flow<Playlist>
}

@Parcelize
data class CreatePlaylistRequest(
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val isCollaborative: Boolean,
    val artworkUrl: String?
) : Parcelable

@Parcelize
data class UpdatePlaylistRequest(
    val name: String?,
    val description: String?,
    val isPublic: Boolean?,
    val isCollaborative: Boolean?,
    val artworkUrl: String?
) : Parcelable