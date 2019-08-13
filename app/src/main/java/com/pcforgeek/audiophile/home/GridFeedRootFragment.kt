package com.pcforgeek.audiophile.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pcforgeek.audiophile.R

class GridFeedRootFragment : Fragment() {

    private lateinit var mediaId: String

    fun setMediaId(id: String) {
        mediaId = id
    }

    companion object {
        fun newInstance(mediaId: String): GridFeedRootFragment {
            return GridFeedRootFragment().apply {
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

        childFragmentManager.beginTransaction().replace(R.id.gridFeedRootContainer, FeedGridFragment.newInstance(mediaId))
            .commit()
    }


}
