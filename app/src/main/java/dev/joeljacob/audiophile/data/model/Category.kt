package dev.joeljacob.audiophile.data.model

import androidx.room.*

sealed class Category {
    data class Artist(
        @ColumnInfo(name = "artistId")
        val artistId: String,
        @ColumnInfo(name = "artist")
        val artist: String
    ) : Category()

    data class Album(
        @ColumnInfo(name = "albumId")
        val albumId: String,
        @ColumnInfo(name = "album")
        val album: String
    ) : Category()

    @Entity(
        tableName = "Playlist",
        indices = [Index(value = ["id"], unique = true)]
    )
    data class Playlist(
        val title: String
    ) : Category() {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0
    }
}