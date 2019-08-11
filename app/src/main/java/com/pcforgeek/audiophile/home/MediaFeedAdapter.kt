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
import com.pcforgeek.audiophile.db.MediaItem
import kotlinx.android.synthetic.main.media_item_collapsed.view.*
import android.media.MediaMetadataRetriever

class MediaFeedAdapter(private val songList: MutableList<MediaItem>, private val listener: OnClick) :
    RecyclerView.Adapter<MediaFeedAdapter.MediaItemCollapsedHolder>() {

    var mmr = MediaMetadataRetriever()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemCollapsedHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_item_collapsed, parent, false)
        return MediaItemCollapsedHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MediaItemCollapsedHolder, position: Int) {
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

    interface OnClick {
        fun mediaItemClicked(mediaItem: MediaItem)
    }
}


