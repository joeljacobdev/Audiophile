package dev.joeljacob.audiophile.home.playlist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.di.ViewModelFactory
import dev.joeljacob.audiophile.util.toast
import kotlinx.android.synthetic.main.dialog_create_playlist.*
import kotlinx.android.synthetic.main.fragment_add_to_playlist.*
import java.util.*
import javax.inject.Inject

class AddToPlaylistFragment : Fragment(R.layout.fragment_add_to_playlist) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by viewModels<PlaylistViewModel> { viewModelFactory }
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistList: List<Category.Playlist>
    private var songId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songId = arguments?.getString(ARG_SONG_ID, "") ?: return
        viewModel.getAllPlaylist(false)
        playlistAdapter = PlaylistAdapter(mutableListOf(), context!!)
        playlistSearch.setAdapter(playlistAdapter)
        playlistSearch.threshold = 1
        setupClickListener()
        setupObserver()
    }

    private fun setupClickListener() {
        createPlaylistButton.setOnClickListener {
            val dialog = Dialog(activity as Context)
            dialog.setContentView(R.layout.dialog_create_playlist)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.createPlaylistDialogButton.setOnClickListener {
                val name = dialog.createPlaylistDialogEditText.text.toString()
                if (!name.isNullOrBlank())
                    viewModel.createPlaylist(name)
                dialog.cancel()
            }
            dialog.show()

        }
        playlistSearch.setOnItemClickListener { _, _, position, _ ->
            // position given is position of 'suggestion' not of 'playlists'
            val index = playlistAdapter.convertSuggestionToPlaylistPosition(position)
            if (index == -1) {
                activity?.toast("Song Not Added")
            } else {
                viewModel.addSongToPlaylist(playlistList[index].id, songId)
                activity?.toast("Song added to ${playlistList[index].title}")
            }
        }
    }

    private fun setupObserver() {
        viewModel.playlist.observe(viewLifecycleOwner, Observer { playlists ->
            playlistList = playlists
            val list = playlists.map { it.title.toLowerCase(Locale.getDefault()) }
            playlistAdapter.setData(list)
        })
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
