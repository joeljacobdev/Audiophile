package dev.joeljacob.audiophile.home.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import dev.joeljacob.audiophile.R
import kotlinx.android.synthetic.main.playlist_holder.view.*
import timber.log.Timber

class PlaylistAdapter(
    private val suggestions: MutableList<String>,
    context: Context
) : ArrayAdapter<String>(context, R.layout.playlist_holder, suggestions) {

    private val playlists = mutableListOf<String>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView
            ?: LayoutInflater.from(parent.context).inflate(R.layout.playlist_holder, parent, false)
        view.playlistName.text = getItem(position)
        return view
    }

    override fun getCount(): Int {
        return suggestions.size
    }

    override fun getItem(position: Int): String? {
        return suggestions[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return if (constraint != null) {
                    suggestions.clear()
                    playlists.forEach { item ->
                        if (item.contains(constraint)) {
                            suggestions.add(item)
                        }
                    }
                    val filterResult = FilterResults()
                    filterResult.values = suggestions
                    filterResult.count = suggestions.size
                    filterResult
                } else {
                    FilterResults()
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (constraint != null && suggestions.isNotEmpty()) {
                    Timber.i("Search term = $constraint suggestions = ${suggestions.size}/${playlists.size}")
                    notifyDataSetChanged()
                }

            }

        }
    }

    fun convertSuggestionToPlaylistPosition(position: Int): Int {
        playlists.forEachIndexed { index, s ->
            if (s.contentEquals(suggestions[position]))
                return index
        }
        return -1
    }

    fun setData(list: List<String>) {
        playlists.clear()
        playlists.addAll(list)
    }
}