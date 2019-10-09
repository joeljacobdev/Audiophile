package com.pcforgeek.audiophile.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pcforgeek.audiophile.home.song.FeedViewModel
import com.pcforgeek.audiophile.home.MainViewModel
import com.pcforgeek.audiophile.home.playlist.PlaylistViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    abstract fun feedViewModel(feedViewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel::class)
    abstract fun playlistViewModel(playlistViewModel: PlaylistViewModel): ViewModel
}