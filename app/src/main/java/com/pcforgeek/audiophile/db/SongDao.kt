package com.pcforgeek.audiophile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pcforgeek.audiophile.data.model.SongItem

@Dao
interface SongDao {

    @Query("SELECT * FROM SongItem")
    suspend fun getAllSongs(): List<SongItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(songItem: SongItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSongs(songs: List<SongItem>)

    @Query("SELECT * FROM SongItem WHERE albumId = :id ")
    suspend fun getSongsForAlbumId(id: String): List<SongItem>

    @Query("SELECT * FROM SongItem WHERE artistId = :id ")
    suspend fun getSongsForArtistId(id: String): List<SongItem>

}