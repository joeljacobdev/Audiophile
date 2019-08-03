package com.pcforgeek.audiophile.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(value = [TypeConverter::class])
@Database(
    entities = [MediaItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songsDao(): SongsDao

    companion object {
        const val DB_NAME = "songs_db"
    }
}