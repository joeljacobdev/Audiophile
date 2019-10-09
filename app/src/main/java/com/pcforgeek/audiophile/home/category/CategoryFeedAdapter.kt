package com.pcforgeek.audiophile.home.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.Category
import kotlinx.android.synthetic.main.grid_item_view.view.*

class CategoryFeedAdapter(
    private val categoryList: MutableList<Category>,
    private val listener: OnClick
) :
    RecyclerView.Adapter<CategoryFeedAdapter.GridItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)
        return GridItemHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: GridItemHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    fun addData(data: List<Category>) {
        categoryList.clear()
        categoryList.addAll(data)
        notifyDataSetChanged()
    }

    inner class GridItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.title
        fun bind(category: Category) {
            if (category is Category.Artist) {
                title.text = category.title
            } else if (category is Category.Album) {
                title.text = category.title
            }
            itemView.setOnClickListener { listener.categoryItemClicked(category, true) }
        }
    }

    interface OnClick {
        fun categoryItemClicked(category: Category, browsable: Boolean = false)
    }
}
