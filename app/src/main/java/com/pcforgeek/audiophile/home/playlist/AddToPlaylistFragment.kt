package com.pcforgeek.audiophile.home.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.di.ViewModelFactory
import javax.inject.Inject

class AddToPlaylistFragment : Fragment(), PlaylistListAdapter.PlaylistHolderClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by viewModels<PlaylistViewModel> { viewModelFactory }
    private lateinit var playlistListAdapter: PlaylistListAdapter
    private var songId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_to_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songId = arguments?.getString(ARG_SONG_ID, "") ?: return
        playlistListAdapter = PlaylistListAdapter(mutableListOf(), this)

    }

    override fun onPlaylistClick(playlistId: Int) {
        viewModel.addSongToPlaylist(playlistId, songId)
    }


    companion object {
        private const val ARG_SONG_ID = "song_id"
        @JvmStatic
        fun newInstance(songId: String) =
            AddToPlaylistFragment().apply {
                val arg = Bundle()
                arg.putString(ARG_SONG_ID, songId)
                arguments = arg
            }
    }
}
