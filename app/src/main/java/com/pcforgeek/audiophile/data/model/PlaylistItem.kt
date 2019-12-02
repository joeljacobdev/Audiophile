package com.pcforgeek.audiophile.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistItem(
    @ForeignKey(
        entity = Category.Playlist::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    @ColumnInfo(name = "playlistId")
    val playlistId: Int,

    @ForeignKey(
        entity = SongItem::class,
        parentColumns = ["id"],
        childColumns = ["songId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    @ColumnInfo(name = "songId")
    val songId: String
)