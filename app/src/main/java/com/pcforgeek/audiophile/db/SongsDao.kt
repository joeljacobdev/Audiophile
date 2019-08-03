package com.pcforgeek.audiophile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(list: List<MediaItem>)

    @Query("SELECT * from MediaItem ORDER BY title ASC")
    suspend fun getAllSongs(): List<MediaItem>

    @Query("DELETE from MediaItem")
    suspend fun deleteAllSongs()
}