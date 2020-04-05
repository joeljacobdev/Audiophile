package dev.joeljacob.audiophile.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.joeljacob.audiophile.data.model.*

@TypeConverters(value = [TypeConverter::class])
@Database(
    entities = [Song::class, Category.Playlist::class,
        PlaylistItem::class, BlacklistPath::class, ArtistSongItem::class,
        AlbumSongItem::class],
    version = 2,
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