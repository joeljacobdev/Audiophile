package dev.joeljacob.audiophile.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BlacklistPath(
    @PrimaryKey
    val path: String
)