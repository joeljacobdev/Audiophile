package dev.joeljacob.audiophile.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.data.MusicSource
import dev.joeljacob.audiophile.data.StorageMediaSource
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