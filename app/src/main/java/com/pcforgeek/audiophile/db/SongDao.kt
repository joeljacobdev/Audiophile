package com.pcforgeek.audiophile.db

import androidx.room.*
import com.pcforgeek.audiophile.data.model.SongItem

@Dao
interface SongDao {

    @Query("SELECT * FROM SongItem")
    suspend fun getAllSongs(): List<SongItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(songItem: SongItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSongs(songs: List<SongItem>)

    @Query("UPDATE SongItem set playCount=(select playCount from SongItem where songId=:id)+1 where songId=:id")
    suspend fun incrementPlayCount(id: String)

    @Delete
    suspend fun deleteSong(song: SongItem)

    @Delete
    suspend fun deleteSongs(songs: List<SongItem>)

    @Query("SELECT * FROM SongItem WHERE albumId = :id ")
    suspend fun getSongsForAlbumId(id: String): List<SongItem>

    @Query("SELECT * FROM SongItem WHERE artistId = :id ")
    suspend fun getSongsForArtistId(id: String): List<SongItem>

}