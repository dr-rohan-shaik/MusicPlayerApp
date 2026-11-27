package com.musicapp.data.repository

import com.musicapp.core.data.AlbumRepository
import com.musicapp.core.model.Album
import com.musicapp.data.local.AppDatabase
import com.musicapp.data.local.dao.AlbumDao
import com.musicapp.data.local.entities.AlbumEntity
import com.musicapp.data.remote.MusicApiService
import com.musicapp.data.remote.AlbumDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao,
    private val apiService: MusicApiService
) : AlbumRepository {

    override suspend fun getAlbum(id: String): Result<Album> = try {
        val localAlbum = albumDao.getById(id)
        if (localAlbum != null) {
            Result.success(localAlbum.toDomain())
        } else {
            val response = apiService.getAlbum(id)
            if (response.isSuccessful && response.body()?.isSuccess() == true) {
                val album = response.body()!!.data!!.toDomain()
                albumDao.insert(album.toEntity())
                Result.success(album)
            } else {
                Result.failure(Exception("Failed to fetch album: ${response.message()}"))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAlbums(ids: List<String>): Result<List<Album>> = try {
        val localAlbums = albumDao.getByIds(ids).map { it.toDomain() }
        val missingIds = ids.filter { id -> !localAlbums.any { it.id == id } }

        missingIds.forEach { id ->
            val response = apiService.getAlbum(id)
            if (response.isSuccessful && response.body()?.isSuccess() == true) {
                val album = response.body()!!.data!!.toDomain()
                albumDao.insert(album.toEntity())
                localAlbums.add(album)
            }
        }
        Result.success(localAlbums)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getNewReleases(limit: Int): Result<List<Album>> = try {
        val response = apiService.getNewReleases(limit, 0)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val albums = response.body()!!.data!!.items.map { it.toDomain() }
            albums.forEach { albumDao.insert(it.toEntity()) }
            Result.success(albums)
        } else {
            Result.failure(Exception("Failed to get new releases: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getArtistAlbums(artistId: String, limit: Int, offset: Int): Result<com.musicapp.core.data.PagedResult<Album>> = try {
        val response = apiService.getArtistAlbums(artistId, limit, offset)
        if (response.isSuccessful && response.body()?.isSuccess() == true) {
            val dto = response.body()!!.data!!
            val albums = dto.items.map { it.toDomain() }
            albums.forEach { albumDao.insert(it.toEntity()) }
            Result.success(com.musicapp.core.data.PagedResult(albums, dto.total, offset, limit, dto.next != null))
        } else {
            Result.failure(Exception("Failed to get artist albums: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSavedAlbums(limit: Int, offset: Int): Result<com.musicapp.core.data.PagedResult<Album>> = try {
        val albums = albumDao.getSavedAlbums(limit, offset).map { it.toDomain() }
        Result.success(com.musicapp.core.data.PagedResult(albums, albums.size.toLong(), offset, limit, albums.size >= limit))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveAlbum(albumId: String): Result<Unit> = try {
        val response = apiService.likeAlbum(albumId)
        if (response.isSuccessful) {
            albumDao.setLiked(albumId, true)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to save album: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unsaveAlbum(albumId: String): Result<Unit> = try {
        val response = apiService.unlikeAlbum(albumId)
        if (response.isSuccessful) {
            albumDao.setLiked(albumId, false)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unsave album: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeSavedAlbums(): Flow<List<Album>> =
        albumDao.observeSavedAlbums().map { it.map { it.toDomain() } }
}

private fun AlbumEntity.toDomain(): Album {
    return Album(
        id = this.id,
        title = this.title,
        artist = com.musicapp.core.model.ArtistRef(this.artistId, "", null),
        artworkUrl = this.artworkUrl,
        releaseDate = this.releaseDate,
        trackCount = this.trackCount,
        duration = this.duration,
        type = com.musicapp.core.model.AlbumType.values()[this.type],
        isLiked = this.isLiked,
        tracks = emptyList()
    )
}

private fun Album.toEntity(): AlbumEntity {
    return AlbumEntity(
        id = this.id,
        title = this.title,
        artistId = this.artist.id,
        artworkUrl = this.artworkUrl,
        releaseDate = this.releaseDate,
        trackCount = this.trackCount,
        duration = this.duration,
        type = this.type.ordinal,
        isLiked = this.isLiked,
        addedAt = this.addedAt
    )
}

private fun AlbumDto.toDomain(): Album {
    return Album(
        id = this.id,
        title = this.name,
        artist = artists.firstOrNull()?.toDomainRef() ?: com.musicapp.core.model.ArtistRef("", "Unknown", null),
        artworkUrl = images?.maxByOrNull { it.width ?: 0 }?.url,
        releaseDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse(release_date)?.time ?: 0L,
        trackCount = this.total_tracks,
        duration = this.duration_ms,
        type = com.musicapp.core.model.AlbumType.valueOf(this.album_type.uppercase()),
        isLiked = false,
        tracks = emptyList()
    )
}

private fun ArtistRefDto.toDomainRef(): com.musicapp.core.model.ArtistRef {
    return com.musicapp.core.model.ArtistRef(
        id = this.id,
        name = this.name,
        artworkUrl = images?.maxByOrNull { it.width ?: 0 }?.url
    )
}