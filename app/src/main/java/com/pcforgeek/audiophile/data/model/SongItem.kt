package com.pcforgeek.audiophile.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.pcforgeek.audiophile.home.song.NO_RES

@Entity(primaryKeys = ["songId", "artistId", "albumId"])
data class SongItem(
    @ColumnInfo(name = "songId")
    val id: String,
    @ForeignKey(
        entity = Category.Artist::class,
        parentColumns = ["artistId"],
        childColumns = ["artistId"]
    )
    @ColumnInfo(name = "artistId")
    val artistId: String,
    @ForeignKey(
        entity = Category.Artist::class,
        parentColumns = ["albumId"],
        childColumns = ["albumId"]
    )
    @ColumnInfo(name = "albumId")
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