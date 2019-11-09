package com.pcforgeek.audiophile.home.option

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.BlacklistPath
import kotlinx.android.synthetic.main.blacklist_path_holder.view.*

class BlacklistPathAdapter(private val listener: BlacklistPathOnClickListener) : RecyclerView.Adapter<BlacklistPathAdapter.BlacklistPathHolder>() {

    private val blacklistPaths = mutableListOf<BlacklistPath>()

    override fun getItemCount(): Int {
        return blacklistPaths.size
    }

    override fun onBindViewHolder(holder: BlacklistPathHolder, position: Int) {
        holder.bind(blacklistPaths[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlacklistPathHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blacklist_path_holder, parent, false)
        return BlacklistPathHolder(view)
    }

    fun setData(list: List<BlacklistPath>) {
        blacklistPaths.clear()
        blacklistPaths.addAll(list)
        notifyDataSetChanged()
    }

    inner class BlacklistPathHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pathText: TextView = itemView.pathTextView
        private val removePathButton: ImageButton = itemView.removePathButton
        fun bind(blacklistPath: BlacklistPath) {
            pathText.text = blacklistPath.path
            removePathButton.setOnClickListener { listener.removePath(blacklistPath) }
        }
    }

    interface BlacklistPathOnClickListener {
        fun removePath(blacklistPath: BlacklistPath)
    }
}