package com.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.musicapp.data.local.entities.PlaylistEntity
import com.musicapp.data.local.entities.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity)

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getById(id: String): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE ownerId = :ownerId AND source = 0 ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getUserPlaylists(ownerId: String, limit: Int, offset: Int): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE ownerId = :ownerId AND source = 0 ORDER BY updatedAt DESC")
    fun observeUserPlaylists(ownerId: String): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE source = 1 ORDER BY followerCount DESC LIMIT :limit OFFSET :offset")
    suspend fun getFollowedPlaylists(limit: Int, offset: Int): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE source = 1 ORDER BY followerCount DESC")
    fun observeFollowedPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE source = 0 ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun getMadeForYouPlaylists(limit: Int): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE source = 1 ORDER BY followerCount DESC LIMIT :limit")
    suspend fun getPopularNearYou(limit: Int): List<PlaylistEntity>

    // Playlist tracks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTracks(tracks: List<PlaylistTrackEntity>)

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getPlaylistTracks(playlistId: String): List<PlaylistTrackEntity>

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    fun observePlaylistTracks(playlistId: String): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun clearPlaylistTracks(playlistId: String)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

    @Query("UPDATE playlist_tracks SET position = :newPosition WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun updateTrackPosition(playlistId: String, trackId: String, newPosition: Int)

    @Transaction
    suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>, position: Int?) {
        val existingTracks = getPlaylistTracks(playlistId)
        var insertPosition = position ?: existingTracks.size

        val newTracks = trackIds.mapIndexed { index, trackId ->
            PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = trackId,
                position = insertPosition + index,
                addedAt = System.currentTimeMillis(),
                addedBy = null
            )
        }

        // Shift existing tracks after insert position
        existingTracks
            .filter { it.position >= insertPosition }
            .forEach { track ->
                updateTrackPosition(playlistId, track.trackId, track.position + trackIds.size)
            }

        insertPlaylistTracks(newTracks)
    }

    @Transaction
    suspend fun removeTracksFromPlaylist(playlistId: String, trackIds: List<String>) {
        trackIds.forEach { trackId ->
            removeTrackFromPlaylist(playlistId, trackId)
        }
        // Re-index remaining tracks
        val remainingTracks = getPlaylistTracks(playlistId)
        remainingTracks.forEachIndexed { index, track ->
            if (track.position != index) {
                updateTrackPosition(playlistId, track.trackId, index)
            }
        }
    }

    @Transaction
    suspend fun reorderTrack(playlistId: String, fromIndex: Int, toIndex: Int) {
        val tracks = getPlaylistTracks(playlistId)
        if (fromIndex < 0 || fromIndex >= tracks.size || toIndex < 0 || toIndex >= tracks.size) return

        val movedTrack = tracks.removeAt(fromIndex)
        tracks.add(toIndex, movedTrack)

        tracks.forEachIndexed { index, track ->
            if (track.position != index) {
                updateTrackPosition(playlistId, track.trackId, index)
            }
        }
    }
}