package com.pcforgeek.audiophile.data


data class MediaCursorItem(
    var id: Long,
    var artist: String = "",
    var title: String,
    var path: String, // DATA
    var displayTitle: String = "", // DISPLAY_NAME
    var duration: Long,
    var album: String = "",
    var albumId: Long,
    var artistId: Long,
    var dateAdded: Int
)
