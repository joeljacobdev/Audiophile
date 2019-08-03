package com.pcforgeek.audiophile.service

import android.net.Uri

data class MediaCursorItem(
    var id: Int,
    var artist: String = "",
    var title: String,
    var path: String, // DATA
    var displayTitle: String = "", // DISPLAY_NAME
    var duration: Long,
    var album: String = "",
    var songCoverUri: Uri
)
