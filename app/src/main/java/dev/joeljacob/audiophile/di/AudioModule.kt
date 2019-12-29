package dev.joeljacob.audiophile.di

import android.content.ComponentName
import android.content.Context
import dev.joeljacob.audiophile.service.MediaSessionConnection
import dev.joeljacob.audiophile.service.MusicService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AudioModule {

    @Provides
    @Singleton
    fun providesComponentName(context: Context): ComponentName {
        return ComponentName(context, MusicService::class.java)
    }

    @Provides
    @Singleton
    fun provideMediaSessionConnection(context: Context, componentName: ComponentName): MediaSessionConnection {
        return MediaSessionConnection.getInstance(
            context,
            componentName
        )
    }
}