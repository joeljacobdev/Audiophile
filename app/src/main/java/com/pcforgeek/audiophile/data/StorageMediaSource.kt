package com.pcforgeek.audiophile.data

import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.data.model.Category
import com.pcforgeek.audiophile.data.model.SongItem
import com.pcforgeek.audiophile.db.CategoryDao
import com.pcforgeek.audiophile.db.PlaylistDao
import com.pcforgeek.audiophile.db.SongDao
import com.pcforgeek.audiophile.util.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


// HOW is abstract media source after being abstract have constructor invocation
class StorageMediaSource @Inject constructor(
    private val context: Context,
    private val songDao: SongDao,
    private val categoryDao: CategoryDao,
    private val playlistDao: PlaylistDao
) : AbstractMusicSource() {

    private var songList: List<SongItem> = emptyList()
    private var albumList: List<Category.Album> = emptyList()
    private var artistList: List<Category.Artist> = emptyList()
    private val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

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
                        MediaStore.Audio.Albums._ID + "=?",
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
                    categoryDao.insertAlbum(
                        Category.Album(
                            "${Type.ALBUM}/${mediaItem.albumId}",
                            mediaItem.album
                        )
                    )
                    categoryDao.insertArtist(
                        Category.Artist(
                            "${Type.ARTIST}/${mediaItem.artistId}",
                            mediaItem.artist
                        )
                    )
                    songsList.add(mediaItem)
                }
                cursor.close()

            }
            GlobalScope.launch {
                if (songsList.isNotEmpty()) {
                    songsList.forEach { item ->
                    }
                }
            }
            songDao.insertAllSongs(songsList)
            songsList
        }
    }

    override suspend fun load() {
        val list = getAllAudioFiles()
        songList = list
        state = if (list.isNotEmpty()) {
            STATE_INITIALIZED
        } else {
            STATE_ERROR
        }
    }

    override suspend fun incrementPlayCount(id: String, duration: Long, current: Long) {
        val percent = current / duration
        if (percent > 0.8)
            songDao.incrementPlayCount(id)
    }

    override suspend fun getCategoryForParenId(parentId: String): List<Category> {
        return when (parentId) {
            Type.ALBUM_MEDIA_ID -> getAllAlbums()
            Type.ARTIST_MEDIA_ID -> getAllArtist()
            Type.PLAYLIST_MEDIA_ID -> getAllPlaylist()
            else -> emptyList()
        }
    }

    override suspend fun getSongItemsForParentId(parentId: String): List<SongItem> {
        return when (parentId) {
            Type.ALL_MEDIA_ID -> getAllSongs()
            Type.EMPTY -> emptyList()
            else -> {
                val split = parentId.split("/")
                if (split.size < 2) return emptyList()
                when {
                    split[0] == Type.ALBUM -> getSongItemsForAlbumId(split[1])
                    split[0] == Type.ARTIST -> getSongItemsForArtistId(split[1])
                    split[0] == Type.PLAYLIST -> getSongItemsForPlaylistId(split[1].toInt()) // TODO use String
                    else -> emptyList()
                }
            }
        }
    }

    override suspend fun getSongItemsForType(type: String, id: String): List<SongItem> {
        return when (type) {
            Type.ALL_MEDIA_ID -> getAllSongs()
            Type.EMPTY -> emptyList()
            Type.ALBUM -> getSongItemsForAlbumId(id)
            Type.ARTIST -> getSongItemsForArtistId(id)
            Type.PLAYLIST -> getSongItemsForPlaylistId(id.toInt()) // TODO use String
            else -> emptyList()
        }
    }


    private suspend fun getAllSongs(): List<SongItem> {
        if (songList.isNotEmpty()) return songList
        withContext(Dispatchers.IO) {
            load()
        }
        return songList
    }

    private suspend fun getAllAlbums(): List<Category.Album> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllAlbums()
        }
    }

    private suspend fun getAllArtist(): List<Category.Artist> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllArtists()
        }
    }

    private suspend fun getAllPlaylist(): List<Category.Playlist> {
        return withContext(Dispatchers.IO) {
            playlistDao.getAllPlaylist()
        }
    }

    private suspend fun getSongItemsForAlbumId(albumId: String): List<SongItem> {
        return withContext(Dispatchers.IO) {
            songDao.getSongsForAlbumId(albumId)
        }
    }

    private suspend fun getSongItemsForArtistId(artistId: String): List<SongItem> {
        return withContext(Dispatchers.IO) {
            songDao.getSongsForArtistId(artistId)
        }
    }

    private suspend fun getSongItemsForPlaylistId(playlistId: Int): List<SongItem> {
        return withContext(Dispatchers.IO) {
            //val list = playlistDao.getAllSongsWithPlaylistId(playlistId) TODO??
            songDao.getSongsForArtistId(playlistId.toString())
        }
    }

    override fun iterator(): Iterator<SongItem> = songList.iterator()
}