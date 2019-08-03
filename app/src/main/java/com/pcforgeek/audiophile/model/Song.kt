package com.pcforgeek.audiophile.model

data class Song(
    private val songID: Long,
    private val path: String,
    private val title:String,
    private val artistId: Long,
    private val artist: String,
    private val albumID: Long,
    private val album: String,
    private val duration: Long,
    private val year: Int,
    private val track: Int,
    private val dataAdded: Long,
    private val isPodcast: Boolean,
    private val bookmark: Long
)