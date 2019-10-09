package com.pcforgeek.audiophile.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.pcforgeek.audiophile.home.playlist.PlaylistFragment
import com.pcforgeek.audiophile.home.song.SongFeedFragment
import com.pcforgeek.audiophile.util.Constants

class TabsAdapter(private val fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val list = listOf(Constants.ALL_MEDIA_ID, Constants.ALBUM_MEDIA_ID, Constants.ARTIST_MEDIA_ID, Constants.PLAYLIST_MEDIA_ID)
    override fun getItem(position: Int): Fragment {
        return when (list[position]) {
            Constants.ALL_MEDIA_ID ->
                SongFeedFragment.newInstance(list[position])
            Constants.ALBUM_MEDIA_ID ->
                GridFeedRootFragment.newInstance(list[position])
            Constants.ARTIST_MEDIA_ID ->
                GridFeedRootFragment.newInstance(list[position])
            Constants.PLAYLIST_MEDIA_ID ->
                PlaylistFragment.newInstance()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (list[position]) {
            Constants.ALBUM_MEDIA_ID -> "Album"
            Constants.ALL_MEDIA_ID -> "All"
            Constants.FOLDER_MEDIA_ID -> "Folder"
            Constants.ARTIST_MEDIA_ID -> "Artist"
            Constants.PLAYLIST_MEDIA_ID -> "Playlist"
            else -> "None"
        }
    }
}