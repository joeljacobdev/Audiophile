package com.pcforgeek.audiophile.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    val title: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}