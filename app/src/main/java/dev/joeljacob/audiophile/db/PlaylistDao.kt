package dev.joeljacob.audiophile.db

import androidx.room.*
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.data.model.PlaylistItem
import dev.joeljacob.audiophile.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM Playlist order by createdAt ASC")
    suspend fun getAllPlaylist(): List<Category.Playlist>

    @Query("SELECT * FROM Playlist order by createdAt ASC")
    fun getAllPlaylistFlow(): Flow<List<Category.Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Category.Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(playlistItem: PlaylistItem)

    @Delete
    suspend fun deletePlaylist(playlist: Category.Playlist)

    @Delete
    suspend fun deletePlaylistItem(playlistItem: PlaylistItem)

    @Query("SELECT s.* FROM PlaylistItem p, SongItem s WHERE p.playlistId=:id and p.songId = s.id order by s.title ASC")
    suspend fun getAllSongItemsWithPlaylistId(id: Int): List<Song>

    @Query("SELECT s.* FROM PlaylistItem p, SongItem s WHERE p.playlistId=:id and p.songId = s.id order by s.title ASC")
    fun getAllSongItemsWithPlaylistIdFlow(id: Int): Flow<List<Song>>

    @Query("SELECT * FROM SongItem WHERE playCount = 0 order by title ASC")
    suspend fun getAllSongItemsNotPlayedOnce(): List<Song>

    @Query("SELECT * FROM SongItem WHERE playCount = 0 order by title ASC")
    fun getAllSongItemsNotPlayedOnceFlow(): Flow<List<Song>>

    @Query("SELECT * FROM SongItem WHERE favourite = 1 order by title ASC")
    suspend fun getAllSongItemsFavourited(): List<Song>

    @Query("SELECT * FROM SongItem WHERE favourite = 1 order by title ASC")
    fun getAllSongItemsFavouritedFlow(): Flow<List<Song>>

    @Query("SELECT * FROM SongItem where playCount not in (0) order by playCount desc limit 30")
    suspend fun getAllSongItemsMostPlayed(): List<Song>

    @Query("SELECT * FROM SongItem where playCount not in (0) order by playCount desc limit 30")
    fun getAllSongItemsMostPlayedFlow(): Flow<List<Song>>

    @Query("SELECT count(*) FROM PlaylistItem p, SongItem s WHERE p.playlistId=:id and p.songId = s.id")
    suspend fun getAllSongItemsWithPlaylistIdCount(id: Int): Int

    @Query("SELECT * from Playlist where id NOT IN (1, 2, 3) ")
    suspend fun getAllPlaylistForAutoComplete(): List<Category.Playlist>

    @Query("SELECT * from Playlist where id NOT IN (1, 2, 3) order by title ASC")
    fun getAllPlaylistForAutoCompleteFlow(): Flow<List<Category.Playlist>>
}