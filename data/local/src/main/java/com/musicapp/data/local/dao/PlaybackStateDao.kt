package com.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.musicapp.data.local.entities.PlaybackStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: PlaybackStateEntity)

    @Update
    suspend fun update(state: PlaybackStateEntity)

    @Query("SELECT * FROM playback_state WHERE id = 1")
    suspend fun getState(): PlaybackStateEntity?

    @Query("SELECT * FROM playback_state WHERE id = 1")
    fun observeState(): Flow<PlaybackStateEntity?>

    @Query("DELETE FROM playback_state")
    suspend fun clearState()
}