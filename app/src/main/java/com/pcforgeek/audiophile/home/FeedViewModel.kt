package com.pcforgeek.audiophile.home

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.SongItem
import com.pcforgeek.audiophile.service.EMPTY_PLAYBACK_STATE
import com.pcforgeek.audiophile.service.MediaSessionConnection
import com.pcforgeek.audiophile.service.NOTHING_PLAYING
import com.pcforgeek.audiophile.util.*
import javax.inject.Inject

class FeedViewModel @Inject constructor(private val mediaSessionConnection: MediaSessionConnection) : ViewModel() {

    private val _mediaList = MutableLiveData<List<SongItem>>()
    val mediaList: LiveData<List<SongItem>>
        get() = _mediaList

    private var mediaId: String = Constants.ROOT_MEDIA_ID
    fun setMediaId(value: String) {
        mediaId = value
    }


    val rootMediaId: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected) {
                mediaSessionConnection.also {
                    it.subscribe(mediaId, subscriptionCallback)
                    it.playbackState.observeForever(playbackStateObserver)
                    it.nowPlaying.observeForever(mediaMetadataObserver)
                }
                mediaSessionConnection.rootMediaId
            } else {
                null
            }
        }

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.nowPlaying.value ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaList.postValue(updateState(playbackState, metadata))
        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaList.postValue(updateState(playbackState, metadata))
        }
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ): List<SongItem> {

        val newResId = when (playbackState.isPlaying) {
            true -> R.drawable.ic_pause_circle_filled_black_24dp
            else -> R.drawable.ic_play_circle_filled_black_24dp
        }

        return mediaList.value?.map {
            val useResId = if (it.id == mediaMetadata.id) newResId else NO_RES
            it.copy(playbackRes = useResId)
        } ?: emptyList()
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val list = children.map { child ->
                SongItem(
                    id = child.description.mediaId ?: "empty",
                    title = child.description.title.toString(),
                    displayName = "",
                    mediaUri = child.description.mediaUri ?: Uri.parse(""),
                    duration = child.description.extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) ?: 0L,
                    albumId = "",
                    artist = child.description.subtitle.toString(),
                    artistId = "",
                    albumArtPath = child.description.iconUri?.path
                )
            }
            println("FeedViewModel=$mediaId onLoadedChildren parentId=$parentId size=${list.size}")
            _mediaList.postValue(list)
        }

    }

    fun mediaItemClicked(clickedItem: SongItem) {
        playMedia(clickedItem, pauseAllowed = false)
    }

    fun playMedia(mediaItem: SongItem, pauseAllowed: Boolean = true) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && mediaItem.id == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) transportControls.pause() else Unit
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=${mediaItem.id})"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaItem.id, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        mediaSessionConnection.unsubscribe("/", subscriptionCallback)
    }


}

const val NO_RES = 0
private const val TAG = "MainViewModel"
