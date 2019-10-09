package com.pcforgeek.audiophile.di

import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.data.StorageMediaSource
import com.pcforgeek.audiophile.home.song.SongFeedFragment
import com.pcforgeek.audiophile.home.FeedGridFragment
import com.pcforgeek.audiophile.home.playlist.PlaylistFragment
import com.pcforgeek.audiophile.home.MainActivity
import com.pcforgeek.audiophile.notifcation.NotificationBuilder
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class, AudioModule::class, DbModule::class])
interface ApplicationComponent {
    fun inject(app: App)
    fun inject(mainActivity: MainActivity)
    fun inject(notificationBuilder: NotificationBuilder)
    fun inject(feedFragment: SongFeedFragment)
    fun inject(feedGridFragment: FeedGridFragment)
    fun inject(feedListFragment: PlaylistFragment)
    fun inject(storageMediaSource: StorageMediaSource)
}