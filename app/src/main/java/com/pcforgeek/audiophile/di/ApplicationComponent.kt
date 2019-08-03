package com.pcforgeek.audiophile.di

import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.MainActivity
import com.pcforgeek.audiophile.notifcation.NotificationBuilder
import com.pcforgeek.audiophile.repo.SongsRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, DatabaseModule::class, ViewModelModule::class, AudioModule::class])
interface ApplicationComponent {
    fun inject(app: App)
    fun inject(mainActivity: MainActivity)
    fun inject(songsRepository: SongsRepository)
    fun inject(notificationBuilder: NotificationBuilder)
}