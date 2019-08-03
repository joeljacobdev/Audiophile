package com.pcforgeek.audiophile.repo

import android.content.Context
import android.provider.MediaStore
import com.pcforgeek.audiophile.model.Query
import com.pcforgeek.audiophile.model.Song
import com.pcforgeek.audiophile.util.Result
import com.pcforgeek.audiophile.util.boolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SongsRepository @Inject constructor(private val context: Context) {
    private val projections = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.IS_PODCAST,
        MediaStore.Audio.Media.BOOKMARK
    )

    suspend fun getAllSongs(): Result<List<Song>> {

        var result: Result<List<Song>>? = null
        runCatching {
            val query = Query.Builder()
                .setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                .setProjection(projections)
                .setSelection(MediaStore.Audio.Media.IS_MUSIC + "=1 OR " + MediaStore.Audio.Media.IS_PODCAST + "=1")
                .build()

            val cursor = context.contentResolver.query(
                query.uri, query.projection, query.selection, query.args, query.sort
            )

            val list = mutableListOf<Song>()
            cursor?.let {

                val item = Song(
                    it.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                    it.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                    it.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                    it.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)),
                    it.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                    it.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                    it.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                    it.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                    it.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
                    it.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)),
                    it.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
                    it.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_PODCAST)).boolean,
                    it.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BOOKMARK))
                )
                list.add(item)
            }

            result = Result.Success(list)
            cursor?.close()
        }.onFailure {
            result = if (it is CancellationException) Result.Error("")
            else Result.Error(it.stackTrace.toString())
        }
        return result!!

    }

}