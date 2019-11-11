package com.pcforgeek.audiophile.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.home.category.CategoryFeedGridFragment
import com.pcforgeek.audiophile.home.playlist.PlaylistFragment
import com.pcforgeek.audiophile.util.Type

class ContainerRootFragment : Fragment() {

    private lateinit var mediaId: String

    fun setMediaId(id: String) {
        mediaId = id
    }

    companion object {
        fun newInstance(mediaId: String): ContainerRootFragment {
            return ContainerRootFragment().apply {
                setMediaId(mediaId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grid_feed_root, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mediaId == Type.PLAYLIST_MEDIA_ID)
            childFragmentManager.beginTransaction().replace(
                R.id.gridFeedRootContainer,
                PlaylistFragment.newInstance()
            )
                .commit()
        else
            childFragmentManager.beginTransaction().replace(
                R.id.gridFeedRootContainer,
                CategoryFeedGridFragment.newInstance(mediaId)
            )
                .commit()
    }


}
