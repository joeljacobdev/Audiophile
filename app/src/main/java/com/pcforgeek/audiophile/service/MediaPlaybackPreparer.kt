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
import com.pcforgeek.audiophile.util.album
import com.pcforgeek.audiophile.util.id
import com.pcforgeek.audiophile.util.toMediaSource


/**
 * Connect the bridge between exoplayer and mediasession (MediaSessionCompat.Callback)
 */
class MediaPlaybackPreparer(
    private val musicSource: MusicSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DefaultDataSourceFactory
) : MediaSessionConnector.PlaybackPreparer {

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        musicSource.whenReady {
            val metadataList = musicSource.search(query ?: "", extras ?: Bundle.EMPTY)
            if (metadataList.isNotEmpty()) {
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

    override fun getSupportedPrepareActions(): Long = PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
            PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
            PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        musicSource.whenReady {
            val item: MediaMetadataCompat? = musicSource.find { item ->
                item.id == mediaId
            }

            if (item == null) {
                //
            } else {
                val metadataList = buildPlaylist(item)
                val mediaSource = metadataList.toMediaSource(dataSourceFactory)

                val indexOfItem = metadataList.indexOf(item)
                exoPlayer.prepare(mediaSource)
                // what is the UI where this happen
                exoPlayer.seekTo(indexOfItem, 0)
            }
        }
    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit

    override fun onPrepare() = Unit


    // create playlist by album
    private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
        musicSource.filter { item.album == it.album }
}