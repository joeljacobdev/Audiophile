package com.pcforgeek.audiophile.db

import androidx.room.*
import com.pcforgeek.audiophile.data.model.Playlist
import com.pcforgeek.audiophile.data.model.PlaylistItem
import com.pcforgeek.audiophile.data.model.SongItem

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM Playlist")
    suspend fun getAllPlaylist(): List<Playlist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(playlistItem: PlaylistItem)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylistItem(playlistItem: PlaylistItem)

    @Query("SELECT s.* FROM PlaylistItem p, SongItem s WHERE p.playlistId=:id and p.songId = s.id")
    suspend fun getAllSongsWithPlaylistId(id: Int): List<SongItem>
}