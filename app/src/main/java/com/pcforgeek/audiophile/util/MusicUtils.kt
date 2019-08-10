package com.pcforgeek.audiophile.util

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

object MusicUtils {

    fun getAlbumCoverUri(albumId: Int): Uri {
        val sArtworkUri = Uri.parse("content://media/external/audio/albumart")

        return ContentUris.withAppendedId(sArtworkUri, albumId.toLong())
    }

    fun getSongUri(songId: Int): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId.toLong())
    }
}