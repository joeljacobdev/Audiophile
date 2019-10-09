package com.pcforgeek.audiophile.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pcforgeek.audiophile.data.model.Playlist
import com.pcforgeek.audiophile.db.*
import com.pcforgeek.audiophile.util.Constants
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        lateinit var database: AppDatabase
        database = Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME)
            .addCallback(object: RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val favouritesPlaylist = Playlist("Favourites")
                    favouritesPlaylist.id = Constants.FAVOURITES_PLAYLIST
                    val mostPlayedPlaylist = Playlist("Most Played")
                    mostPlayedPlaylist.id = Constants.MOST_PLAYED_PLAYLIST
                    val notPlayedOncePlaylist = Playlist("Not Played Once")
                    notPlayedOncePlaylist.id = Constants.NOT_ONCE_PLAYED_PLAYLIST
                    GlobalScope.launch {
                        database.playlistDao().insertPlaylist(notPlayedOncePlaylist)
                        database.playlistDao().insertPlaylist(favouritesPlaylist)
                        database.playlistDao().insertPlaylist(mostPlayedPlaylist)
                    }
                    super.onCreate(db)
                }
            })
            .fallbackToDestructiveMigration()
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

}