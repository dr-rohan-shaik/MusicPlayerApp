package com.musicapp.data.remote

import com.musicapp.core.model.Track
import com.musicapp.core.model.Album
import com.musicapp.core.model.Artist
import com.musicapp.core.model.Playlist
import com.musicapp.core.data.Category
import com.musicapp.core.data.PagedResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicApiService {
    // Tracks
    @GET("tracks")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String = "track"
    ): Response<ApiResponse<SearchResultsDto>>

    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: String): Response<ApiResponse<TrackDto>>

    @GET("tracks/{id}/stream")
    suspend fun getTrackStreamUrl(@Path("id") id: String): Response<ApiResponse<StreamUrlDto>>

    // Albums
    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: String): Response<ApiResponse<AlbumDto>>

    @GET("albums/{id}/tracks")
    suspend fun getAlbumTracks(
        @Path("id") id: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<TrackDto>>>

    // Artists
    @GET("artists/{id}")
    suspend fun getArtist(@Path("id") id: String): Response<ApiResponse<ArtistDto>>

    @GET("artists/{id}/albums")
    suspend fun getArtistAlbums(
        @Path("id") id: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<AlbumDto>>>

    @GET("artists/{id}/related")
    suspend fun getRelatedArtists(@Path("id") id: String): Response<ApiResponse<List<ArtistDto>>>

    // Playlists
    @GET("playlists/{id}")
    suspend fun getPlaylist(@Path("id") id: String): Response<ApiResponse<PlaylistDto>>

    @GET("playlists/{id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("id") id: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<TrackDto>>>

    @POST("playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequestDto): Response<ApiResponse<PlaylistDto>>

    @PUT("playlists/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: String,
        @Body request: UpdatePlaylistRequestDto
    ): Response<ApiResponse<PlaylistDto>>

    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(@Path("id") id: String): Response<ApiResponse<Unit>>

    @PUT("playlists/{id}/follow")
    suspend fun followPlaylist(@Path("id") id: String): Response<ApiResponse<Unit>>

    @DELETE("playlists/{id}/follow")
    suspend fun unfollowPlaylist(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("playlists/{id}/tracks")
    suspend fun addTracksToPlaylist(
        @Path("id") id: String,
        @Body request: AddTracksRequestDto
    ): Response<ApiResponse<Unit>>

    @DELETE("playlists/{id}/tracks")
    suspend fun removeTracksFromPlaylist(
        @Path("id") id: String,
        @Body request: RemoveTracksRequestDto
    ): Response<ApiResponse<Unit>>

    // Browse
    @GET("browse/categories")
    suspend fun getCategories(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<CategoryDto>>>

    @GET("browse/categories/{id}/playlists")
    suspend fun getCategoryPlaylists(
        @Path("id") id: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<PlaylistDto>>>

    @GET("browse/new-releases")
    suspend fun getNewReleases(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<AlbumDto>>>

    // Recommendations
    @GET("recommendations")
    suspend fun getRecommendations(
        @Query("seed_tracks") seedTracks: String?,
        @Query("seed_artists") seedArtists: String?,
        @Query("seed_genres") seedGenres: String?,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<RecommendationsDto>>

    // Likes
    @PUT("tracks/{id}/like")
    suspend fun likeTrack(@Path("id") id: String): Response<ApiResponse<Unit>>

    @DELETE("tracks/{id}/like")
    suspend fun unlikeTrack(@Path("id") id: String): Response<ApiResponse<Unit>>

    @PUT("albums/{id}/like")
    suspend fun likeAlbum(@Path("id") id: String): Response<ApiResponse<Unit>>

    @DELETE("albums/{id}/like")
    suspend fun unlikeAlbum(@Path("id") id: String): Response<ApiResponse<Unit>>

    @PUT("artists/{id}/follow")
    suspend fun followArtist(@Path("id") id: String): Response<ApiResponse<Unit>>

    @DELETE("artists/{id}/follow")
    suspend fun unfollowArtist(@Path("id") id: String): Response<ApiResponse<Unit>>

    // User
    @GET("me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    @GET("me/playlists")
    suspend fun getUserPlaylists(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<PagedResultDto<PlaylistDto>>>
}

// DTOs
data class ApiResponse<T>(
    val data: T?,
    val error: ApiError?
) {
    fun isSuccess(): Boolean = error == null && data != null
}

data class ApiError(
    val code: Int,
    val message: String,
    val details: String?
)

data class PagedResultDto<T>(
    val items: List<T>,
    val total: Long,
    val limit: Int,
    val offset: Int,
    val next: String?
)

data class SearchResultsDto(
    val tracks: PagedResultDto<TrackDto>?,
    val artists: PagedResultDto<ArtistDto>?,
    val albums: PagedResultDto<AlbumDto>?,
    val playlists: PagedResultDto<PlaylistDto>?
)

data class TrackDto(
    val id: String,
    val name: String,
    val artists: List<ArtistRefDto>,
    val album: AlbumRefDto,
    val duration_ms: Long,
    val images: List<ImageDto>,
    val external_urls: Map<String, String>?,
    val explicit: Boolean,
    val track_number: Int,
    val disc_number: Int,
    val preview_url: String?
)

data class AlbumDto(
    val id: String,
    val name: String,
    val artists: List<ArtistRefDto>,
    val images: List<ImageDto>,
    val release_date: String,
    val total_tracks: Int,
    val duration_ms: Long,
    val album_type: String,
    val external_urls: Map<String, String>?
)

data class ArtistDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>,
    val genres: List<String>,
    val followers: FollowersDto,
    val external_urls: Map<String, String>?
)

data class PlaylistDto(
    val id: String,
    val name: String,
    val description: String?,
    val images: List<ImageDto>,
    val owner: UserRefDto,
    val public: Boolean,
    val collaborative: Boolean,
    val tracks: TracksRefDto,
    val followers: FollowersDto,
    val external_urls: Map<String, String>?
)

data class CategoryDto(
    val id: String,
    val name: String,
    val icons: List<ImageDto>
)

data class RecommendationsDto(
    val tracks: List<TrackDto>
)

data class StreamUrlDto(
    val url: String,
    val expires_at: Long
)

data class UserDto(
    val id: String,
    val display_name: String?,
    val email: String?,
    val images: List<ImageDto>,
    val product: String,
    val country: String
)

data class UserRefDto(
    val id: String,
    val display_name: String?,
    val images: List<ImageDto>
)

data class ArtistRefDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>?
)

data class AlbumRefDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>
)

data class ImageDto(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class FollowersDto(
    val total: Long
)

data class TracksRefDto(
    val total: Int,
    val href: String?
)

data class CreatePlaylistRequestDto(
    val name: String,
    val description: String?,
    val public: Boolean,
    val collaborative: Boolean
)

data class UpdatePlaylistRequestDto(
    val name: String?,
    val description: String?,
    val public: Boolean?,
    val collaborative: Boolean?
)

data class AddTracksRequestDto(
    val uris: List<String>,
    val position: Int?
)

data class RemoveTracksRequestDto(
    val tracks: List<TrackUriDto>
)

data class TrackUriDto(
    val uri: String
)