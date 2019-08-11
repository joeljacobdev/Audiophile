package com.pcforgeek.audiophile.home

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.db.MediaItem
import com.pcforgeek.audiophile.service.EMPTY_PLAYBACK_STATE
import com.pcforgeek.audiophile.service.MediaSessionConnection
import com.pcforgeek.audiophile.service.NOTHING_PLAYING
import com.pcforgeek.audiophile.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeedViewModel @Inject constructor(private val mediaSessionConnection: MediaSessionConnection) : ViewModel() {

    private val _mediaList = MutableLiveData<List<MediaItem>>()
    val mediaList: LiveData<List<MediaItem>>
        get() = _mediaList

    private var mediaId: String = Constants.ROOT_MEDIA_ID
    fun setMediaId(value: String) {
        mediaId = value
    }


    val rootMediaId: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected) {
                mediaSessionConnection.also {
                    println("FeedViewModel=$mediaId subscribed")
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
    ): List<MediaItem> {

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
                MediaItem(
                    id = child.mediaId ?: "empty",
                    title = child.description.title.toString(),
                    displayTitle = child.description.subtitle.toString(),
                    mediaUri = child.description.mediaUri ?: Uri.parse(""),
                    duration = 66L,//TODO 6
                    albumId = 0L,
                    artistId = 0L,
                    albumArtUri = child.description.iconUri
                )
            }
            println("FeedViewModel=$mediaId onLoadedChildren parentId=$parentId size=${list.size}")
            _mediaList.postValue(list)
        }

    }

    suspend fun work(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        var mmr = MediaMetadataRetriever()
        val list = children.map { child ->
            var path: String
            if (child.description.mediaUri?.path != null) {
                mmr.setDataSource(child.description.mediaUri?.path)
                val data = mmr.embeddedPicture
                path = if (data != null) {
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    withContext(Dispatchers.IO) {
                        StorageUtils.createDirectoryAndSaveFile(
                            bitmap, child.description.extras?.getInt(
                                METADATA_KEY_ALBUM_ID
                            ).toString()
                        )
                    }
                } else {
                    ""
                }
            } else {
                path = ""
            }
            MediaItem(
                id = child.mediaId ?: "empty",
                title = child.description.title.toString(),
                displayTitle = child.description.subtitle.toString(),
                mediaUri = child.description.mediaUri
                    ?: Uri.parse(""),
                duration = 66L,//TODO 6
                albumId = 0L,
                artistId = 0L,
                albumArtUri = child.description.iconUri
            )
        }
        println("FeedViewModel=$mediaId onLoadedChildren parentId=$parentId size=${list.size}")
        _mediaList.postValue(list)
    }

    fun mediaItemClicked(clickedItem: MediaItem) {
        playMedia(clickedItem, pauseAllowed = false)
    }

    fun playMedia(mediaItem: MediaItem, pauseAllowed: Boolean = true) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false

        println("playMedia -- isPrepared=$isPrepared mediaId=${mediaItem.id} nowPlayingId=${nowPlaying?.id}")
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
