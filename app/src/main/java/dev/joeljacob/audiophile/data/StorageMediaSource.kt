package dev.joeljacob.audiophile.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.data.model.AlbumSongItem
import dev.joeljacob.audiophile.data.model.ArtistSongItem
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.data.model.Song
import dev.joeljacob.audiophile.db.BlacklistPathDao
import dev.joeljacob.audiophile.db.CategoryDao
import dev.joeljacob.audiophile.db.PlaylistDao
import dev.joeljacob.audiophile.db.SongDao
import dev.joeljacob.audiophile.util.Playlist
import dev.joeljacob.audiophile.util.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject


// HOW is abstract media source after being abstract have constructor invocation
class StorageMediaSource @Inject constructor(
    private val context: Context,
    private val songDao: SongDao,
    private val categoryDao: CategoryDao,
    private val playlistDao: PlaylistDao,
    private val blacklistPathDao: BlacklistPathDao
) : AbstractMusicSource() {

    private var songList: List<Song> = emptyList()
    private var isLoading = false
    private var selection: String? = null
    private lateinit var selectionArg: Array<String>

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

    private suspend fun getAllAudioFiles(): List<Song> {
        return withContext(Dispatchers.IO) {
            val songsList = mutableListOf<Song>()
            selection = createSelectionCondition()
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArg,
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

                    val mediaItem = Song(
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
                    songDao.insertSong(mediaItem)
                    categoryDao.insertAlbumSongItem(
                        AlbumSongItem(mediaItem.id, mediaItem.albumId, mediaItem.album)
                    )
                    categoryDao.insertArtistSongItem(
                        ArtistSongItem(mediaItem.id, mediaItem.artistId, mediaItem.artist)
                    )
                    songsList.add(mediaItem)
                }
                cursor.close()

            }
            songsList
        }
    }

    private suspend fun createSelectionCondition(): String? {
        val paths = blacklistPathDao.getAllBlacklistPath()
        var i = 0
        var selection: String? = ""
        while (i < paths.size) {
            selection = if (i == paths.size - 1)
                selection + MediaStore.Audio.Media.DATA + " not like ? "
            else
                selection + MediaStore.Audio.Media.DATA + " not like ? " + " and "
            i++
        }
        if (selection?.isEmpty() == true || selection?.isBlank() == true) selection = null
        selectionArg = paths.map { "%${it.path}%" }.toTypedArray()
        return selection
    }

    override suspend fun load() {
        if (isLoading) return
        isLoading = true
        val list = getAllAudioFiles()
        songList = list
        state = if (list.isNotEmpty()) {
            STATE_INITIALIZED
        } else {
            STATE_ERROR
        }
        isLoading = false
    }

    override suspend fun incrementPlayCount(id: String, duration: Long, current: Long) {
        val percent = current / duration
        if (percent > 0.8)
            withContext(Dispatchers.IO) {
                songDao.incrementPlayCount(id)
            }
    }

    override suspend fun incrementPlayCount(id: String) {
        withContext(Dispatchers.IO) {
            songDao.incrementPlayCount(id)
        }
    }

    suspend fun setSongToFavourite(songId: String) {
        withContext(Dispatchers.IO) {
            songDao.setSongToFavourite(songId)
        }
    }

    override suspend fun getCategoryForParentId(parentId: String): Flow<List<Category>> {
        return when (parentId) {
            Type.ALBUM_MEDIA_ID -> getAllAlbums()
            Type.ARTIST_MEDIA_ID -> getAllArtist()
            Type.PLAYLIST_MEDIA_ID -> getAllPlaylist()
            else -> flowOf(listOf())
        }
    }

    override suspend fun getSongItemsForType(type: String, typeId: String): Flow<List<Song>> {
        return when (type) {
            Type.ALL_MEDIA_ID -> getAllSongs()
            Type.ALBUM -> getSongItemsForAlbumId(typeId)
            Type.ARTIST -> getSongItemsForArtistId(typeId)
            Type.PLAYLIST -> getSongItemsForPlaylistId(typeId.toInt()) // TODO use String
            else -> flowOf(mutableListOf()) // Type.EMPTY
        }
    }

    override suspend fun onBlacklistUpdated() {
        if (isLoading) return
        isLoading = true
        selection = createSelectionCondition()
        val list = getAllAudioFiles()
        val ids = list.map { it.id }
        songDao.deleteRedundantItems(ids)
        songDao.insertAllSongs(list)
        isLoading = false
    }

    override suspend fun delete(song: Song): Boolean {
        return withContext(Dispatchers.IO) {
            if (deleteSong(song)) {
                songDao.deleteSong(song)
                true
            } else {
                false
            }
        }
    }

    private fun deleteSong(song: Song): Boolean {
        runCatching {
            val file = File(song.mediaUri.encodedPath ?: return false)
            file.delete()
            if (file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    context.deleteFile(file.name)
                }
            }
            if (file.exists()) {
                return false
            } else {
                val resolver = context.contentResolver
                val deleteUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    song.id.toLong()
                )
                resolver.delete(deleteUri, null, null)
            }
            return true
        }.onFailure {
            Timber.e(it)
            return false
        }
        return false
    }


    private suspend fun getAllSongs(): Flow<List<Song>> {
        return withContext(Dispatchers.IO) { songDao.getAllSongsFlow() }
    }

    private suspend fun getAllAlbums(): Flow<List<Category.Album>> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllAlbumsFlow()
        }
    }

    private suspend fun getAllArtist(): Flow<List<Category.Artist>> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllArtistsFlow()
        }
    }

    private suspend fun getAllPlaylist(): Flow<List<Category.Playlist>> =
        withContext(Dispatchers.IO) {
            return@withContext playlistDao.getAllPlaylistFlow()
        }


    private suspend fun getSongItemsForAlbumId(albumId: String): Flow<List<Song>> {
        return withContext(Dispatchers.IO) { songDao.getSongsForAlbumIdFlow(albumId) }
    }

    private suspend fun getSongItemsForArtistId(artistId: String): Flow<List<Song>> {
        return withContext(Dispatchers.IO) { songDao.getSongsForArtistIdFlow(artistId) }
    }

    private suspend fun getSongItemsForPlaylistId(playlistId: Int): Flow<List<Song>> {
        return withContext(Dispatchers.IO) {
            when (playlistId) {
                Playlist.NOT_ONCE_PLAYED_PLAYLIST -> playlistDao.getAllSongItemsNotPlayedOnceFlow()
                Playlist.FAVOURITES_PLAYLIST -> playlistDao.getAllSongItemsFavouritedFlow()
                Playlist.MOST_PLAYED_PLAYLIST -> playlistDao.getAllSongItemsMostPlayedFlow()
                else -> playlistDao.getAllSongItemsWithPlaylistIdFlow(playlistId)
            }
        }
    }

    // TODO remove all when cases
    override fun iterator(): Iterator<Song> = songList.iterator()
}