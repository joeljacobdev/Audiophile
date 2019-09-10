package com.pcforgeek.audiophile.data

import android.content.Context
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.data.model.CategoryItem
import com.pcforgeek.audiophile.data.model.SongItem
import com.pcforgeek.audiophile.data.model.Type
import com.pcforgeek.audiophile.db.CategoryDao
import com.pcforgeek.audiophile.db.SongDao
import com.pcforgeek.audiophile.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


// HOW is abstract media source after being abstract have constructor invocation
class StorageMediaSource(private val context: Context) : AbstractMusicSource() {
    private var mediaList: List<MediaMetadataCompat> = emptyList()
    private var albumList: List<MediaMetadataCompat> = emptyList()
    private var artistList: List<MediaMetadataCompat> = emptyList()
    private val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    @Inject
    lateinit var songDao: SongDao
    @Inject
    lateinit var categoryDao: CategoryDao

    init {
        App.component.inject(this)
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

    private suspend fun getAllAudioFiles(): List<SongItem> {
        return withContext(Dispatchers.IO) {
            val songsList = mutableListOf<SongItem>()
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )
            cursor?.let {
                while (cursor.moveToNext()) {

                    val cursor1 = context.contentResolver.query(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART),
                        MediaStore.Audio.Albums._ID+ "=?",
                        arrayOf(it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()),
                        null
                    )

                    val albumArtPath: String? = if (cursor1?.moveToNext() == true)
                        cursor1.getString(cursor1.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                    else
                        null

                    val mediaItem = SongItem(
                        id = it.getLong(it.getColumnIndex(MediaStore.Audio.Media._ID)).toString(),
                        artist = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        title = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        mediaUri = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA)).toUri(),
                        displayName = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                        duration = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        album = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        albumId = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString(),
                        artistId = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)).toString(),
                        albumArtPath = albumArtPath
                    )
                    categoryDao.insertCategory(
                        CategoryItem(
                            "album/${mediaItem.albumId}",
                            mediaItem.album,
                            Type.Album
                        )
                    )
                    categoryDao.insertCategory(
                        CategoryItem(
                            "artist/${mediaItem.artistId}",
                            mediaItem.artist,
                            Type.Artist
                        )
                    )
                    songsList.add(mediaItem)
                }
                cursor.close()

            }
            songDao.insertAllSongs(songsList)
            songsList
        }
    }

    override suspend fun load() {
        val list = getAllAudioFiles()
        mediaList = list.map { song ->
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

    override suspend fun getMediaMetadataForParenId(parentId: String): List<MediaMetadataCompat> {
        return when (parentId) {
            Constants.ALL_MEDIA_ID -> getAllSongs()
            Constants.ALBUM_MEDIA_ID -> getAllAlbums()
            Constants.ARTIST_MEDIA_ID -> getAllArtist()
            else -> {
                val split = parentId.split("/")
                if (split.size < 2) return emptyList()
                when {
                    split[0] == "album" -> getSongsForAlbumId(split[1])
                    split[0] == "artist" -> getSongsForArtistId(split[1])
                    else -> emptyList()
                }
            }
        }
    }

    private suspend fun getAllSongs(): List<MediaMetadataCompat> {
        if (mediaList.isNotEmpty()) return mediaList
        load()
        return mediaList
    }

    private suspend fun getAllAlbums(): List<MediaMetadataCompat> {
        return withContext(Dispatchers.IO) {
            val list = categoryDao.getAllAlbums()
            albumList = list.map { item ->
                MediaMetadataCompat.Builder()
                    .from(item)
                    .build()
            }
            albumList
        }
    }

    private suspend fun getAllArtist(): List<MediaMetadataCompat> {
        return withContext(Dispatchers.IO) {
            val list = categoryDao.getAllArtists()
            artistList = list.map { item ->
                MediaMetadataCompat.Builder()
                    .from(item)
                    .build()
            }
            artistList
        }
    }

    private suspend fun getSongsForAlbumId(albumId: String): List<MediaMetadataCompat> {
        return withContext(Dispatchers.IO) {
            val list = songDao.getSongsForAlbumId(albumId)
            println("album songs id=$albumId size=${list.size}")
            list.map { song ->
                MediaMetadataCompat.Builder()
                    .from(song)
                    .build()
            }.toList()
        }
    }

    private suspend fun getSongsForArtistId(artistId: String): List<MediaMetadataCompat> {
        return withContext(Dispatchers.IO) {
            val list = songDao.getSongsForArtistId(artistId)
            println("artist songs id=$artistId size=${list.size}")
            list.map { song ->
                MediaMetadataCompat.Builder()
                    .from(song)
                    .build()
            }.toList()
        }
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = mediaList.iterator()
}

fun MediaMetadataCompat.Builder.from(mediaItem: SongItem): MediaMetadataCompat.Builder {
    id = mediaItem.id
    albumId = mediaItem.albumId.toLong()
    artistId = mediaItem.artistId.toLong()
    title = mediaItem.title
    displayTitle = mediaItem.displayName
    duration = mediaItem.duration
    album = mediaItem.album
    artist = mediaItem.artist
    albumArtUri = mediaItem.albumArtPath
    mediaUri = mediaItem.mediaUri.path
    type = Type.Song
    return this
}

fun MediaMetadataCompat.Builder.from(mediaItem: CategoryItem): MediaMetadataCompat.Builder {
    id = mediaItem.id
    title = mediaItem.title
    type = mediaItem.type
    return this
}