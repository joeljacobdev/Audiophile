package com.pcforgeek.audiophile.di

import android.app.Application
import android.content.Context
import androidx.room.PrimaryKey
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.data.MusicSource
import com.pcforgeek.audiophile.data.StorageMediaSource
import com.pcforgeek.audiophile.service.MusicService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(var app: App) {

    @Provides
    @Singleton
    fun provideApp(): Application {
        return app
    }

    @Provides
    @Singleton
    fun getContext(): Context = app

    @Provides
    @Singleton
    fun getMusicSource(mediaSource: StorageMediaSource): MusicSource {
        return mediaSource
    }
}