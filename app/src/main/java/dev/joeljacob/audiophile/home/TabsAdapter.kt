package dev.joeljacob.audiophile.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.joeljacob.audiophile.home.song.SongFeedFragment
import dev.joeljacob.audiophile.util.Type

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
                SongFeedFragment.newInstance(list[position], Type.ALL_MEDIA_ID)
            Type.ALBUM_MEDIA_ID ->
                ContainerRootFragment.newInstance(list[position])
            Type.ARTIST_MEDIA_ID ->
                ContainerRootFragment.newInstance(list[position])
            Type.PLAYLIST_MEDIA_ID ->
                ContainerRootFragment.newInstance(list[position])
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