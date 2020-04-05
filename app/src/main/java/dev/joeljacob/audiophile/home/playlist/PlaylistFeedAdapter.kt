package dev.joeljacob.audiophile.home.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.di.GlideApp
import dev.joeljacob.audiophile.util.makeGone
import dev.joeljacob.audiophile.util.makeVisible
import kotlinx.android.synthetic.main.playlist_feed_item.view.*

class PlaylistFeedAdapter(
    private val playList: MutableList<Category.Playlist>,
    private val listener: OnClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_feed_item, parent, false)
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
        private val overflowOption = itemView.overflowOption
        fun bind(playlist: Category.Playlist) {
            itemView.setOnClickListener { listener.playlistClicked(playlist, browsable = true) }
            name.text = playlist.title
            GlideApp.with(itemView.context)
                .load(R.drawable.ic_playlist)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnail)
            if (playlist.id in 1..3) {
                overflowOption.makeGone()
            } else {
                overflowOption.makeVisible()
                overflowOption.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, overflowOption)
                    popupMenu.inflate(R.menu.playlist_overflow_options)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.delete_menu -> {
                                listener.deletePlaylist(playlist)
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }
            }

        }
    }

    interface OnClick {
        fun playlistClicked(playlist: Category.Playlist, browsable: Boolean = false)
        fun deletePlaylist(playlist: Category.Playlist)
    }
}
