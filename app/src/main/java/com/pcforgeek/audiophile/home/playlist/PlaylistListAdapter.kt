package com.pcforgeek.audiophile.home.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.Category
import kotlinx.android.synthetic.main.playlist_holder.view.*

class PlaylistListAdapter(
    private val playlists: MutableList<Category.Playlist>,
    private val listener: PlaylistHolderClickListener
) : RecyclerView.Adapter<PlaylistListAdapter.PlaylistHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_holder, parent, false)
        return PlaylistHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(playlists[position])
    }


    inner class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.playlistName
        fun bind(playlist: Category.Playlist) {
            name.text = playlist.title
            itemView.setOnClickListener { listener.onPlaylistClick(playlist.id) }
        }

    }

    interface PlaylistHolderClickListener {
        fun onPlaylistClick(playlistId: Int)
    }
}