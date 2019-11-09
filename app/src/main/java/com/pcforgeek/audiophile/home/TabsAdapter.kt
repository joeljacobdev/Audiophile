package com.pcforgeek.audiophile.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.pcforgeek.audiophile.home.playlist.PlaylistFragment
import com.pcforgeek.audiophile.home.song.SongFeedFragment
import com.pcforgeek.audiophile.util.Type

class TabsAdapter(private val fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {
    private val list = listOf(
        Type.ALL_MEDIA_ID,
        Type.ALBUM_MEDIA_ID,
        Type.ARTIST_MEDIA_ID,
        Type.PLAYLIST_MEDIA_ID
    )

    override fun getItem(position: Int): Fragment {
        return when (list[position]) {
            Type.ALL_MEDIA_ID ->
                SongFeedFragment.newInstance(list[position])
            Type.ALBUM_MEDIA_ID ->
                GridFeedRootFragment.newInstance(list[position])
            Type.ARTIST_MEDIA_ID ->
                GridFeedRootFragment.newInstance(list[position])
            Type.PLAYLIST_MEDIA_ID ->
                PlaylistFragment.newInstance()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (list[position]) {
            Type.ALBUM_MEDIA_ID -> "Album"
            Type.ALL_MEDIA_ID -> "All"
            Type.FOLDER_MEDIA_ID -> "Folder"
            Type.ARTIST_MEDIA_ID -> "Artist"
            Type.PLAYLIST_MEDIA_ID -> "Playlist"
            else -> "None"
        }
    }
}