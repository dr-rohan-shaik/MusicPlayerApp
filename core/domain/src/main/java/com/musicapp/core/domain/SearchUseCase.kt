package com.musicapp.core.domain

import com.musicapp.core.data.SearchRepository
import com.musicapp.core.data.SearchFilters
import com.musicapp.core.data.SearchResults
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(query: String, filters: SearchFilters = SearchFilters()): Result<SearchResults> {
        if (query.isBlank()) {
            return searchRepository.getCategories().map { SearchResults.Empty(it) }
        }
        return searchRepository.search(query, filters)
    }

    suspend fun getSuggestions(query: String): Result<List<SearchSuggestion>> {
        return searchRepository.getSuggestions(query)
    }

    suspend fun getRecentSearches(limit: Int = 10): Result<List<String>> {
        return searchRepository.getRecentSearches(limit)
    }

    suspend fun saveRecentSearch(query: String): Result<Unit> {
        return searchRepository.saveRecentSearch(query)
    }

    suspend fun clearRecentSearches(): Result<Unit> {
        return searchRepository.clearRecentSearches()
    }

    suspend fun getTrendingSearches(): Result<List<String>> {
        return searchRepository.getTrendingSearches()
    }

    suspend fun getCategories(): Result<List<Category>> {
        return searchRepository.getCategories()
    }
}