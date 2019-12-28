package com.pcforgeek.audiophile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pcforgeek.audiophile.data.model.AlbumSongItem
import com.pcforgeek.audiophile.data.model.ArtistSongItem
import com.pcforgeek.audiophile.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbumSongItem(albumSongItem: AlbumSongItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtistSongItem(artistSongItem: ArtistSongItem)

    @Query("SELECT DISTINCT a.albumId as albumId, a.title as album FROM AlbumSongItem as a GROUP BY a.albumId")
    suspend fun getAllAlbums(): List<Category.Album>

    @Query("SELECT DISTINCT a.albumId as albumId, a.title as album FROM AlbumSongItem as a GROUP BY a.albumId")
    fun getAllAlbumsFlow(): Flow<List<Category.Album>>

    @Query("SELECT DISTINCT a.artistId as artistId, a.title as artist FROM ArtistSongItem as a GROUP BY a.artistId")
    suspend fun getAllArtists(): List<Category.Artist>

    @Query("SELECT DISTINCT a.artistId as artistId, a.title as artist FROM ArtistSongItem as a GROUP BY a.artistId")
    fun getAllArtistsFlow(): Flow<List<Category.Artist>>
}