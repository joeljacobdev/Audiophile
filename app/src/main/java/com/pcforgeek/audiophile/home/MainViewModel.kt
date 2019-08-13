package com.pcforgeek.audiophile.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.pcforgeek.audiophile.service.MediaSessionConnection
import javax.inject.Inject


class MainViewModel @Inject constructor(private val mediaSessionConnection: MediaSessionConnection): ViewModel() {

    val rootMediaId: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected) {
                mediaSessionConnection.rootMediaId
            } else {
                null
            }
        }
}