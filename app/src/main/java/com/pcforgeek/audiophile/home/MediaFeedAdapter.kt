package com.pcforgeek.audiophile.home

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.MediaItem
import kotlinx.android.synthetic.main.media_item_collapsed.view.*
import android.media.MediaMetadataRetriever
import kotlinx.android.synthetic.main.grid_item_view.view.*

class MediaFeedAdapter(private val songList: MutableList<MediaItem>, private val listener: OnClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mmr = MediaMetadataRetriever()
    private var viewTypeGrid: Boolean = false

    fun isViewTypeGrid(viewTypeGrid: Boolean) {
        this.viewTypeGrid = viewTypeGrid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewTypeGrid) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)
            return MediaTypeHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.media_item_collapsed, parent, false)
            return MediaItemCollapsedHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MediaItemCollapsedHolder)
            holder.bind(songList[position])
        else if (holder is MediaTypeHolder)
            holder.bind(songList[position])
    }

    fun addData(data: List<MediaItem>) {
        songList.clear()
        songList.addAll(data)
        notifyDataSetChanged()
    }

    inner class MediaItemCollapsedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mediaName = itemView.mediaName
        private val mediaArtist = itemView.mediaArtist
        private val mediaArt = itemView.mediaArtThumbnail
        fun bind(mediaItem: MediaItem) {
            mediaName.text = mediaItem.title
            mediaArtist.text = mediaItem.artist
            if (mediaItem.albumArtUri == null)
                mediaArt.background = ColorDrawable(Color.DKGRAY)
            else {
                try {
                    mmr.setDataSource(mediaItem.mediaUri.path)
                    val data = mmr.embeddedPicture
                    if (data == null) {
                        mediaArt.background = ColorDrawable(Color.DKGRAY)
                    } else {
                        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                        Glide.with(itemView.context).load(bitmap).thumbnail(0.1f).into(mediaArt)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            itemView.setOnClickListener { listener.mediaItemClicked(mediaItem) }
        }
    }

    inner class MediaTypeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.title
        fun bind(mediaItem: MediaItem) {
            itemView.background = ColorDrawable(Color.MAGENTA)
            title.text = mediaItem.title
            itemView.setOnClickListener { listener.mediaItemClicked(mediaItem, true) }
        }
    }

    interface OnClick {
        fun mediaItemClicked(mediaItem: MediaItem, browsable: Boolean = false)
    }
}


