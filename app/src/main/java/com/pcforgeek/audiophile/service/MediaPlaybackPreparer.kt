package com.pcforgeek.audiophile.service

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
import com.pcforgeek.audiophile.data.MusicSource
import com.pcforgeek.audiophile.data.model.SongItem
import com.pcforgeek.audiophile.util.*
import com.pcforgeek.audiophile.util.Type.AUDIOPHILE_TYPE
import kotlinx.coroutines.CoroutineScope
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
    private val listener: OnPlaylistListener
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
                exoPlayer.prepare(mediaSource)
                // what is the UI where this happen
                exoPlayer.seekTo(indexOfItem, 0)
                listener.onPlaylistCreated(metadataList)
            }
        }
    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit

    override fun onPrepare() = Unit

    // create playlist by based on type
    private suspend fun buildPlaylist(type: String, mediaId: String): List<MediaMetadataCompat> {
        val songItems = musicSource.getSongItemsForType(type, mediaId)
        return songItems.map { song ->
            MediaMetadataCompat.Builder().from(song).build()
        }
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