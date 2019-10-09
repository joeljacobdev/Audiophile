package com.pcforgeek.audiophile.home.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pcforgeek.audiophile.data.model.Category
import com.pcforgeek.audiophile.data.model.PlaylistItem
import com.pcforgeek.audiophile.db.PlaylistDao
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistViewModel @Inject constructor(private val playlistDao: PlaylistDao) : ViewModel() {

    private val _playlist = MutableLiveData<List<Category.Playlist>>()
    val playlist: LiveData<List<Category.Playlist>>
        get() = _playlist

    fun getAllPlaylist() {
        viewModelScope.launch {
            _playlist.value = playlistDao.getAllPlaylist()
        }
    }

    fun addSongToPlaylist(playlistId: Int, songId: String) {
        viewModelScope.launch {
            val playlistItem = PlaylistItem(playlistId, songId)
            playlistDao.insertPlaylistItem(playlistItem)
        }
    }

}