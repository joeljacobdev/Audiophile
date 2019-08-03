package com.pcforgeek.audiophile.db

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pcforgeek.audiophile.NO_RES

@Entity
data class MediaItem(
    @PrimaryKey
    val id: String,
    val displayTitle: String = "",
    val album: String = "",
    val displayIconUri: Uri? = null,
    val duration: Long,
    val artUri: String = "",//path use util function
    val albumArtUri: Uri?,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val mediaUri: Uri,
    val userRating: Long = 0L,
    val playbackRes: Int = NO_RES
)