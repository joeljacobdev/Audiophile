package com.pcforgeek.audiophile.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pcforgeek.audiophile.home.MainViewModel
import com.pcforgeek.audiophile.home.category.CategoryFeedViewModel
import com.pcforgeek.audiophile.home.option.SettingViewModel
import com.pcforgeek.audiophile.home.playlist.PlaylistViewModel
import com.pcforgeek.audiophile.home.song.SongFeedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SongFeedViewModel::class)
    abstract fun feedViewModel(feedViewModel: SongFeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel::class)
    abstract fun playlistViewModel(playlistViewModel: PlaylistViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoryFeedViewModel::class)
    abstract fun categoryFeedViewModel(categoryFeedViewModel: CategoryFeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun settingViewModel(settingViewModel: SettingViewModel): ViewModel
}