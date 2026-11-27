package com.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.musicapp.data.local.entities.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(artists: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: ArtistEntity)

    @Update
    suspend fun update(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getById(id: String): ArtistEntity?

    @Query("SELECT * FROM artists WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<ArtistEntity>

    @Query("SELECT * FROM artists WHERE isFollowed = 1 ORDER BY followerCount DESC LIMIT :limit OFFSET :offset")
    suspend fun getFollowedArtists(limit: Int, offset: Int): List<ArtistEntity>

    @Query("SELECT * FROM artists WHERE isFollowed = 1 ORDER BY followerCount DESC")
    fun observeFollowedArtists(): Flow<List<ArtistEntity>>

    @Query("UPDATE artists SET isFollowed = :followed WHERE id = :id")
    suspend fun setFollowed(id: String, followed: Boolean)

    @Query("DELETE FROM artists WHERE id = :id")
    suspend fun deleteById(id: String)
}