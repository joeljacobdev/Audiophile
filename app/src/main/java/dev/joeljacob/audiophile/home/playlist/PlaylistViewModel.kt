package dev.joeljacob.audiophile.home.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.data.model.PlaylistItem
import dev.joeljacob.audiophile.db.PlaylistDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistViewModel @Inject constructor(private val playlistDao: PlaylistDao) : ViewModel() {

    private val _playlist = MutableLiveData<List<Category.Playlist>>()
    val playlist: LiveData<List<Category.Playlist>>
        get() = _playlist

    fun getAllPlaylist(showDefaultPlaylist: Boolean = true) {
        viewModelScope.launch {
            if (!showDefaultPlaylist)
                playlistDao.getAllPlaylistForAutoCompleteFlow().collect {
                    _playlist.value = it
                }
            else
                playlistDao.getAllPlaylistFlow().collect {
                    _playlist.value = it
                }
        }
    }

    fun addSongToPlaylist(playlistId: Int, songId: String) {
        viewModelScope.launch {
            val playlistItem = PlaylistItem(playlistId, songId)
            playlistDao.insertPlaylistItem(playlistItem)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val playlist = Category.Playlist(name)
            playlistDao.insertPlaylist(playlist)
        }
    }

    fun deletePlaylist(playlist: Category.Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistDao.deletePlaylist(playlist)
        }
    }

}