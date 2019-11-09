package com.pcforgeek.audiophile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pcforgeek.audiophile.data.model.Category

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Category.Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Category.Artist)

    @Query("SELECT * FROM Album")
    suspend fun getAllAlbums(): List<Category.Album>

    @Query("SELECT * FROM Artist")
    suspend fun getAllArtists(): List<Category.Artist>
}