package com.musicapp.core.data

import com.musicapp.core.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    suspend fun getAlbum(id: String): Result<Album>
    suspend fun getAlbums(ids: List<String>): Result<List<Album>>
    suspend fun getNewReleases(limit: Int): Result<List<Album>>
    suspend fun getArtistAlbums(artistId: String, limit: Int, offset: Int): Result<PagedResult<Album>>
    suspend fun getSavedAlbums(limit: Int, offset: Int): Result<PagedResult<Album>>
    suspend fun saveAlbum(albumId: String): Result<Unit>
    suspend fun unsaveAlbum(albumId: String): Result<Unit>
    fun observeSavedAlbums(): Flow<List<Album>>
}