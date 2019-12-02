package com.pcforgeek.audiophile.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.SongItem
import kotlinx.android.synthetic.main.grid_item_view.view.*
import kotlinx.android.synthetic.main.media_feed_item_view.view.*

class MediaFeedAdapter(private val songList: MutableList<SongItem>, private val listener: OnClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var viewTypeGrid: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewTypeGrid) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)
            GridItemHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.media_feed_item_view, parent, false)
            MediaFeedItemHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MediaFeedItemHolder)
            holder.bind(songList[position])
        else if (holder is GridItemHolder)
            holder.bind(songList[position])
    }

    fun addData(data: List<SongItem>) {
        songList.clear()
        songList.addAll(data)
        notifyDataSetChanged()
    }

    inner class MediaFeedItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.name
        private val artist = itemView.artist
        private val thumbnail = itemView.thumbnail
        private val overflowOptions = itemView.overflowOption
        fun bind(songItem: SongItem) {
            name.text = songItem.title
            artist.text = songItem.artist
            if (songItem.albumArtPath != null) {
                Glide.with(itemView.context).load(songItem.albumArtPath)
                    .error(ColorDrawable(Color.DKGRAY)).into(thumbnail)
            } else {
                Glide.with(itemView.context).load(R.drawable.default_artwork).into(thumbnail)
            }
            overflowOptions.setOnClickListener {
                val popupMenu = PopupMenu(it.context, overflowOptions)
                popupMenu.inflate(R.menu.song_overflow_options)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delete_menu -> {
                            listener.deleteSong(songItem)
                            true
                        }
                        R.id.add_to_playlist -> {
                            listener.addSongToPlaylist(songItem.id)
                            true
                        }
                        R.id.add_song_to_favourite -> {
                            listener.setSongToFavourite(songItem.id)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            itemView.setOnClickListener { listener.mediaItemClicked(songItem) }
        }
    }

    inner class GridItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.title
        fun bind(mediaItem: SongItem) {
            title.text = mediaItem.title
            itemView.setOnClickListener { listener.mediaItemClicked(mediaItem, true) }
        }
    }

    interface OnClick {
        fun mediaItemClicked(mediaItem: SongItem, browsable: Boolean = false)
        fun addSongToPlaylist(songId: String)
        fun deleteSong(songItem: SongItem)
        fun setSongToFavourite(songId: String)
    }
}


