package com.pcforgeek.audiophile.home.playlist


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.Category
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.home.song.SongFeedFragment
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistFragment : Fragment(R.layout.fragment_playlist),
    PlaylistFeedAdapter.OnClick {

    private lateinit var playlistFeedAdapter: PlaylistFeedAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: PlaylistViewModel by viewModels { viewModelFactory }


    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.component.inject(this)
        setupRecyclerView()
        setupObservers()
        viewModel.getAllPlaylist()
    }

    private fun setupObservers() {
        viewModel.playlist.observe(viewLifecycleOwner, Observer {
            playlistFeedAdapter.addData(it)
        })
    }

    private fun setupRecyclerView() {
        playlistFeedAdapter = PlaylistFeedAdapter(mutableListOf(), this)
        linearFeedRecyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = playlistFeedAdapter
        }
    }

    override fun playlistClicked(playlist: Category.Playlist, browsable: Boolean) {
        if (browsable) {
            val id = "playlist/${playlist.id}"
            fragmentManager?.let {
                it.beginTransaction().replace(
                    R.id.gridFeedRootContainer,
                    SongFeedFragment.newInstance(id)
                ).addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun deletePlaylist(playlist: Category.Playlist) {
        lifecycleScope.launch {
            viewModel.deletePlaylist(playlist)
        }
    }

}
