package com.musicapp.feature.search.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicapp.core.common.theme.CardSubtitle
import com.musicapp.core.common.theme.MetadataText
import com.musicapp.core.common.theme.SectionHeader
import com.musicapp.core.common.ui.card.AlbumCard
import com.musicapp.core.common.ui.card.ArtistCard
import com.musicapp.core.common.ui.card.PlaylistCard
import com.musicapp.core.common.ui.card.TrackCard
import com.musicapp.core.common.ui.list.HorizontalAlbumList
import com.musicapp.core.common.ui.list.HorizontalArtistList
import com.musicapp.core.common.ui.list.HorizontalPlaylistList
import com.musicapp.core.common.ui.list.HorizontalTrackList
import com.musicapp.core.common.ui.navigation.SearchAppBar
import com.musicapp.core.model.Category
import com.musicapp.core.model.Track
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onNavigate: (String, String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SearchAppBar(
                query = query,
                onQueryChange = { query = it },
                onSearchClick = { viewModel.search(query) },
                onFilterClick = { /* Show filter bottom sheet */ }
            )
        },
        content = { innerPadding ->
            when {
                query.isBlank() -> SearchHomeScreen(
                    padding = innerPadding,
                    uiState = uiState,
                    onNavigate = onNavigate,
                    onPlayTrack = onPlayTrack,
                    onCategoryClick = { category -> onNavigate("categoryDetail", category.id) },
                    onRecentSearchClick = { recentQuery ->
                        query = recentQuery
                        viewModel.search(recentQuery)
                    },
                    onClearRecentClick = { viewModel.clearRecentSearches() }
                )
                uiState is SearchUiState.Loading -> SearchSkeleton(innerPadding)
                uiState is SearchUiState.Error -> ErrorScreen(message = uiState.message, onRetry = { viewModel.search(query) })
                else -> {
                    // Navigate to results screen
                    // This would be handled by navigation
                    SearchHomeScreen(
                        padding = innerPadding,
                        uiState = uiState,
                        onNavigate = onNavigate,
                        onPlayTrack = onPlayTrack,
                        onCategoryClick = { category -> onNavigate("categoryDetail", category.id) },
                        onRecentSearchClick = { recentQuery ->
                            query = recentQuery
                            viewModel.search(recentQuery)
                        },
                        onClearRecentClick = { viewModel.clearRecentSearches() }
                    )
                }
            }
        }
    )
}

@Composable
fun SearchHomeScreen(
    padding: androidx.compose.foundation.layout.PaddingValues,
    uiState: SearchUiState,
    onNavigate: (String, String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onClearRecentClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Recent searches
        val recentSearches = (uiState as? SearchUiState.SearchHome)?.recentSearches ?: emptyList()
        if (recentSearches.isNotEmpty()) {
            item {
                SectionHeaderWithAction(
                    title = "Recent Searches",
                    actionIcon = androidx.compose.material.icons.filled.ClearAll,
                    actionContentDescription = "Clear all",
                    onActionClick = onClearRecentClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    items(recentSearches) { search ->
                        RecentSearchChip(
                            text = search,
                            onClick = { onRecentSearchClick(search) }
                        )
                    }
                }
            }
        }

        // Categories
        val categories = (uiState as? SearchUiState.SearchHome)?.categories ?: emptyList()
        if (categories.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Browse Categories",
                    onSeeAllClick = { onNavigate("browseCategories", "") },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                CategoryGrid(
                    categories = categories,
                    onItemClick = onCategoryClick,
                    imageLoader = remember { coil.ImageLoader(LocalContext.current) }
                )
            }
        }

        // Trending searches (if available)
        val trending = (uiState as? SearchUiState.SearchHome)?.trendingSearches ?: emptyList()
        if (trending.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Trending Searches",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                TrendingList(
                    searches = trending,
                    onItemClick = onRecentSearchClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<Category>,
    onItemClick: (Category) -> Unit,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyHorizontalGrid(
        modifier = modifier.fillMaxWidth(),
        rows = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onItemClick(category) },
                imageLoader = imageLoader
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier,
    onClick: () -> Unit,
    imageLoader: ImageLoader
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant, com.musicapp.core.common.theme.MusicShapes.CardLarge)
    ) {
        // Background image or color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(category.color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            // Category name overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), com.musicapp.core.common.theme.MusicShapes.CardLarge),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Icon if available
            category.iconUrl?.let { url ->
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(imageLoader.context)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    contentDescription = category.name
                )
            }
        }
    }
}

@Composable
fun RecentSearchChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Chip(
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp),
        label = { Text(text = text) },
        leadingIcon = { androidx.compose.material3.Icon(androidx.compose.material.icons.filled.History, contentDescription = "Recent") },
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun TrendingList(
    searches: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(searches) { search ->
            androidx.compose.material3.ListItem(
                onClick = { onItemClick(search) },
                modifier = Modifier.fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = search,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                leading = {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.filled.TrendingUp,
                        contentDescription = "Trending",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

@Composable
fun SearchSkeleton(padding: androidx.compose.foundation.layout.PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(3) {
            item { SectionHeaderSkeleton(modifier = Modifier.padding(horizontal = 16.dp)) }
            item { HorizontalSkeletonList(modifier = Modifier.fillMaxWidth()) }
        }
    }
}

@Composable
fun SectionHeaderSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, com.musicapp.core.common.theme.MusicShapes.Card)
    )
}

@Composable
fun HorizontalSkeletonList(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            items(4) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, com.musicapp.core.common.theme.MusicShapes.Card)
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.filled.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(text = "Something went wrong", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.material3.Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@AndroidEntryPoint
class SearchViewModel @Inject constructor(
    private val searchUseCase: com.musicapp.core.domain.SearchUseCase
) : ViewModel() {
    private val _uiState = mutableStateOf<SearchUiState>(SearchUiState.Initial)
    val uiState: androidx.compose.runtime.State<SearchUiState> = _uiState

    fun search(query: String) {
        if (query.isBlank()) {
            loadHome()
            return
        }
        _uiState.value = SearchUiState.Loading
        lifecycleScope.launch {
            val result = searchUseCase(query)
            _uiState.value = when {
                result.isSuccess -> SearchUiState.Results(result.getOrThrow())
                else -> SearchUiState.Error(result.exceptionOrNull()?.message ?: "Search failed")
            }
        }
    }

    fun loadHome() {
        lifecycleScope.launch {
            val recentResult = searchUseCase.getRecentSearches(10)
            val categoriesResult = searchUseCase.getCategories()
            val trendingResult = searchUseCase.getTrendingSearches()

            val recent = recentResult.getOrNull() ?: emptyList()
            val categories = categoriesResult.getOrNull() ?: emptyList()
            val trending = trendingResult.getOrNull() ?: emptyList()

            _uiState.value = SearchUiState.SearchHome(
                recentSearches = recent,
                categories = categories,
                trendingSearches = trending
            )
        }
    }

    fun clearRecentSearches() {
        lifecycleScope.launch {
            searchUseCase.clearRecentSearches().getOrNull()
            loadHome()
        }
    }
}

sealed interface SearchUiState {
    object Initial : SearchUiState
    object Loading : SearchUiState
    data class SearchHome(
        val recentSearches: List<String>,
        val categories: List<com.musicapp.core.data.Category>,
        val trendingSearches: List<String>
    ) : SearchUiState
    data class Results(val results: com.musicapp.core.data.SearchResults) : SearchUiState
    data class Error(val message: String) : SearchUiState
}