package dev.joeljacob.audiophile.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["songId", "artistId"],
    foreignKeys = [ForeignKey(
        entity = Song::class,
        parentColumns = ["id"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("songId")]
)
data class ArtistSongItem(
    @ColumnInfo(name = "songId")
    val songId: String,
    @ColumnInfo(name = "artistId")
    val artistId: String,
    val title: String
)