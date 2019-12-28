package com.pcforgeek.audiophile.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(primaryKeys = ["songId", "albumId"],
    foreignKeys = [ForeignKey(
        entity = SongItem::class,
        parentColumns = ["id"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("songId")])
data class AlbumSongItem(
    @ColumnInfo(name = "songId")
    val songId: String,
    @ColumnInfo(name = "albumId")
    val albumId: String,
    val title: String
)