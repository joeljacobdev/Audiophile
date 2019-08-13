package com.pcforgeek.audiophile.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pcforgeek.audiophile.home.NO_RES

@Entity
data class MediaItem(
    @PrimaryKey
    val id: String,
    val artistId: Long,
    val albumId: Long,
    val displayTitle: String = "",
    val album: String = "",
    val duration: Long,
    val albumArtUri: Uri?,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val mediaUri: Uri,
    val userRating: Long = 0L,
    val playbackRes: Int = NO_RES
)