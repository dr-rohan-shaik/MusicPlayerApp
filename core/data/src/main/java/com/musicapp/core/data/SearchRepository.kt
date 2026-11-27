package com.musicapp.core.data

import com.musicapp.core.model.Track
import com.musicapp.core.model.Playlist
import com.musicapp.core.model.Album
import com.musicapp.core.model.Artist
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(query: String, filters: SearchFilters): Result<SearchResults>
    suspend fun getSuggestions(query: String): Result<List<SearchSuggestion>>
    suspend fun getRecentSearches(limit: Int): Result<List<String>>
    suspend fun saveRecentSearch(query: String): Result<Unit>
    suspend fun clearRecentSearches(): Result<Unit>
    suspend fun getTrendingSearches(): Result<List<String>>
    suspend fun getCategories(): Result<List<Category>>
    fun observeRecentSearches(): Flow<List<String>>
}

@Parcelize
data class SearchFilters(
    val types: Set<SearchType> = setOf(SearchType.TRACK, SearchType.ARTIST, SearchType.ALBUM, SearchType.PLAYLIST),
    val limit: Int = 20,
    val offset: Int = 0
) : Parcelable

enum class SearchType { TRACK, ARTIST, ALBUM, PLAYLIST, USER, SHOW, EPISODE }

@Parcelize
data class SearchSuggestion(
    val text: String,
    val type: SearchSuggestionType
) : Parcelable

enum class SearchSuggestionType { RECENT, TRENDING, QUERY_COMPLETION }

@Parcelize
data class SearchResults(
    val tracks: PagedResult<Track>?,
    val artists: PagedResult<Artist>?,
    val albums: PagedResult<Album>?,
    val playlists: PagedResult<Playlist>?,
    val categories: List<Category>?
) : Parcelable

@Parcelize
data class Category(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val color: String?
) : Parcelable