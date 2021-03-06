package dev.joeljacob.audiophile.data

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.IntDef
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.data.model.Song
import dev.joeljacob.audiophile.util.*
import kotlinx.coroutines.flow.Flow

interface MusicSource : Iterable<Song> {

    suspend fun load()

    fun whenReady(prepare: (Boolean) -> Unit): Boolean
    fun search(term: String, extras: Bundle): List<Song>
    suspend fun incrementPlayCount(id: String, duration: Long, current: Long)
    suspend fun incrementPlayCount(id: String)
    suspend fun getCategoryForParentId(parentId: String): Flow<List<Category>>
    suspend fun getSongItemsForType(type: String, typeId: String): Flow<List<Song>>
    suspend fun onBlacklistUpdated()
    suspend fun delete(song: Song): Boolean
}


@IntDef(
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class State

/**
 * State indicating the source was created, but no initialization has performed.
 */
const val STATE_CREATED = 1

/**
 * State indicating initialization of the source is in progress.
 */
const val STATE_INITIALIZING = 2

/**
 * State indicating the source has been initialized and is ready to be used.
 */
const val STATE_INITIALIZED = 3

/**
 * State indicating an error has occurred.
 */
const val STATE_ERROR = 4

abstract class AbstractMusicSource : MusicSource {
    @State
    var state: Int = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()
    override fun whenReady(prepare: (Boolean) -> Unit): Boolean =
        when (state) {
            STATE_CREATED, STATE_INITIALIZING -> {
                onReadyListeners += prepare
                false
            }
            else -> {
                prepare(state != STATE_ERROR)
                true
            }
        }


    override fun search(term: String, extras: Bundle): List<Song> {
        // First attempt to search with the "focus" that's provided in the extras.
        val focusSearchResult = when (extras[MediaStore.EXTRA_MEDIA_FOCUS]) {
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                // For a Genre focused search, only genre is set.
                val genre = extras[EXTRA_MEDIA_GENRE]
                Log.d(TAG, "Focused genre search: '$genre'")
                filter { song ->
                    song.genre == genre
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                // For an Artist focused search, only the artist is set.
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                Log.d(TAG, "Focused artist search: '$artist'")
                filter { song ->
                    (song.artist == artist)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                // For an Album focused search, album and artist are set.
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                val album = extras[MediaStore.EXTRA_MEDIA_ALBUM]
                Log.d(TAG, "Focused album search: album='$album' artist='$artist")
                filter { song ->
                    song.artist == artist && song.album == album
                }
            }
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                // For a Song (aka Media) focused search, title, album, and artist are set.
                val title = extras[MediaStore.EXTRA_MEDIA_TITLE]
                val album = extras[MediaStore.EXTRA_MEDIA_ALBUM]
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                Log.d(TAG, "Focused media search: title='$title' album='$album' artist='$artist")
                filter { song ->
                    song.artist == artist && song.album == album
                            && song.title == title
                }
            }
            else -> {
                // There isn't a focus, so no results yet.
                emptyList()
            }
        }

        // If there weren't any results from the focused search (or if there wasn't a focus
        // to begin with), try to find any matches given the 'query' provided, searching against
        // a few of the fields.
        // In this sample, we're just checking a few fields with the provided query, but in a
        // more complex app, more logic could be used to find fuzzy matches, etc...
        if (focusSearchResult.isEmpty()) {
            return if (term.isNotBlank()) {
                Log.d(TAG, "Unfocused search for '$term'")
                filter { song ->
                    song.title.containsCaseInsensitive(term)
                            || song.genre.containsCaseInsensitive(term)
                }
            } else {
                // If the user asked to "play music", or something similar, the query will also
                // be blank. Given the small catalog of songs in the sample, just return them
                // all, shuffled, as something to play.
                Log.d(TAG, "Unfocused search without keyword")
                return shuffled()
            }
        } else {
            return focusSearchResult
        }
    }

    /**
     * [MediaStore.EXTRA_MEDIA_GENRE] is missing on API 19. Hide this fact by using our
     * own version of it.
     */
    private val EXTRA_MEDIA_GENRE
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaStore.EXTRA_MEDIA_GENRE
        } else {
            "android.intent.extra.genre"
        }
}

private const val TAG = "MusicSource"


