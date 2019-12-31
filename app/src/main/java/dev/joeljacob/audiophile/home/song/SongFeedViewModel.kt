package dev.joeljacob.audiophile.home.song

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.data.StorageMediaSource
import dev.joeljacob.audiophile.data.model.SongItem
import dev.joeljacob.audiophile.service.EMPTY_PLAYBACK_STATE
import dev.joeljacob.audiophile.service.MediaSessionConnection
import dev.joeljacob.audiophile.service.NOTHING_PLAYING
import dev.joeljacob.audiophile.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SongFeedViewModel @Inject constructor(
    private val mediaSessionConnection: MediaSessionConnection,
    private val storage: StorageMediaSource
) : ViewModel() {

    // TODO usage?
    private val _mediaList = MutableLiveData<List<SongItem>>()
    val mediaList: LiveData<List<SongItem>> = _mediaList

    private var typeId: String = Type.EMPTY
    private var type: String = Type.EMPTY
    fun setMediaIdAndType(typeId: String, type: String) {
        this.typeId = typeId
        this.type = type
        Timber.i("SongFeedViewModel mediaId=${this.typeId} type=${this.type} set")
    }

    suspend fun getSongs(): LiveData<List<SongItem>> =
        withContext(Dispatchers.IO) {
            return@withContext storage.getSongItemsForType(type, typeId).asLiveData()
        }

    val rootMediaId: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected) {
                mediaSessionConnection.also {
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

    fun mediaItemClicked(clickedItem: SongItem) {
        playMedia(clickedItem, pauseAllowed = false)
    }

    private fun playMedia(mediaItem: SongItem, pauseAllowed: Boolean = true) {
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
                        Timber.i(
                            "Playable item clicked but neither play nor pause are enabled! (mediaId=${mediaItem.id})"
                        )
                    }
                }
            }
        } else {
            val extras = Bundle()
            val type = findTypeFromMediaId()
            extras.putString(Type.AUDIOPHILE_TYPE, type)
            extras.putString(Type.AUDIOPHILE_TYPE_ID, typeId)
            transportControls.playFromMediaId(mediaItem.id, extras)
        }
    }

    private fun findTypeFromMediaId(): String {
        return when (type) {
            Type.ALL_MEDIA_ID -> Type.ALL_MEDIA_ID
            Type.ARTIST -> Type.ARTIST
            Type.ALBUM -> Type.ALBUM
            Type.PLAYLIST -> Type.PLAYLIST
            else -> {
                val split = typeId.split("/")
                if (split.size < 2)
                    return Type.EMPTY
                when {
                    split[0] == Type.ALBUM -> Type.ALBUM
                    split[0] == Type.ARTIST -> Type.ARTIST
                    split[0] == Type.PLAYLIST -> Type.PLAYLIST
                    else -> Type.EMPTY
                }
            }
        }
    }

    fun setSongToFavourite(songId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storage.setSongToFavourite(songId)
        }
    }

    fun deleteSong(songItem: SongItem) {
        viewModelScope.launch(Dispatchers.IO) {
            if (storage.delete(songItem)) {
                Timber.i("Song deleted ${songItem.title}")
            } else {
                Timber.i("Song=${songItem.title} at path=${songItem.mediaUri.path} not deleted ")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)
    }


}

const val NO_RES = 0
