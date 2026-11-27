package com.musicapp.data.repository

import com.musicapp.core.data.PagedResult
import com.musicapp.core.data.SearchRepository
import com.musicapp.core.data.SearchFilters
import com.musicapp.core.data.SearchResults
import com.musicapp.core.data.SearchSuggestion
import com.musicapp.core.data.SearchSuggestionType
import com.musicapp.core.data.Category
import com.musicapp.core.model.Track
import com.musicapp.core.model.Album
import com.musicapp.core.model.Artist
import com.musicapp.core.model.Playlist
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.SearchHistoryDao
import com.musicapp.data.local.entities.SearchHistoryEntity
import com.musicapp.data.remote.MusicApiService
import com.musicapp.data.remote.ApiResponse
import com.musicapp.data.remote.SearchResultsDto
import com.musicapp.data.remote.CategoryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
    private val apiService: MusicApiService
) : SearchRepository {

    override suspend fun search(query: String, filters: SearchFilters): Result<SearchResults> = try {
        val response = apiService.searchTracks(query, filters.limit, filters.offset)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val dto = response.body()!!.data!!

            val tracks = dto.tracks?.let {
                PagedResult(it.items.map { it.toDomain() }, it.total, it.offset, it.limit, it.next != null)
            }
            val artists = dto.artists?.let {
                PagedResult(it.items.map { it.toDomain() }, it.total, it.offset, it.limit, it.next != null)
            }
            val albums = dto.albums?.let {
                PagedResult(it.items.map { it.toDomain() }, it.total, it.offset, it.limit, it.next != null)
            }
            val playlists = dto.playlists?.let {
                PagedResult(it.items.map { it.toDomain() }, it.total, it.offset, it.limit, it.next != null)
            }

            // Save to history
            searchHistoryDao.insert(SearchHistoryEntity(query, System.currentTimeMillis(),
                (tracks?.items?.size ?: 0) + (artists?.items?.size ?: 0) + (albums?.items?.size ?: 0) + (playlists?.items?.size ?: 0)))

            Result.success(SearchResults(tracks, artists, albums, playlists, null))
        } else {
            Result.failure(Exception("Search failed: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSuggestions(query: String): Result<List<SearchSuggestion>> = try {
        // Would call API for suggestions
        Result.success(emptyList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentSearches(limit: Int): Result<List<String>> = try {
        val searches = searchHistoryDao.getRecentSearches(limit).map { it.query }
        Result.success(searches)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveRecentSearch(query: String): Result<Unit> = try {
        searchHistoryDao.insert(SearchHistoryEntity(query, System.currentTimeMillis(), 0))
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun clearRecentSearches(): Result<Unit> = try {
        searchHistoryDao.clearHistory()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTrendingSearches(): Result<List<String>> = try {
        // Would call API for trending
        Result.success(emptyList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCategories(): Result<List<Category>> = try {
        val response = apiService.getCategories(50, 0)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val categories = response.body()!!.data!!.items.map { it.toDomain() }
            Result.success(categories)
        } else {
            Result.failure(Exception("Failed to get categories: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeRecentSearches(): Flow<List<String>> =
        searchHistoryDao.observeRecentSearches(10).map { it.map { it.query } }
}

private fun SearchResultsDto.TrackPagedResult?.toDomainPagedResult(): PagedResult<Track>? = this?.let {
    PagedResult(
        items = it.items.map { it.toDomain() },
        totalCount = it.total,
        offset = it.offset,
        limit = it.limit,
        hasMore = it.next != null
    )
}

private fun SearchResultsDto.ArtistPagedResult?.toDomainPagedResult(): PagedResult<Artist>? = this?.let {
    PagedResult(
        items = it.items.map { it.toDomain() },
        totalCount = it.total,
        offset = it.offset,
        limit = it.limit,
        hasMore = it.next != null
    )
}

private fun SearchResultsDto.AlbumPagedResult?.toDomainPagedResult(): PagedResult<Album>? = this?.let {
    PagedResult(
        items = it.items.map { it.toDomain() },
        totalCount = it.total,
        offset = it.offset,
        limit = it.limit,
        hasMore = it.next != null
    )
}

private fun SearchResultsDto.PlaylistPagedResult?.toDomainPagedResult(): PagedResult<Playlist>? = this?.let {
    PagedResult(
        items = it.items.map { it.toDomain() },
        totalCount = it.total,
        offset = it.offset,
        limit = it.limit,
        hasMore = it.next != null
    )
}

private fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        iconUrl = icons?.maxByOrNull { it.width ?: 0 }?.url,
        color = null
    )
}