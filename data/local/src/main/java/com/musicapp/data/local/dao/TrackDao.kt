package com.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.musicapp.data.local.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Update
    suspend fun update(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getById(id: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE artistId = :artistId ORDER BY addedAt DESC LIMIT :limit")
    suspend fun getByArtistId(artistId: String, limit: Int): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE albumId = :albumId ORDER BY trackNumber ASC")
    suspend fun getByAlbumId(albumId: String): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getLikedTracks(limit: Int, offset: Int): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE isLiked = 1")
    fun observeLikedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE lastPlayedAt IS NOT NULL ORDER BY lastPlayedAt DESC LIMIT :limit")
    suspend fun getRecentlyPlayed(limit: Int): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE lastPlayedAt IS NOT NULL ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun observeRecentlyPlayed(limit: Int): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE source = 1 ORDER BY addedAt DESC") // LOCAL_IMPORT
    suspend fun getLocalTracks(): List<TrackEntity>

    @Query("UPDATE tracks SET isLiked = :liked WHERE id = :id")
    suspend fun setLiked(id: String, liked: Boolean)

    @Query("UPDATE tracks SET playCount = playCount + 1, lastPlayedAt = :now WHERE id = :id")
    suspend fun incrementPlayCount(id: String, now: Long)

    @Query("UPDATE tracks SET lastPlayedAt = :now WHERE id = :id")
    suspend fun updateLastPlayed(id: String, now: Long)

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tracks WHERE source = 1") // Clear local imports
    suspend fun clearLocalTracks()
}