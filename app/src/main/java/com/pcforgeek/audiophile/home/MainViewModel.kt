package com.pcforgeek.audiophile.home

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.pcforgeek.audiophile.service.MediaSessionConnection
import com.pcforgeek.audiophile.service.NOTHING_PLAYING
import com.pcforgeek.audiophile.util.id
import com.pcforgeek.audiophile.util.isPlayEnabled
import com.pcforgeek.audiophile.util.isPlaying
import javax.inject.Inject


class MainViewModel @Inject constructor(private val mediaSessionConnection: MediaSessionConnection) : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    val rootMediaId: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected) {
                mediaSessionConnection.rootMediaId
            } else {
                null
            }
        }

    // Should I use SingleLiveEvent
    val nowPlaying: LiveData<MediaMetadataCompat> = Transformations.map(mediaSessionConnection.nowPlaying) { metadata ->
        _isPlaying.value = metadata.id != NOTHING_PLAYING.id
        metadata
    }

    val currentPlaybackState: LiveData<PlaybackStateCompat> =
        Transformations.map(mediaSessionConnection.playbackState) { state ->
            if (state.isPlaying) {
                _isPlaying.value = true
            } else if (state.isPlayEnabled) {
                _isPlaying.value = false
            }
            state
        }

    fun playOrPause() {
        if (isPlaying.value == true) {
            _isPlaying.value = false
            mediaSessionConnection.transportControls.pause()
        } else if (isPlaying.value == false) {
            _isPlaying.value = true
            mediaSessionConnection.transportControls.play()
        }
    }

}