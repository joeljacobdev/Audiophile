package com.pcforgeek.audiophile.service

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.pcforgeek.audiophile.db.MediaItem
import com.pcforgeek.audiophile.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import android.content.ContentUris


// HOW is abstract media source after being abstract have constructor invokation
class StorageMediaSource(private val context: Context) : AbstractMusicSource() {
    private var mediaList: List<MediaMetadataCompat> = emptyList()
    private val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    private val mediaMetadataRetriever = MediaMetadataRetriever()

    init {
        // why this not accessible outside init
        state = STATE_INITIALIZING
    }

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.DATE_ADDED
    )

    private suspend fun getAllAudioFiles(): List<MediaCursorItem> {
        return withContext(Dispatchers.IO) {
            val songsList = mutableListOf<MediaCursorItem>()
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )
            cursor?.let {
                while (cursor.moveToNext()) {
                    val mediaItem = MediaCursorItem(
                        it.getLong(it.getColumnIndex(MediaStore.Audio.Media._ID)),
                        it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        it.getString(it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                        it.getLong(it.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        it.getString(it.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                        it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    )
                    songsList.add(mediaItem)
                }
                cursor.close()
            }
            songsList
        }
    }

    override suspend fun load() {
        val list = getAllAudioFiles()
        val catalog = mutableListOf<MediaItem>()
        list.forEach { item ->
            println("${item.path}")
            try {
                mediaMetadataRetriever.setDataSource(
                    item.path
                )
                val id = item.id.toString()
                val displayTitle: String = item.displayTitle
                val album: String = item.album
                val albumArtUri: Uri? = MusicUtils.getAlbumCoverUri(item.albumId.toInt())
                val mediaUri: Uri = Uri.parse(item.path)
                val duration: Long = item.duration
                val title: String = item.title
                val artist: String = item.artist
                val mediaItem = MediaItem(
                    id = id,
                    artistId = item.artistId,
                    albumId = item.albumId,
                    album = album,
                    albumArtUri = albumArtUri,
                    mediaUri = mediaUri,
                    duration = duration,
                    title = title,
                    artist = artist,
                    displayTitle = displayTitle
                )
                catalog.add(mediaItem)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mediaList = catalog.map { song ->
            MediaMetadataCompat.Builder()
                .from(song)
                .build()
        }.toList()
        state = if (list.isNotEmpty()) {
            STATE_INITIALIZED
        } else {
            STATE_ERROR
        }
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = mediaList.iterator()
}

fun MediaMetadataCompat.Builder.from(mediaItem: MediaItem): MediaMetadataCompat.Builder {
    val durationInMs = TimeUnit.SECONDS.toMillis(mediaItem.duration)
    duration = durationInMs
    id = mediaItem.id
    albumId = mediaItem.albumId
    artistId = mediaItem.artistId
    title = mediaItem.title
    mediaUri = mediaItem.mediaUri.path//TODO
    album = mediaItem.album
    albumArtUri = mediaItem.albumArtUri?.encodedPath
    displayTitle = mediaItem.displayTitle
    artist = mediaItem.artist
    return this
}