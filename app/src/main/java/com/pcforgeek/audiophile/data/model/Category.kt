package com.pcforgeek.audiophile.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

sealed class Category {
    @Entity
    data class Artist(
        @PrimaryKey
        @ColumnInfo(name = "artistId")
        val id: String,
        val title: String
    ): Category()

    @Entity
    data class Album(
        @PrimaryKey
        @ColumnInfo(name = "albumId")
        val id: String,
        val title: String
    ): Category()

    @Entity
    data class Playlist(
        val title: String
    ): Category() {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "playlistId")
        var id: Int = 0
    }
}