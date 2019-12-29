package dev.joeljacob.audiophile.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.home.category.CategoryFeedGridFragment
import dev.joeljacob.audiophile.home.playlist.PlaylistFragment
import dev.joeljacob.audiophile.util.Type

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
