package com.musicapp.core.domain

import com.musicapp.core.data.PlaylistRepository
import com.musicapp.core.data.CreatePlaylistRequest
import com.musicapp.core.data.UpdatePlaylistRequest
import com.musicapp.core.model.Playlist
import javax.inject.Inject

class PlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend fun createPlaylist(request: CreatePlaylistRequest): Result<Playlist> {
        return playlistRepository.createPlaylist(request)
    }

    suspend fun updatePlaylist(playlistId: String, request: UpdatePlaylistRequest): Result<Playlist> {
        return playlistRepository.updatePlaylist(playlistId, request)
    }

    suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        return playlistRepository.deletePlaylist(playlistId)
    }

    suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>, position: Int?): Result<Unit> {
        return playlistRepository.addTracksToPlaylist(playlistId, trackIds, position)
    }

    suspend fun removeTracksFromPlaylist(playlistId: String, trackIds: List<String>): Result<Unit> {
        return playlistRepository.removeTracksFromPlaylist(playlistId, trackIds)
    }

    suspend fun reorderPlaylistTracks(playlistId: String, fromIndex: Int, toIndex: Int): Result<Unit> {
        return playlistRepository.reorderPlaylistTracks(playlistId, fromIndex, toIndex)
    }

    suspend fun followPlaylist(playlistId: String): Result<Unit> {
        return playlistRepository.followPlaylist(playlistId)
    }

    suspend fun unfollowPlaylist(playlistId: String): Result<Unit> {
        return playlistRepository.unfollowPlaylist(playlistId)
    }
}