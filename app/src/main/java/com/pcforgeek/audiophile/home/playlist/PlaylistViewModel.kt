package com.pcforgeek.audiophile.home.playlist

import androidx.lifecycle.*
import com.pcforgeek.audiophile.data.model.Playlist
import com.pcforgeek.audiophile.data.model.PlaylistItem
import com.pcforgeek.audiophile.data.model.SongItem
import com.pcforgeek.audiophile.db.PlaylistDao
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistViewModel @Inject constructor(private val playlistDao: PlaylistDao) : ViewModel() {

    private val _playlist = MutableLiveData<List<Playlist>>()
    val playlist: LiveData<List<Playlist>>
        get() = _playlist

    fun getAllPlaylist() {
        viewModelScope.launch {
           _playlist.value = playlistDao.getAllPlaylist()
        }
    }

}