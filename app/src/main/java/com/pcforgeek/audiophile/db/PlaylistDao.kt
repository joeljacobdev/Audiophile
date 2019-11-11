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

    @Query("SELECT * FROM SongItem WHERE playCount = 0")
    suspend fun getAllSongItemsNotPlayedOnce(): List<SongItem>

    @Query("SELECT * FROM SongItem WHERE favourite = 1")
    suspend fun getAllSongItemsFavourited(): List<SongItem>

    @Query("SELECT * FROM SongItem where playCount not in (0) order by playCount desc limit 30")
    suspend fun getAllSongItemsMostPlayed(): List<SongItem>

    @Query("SELECT count(*) FROM PlaylistItem p, SongItem s WHERE p.playlistId=:id and p.songId = s.songId")
    suspend fun getAllSongItemsWithPlaylistIdCount(id: Int): Int

    // for autocomplete text
    @Query("SELECT * from Playlist where playlistId NOT IN (1, 2, 3) ")
    suspend fun getAllPlaylistForAutoComplete(): List<Category.Playlist>
}