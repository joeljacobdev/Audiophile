package com.pcforgeek.audiophile

import android.app.Application
import com.facebook.stetho.Stetho
import com.pcforgeek.audiophile.di.ApplicationComponent
import com.pcforgeek.audiophile.di.ApplicationModule
import com.pcforgeek.audiophile.di.DaggerApplicationComponent
import timber.log.Timber

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        component = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var instance: App
            private set
        lateinit var component: ApplicationComponent
            private set
    }
}