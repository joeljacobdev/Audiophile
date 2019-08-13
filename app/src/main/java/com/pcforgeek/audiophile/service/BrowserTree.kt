package com.pcforgeek.audiophile.service

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.pcforgeek.audiophile.util.*
import com.pcforgeek.audiophile.util.Constants.ALBUM_MEDIA_ID
import com.pcforgeek.audiophile.util.Constants.ALL_MEDIA_ID
import com.pcforgeek.audiophile.util.Constants.ARTIST_MEDIA_ID
import com.pcforgeek.audiophile.util.Constants.ROOT_MEDIA_ID

class BrowserTree(context: Context, musicSource: MusicSource) {
    private val mediaIdToChildrenMapper = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        val rootlist = mediaIdToChildrenMapper[ROOT_MEDIA_ID] ?: mutableListOf()

        val folderMediaMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.FOLDER_MEDIA_ID
            title = "Folder"
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val allMediaMetadata = MediaMetadataCompat.Builder().apply {
            id = ALL_MEDIA_ID
            title = "All"
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val albumMediaMetadata = MediaMetadataCompat.Builder().apply {
            id = ALBUM_MEDIA_ID
            title = "Album"
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val artistMediaMetadata = MediaMetadataCompat.Builder().apply {
            id = ARTIST_MEDIA_ID
            title = "Artist"
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        rootlist += folderMediaMetadata
        rootlist += allMediaMetadata
        rootlist += albumMediaMetadata
        rootlist += artistMediaMetadata
        mediaIdToChildrenMapper[ROOT_MEDIA_ID] = rootlist
        musicSource.forEach { item ->
            val albumId = item.album ?: UNKNOWN_ALBUM_ID
            val albumChildren = mediaIdToChildrenMapper[albumId] ?: buildAlbumRoot(item)
            albumChildren += item
            mediaIdToChildrenMapper[albumId] = albumChildren

            val artistId = item.artist ?: UNKNOWN_ARTIST_ID
            val artistChildren = mediaIdToChildrenMapper[artistId] ?: buildArtistRoot(item)
            artistChildren += item
            mediaIdToChildrenMapper[artistId] = artistChildren

            val allChildren = mediaIdToChildrenMapper[ALL_MEDIA_ID] ?: mutableListOf()
            allChildren += item
            mediaIdToChildrenMapper[ALL_MEDIA_ID] = allChildren
        }

        println("mediaIdToChildrenMapper[ALL_MEDIA_ID].size=${mediaIdToChildrenMapper[ALL_MEDIA_ID]?.size}")
        println("mediaIdToChildrenMapper[ALBUM_MEDIA_ID].size=${mediaIdToChildrenMapper[ALBUM_MEDIA_ID]?.size}")
        println("mediaIdToChildrenMapper[ARTIST_MEDIA_ID].size=${mediaIdToChildrenMapper[ARTIST_MEDIA_ID]?.size}")
    }

    private fun buildAlbumRoot(mediaItem: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.album ?: UNKNOWN_ALBUM_ID
            title = mediaItem.album ?: UNKNOWN
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()
        val rootList = mediaIdToChildrenMapper[ALBUM_MEDIA_ID] ?: mutableListOf()
        rootList += albumMetadata
        mediaIdToChildrenMapper[ALBUM_MEDIA_ID] = rootList
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildrenMapper[mediaItem.album ?: UNKNOWN_ALBUM_ID] = it
        }
    }

    private fun buildArtistRoot(mediaItem: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val artistMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.artist ?: UNKNOWN_ARTIST_ID
            title = mediaItem.artist ?: UNKNOWN
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val rootList = mediaIdToChildrenMapper[ARTIST_MEDIA_ID] ?: mutableListOf()
        rootList += artistMetadata
        mediaIdToChildrenMapper[ARTIST_MEDIA_ID] = rootList

        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildrenMapper[mediaItem.artist ?: UNKNOWN_ARTIST_ID] = it
        }
    }

    operator fun get(mediaId: String) = mediaIdToChildrenMapper[mediaId]
}

const val UNKNOWN = "<unknown>"
const val UNKNOWN_ARTIST_ID = "<unknown_artist>"
const val UNKNOWN_ALBUM_ID = "<unknown_album>"