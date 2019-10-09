package com.pcforgeek.audiophile.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pcforgeek.audiophile.data.model.CategoryItem
import com.pcforgeek.audiophile.data.model.Playlist
import com.pcforgeek.audiophile.data.model.PlaylistItem
import com.pcforgeek.audiophile.data.model.SongItem

@TypeConverters(value = [TypeConverter::class])
@Database(
    entities = [SongItem::class, CategoryItem::class, Playlist::class, PlaylistItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun categoryDao(): CategoryDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        const val DB_NAME = "songs_db"
    }
}