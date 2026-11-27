package com.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.musicapp.data.local.entities.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: AlbumEntity)

    @Update
    suspend fun update(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getById(id: String): AlbumEntity?

    @Query("SELECT * FROM albums WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<AlbumEntity>

    @Query("SELECT * FROM albums WHERE artistId = :artistId ORDER BY releaseDate DESC LIMIT :limit OFFSET :offset")
    suspend fun getByArtistId(artistId: String, limit: Int, offset: Int): List<AlbumEntity>

    @Query("SELECT * FROM albums WHERE isLiked = 1 ORDER BY addedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getSavedAlbums(limit: Int, offset: Int): List<AlbumEntity>

    @Query("SELECT * FROM albums WHERE isLiked = 1 ORDER BY addedAt DESC")
    fun observeSavedAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums ORDER BY releaseDate DESC LIMIT :limit")
    suspend fun getNewReleases(limit: Int): List<AlbumEntity>

    @Query("UPDATE albums SET isLiked = :liked WHERE id = :id")
    suspend fun setLiked(id: String, liked: Boolean)

    @Query("DELETE FROM albums WHERE id = :id")
    suspend fun deleteById(id: String)
}