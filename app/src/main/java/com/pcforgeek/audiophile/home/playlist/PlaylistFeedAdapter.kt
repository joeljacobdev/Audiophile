package com.pcforgeek.audiophile.home.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.Category
import kotlinx.android.synthetic.main.playlist_feed_item.view.*

class PlaylistFeedAdapter(private val playList: MutableList<Category.Playlist>, private val listener: OnClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_feed_item, parent, false)
            return PlaylistHolder(view)
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlaylistHolder)
            holder.bind(playList[position])
    }

    fun addData(data: List<Category.Playlist>) {
        playList.clear()
        playList.addAll(data)
        notifyDataSetChanged()
    }

    inner class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail = itemView.thumbnail
        private val name = itemView.name
        fun bind(playlist: Category.Playlist) {
            itemView.setOnClickListener { listener.playlistClicked(playlist, browsable = true) }
            name.text = playlist.title
            Glide.with(itemView.context).load(R.drawable.ic_playlist).into(thumbnail)
        }
    }

    interface OnClick {
        fun playlistClicked(playlist: Category.Playlist, browsable: Boolean = false)
    }
}
