package dev.joeljacob.audiophile.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import dev.joeljacob.audiophile.home.song.NO_RES
import dev.joeljacob.audiophile.util.Constants

@Entity(
    tableName = "SongItem",
    primaryKeys = ["id"],
    indices = [Index(value = ["id"], unique = true)]
)
data class Song(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "artistId")
    val artistId: String,
    @ColumnInfo(name = "albumId")
    val albumId: String,
    val title: String,
    val displayName: String,
    val album: String,
    val artist: String,
    val duration: Long,
    val albumArtPath: String?,
    val genre: String,
    val mediaUri: Uri,
    val favourite: Boolean,
    val playCount: Int,
    val userRating: Long,
    val playbackRes: Int
) {
    @Ignore
    constructor(
        id: String,
        artistId: String,
        albumId: String,
        title: String? = null,
        displayName: String? = null,
        album: String? = null,
        artist: String? = null,
        duration: Long,
        albumArtPath: String? = null,
        genre: String? = null,
        mediaUri: Uri,
        favourite: Boolean = false,
        playCount: Int = 0,
        userRating: Long = 0L
    ) : this(
        id,
        artistId,
        albumId,
        title ?: Constants.UNKNOWN,
        displayName ?: Constants.UNKNOWN,
        album ?: Constants.UNKNOWN,
        artist ?: Constants.UNKNOWN,
        duration,
        albumArtPath,
        genre ?: Constants.UNKNOWN,
        mediaUri,
        favourite,
        playCount,
        userRating,
        NO_RES
    )
}