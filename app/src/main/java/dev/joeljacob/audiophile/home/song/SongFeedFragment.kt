package dev.joeljacob.audiophile.home.song


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.data.model.Song
import dev.joeljacob.audiophile.di.ViewModelFactory
import dev.joeljacob.audiophile.home.MainActivity
import dev.joeljacob.audiophile.home.MediaFeedAdapter
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject


private const val MEDIA_TYPE_ID_ARG = "media_id_arg"
private const val MEDIA_TYPE_ARG = "media_type_arg"

class SongFeedFragment : Fragment(), MediaFeedAdapter.OnClick {
    private lateinit var mediaTypeId: String
    private lateinit var mediaType: String
    private lateinit var mediaFeedAdapter: MediaFeedAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SongFeedViewModel by viewModels { viewModelFactory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    companion object {

        fun newInstance(typeId: String, type: String): SongFeedFragment {
            return SongFeedFragment().apply {
                val args = Bundle()
                args.putString(MEDIA_TYPE_ID_ARG, typeId)
                args.putString(MEDIA_TYPE_ARG, type)
                arguments = args
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.component.inject(this)
        mediaTypeId = arguments?.getString(MEDIA_TYPE_ID_ARG) ?: return
        mediaType = arguments?.getString(MEDIA_TYPE_ARG) ?: return
        viewModel.setMediaIdAndType(mediaTypeId, mediaType)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.getSongs().observe(viewLifecycleOwner, Observer { list ->
                mediaFeedAdapter.submitData(list)
            })
        }
        viewModel.rootMediaId.observe(viewLifecycleOwner, Observer { rootId ->
        })
    }

    private fun setupRecyclerView() {
        mediaFeedAdapter = MediaFeedAdapter(this)
        songFeed.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = mediaFeedAdapter
        }
    }

    override fun onDestroyView() {
        songFeed.adapter = null
        super.onDestroyView()
    }

    override fun mediaItemClicked(song: Song) {
            viewModel.mediaItemClicked(song)
    }

    override fun addSongToPlaylist(songId: String) {
        if (activity is MainActivity) {
            (activity as MainActivity).openAddToPlaylistFragment(songId)
        }
    }

    override fun deleteSong(song: Song) {
        viewModel.deleteSong(song)
    }

    override fun setSongToFavourite(songId: String) {
        viewModel.setSongToFavourite(songId)
    }
}
