package dev.joeljacob.audiophile.db

import androidx.room.*
import dev.joeljacob.audiophile.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM SongItem order by title ASC")
    suspend fun getAllSongs(): List<Song>

    @Query("SELECT * FROM SongItem order by title ASC")
    fun getAllSongsFlow(): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(song: Song)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSongs(songs: List<Song>)

    @Query("UPDATE SongItem set playCount=(select playCount from SongItem where id=:id)+1 where id=:id")
    suspend fun incrementPlayCount(id: String)

    @Query("UPDATE SongItem set favourite=1 where id=:songId")
    suspend fun setSongToFavourite(songId: String)

    @Delete
    suspend fun deleteSong(song: Song)

    @Delete
    suspend fun deleteSongs(songs: List<Song>)

    @Query("DELETE from SongItem where id NOT IN (:filterValues)")
    suspend fun deleteRedundantItems(filterValues: List<String>)

    @Query("SELECT count(*) from SongItem where id NOT IN (:filterValues)")
    suspend fun deleteRedundantItemsCount(filterValues: List<String>): Int

    @Query("SELECT * FROM SongItem WHERE albumId = :id order by title ASC")
    suspend fun getSongsForAlbumId(id: String): List<Song>

    @Query("SELECT * FROM SongItem WHERE albumId = :id order by title ASC")
    fun getSongsForAlbumIdFlow(id: String): Flow<List<Song>>

    @Query("SELECT * FROM SongItem WHERE artistId = :id order by title ASC")
    suspend fun getSongsForArtistId(id: String): List<Song>

    @Query("SELECT * FROM SongItem WHERE artistId = :id order by title ASC")
    fun getSongsForArtistIdFlow(id: String): Flow<List<Song>>

}