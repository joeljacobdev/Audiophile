package com.pcforgeek.audiophile.data.model

import androidx.room.*

sealed class Category {
    @Entity
    data class Artist(
        @ForeignKey(
            entity = SongItem::class,
            parentColumns = ["artistId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
        @ColumnInfo(name = "id")
        @PrimaryKey
        val id: String,
        val title: String
    ) : Category()

    @Entity
    data class Album(
        @ForeignKey(
            entity = SongItem::class,
            parentColumns = ["albumId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
        @ColumnInfo(name = "id")
        @PrimaryKey
        val id: String,
        val title: String
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