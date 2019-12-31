package dev.joeljacob.audiophile.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.data.model.Song
import kotlinx.android.synthetic.main.grid_item_view.view.*
import kotlinx.android.synthetic.main.media_feed_item_view.view.*

class MediaFeedAdapter(private val listener: OnClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var viewTypeGrid: Boolean = false

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

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
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MediaFeedItemHolder)
            holder.bind(differ.currentList[position])
        else if (holder is GridItemHolder)
            holder.bind(differ.currentList[position])
    }

    fun submitData(data: List<Song>) {
        differ.submitList(data)
    }

    inner class MediaFeedItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.name
        private val artist = itemView.artist
        private val thumbnail = itemView.thumbnail
        private val overflowOptions = itemView.overflowOption
        fun bind(song: Song) {
            name.text = song.title
            artist.text = song.artist
            if (song.albumArtPath != null) {
                Glide.with(itemView.context).load(song.albumArtPath)
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
                            listener.deleteSong(song)
                            true
                        }
                        R.id.add_to_playlist -> {
                            listener.addSongToPlaylist(song.id)
                            true
                        }
                        R.id.add_song_to_favourite -> {
                            listener.setSongToFavourite(song.id)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            itemView.setOnClickListener { listener.mediaItemClicked(song) }
        }
    }

    inner class GridItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.title
        fun bind(song: Song) {
            title.text = song.title
            itemView.setOnClickListener { listener.mediaItemClicked(song, true) }
        }
    }

    interface OnClick {
        fun mediaItemClicked(song: Song, browsable: Boolean = false)
        fun addSongToPlaylist(songId: String)
        fun deleteSong(song: Song)
        fun setSongToFavourite(songId: String)
    }
}

