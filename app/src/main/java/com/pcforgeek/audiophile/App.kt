package com.pcforgeek.audiophile

import android.app.Application
import com.facebook.stetho.Stetho
import com.pcforgeek.audiophile.di.ApplicationComponent
import com.pcforgeek.audiophile.di.ApplicationModule
import com.pcforgeek.audiophile.di.DaggerApplicationComponent

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        component = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    companion object {
        lateinit var instance: App
            private set
        lateinit var component: ApplicationComponent
            private set
    }
}