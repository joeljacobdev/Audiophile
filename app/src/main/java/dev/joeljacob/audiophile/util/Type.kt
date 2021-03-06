package dev.joeljacob.audiophile.util


object Type {
    // group of type
    const val ALL_MEDIA_ID = "media:/all"
    const val ARTIST_MEDIA_ID = "media:/artist"
    const val FOLDER_MEDIA_ID = "media:/folder"
    const val ALBUM_MEDIA_ID = "media:/album"
    const val PLAYLIST_MEDIA_ID = "media:/playlist"

    // individual type
    const val ALBUM = "album"
    const val ARTIST = "artist"
    const val PLAYLIST = "playlist"

    const val EMPTY = ""

    const val AUDIOPHILE_TYPE_ID: String = "type_id"
    const val AUDIOPHILE_TYPE = "type"
}

object Playlist {
    // Default playlist
    const val MOST_PLAYED_PLAYLIST = 3
    const val NOT_ONCE_PLAYED_PLAYLIST = 1
    const val FAVOURITES_PLAYLIST = 2
}