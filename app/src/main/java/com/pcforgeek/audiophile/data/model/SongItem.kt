package com.pcforgeek.audiophile.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.pcforgeek.audiophile.home.song.NO_RES

@Entity(
    tableName = "SongItem",
    primaryKeys = ["id"],
    indices = [Index(value = ["id"], unique = true)]
)
data class SongItem(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "artistId")
    val artistId: String,
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