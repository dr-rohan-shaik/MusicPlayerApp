package com.musicapp.core.data

import com.musicapp.core.model.Artist
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    suspend fun getArtist(id: String): Result<Artist>
    suspend fun getArtists(ids: List<String>): Result<List<Artist>>
    suspend fun getFollowedArtists(limit: Int, offset: Int): Result<PagedResult<Artist>>
    suspend fun getRelatedArtists(artistId: String, limit: Int): Result<List<Artist>>
    suspend fun followArtist(artistId: String): Result<Unit>
    suspend fun unfollowArtist(artistId: String): Result<Unit>
    fun observeFollowedArtists(): Flow<List<Artist>>
}