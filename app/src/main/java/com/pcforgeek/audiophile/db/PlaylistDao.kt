package com.pcforgeek.audiophile.db

import androidx.room.*
import com.pcforgeek.audiophile.data.model.Category
import com.pcforgeek.audiophile.data.model.PlaylistItem
import com.pcforgeek.audiophile.data.model.SongItem

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM Playlist")
    suspend fun getAllPlaylist(): List<Category.Playlist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Category.Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(playlistItem: PlaylistItem)

    @Delete
    suspend fun deletePlaylist(playlist: Category.Playlist)

    @Delete
    suspend fun deletePlaylistItem(playlistItem: PlaylistItem)

    @Query("SELECT s.* FROM PlaylistItem p, SongItem s WHERE p.playlistId=:id and p.songId = s.songId")
    suspend fun getAllSongItemsWithPlaylistId(id: Int): List<SongItem>

    // for autocomplete text
    @Query("SELECT title from Playlist where playlistId NOT IN (1, 2, 3) ")
    suspend fun getAllPlaylistName(): List<String>
}