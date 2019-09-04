package com.pcforgeek.audiophile.util

import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DataSource

@MediaBrowserCompat.MediaItem.Flags
inline val MediaMetadataCompat.flag
    get() = this.getLong(METADATA_KEY_AUDIOPHILE_FLAGS).toInt()

inline val MediaMetadataCompat.type
    get() = this.getLong(METADATA_KEY_TYPE).toInt()

inline val MediaMetadataCompat.albumId
    get() = this.getLong(METADATA_KEY_ALBUM_ID).toInt()


inline val MediaMetadataCompat.artistId
    get() = this.getLong(METADATA_KEY_ARTIST_ID).toInt()

inline val MediaMetadataCompat.id: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.title: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.displayTitle: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadataCompat.displayIconUri: Uri
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI).toUri()

inline val MediaMetadataCompat.duration: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

// artwork of song, scaled down version
inline val MediaMetadataCompat.art: Bitmap
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ART)

// artwork of song's album, scaled down version
inline val MediaMetadataCompat.albumArt: Bitmap
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)

// artwork of song, full version
inline val MediaMetadataCompat.artUri: Uri?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ART_URI).toUri()

// artwork of song's album, full version
inline val MediaMetadataCompat.albumArtUri: Uri
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI).toUri()

inline val MediaMetadataCompat.artist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.album: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

inline val MediaMetadataCompat.genre: String
    get() = getString(MediaMetadataCompat.METADATA_KEY_GENRE)

inline val MediaMetadataCompat.userRating
    get() = getLong(MediaMetadataCompat.METADATA_KEY_USER_RATING)

inline val MediaMetadataCompat.mediaUri: Uri?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri()

/**
 * GETTERS
 */

inline var MediaMetadataCompat.Builder.id: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, value)
    }

inline var MediaMetadataCompat.Builder.artistId: Long
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putLong(METADATA_KEY_ARTIST_ID, value)
    }

inline var MediaMetadataCompat.Builder.albumId: Long
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putLong(METADATA_KEY_ALBUM_ID, value)
    }


inline var MediaMetadataCompat.Builder.title: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)
    }


inline var MediaMetadataCompat.Builder.displayTitle: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, value)
    }

inline var MediaMetadataCompat.Builder.displayIconUri: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, value)
    }

inline var MediaMetadataCompat.Builder.duration: Long
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, value)
    }

// artwork of song, scaled down version
inline var MediaMetadataCompat.Builder.art: Bitmap
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putBitmap(MediaMetadataCompat.METADATA_KEY_ART, value)
    }

// artwork of song's album, scaled down version
inline var MediaMetadataCompat.Builder.albumArt: Bitmap
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, value)
    }

// artwork of song, full version
inline var MediaMetadataCompat.Builder.artUri: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ART_URI, value)
    }

// artwork of song's album, full version
inline var MediaMetadataCompat.Builder.albumArtUri: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, value)
    }

inline var MediaMetadataCompat.Builder.artist: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }

inline var MediaMetadataCompat.Builder.album: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, value)
    }

inline var MediaMetadataCompat.Builder.genre: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_GENRE, value)
    }

inline var MediaMetadataCompat.Builder.userRating: Long
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_USER_RATING, value)
    }

inline var MediaMetadataCompat.Builder.mediaUri: String?
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, value)
    }

@MediaBrowserCompat.MediaItem.Flags
inline var MediaMetadataCompat.Builder.flag: Int
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putLong(METADATA_KEY_AUDIOPHILE_FLAGS, value.toLong())
    }

inline var MediaMetadataCompat.Builder.type: Int
    get() = throw IllegalAccessException("Cannot access data")
    set(value) {
        putLong(METADATA_KEY_TYPE, value.toLong())
    }

fun MediaMetadataCompat.toMediaSource(dataSourceFactory: DataSource.Factory): ExtractorMediaSource =
    ExtractorMediaSource.Factory(dataSourceFactory)
        .createMediaSource(mediaUri)

fun List<MediaMetadataCompat>.toMediaSource(
    dataSourceFactory: DataSource.Factory
): ConcatenatingMediaSource {

    val concatenatingMediaSource = ConcatenatingMediaSource()
    forEach {
        concatenatingMediaSource.addMediaSource(it.toMediaSource(dataSourceFactory))
    }
    return concatenatingMediaSource
}


const val METADATA_KEY_AUDIOPHILE_FLAGS =
    "com.pcforgeek.audiophile.media.METADATA_KEY_AUDIOPHILE_FLAGS"
const val METADATA_KEY_ARTIST_ID = "com.pcforgeek.audiophile.media.METADATA_KEY_ARTIST_ID"
const val METADATA_KEY_ALBUM_ID = "com.pcforgeek.audiophile.media.METADATA_KEY_ALBUM_ID"
const val METADATA_KEY_TYPE = "com.pcforgeek.audiophile.media.METADATA_KEY_TYPE"
const val METADATA_KEY_PLAY_COUNT = "com.pcforgeek.audiophile.media.METADATA_KEY_PLAY_COUNT"
const val METADATA_KEY_FAVOURITE = "com.pcforgeek.audiophile.media.METADATA_KEY_FAVOURITE"
