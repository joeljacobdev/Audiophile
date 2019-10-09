package com.pcforgeek.audiophile.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pcforgeek.audiophile.home.song.NO_RES

@Entity
data class SongItem(
    @PrimaryKey
    val id: String,
    val artistId: String,
    val albumId: String,
    val title: String = "",
    val displayName: String = "",
    val album: String = "",
    val artist: String = "",
    val duration: Long,
    val albumArtPath: String?,
    val genre: String = "",
    val mediaUri: Uri,
    val favourite: Boolean = false,
    val playCount: Int = 0,
    val userRating: Long = 0L,
    val playbackRes: Int = NO_RES
)