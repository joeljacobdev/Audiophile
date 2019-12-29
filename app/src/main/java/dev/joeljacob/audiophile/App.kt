package dev.joeljacob.audiophile

import android.app.Application
import com.facebook.stetho.Stetho
import dev.joeljacob.audiophile.di.ApplicationComponent
import dev.joeljacob.audiophile.di.ApplicationModule
import dev.joeljacob.audiophile.di.DaggerApplicationComponent
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