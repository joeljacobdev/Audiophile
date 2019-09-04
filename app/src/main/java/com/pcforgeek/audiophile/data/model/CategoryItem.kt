package com.pcforgeek.audiophile.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryItem(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: Int = 0
)

object Type {
    const val Song = 0
    const val Album = 1
    const val Artist = 2
}