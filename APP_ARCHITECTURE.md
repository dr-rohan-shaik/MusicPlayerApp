# Application Architecture: Independent Music Streaming App

## Overview
This document defines the complete architecture for the independent Android music streaming application, based on Clean Architecture principles with MVVM, Repository pattern, and modern Android stack (Kotlin, Compose, Media3, Room, Hilt).

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              PRESENTATION LAYER                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Home      │  │   Search    │  │  Library    │  │ NowPlaying  │        │
│  │  Screen     │  │  Screen     │  │  Screen     │  │  Screen     │        │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘        │
│         │                │                │                │                │
│  ┌──────▼────────────────▼────────────────▼────────────────▼──────┐        │
│  │                    ViewModels (StateFlow/UI State)              │        │
│  │  HomeVM    SearchVM    LibraryVM    PlaybackVM    SettingsVM   │        │
│  └────────────────────────────┬────────────────────────────────────┘        │
└───────────────────────────────│─────────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────────┐
│                               DOMAIN LAYER                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  GetTracks  │  │  Search     │  │  Manage     │  │  Playback   │        │
│  │  UseCase    │  │  UseCase    │  │  Playlist   │  │  UseCase    │        │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘        │
│         │                │                │                │                │
│  ┌──────▼────────────────▼────────────────▼────────────────▼──────┐        │
│  │                    Repository Interfaces                         │        │
│  │  TrackRepository  AlbumRepository  ArtistRepository             │        │
│  │  PlaylistRepository  UserRepository  PlaybackRepository        │        │
│  └────────────────────────────┬────────────────────────────────────┘        │
└───────────────────────────────│─────────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────────┐
│                                DATA LAYER                                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Room DB   │  │  Remote API │  │  MediaStore │  │  DataStore  │        │
│  │  (Local)    │  │  (Licensed) │  │  (Local FS) │  │  (Prefs)    │        │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘        │
│         │                │                │                │                │
│  ┌──────▼────────────────▼────────────────▼────────────────▼──────┐        │
│  │                 Repository Implementations                       │        │
│  │  TrackRepositoryImpl  PlaylistRepositoryImpl  etc.              │        │
│  └─────────────────────────────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────────┐
│                            PLATFORM LAYER                                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  ExoPlayer  │  │ MediaSession│  │ForegroundSvc│  │ Notifications│       │
│  │  (Media3)   │  │  (Media3)   │  │  (Playback) │  │  (Media3)   │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Module Structure (Gradle)

```
MusicPlayerApp/
├── app/                          # Application module (entry point)
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/musicapp/
│   │   │   ├── MusicApplication.kt
│   │   │   ├── di/               # Hilt modules
│   │   │   ├── navigation/       # NavGraph, DeepLinks
│   │   │   └── ui/               # Compose theme, MainActivity
│   │   └── res/
│   └── build.gradle.kts
│
├── core/
│   ├── common/                   # Shared utilities, extensions, constants
│   │   └── build.gradle.kts
│   ├── data/                     # Data layer interfaces (Repository contracts)
│   │   └── build.gradle.kts
│   ├── domain/                   # Domain layer (UseCases, Entities, Repository interfaces)
│   │   └── build.gradle.kts
│   ├── model/                    # Shared data models (Parcelable, serializable)
│   │   └── build.gradle.kts
│   └── testing/                  # Test utilities, fakes, TestModules
│       └── build.gradle.kts
│
├── feature/
│   ├── home/                     # Home feature module
│   │   ├── src/main/java/com/musicapp/feature/home/
│   │   │   ├── ui/               # HomeScreen, Components
│   │   │   ├── viewmodel/        # HomeViewModel
│   │   │   └── di/               # HomeModule
│   │   └── build.gradle.kts
│   ├── search/                   # Search feature module
│   │   └── build.gradle.kts
│   ├── library/                  # Library feature module
│   │   └── build.gradle.kts
│   ├── playback/                 # Playback feature module
│   │   ├── src/main/java/com/musicapp/feature/playback/
│   │   │   ├── ui/               # NowPlayingScreen, Miniplayer, Queue
│   │   │   ├── viewmodel/        # PlaybackViewModel
│   │   │   ├── service/          # PlaybackService, MediaController
│   │   │   └── di/               # PlaybackModule
│   │   └── build.gradle.kts
│   ├── playlist/                 # Playlist management feature
│   │   └── build.gradle.kts
│   ├── settings/                 # Settings feature module
│   │   └── build.gradle.kts
│   └── profile/                  # Profile feature module
│       └── build.gradle.kts
│
├── data/
│   ├── local/                    # Room database, DAOs, LocalDataSource
│   │   └── build.gradle.kts
│   ├── remote/                   # Retrofit API, RemoteDataSource
│   │   └── build.gradle.kts
│   └── repository/               # Repository implementations
│       └── build.gradle.kts
│
├── media/                        # Media3/ExoPlayer integration
│   ├── src/main/java/com/musicapp/media/
│   │   ├── player/               # ExoPlayer wrapper, PlayerController
│   │   ├── session/              # MediaSession, MediaController
│   │   ├── notification/         # MediaNotificationProvider
│   │   ├── service/              # MediaPlaybackService
│   │   └── di/                   # MediaModule
│   └── build.gradle.kts
│
├── build-logic/                  # Convention plugins (buildSrc)
│   └── convention/
│       └── src/main/kotlin/
│
├── gradle/
│   └── libs.versions.toml        # Version catalog
│
├── settings.gradle.kts
└── build.gradle.kts
```

## Dependency Rules (Clean Architecture)

```
ALLOWED DEPENDENCIES (Direction: Presentation → Domain → Data → Platform)

Presentation (feature/*)
  ├── Depends on: Domain (core/domain), Core (core/common, core/model)
  ├── Implements: ViewModels, Screens, Navigation
  └── NO direct dependency on: Data, Platform, other features

Domain (core/domain)
  ├── Depends on: Core (core/common, core/model)
  ├── Contains: Entities, Repository Interfaces, UseCases
  └── NO dependency on: Data, Platform, Presentation, Android framework

Data (data/*)
  ├── Depends on: Domain (interfaces), Core (core/common, core/model)
  ├── Implements: Repository interfaces, DataSources (Local/Remote)
  └── NO dependency on: Presentation, Platform (except via interfaces)

Platform (media/)
  ├── Depends on: Domain (PlaybackRepository), Core
  ├── Implements: ExoPlayer, MediaSession, ForegroundService
  └── Exposed via: Domain repository interfaces

Core (core/*)
  ├── common: Utilities, extensions, constants (no external deps)
  ├── model: Data classes, Parcelable implementations
  ├── data: Repository interfaces (pure Kotlin)
  └── testing: Test fakes, helpers
```

## Domain Layer Details

### Entities (core/model)

```kotlin
// Track.kt
@Parcelize
data class Track(
    val id: String,
    val title: String,
    val artist: ArtistRef,
    val album: AlbumRef,
    val duration: Long,           // milliseconds
    val artworkUrl: String?,
    val audioUrl: String?,        // licensed stream URL
    val localPath: String?,       // for imported files
    val genre: String?,
    val releaseDate: Long?,       // timestamp
    val trackNumber: Int,
    val discNumber: Int,
    val isExplicit: Boolean,
    val isLiked: Boolean,
    val playCount: Long,
    val addedAt: Long
) : Parcelable

// Artist.kt
@Parcelize
data class Artist(
    val id: String,
    val name: String,
    val artworkUrl: String?,
    val genres: List<String>,
    val followerCount: Long,
    val isVerified: Boolean,
    val biography: String?
) : Parcelable

// Album.kt
@Parcelize
data class Album(
    val id: String,
    val title: String,
    val artist: ArtistRef,
    val artworkUrl: String?,
    val releaseDate: Long,
    val trackCount: Int,
    val duration: Long,
    val type: AlbumType,          // ALBUM, SINGLE, EP, COMPILATION
    val isLiked: Boolean,
    val tracks: List<TrackRef>
) : Parcelable

// Playlist.kt
@Parcelize
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val artworkUrl: String?,
    val owner: UserRef,
    val isPublic: Boolean,
    val isCollaborative: Boolean,
    val trackCount: Int,
    val duration: Long,
    val followerCount: Long,
    val tracks: List<TrackRef>,
    val createdAt: Long,
    val updatedAt: Long
) : Parcelable

// Reference types (lightweight for lists)
@Parcelize
data class ArtistRef(val id: String, val name: String, val artworkUrl: String?) : Parcelable

@Parcelize
data class AlbumRef(val id: String, val title: String, val artworkUrl: String?) : Parcelable

@Parcelize
data class TrackRef(val id: String, val title: String, val artist: ArtistRef, val duration: Long) : Parcelable

@Parcelize
data class UserRef(val id: String, val name: String, val avatarUrl: String?) : Parcelable

enum class AlbumType { ALBUM, SINGLE, EP, COMPILATION }
```

### Repository Interfaces (core/data)

```kotlin
// TrackRepository.kt
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
}

// PlaylistRepository.kt
interface PlaylistRepository {
    suspend fun getPlaylist(id: String): Result<Playlist>
    suspend fun getUserPlaylists(limit: Int, offset: Int): Result<PagedResult<Playlist>>
    suspend fun getFollowedPlaylists(limit: Int, offset: Int): Result<PagedResult<Playlist>>
    suspend fun createPlaylist(request: CreatePlaylistRequest): Result<Playlist>
    suspend fun updatePlaylist(playlistId: String, request: UpdatePlaylistRequest): Result<Playlist>
    suspend fun deletePlaylist(playlistId: String): Result<Unit>
    suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>, position: Int?): Result<Unit>
    suspend fun removeTracksFromPlaylist(playlistId: String, trackIds: List<String>): Result<Unit>
    suspend fun reorderPlaylistTracks(playlistId: String, fromIndex: Int, toIndex: Int): Result<Unit>
    suspend fun followPlaylist(playlistId: String): Result<Unit>
    suspend fun unfollowPlaylist(playlistId: String): Result<Unit>
}

// PlaybackRepository.kt
interface PlaybackRepository {
    // Queue management
    suspend fun getQueue(): Result<PlaybackQueue>
    suspend fun setQueue(queue: PlaybackQueue): Result<Unit>
    suspend fun addToQueue(track: Track, position: Int?): Result<Unit>
    suspend fun removeFromQueue(trackId: String): Result<Unit>
    suspend fun clearQueue(): Result<Unit>
    suspend fun reorderQueue(fromIndex: Int, toIndex: Int): Result<Unit>
    
    // Playback state
    suspend fun getPlaybackState(): Result<PlaybackState>
    suspend fun savePlaybackState(state: PlaybackState): Result<Unit>
    
    // History
    suspend fun addToHistory(track: Track): Result<Unit>
    suspend fun getHistory(limit: Int): Result<List<Track>>
}

// SearchRepository.kt
interface SearchRepository {
    suspend fun search(query: String, filters: SearchFilters): Result<SearchResults>
    suspend fun getSuggestions(query: String): Result<List<SearchSuggestion>>
    suspend fun getRecentSearches(limit: Int): Result<List<String>>
    suspend fun saveRecentSearch(query: String): Result<Unit>
    suspend fun clearRecentSearches(): Result<Unit>
    suspend fun getTrendingSearches(): Result<List<String>>
    suspend fun getCategories(): Result<List<Category>>
}
```

### Use Cases (core/domain)

```kotlin
// GetHomeContentUseCase.kt
class GetHomeContentUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Result<HomeContent> = try {
        val recentlyPlayed = trackRepository.getRecentlyPlayed(10).getOrThrow()
        val recommended = trackRepository.getRecommendedTracks(20).getOrThrow()
        val newReleases = albumRepository.getNewReleases(10).getOrThrow()
        val madeForYou = playlistRepository.getMadeForYouPlaylists(10).getOrThrow()
        val popularNearYou = playlistRepository.getPopularNearYou(10).getOrThrow()
        
        Result.success(HomeContent(
            recentlyPlayed = recentlyPlayed,
            recommended = recommended,
            newReleases = newReleases,
            madeForYou = madeForYou,
            popularNearYou = popularNearYou
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// SearchUseCase.kt
class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(query: String, filters: SearchFilters = SearchFilters()): Result<SearchResults> {
        if (query.isBlank()) {
            return searchRepository.getCategories().map { SearchResults.Empty(it) }
        }
        return searchRepository.search(query, filters)
    }
}

// PlaybackUseCase.kt
class PlaybackUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val playerController: PlayerController
) {
    suspend fun playTrack(track: Track, queue: List<Track> = emptyList(), startIndex: Int = 0) {
        val newQueue = if (queue.isNotEmpty()) queue else listOf(track)
        val index = if (queue.isNotEmpty()) startIndex else 0
        
        playbackRepository.setQueue(PlaybackQueue(newQueue, index)).getOrThrow()
        playerController.play(newQueue[index])
        playbackRepository.addToHistory(track).getOrThrow()
    }
    
    suspend fun playPlaylist(playlist: Playlist, shuffle: Boolean = false) {
        val tracks = playlist.tracks.map { it.toTrack() }
        val queue = if (shuffle) tracks.shuffled() else tracks
        playbackRepository.setQueue(PlaybackQueue(queue, 0)).getOrThrow()
        playerController.play(queue[0])
        tracks.forEach { playbackRepository.addToHistory(it).getOrThrow() }
    }
    
    // Controls
    suspend fun togglePlayPause() = playerController.togglePlayPause()
    suspend fun skipNext() = playerController.skipNext()
    suspend fun skipPrevious() = playerController.skipPrevious()
    suspend fun seekTo(position: Long) = playerController.seekTo(position)
    suspend fun setShuffleMode(mode: ShuffleMode) = playerController.setShuffleMode(mode)
    suspend fun setRepeatMode(mode: RepeatMode) = playerController.setRepeatMode(mode)
    suspend fun setVolume(volume: Float) = playerController.setVolume(volume)
}
```

## Data Layer Details

### Room Database (data/local)

```kotlin
// AppDatabase.kt
@Database(
    entities = [
        TrackEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        UserEntity::class,
        PlaybackStateEntity::class,
        HistoryEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun userDao(): UserDao
    abstract fun playbackStateDao(): PlaybackStateDao
    abstract fun historyDao(): HistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}

// TrackEntity.kt
@Entity(tableName = "tracks",
    indices = [
        Index("artistId"),
        Index("albumId"),
        Index("isLiked"),
        Index("addedAt")
    ])
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val duration: Long,
    val artworkUrl: String?,
    val audioUrl: String?,
    val localPath: String?,
    val genre: String?,
    val releaseDate: Long?,
    val trackNumber: Int,
    val discNumber: Int,
    val isExplicit: Boolean,
    val isLiked: Boolean,
    val playCount: Long,
    val addedAt: Long,
    val lastPlayedAt: Long?,
    val source: TrackSource  // REMOTE, LOCAL_IMPORT
)

enum class TrackSource { REMOTE, LOCAL_IMPORT }

// PlaylistEntity.kt
@Entity(tableName = "playlists",
    indices = [Index("ownerId"), Index("updatedAt")])
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val artworkUrl: String?,
    val ownerId: String,
    val isPublic: Boolean,
    val isCollaborative: Boolean,
    val trackCount: Int,
    val duration: Long,
    val followerCount: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val source: PlaylistSource  // USER_CREATED, FOLLOWED
)

enum class PlaylistSource { USER_CREATED, FOLLOWED }

// PlaylistTrackEntity.kt (Many-to-Many with ordering)
@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    indices = [Index("playlistId"), Index("position")]
)
data class PlaylistTrackEntity(
    val playlistId: String,
    val trackId: String,
    val position: Int,
    val addedAt: Long,
    val addedBy: String?
)

// Converters.kt
class Converters {
    @TypeConverter
    fun fromTrackSource(source: TrackSource?): Int? = source?.ordinal
    
    @TypeConverter
    fun toTrackSource(value: Int?): TrackSource? = value?.let { TrackSource.values()[it] }
    
    @TypeConverter
    fun fromList(list: List<String>?): String? = list?.joinToString(",")
    
    @TypeConverter
    fun toList(value: String?): List<String> = value?.split(",") ?: emptyList()
    
    @TypeConverter
    fun fromGenres(genres: List<String>?): String? = genres?.joinToString("|")
    
    @TypeConverter
    fun toGenres(value: String?): List<String> = value?.split("|") ?: emptyList()
}
```

### Remote API (data/remote)

```kotlin
// MusicApiService.kt
interface MusicApiService {
    @GET("tracks")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String = "track"
    ): ApiResponse<SearchResultsDto>
    
    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: String): ApiResponse<TrackDto>
    
    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: String): ApiResponse<AlbumDto>
    
    @GET("artists/{id}")
    suspend fun getArtist(@Path("id") id: String): ApiResponse<ArtistDto>
    
    @GET("playlists/{id}")
    suspend fun getPlaylist(@Path("id") id: String): ApiResponse<PlaylistDto>
    
    @GET("playlists/{id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("id") id: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PagedResultDto<TrackDto>>
    
    @GET("browse/categories")
    suspend fun getCategories(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PagedResultDto<CategoryDto>>
    
    @GET("browse/categories/{id}/playlists")
    suspend fun getCategoryPlaylists(
        @Path("id") id: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PagedResultDto<PlaylistDto>>
    
    @GET("recommendations")
    suspend fun getRecommendations(
        @Query("seed_tracks") seedTracks: String?,
        @Query("seed_artists") seedArtists: String?,
        @Query("seed_genres") seedGenres: String?,
        @Query("limit") limit: Int = 20
    ): ApiResponse<RecommendationsDto>
    
    @POST("playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequestDto): ApiResponse<PlaylistDto>
    
    @PUT("playlists/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: String,
        @Body request: UpdatePlaylistRequestDto
    ): ApiResponse<PlaylistDto>
    
    @DELETE("playlists/{id}/followers")
    suspend fun unfollowPlaylist(@Path("id") id: String): ApiResponse<Unit>
    
    @PUT("playlists/{id}/followers")
    suspend fun followPlaylist(@Path("id") id: String): ApiResponse<Unit>
    
    @PUT("tracks/{id}/like")
    suspend fun likeTrack(@Path("id") id: String): ApiResponse<Unit>
    
    @DELETE("tracks/{id}/like")
    suspend fun unlikeTrack(@Path("id") id: String): ApiResponse<Unit>
}

// AuthInterceptor.kt
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}

// TokenManager.kt
interface TokenManager {
    fun getAccessToken(): String
    suspend fun refreshToken(): Result<String>
    fun clearTokens()
}
```

### Repository Implementations (data/repository)

```kotlin
// TrackRepositoryImpl.kt
class TrackRepositoryImpl @Inject constructor(
    private val localDataSource: TrackLocalDataSource,
    private val remoteDataSource: TrackRemoteDataSource,
    private val playlistLocalDataSource: PlaylistLocalDataSource
) : TrackRepository {
    
    override suspend fun getTrack(id: String): Result<Track> = runCatching {
        // Try local first
        localDataSource.getTrack(id)?.toDomain() 
            ?: remoteDataSource.getTrack(id).getOrThrow().toDomain().also { track ->
                localDataSource.upsertTrack(track.toEntity())
            }
    }
    
    override suspend fun searchTracks(query: String, limit: Int, offset: Int): Result<PagedResult<Track>> = 
        remoteDataSource.searchTracks(query, limit, offset)
            .map { it.map { it.toDomain() } }
    
    override suspend fun getRecommendedTracks(limit: Int): Result<List<Track>> =
        remoteDataSource.getRecommendations(limit = limit)
            .map { it.tracks.map { it.toDomain() } }
    
    override suspend fun getRecentlyPlayed(limit: Int): Result<List<Track>> =
        localDataSource.getRecentlyPlayed(limit)
            .map { it.map { it.toDomain() } }
    
    override suspend fun likeTrack(trackId: String): Result<Unit> = runCatching {
        remoteDataSource.likeTrack(trackId).getOrThrow()
        localDataSource.setTrackLiked(trackId, true)
    }
    
    override suspend fun incrementPlayCount(trackId: String): Result<Unit> =
        localDataSource.incrementPlayCount(trackId)
}
```

## Presentation Layer (Feature Modules)

### ViewModel Pattern

```kotlin
// BaseViewModel.kt
abstract class BaseViewModel @Inject constructor(
    protected val scope: CoroutineScope
) : ViewModel() {
    
    protected fun <T> launch(catching: suspend () -> Result<T>, onSuccess: (T) -> Unit) {
        scope.launch {
            catching().onSuccess(onSuccess).onFailure { handleError(it) }
        }
    }
    
    protected fun <T> launchUi(catching: suspend () -> Result<T>, onSuccess: (T) -> Unit) {
        scope.launch(Dispatchers.Main.immediate) {
            catching().onSuccess(onSuccess).onFailure { handleError(it) }
        }
    }
    
    protected open fun handleError(throwable: Throwable) {
        // Log error, show snackbar via event
    }
}

// HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContent: GetHomeContentUseCase,
    private val playbackUseCase: PlaybackUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState
    
    init { loadContent() }
    
    fun loadContent() {
        launchUi(catching = { getHomeContent() }) { content ->
            _uiState.value = HomeUiState.Success(content)
        }
    }
    
    fun onTrackClick(track: Track, queue: List<Track> = emptyList()) {
        launchUi(catching = { playbackUseCase.playTrack(track, queue) }) {
            // Navigation handled by PlaybackUseCase via event
        }
    }
    
    fun onPlaylistClick(playlist: Playlist) {
        // Navigate to playlist detail
    }
    
    fun onRefresh() { loadContent() }
}

sealed interface HomeUiState {
    data class Success(val content: HomeContent) : HomeUiState
    object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```

### Compose Screen Structure

```kotlin
// HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (String, String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { HomeTopAppBar(onProfileClick = { /* navigate */ }) },
        content = { padding ->
            when (uiState) {
                is HomeUiState.Loading -> HomeSkeleton(padding)
                is HomeUiState.Error -> ErrorScreen(uiState.message, viewModel::loadContent)
                is HomeUiState.Success -> HomeContentScreen(
                    content = uiState.content,
                    padding = padding,
                    onItemClick = { track, queue -> onPlayTrack(track, queue) },
                    onNavigate = onNavigate
                )
            }
        }
    )
}

// HomeContentScreen.kt
@Composable
fun HomeContentScreen(
    content: HomeContent,
    padding: PaddingValues,
    onItemClick: (Track, List<Track>) -> Unit,
    onNavigate: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(padding),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Recently Played
        if (content.recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recently Played",
                    onSeeAllClick = { onNavigate("recentlyPlayedDetail", "") }
                )
            }
            item {
                HorizontalTrackList(
                    tracks = content.recentlyPlayed,
                    onItemClick = onItemClick
                )
            }
        }
        
        // Made For You
        if (content.madeForYou.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Made For You",
                    onSeeAllClick = { /* navigate */ }
                )
            }
            item {
                HorizontalPlaylistGrid(
                    playlists = content.madeForYou,
                    onItemClick = { playlist -> onNavigate("playlistDetail", playlist.id) }
                )
            }
        }
        
        // New Releases
        if (content.newReleases.isNotEmpty()) {
            item {
                SectionHeader(title = "New Releases", onSeeAllClick = { /* navigate */ })
            }
            item {
                HorizontalAlbumGrid(
                    albums = content.newReleases,
                    onItemClick = { album -> onNavigate("albumDetail", album.id) }
                )
            }
        }
        
        // Recommended Tracks
        if (content.recommended.isNotEmpty()) {
            item {
                SectionHeader(title = "Recommended For You", onSeeAllClick = { /* navigate */ })
            }
            item {
                TrackList(
                    tracks = content.recommended,
                    onItemClick = onItemClick,
                    showArtist = true,
                    showAlbum = true
                )
            }
        }
    }
}
```

## Media/Playback Layer (media/)

### Player Controller

```kotlin
// PlayerController.kt
interface PlayerController {
    val playbackState: StateFlow<PlaybackState>
    val currentTrack: StateFlow<Track?>
    val queue: StateFlow<List<Track>>
    val position: StateFlow<Long>
    val duration: StateFlow<Long>
    val bufferedPosition: StateFlow<Long>
    
    suspend fun play(track: Track, queue: List<Track> = emptyList(), index: Int = 0)
    suspend fun playPause()
    suspend fun pause()
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun seekTo(position: Long)
    suspend fun setShuffleMode(mode: ShuffleMode)
    suspend fun setRepeatMode(mode: RepeatMode)
    suspend fun setVolume(volume: Float)
    suspend fun setPlaybackSpeed(speed: Float)
    fun release()
}

// ExoPlayerController.kt
class ExoPlayerController @Inject constructor(
    private val context: Context,
    private val mediaSession: MediaSessionCompat,
    private val notificationProvider: MediaNotificationProvider
) : PlayerController, Player.Listener {
    
    private val exoPlayer = ExoPlayer.Builder(context).build().also {
        it.addListener(this)
        it.repeatMode = Player.REPEAT_MODE_OFF
        it.shuffleMode = Player.SHUFFLE_MODE_OFF
    }
    
    private val _playbackState = MutableStateFlow(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = _playbackState
    
    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack
    
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    override val queue: StateFlow<List<Track>> = _queue
    
    override suspend fun play(track: Track, queue: List<Track> = emptyList(), index: Int = 0) {
        val mediaItems = (if (queue.isNotEmpty()) queue else listOf(track))
            .map { createMediaItem(it) }
        
        exoPlayer.setMediaItems(mediaItems, index)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        
        _queue.value = if (queue.isNotEmpty()) queue else listOf(track)
        _currentTrack.value = (if (queue.isNotEmpty()) queue else listOf(track))[index]
        
        updateMediaSession()
        notificationProvider.updateNotification()
    }
    
    private fun createMediaItem(track: Track): MediaItem {
        return MediaItem.fromUri(track.audioUrl ?: track.localPath!!)
            .buildUpon()
            .setMediaMetadata(MediaMetadata.Builder()
                .setTitle(track.title)
                .setArtist(track.artist.name)
                .setAlbumTitle(track.album.title)
                .setArtworkUri(Uri.parse(track.artworkUrl ?? ""))
                .setDuration(track.duration)
                .build())
            .build()
    }
    
    override fun onPlaybackStateChanged(state: Int) {
        _playbackState.value = when (state) {
            Player.STATE_IDLE -> PlaybackState.Idle
            Player.STATE_BUFFERING -> PlaybackState.Buffering
            Player.STATE_READY -> if (exoPlayer.playWhenReady) PlaybackState.Playing else PlaybackState.Paused
            Player.STATE_ENDED -> PlaybackState.Completed
            else -> PlaybackState.Idle
        }
        updateMediaSession()
    }
    
    override fun onPlayerError(error: PlaybackException) {
        _playbackState.value = PlaybackState.Error(error.message ?: "Playback error")
    }
    
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        updateMediaSession()
        notificationProvider.updateNotification()
    }
    
    // ... other Player.Listener methods
}
```

### Media Session & Foreground Service

```kotlin
// MediaPlaybackService.kt
class MediaPlaybackService : MediaSessionService() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize ExoPlayer, MediaSession
    }
    
    override fun onGetSession(controllerInfo: MediaControllerCompat.ControllerInfo): MediaSessionCompat {
        return mediaSession
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle playback actions from notification
        return super.onStartCommand(intent, flags, startId)
    }
}

// MediaNotificationProvider.kt
class MediaNotificationProvider @Inject constructor(
    private val context: Context,
    private val playerController: PlayerController
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    fun updateNotification() {
        val state = playerController.playbackState.value
        val track = playerController.currentTrack.value
        
        val notification = createMediaNotification(state, track)
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        if (state == PlaybackState.Playing || state == PlaybackState.Paused) {
            startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_MEDIA_PLAYBACK)
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }
    
    private fun createMediaNotification(state: PlaybackState, track: Track?): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setStyle(MediaStyleNotificationHelper.from(context, mediaSession))
            .setSmallIcon(R.drawable.ic_music_note)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setOngoing(state == PlaybackState.Playing)
        
        track?.let {
            builder
                .setContentTitle(it.title)
                .setContentText(it.artist.name)
                .setLargeIcon(loadArtwork(it.artworkUrl))
                .setContentIntent(createPendingIntent(NowPlayingActivity::class.java))
        }
        
        // Add actions: Previous, Play/Pause, Next
        builder.addAction(createAction(R.drawable.ic_skip_previous, "Previous", PREVIOUS_ACTION))
        builder.addAction(createAction(
            if (state == PlaybackState.Playing) R.drawable.ic_pause else R.drawable.ic_play,
            if (state == PlaybackState.Playing) "Pause" else "Play",
            PLAY_PAUSE_ACTION
        ))
        builder.addAction(createAction(R.drawable.ic_skip_next, "Next", NEXT_ACTION))
        
        return builder.build()
    }
}
```

## Dependency Injection (Hilt)

```kotlin
// AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "music.db")
            .fallbackToDestructiveMigration()
            .build()
    
    @Provides
    @Singleton
    fun provideMusicApiService(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): MusicApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(MusicApiService::class.java)
    
    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
}

// Feature Modules
@Module
@InstallIn(ViewModelComponent::class)
object HomeModule {
    @Binds
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): TrackRepository
    
    @Binds
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository
}

// MediaModule.kt
@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun providePlayerController(
        context: Context,
        mediaSession: MediaSessionCompat,
        notificationProvider: MediaNotificationProvider
    ): PlayerController = ExoPlayerController(context, mediaSession, notificationProvider)
    
    @Provides
    @Singleton
    fun provideMediaSession(@ApplicationContext context: Context): MediaSessionCompat =
        MediaSessionCompat(context, "MusicPlayerSession").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or 
                     MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            isActive = true
        }
}
```

## State Management

### UI State Flow

```
User Action → ViewModel → UseCase → Repository → DataSource
                ↓
         StateFlow/UI State ← ViewModel ← UseCase Result
                ↓
         Compose Recomposition → UI Update
```

### State Patterns

1. **UiState Sealed Classes** - Loading, Success, Error
2. **StateFlow** - Hot streams for reactive UI
3. **MutableStateFlow** - Private mutable, public immutable
4. **collectAsStateWithLifecycle()** - Lifecycle-aware collection

### Playback State

```kotlin
sealed interface PlaybackState {
    object Idle : PlaybackState
    object Buffering : PlaybackState
    data class Playing(val position: Long) : PlaybackState
    data class Paused(val position: Long) : PlaybackState
    data class Completed(val nextTrack: Track?) : PlaybackState
    data class Error(val message: String) : PlaybackState
}

data class PlaybackQueue(
    val tracks: List<Track>,
    val currentIndex: Int,
    val shuffleMode: ShuffleMode,
    val repeatMode: RepeatMode
)

enum class ShuffleMode { OFF, ON }
enum class RepeatMode { OFF, ALL, ONE }
```

## Testing Strategy

### Unit Tests (Domain Layer)
- UseCase tests with mocked repositories
- Repository tests with faked data sources
- Entity mapping tests

### Integration Tests (Data Layer)
- Room DAO tests with in-memory database
- Remote API tests with MockWebServer
- Repository implementation tests

### UI Tests (Presentation Layer)
- Compose tests with ComposeTestRule
- ViewModel tests with TestDispatcher
- Navigation tests

### E2E Tests
- Critical user flows (Search → Play, Library → Playlist → Play)
- Background playback verification
- Notification controls

## Performance Considerations

### Startup Optimization
- Baseline Profiles for critical paths
- Lazy loading of feature modules (Dynamic Delivery)
- Deferred initialization of non-critical components

### Memory Management
- Image caching with Coil (memory + disk)
- Pagination for large lists (Paging 3)
- View recycling in LazyColumn/LazyRow

### Battery Optimization
- ExoPlayer's built-in optimizations
- Foreground service only during playback
- Audio focus management
- Network request batching

### Network Optimization
- HTTP caching (OkHttp cache)
- Request deduplication
- Offline-first with Room cache
- Background sync with WorkManager

## Security

### Data Protection
- No hardcoded secrets (BuildConfig/API keys in secure storage)
- Encrypted Room database (SQLCipher) for sensitive data
- DataStore with encryption for tokens
- Certificate pinning for API calls

### Network Security
- TLS 1.2+ only
- Network security config
- Input validation on all user inputs
- Sanitization of file paths for local import

## Build Configuration

### Version Catalog (gradle/libs.versions.toml)

```toml
[versions]
agp = "8.5.0"
kotlin = "2.0.0"
composeBom = "2024.09.00"
hilt = "2.51.1"
room = "2.6.1"
media3 = "1.4.1"
retrofit = "2.11.0"
okhttp = "4.12.0"
coroutines = "1.8.0"
lifecycle = "2.8.0"
navigation = "2.8.0"
paging = "3.2.1"
coil = "2.6.0"
material3 = "1.3.0"
junit = "4.13.2"
junitJupiter = "5.10.2"
mockk = "1.13.13"
espresso = "3.5.1"
truth = "1.1.3"

[libraries]
# AndroidX
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activity" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
androidx-paging-compose = { group = "androidx.paging", name = "paging-compose", version.ref = "paging" }

# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-runtime = { group = "androidx.compose.runtime", name = "runtime" }
compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# Media3
media3-exoplayer = { group = "androidx.media3", name = "media3-exoplayer", version.ref = "media3" }
media3-session = { group = "androidx.media3", name = "media3-session", version.ref = "media3" }
media3-ui = { group = "androidx.media3", name = "media3-ui", version.ref = "media3" }
media3-database = { group = "androidx.media3", name = "media3-database", version.ref = "media3" }

# Network
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }

# Coroutines
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

# Coil
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Test
junit = { group = "junit", name = "junit", version.ref = "junit" }
junit-jupiter = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junitJupiter" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso" }
compose-test = { group = "androidx.compose.ui", name = "ui-test-junit4", version.ref = "composeBom" }
truth = { group = "com.google.truth", name = "truth", version.ref = "truth" }
```

## Summary

| Layer | Modules | Key Technologies |
|-------|---------|------------------|
| Presentation | feature/*, app | Compose, ViewModel, Navigation, Hilt |
| Domain | core/domain, core/model | UseCases, Repository Interfaces, Entities |
| Data | data/local, data/remote, data/repository | Room, Retrofit, DataStore, Paging 3 |
| Platform | media/ | Media3/ExoPlayer, MediaSession, Foreground Service |
| Core | core/common, core/testing | Utilities, Extensions, Test Fakes |

**Total Modules**: ~15 Gradle modules
**Language**: Kotlin 2.0+
**Min SDK**: 24 (Android 7.0)
**Target SDK**: 34 (Android 14)
**Architecture**: Clean Architecture + MVVM + Repository Pattern
**DI**: Hilt
**UI**: Jetpack Compose + Material 3
**Database**: Room
**Networking**: Retrofit + OkHttp
**Media**: Media3/ExoPlayer
**Images**: Coil
**Testing**: JUnit, MockK, Compose Test, Espresso

This architecture provides a solid, scalable foundation for a production-quality music streaming application.