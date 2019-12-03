package com.pcforgeek.audiophile.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

@Entity(primaryKeys = ["playlistId", "songId"],
    foreignKeys = [ForeignKey(
        entity = Category.Playlist::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    ), ForeignKey(
        entity = SongItem::class,
        parentColumns = ["id"],
        childColumns = ["songId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("playlistId"), Index("songId")])
data class PlaylistItem(

    @ColumnInfo(name = "playlistId")
    val playlistId: Int,

    @ColumnInfo(name = "songId")
    val songId: String
)