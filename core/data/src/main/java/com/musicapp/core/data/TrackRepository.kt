package com.musicapp.core.data

import com.musicapp.core.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    suspend fun getTrack(id: String): Result<Track>
    suspend fun getTracks(ids: List<String>): Result<List<Track>>
    suspend fun searchTracks(query: String, limit: Int, offset: Int): Result<PagedResult<Track>>
    suspend fun getRecommendedTracks(limit: Int): Result<List<Track>>
    suspend fun getRecentlyPlayed(limit: Int): Result<List<Track>>
    suspend fun getLikedTracks(limit: Int, offset: Int): Result<PagedResult<Track>>
    suspend fun likeTrack(trackId: String): Result<Unit>
    suspend fun unlikeTrack(trackId: String): Result<Unit>
    suspend fun incrementPlayCount(trackId: String): Result<Unit>
    fun observeRecentlyPlayed(limit: Int): Flow<List<Track>>
    fun observeLikedTracks(): Flow<List<Track>>
}

@Parcelize
data class PagedResult<T>(
    val items: List<T>,
    val totalCount: Long,
    val offset: Int,
    val limit: Int,
    val hasMore: Boolean
) : Parcelable