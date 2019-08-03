package com.pcforgeek.audiophile.model

class CategoryItem(
    @Type
    val type: Int
)

annotation class Type {
    companion object {
        val GENRES = 0
        val SUGGESTED = 1
        val ARTISTS = 2
        val ALBUMS = 3
        val SONGS = 4
        val PLAYLISTS = 5
        val FOLDERS = 6
    }
}
