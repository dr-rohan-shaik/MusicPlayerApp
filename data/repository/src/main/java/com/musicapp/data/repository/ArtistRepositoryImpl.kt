package com.musicapp.data.repository

import com.musicapp.core.data.ArtistRepository
import com.musicapp.core.model.Artist
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.ArtistDao
import com.musicapp.data.local.entities.ArtistEntity
import com.musicapp.data.remote.MusicApiService
import com.musicapp.data.remote.ArtistDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepositoryImpl @Inject constructor(
    private val artistDao: ArtistDao,
    private val apiService: MusicApiService
) : ArtistRepository {

    override suspend fun getArtist(id: String): Result<Artist> = try {
        val localArtist = artistDao.getById(id)
        if (localArtist != null) {
            Result.success(localArtist.toDomain())
        } else {
            val response = apiService.getArtist(id)
            if (response.isSuccessful && response.body()?.isSuccess() == true) {
                val artist = response.body()!!.data!!.toDomain()
                artistDao.insert(artist.toEntity())
                Result.success(artist)
            } else {
                Result.failure(Exception("Failed to fetch artist: ${response.message()}"))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getArtists(ids: List<String>): Result<List<Artist>> = try {
        val localArtists = artistDao.getByIds(ids).map { it.toDomain() }
        val missingIds = ids.filter { id -> !localArtists.any { it.id == id } }

        missingIds.forEach { id ->
            val response = apiService.getArtist(id)
            if (response.isSuccessful && response.body()?.isSuccess() == true) {
                val artist = response.body()!!.data!!.toDomain()
                artistDao.insert(artist.toEntity())
                localArtists.add(artist)
            }
        }
        Result.success(localArtists)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFollowedArtists(limit: Int, offset: Int): Result<com.musicapp.core.data.PagedResult<Artist>> = try {
        val artists = artistDao.getFollowedArtists(limit, offset).map { it.toDomain() }
        Result.success(com.musicapp.core.data.PagedResult(artists, artists.size.toLong(), offset, limit, artists.size >= limit))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRelatedArtists(artistId: String, limit: Int): Result<List<Artist>> = try {
        val response = apiService.getRelatedArtists(artistId)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val artists = response.body()!!.data!!.map { it.toDomain() }
            Result.success(artists)
        } else {
            Result.failure(Exception("Failed to get related artists: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun followArtist(artistId: String): Result<Unit> = try {
        val response = apiService.followArtist(artistId)
        if (response.isSuccessful) {
            artistDao.setFollowed(artistId, true)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to follow artist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unfollowArtist(artistId: String): Result<Unit> = try {
        val response = apiService.unfollowArtist(artistId)
        if (response.isSuccessful) {
            artistDao.setFollowed(artistId, false)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unfollow artist: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeFollowedArtists(): Flow<List<Artist>> =
        artistDao.observeFollowedArtists().map { it.map { it.toDomain() } }
}

private fun ArtistEntity.toDomain(): Artist {
    return Artist(
        id = this.id,
        name = this.name,
        artworkUrl = this.artworkUrl,
        genres = com.google.gson.Gson().fromJson(this.genres, Array<String>::class.java).toList(),
        followerCount = this.followerCount,
        isVerified = this.isVerified,
        biography = this.biography
    )
}

private fun Artist.toEntity(): ArtistEntity {
    return ArtistEntity(
        id = this.id,
        name = this.name,
        artworkUrl = this.artworkUrl,
        genres = com.google.gson.Gson().toJson(this.genres),
        followerCount = this.followerCount,
        isVerified = this.isVerified,
        biography = this.biography,
        isFollowed = false
    )
}

private fun ArtistDto.toDomain(): Artist {
    return Artist(
        id = this.id,
        name = this.name,
        artworkUrl = images?.maxByOrNull { it.width ?: 0 }?.url,
        genres = this.genres,
        followerCount = followers?.total ?: 0,
        isVerified = false,
        biography = null
    )
}