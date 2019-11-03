package com.pcforgeek.audiophile.home.song


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.SongItem
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.home.MainActivity
import com.pcforgeek.audiophile.home.MediaFeedAdapter
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject

class SongFeedFragment : Fragment(), MediaFeedAdapter.OnClick {
    private lateinit var mediaId: String

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


    fun setMediaId(id: String) {
        mediaId = id
    }

    companion object {

        fun newInstance(mediaId: String): SongFeedFragment {
            return SongFeedFragment().apply {
                setMediaId(mediaId)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.component.inject(this)
        viewModel.setMediaId(mediaId)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.mediaList.observe(this, Observer { list ->
            mediaFeedAdapter.addData(list)
        })
        viewModel.rootMediaId.observe(this, Observer { rootId ->
        })
    }

    private fun setupRecyclerView() {
        mediaFeedAdapter = MediaFeedAdapter(mutableListOf(), this)
        songFeed.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = mediaFeedAdapter
        }
    }

    override fun mediaItemClicked(mediaItem: SongItem, browsable: Boolean) {
        if (browsable) {

        } else
            viewModel.mediaItemClicked(mediaItem)
    }

    override fun addSongToPlaylist(songId: String) {
        // TODO open AddToPlaylist
        println("SongFeedFragment clicked - open AddToPlaylistFragment")
        if (activity is MainActivity) {
            (activity as MainActivity).openAddToPlaylistFragment(songId)
        }
    }
}
