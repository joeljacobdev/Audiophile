package dev.joeljacob.audiophile.di

import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.data.StorageMediaSource
import dev.joeljacob.audiophile.home.song.SongFeedFragment
import dev.joeljacob.audiophile.home.category.CategoryFeedGridFragment
import dev.joeljacob.audiophile.home.playlist.PlaylistFragment
import dev.joeljacob.audiophile.home.MainActivity
import dev.joeljacob.audiophile.home.option.SettingFragment
import dev.joeljacob.audiophile.home.playlist.AddToPlaylistFragment
import dev.joeljacob.audiophile.notifcation.NotificationBuilder
import dev.joeljacob.audiophile.service.MusicService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class, AudioModule::class, DbModule::class])
interface ApplicationComponent {
    fun inject(app: App)
    fun inject(mainActivity: MainActivity)
    fun inject(notificationBuilder: NotificationBuilder)
    fun inject(feedFragment: SongFeedFragment)
    fun inject(feedGridFragment: CategoryFeedGridFragment)
    fun inject(feedListFragment: PlaylistFragment)
    fun inject(musicService: MusicService)
    fun inject(addToPlaylistFragment: AddToPlaylistFragment)
    fun inject(settingFragment: SettingFragment)
    fun inject(storageMediaSource: StorageMediaSource)
}