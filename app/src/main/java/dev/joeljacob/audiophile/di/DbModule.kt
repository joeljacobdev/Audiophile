package dev.joeljacob.audiophile.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.db.*
import dev.joeljacob.audiophile.util.Playlist
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Singleton

@Module
class DbModule {
    private val MIGRATIONS_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Timber.i("MIGRATION RUN")
            database.execSQL("ALTER TABLE Playlist ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        lateinit var database: AppDatabase
        database = Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val favouritesPlaylist = Category.Playlist("Favourites")
                    favouritesPlaylist.id = Playlist.FAVOURITES_PLAYLIST
                    val mostPlayedPlaylist = Category.Playlist("Most Played")
                    mostPlayedPlaylist.id = Playlist.MOST_PLAYED_PLAYLIST
                    val notPlayedOncePlaylist = Category.Playlist("Not Played Once")
                    notPlayedOncePlaylist.id = Playlist.NOT_ONCE_PLAYED_PLAYLIST
                    GlobalScope.launch {
                        database.playlistDao().insertPlaylist(favouritesPlaylist)
                        database.playlistDao().insertPlaylist(mostPlayedPlaylist)
                        database.playlistDao().insertPlaylist(notPlayedOncePlaylist)
                    }
                    super.onCreate(db)
                }
            })
            .addMigrations(MIGRATIONS_1_2)
            .build()
        return database
    }

    @Provides
    @Singleton
    fun getSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }

    @Provides
    @Singleton
    fun getCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()

    @Provides
    @Singleton
    fun getPlaylistDao(appDatabase: AppDatabase): PlaylistDao = appDatabase.playlistDao()

    @Provides
    @Singleton
    fun getBlacklistPathDao(appDatabase: AppDatabase): BlacklistPathDao =
        appDatabase.blacklistPathDao()

}