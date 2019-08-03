package com.pcforgeek.audiophile.di

import android.content.Context
import androidx.room.Room
import com.pcforgeek.audiophile.db.AppDatabase
import com.pcforgeek.audiophile.db.SongsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun getAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun getSongsDao(appDatabase: AppDatabase): SongsDao = appDatabase.songsDao()
}