package com.pcforgeek.audiophile.di

import android.app.Application
import androidx.room.Room
import com.pcforgeek.audiophile.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun getSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }

    @Provides
    @Singleton
    fun getCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()
}