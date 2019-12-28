package com.pcforgeek.audiophile.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pcforgeek.audiophile.data.model.*

@TypeConverters(value = [TypeConverter::class])
@Database(
    entities = [SongItem::class, Category.Playlist::class,
        PlaylistItem::class, BlacklistPath::class, ArtistSongItem::class,
        AlbumSongItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun categoryDao(): CategoryDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun blacklistPathDao(): BlacklistPathDao

    companion object {
        const val DB_NAME = "songs_db"
    }
}