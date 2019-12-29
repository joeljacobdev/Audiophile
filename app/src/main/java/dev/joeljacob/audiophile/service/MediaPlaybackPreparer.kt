package dev.joeljacob.audiophile.service

import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dev.joeljacob.audiophile.data.MusicSource
import dev.joeljacob.audiophile.data.model.SongItem
import dev.joeljacob.audiophile.util.*
import dev.joeljacob.audiophile.util.Type.AUDIOPHILE_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Connect the bridge between exoplayer and mediasession (MediaSessionCompat.Callback)
 */
class MediaPlaybackPreparer(
    private val scope: CoroutineScope,
    private val musicSource: MusicSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DefaultDataSourceFactory,
    private val listener: OnPlaylistListener,
    private val audioFocusHelper: AudioFocusHelper
) : MediaSessionConnector.PlaybackPreparer {

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        musicSource.whenReady {
            val songList = musicSource.search(query ?: "", extras ?: Bundle.EMPTY)
            if (songList.isNotEmpty()) {
                val metadataList = songList.map { song ->
                    MediaMetadataCompat.Builder().from(song).build()
                }
                listener.onPlaylistCreated(metadataList)
                val mediaSource = metadataList.toMediaSource(dataSourceFactory)
                exoPlayer.prepare(mediaSource)
                // ?? no seek to here
            }
        }
    }

    override fun onCommand(
        player: Player?,
        controlDispatcher: ControlDispatcher?,
        command: String?,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

    @ExperimentalCoroutinesApi
    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        if (mediaId == null) return
        musicSource.whenReady {
            scope.launch {
                val type = extras?.getString(AUDIOPHILE_TYPE) ?: return@launch
                val metadataList = buildPlaylist(type, mediaId)
                val songMetadata = metadataList.find { item ->
                    item.id == mediaId
                } ?: return@launch

                Timber.i("onPlayFromMediaID=${mediaId} size=${metadataList.size}")
                val mediaSource = metadataList.toMediaSource(dataSourceFactory)
                val indexOfItem = metadataList.indexOf(songMetadata)
                listener.onPlaylistCreated(metadataList)
                when (audioFocusHelper.requestAudioFocus()) {
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                        exoPlayer.prepare(mediaSource)
                        // what is the UI where this happen
                        exoPlayer.seekTo(indexOfItem, 0)
                    }
                    AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                    }
                    AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                    }
                    // TODO what to do on failure and delay?
                }
            }
        }
    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit

    override fun onPrepare() = Unit

    // create playlist by based on type
    @ExperimentalCoroutinesApi
    private suspend fun buildPlaylist(type: String, mediaId: String): List<MediaMetadataCompat> {
        var ans: List<MediaMetadataCompat> = emptyList()
        musicSource.getSongItemsForType(type, mediaId)
            .take(1)
            .map { songs ->
                songs.map { song: SongItem ->
                    MediaMetadataCompat.Builder().from(song).build()
                }
            }.collect {
                ans = it
            }
        return ans
    }

    interface OnPlaylistListener {
        fun onPlaylistCreated(list: List<MediaMetadataCompat>)
    }
}

fun MediaMetadataCompat.Builder.from(mediaItem: SongItem): MediaMetadataCompat.Builder {
    id = mediaItem.id
    albumId = mediaItem.albumId.toLong()
    artistId = mediaItem.artistId.toLong()
    title = mediaItem.title
    displayTitle = mediaItem.displayName
    duration = mediaItem.duration
    album = mediaItem.album
    artist = mediaItem.artist
    albumArtUri = mediaItem.albumArtPath
    mediaUri = mediaItem.mediaUri.path
    return this
}