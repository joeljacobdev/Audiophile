package com.pcforgeek.audiophile.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.SongItem
import kotlinx.android.synthetic.main.grid_item_view.view.*
import kotlinx.android.synthetic.main.media_feed_item_view.view.*

class MediaFeedAdapter(private val songList: MutableList<SongItem>, private val listener: OnClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var viewTypeGrid: Boolean = false

    fun isViewTypeGrid(viewTypeGrid: Boolean) {
        this.viewTypeGrid = viewTypeGrid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewTypeGrid) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)
            return GridItemHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.media_feed_item_view, parent, false)
            return MediaFeedItemHolder(view)
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
        fun bind(mediaItem: SongItem) {
            name.text = mediaItem.title
            artist.text = mediaItem.artist
            if (mediaItem.albumArtPath != null){
                Glide.with(itemView.context).load(mediaItem.albumArtPath).error(ColorDrawable(Color.DKGRAY)).into(thumbnail)
            } else {
                Glide.with(itemView.context).load(R.drawable.default_artwork).into(thumbnail)
            }
            itemView.setOnClickListener { listener.mediaItemClicked(mediaItem) }
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
    }
}


