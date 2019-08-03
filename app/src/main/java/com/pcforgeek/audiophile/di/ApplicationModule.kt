package com.pcforgeek.audiophile.di

import android.app.Application
import android.content.Context
import com.pcforgeek.audiophile.App
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
}