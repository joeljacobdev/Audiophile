package com.pcforgeek.audiophile.home.category


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.Category
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.home.song.SongFeedFragment
import com.pcforgeek.audiophile.util.Type
import kotlinx.android.synthetic.main.fragment_feed_grid.*
import javax.inject.Inject

class CategoryFeedGridFragment : Fragment(),
    CategoryFeedAdapter.OnClick {

    private lateinit var mediaId: String
    private lateinit var categoryFeedAdapter: CategoryFeedAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CategoryFeedViewModel by viewModels { viewModelFactory }

    fun setMediaId(id: String) {
        mediaId = id
    }

    companion object {
        fun newInstance(mediaId: String): CategoryFeedGridFragment {
            return CategoryFeedGridFragment().apply {
                setMediaId(mediaId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.component.inject(this)
        viewModel.setCategoryId(mediaId)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.categoryItemLiveData.observe(this, Observer { list ->
            categoryFeedAdapter.addData(list)
        })
    }

    private fun setupRecyclerView() {
        categoryFeedAdapter = CategoryFeedAdapter(mutableListOf(), this)
        gridRecyclerView.also {
            it.layoutManager = GridLayoutManager(context, 2)
            it.adapter = categoryFeedAdapter
        }
    }

    override fun categoryItemClicked(category: Category, browsable: Boolean) {
        var type = Type.EMPTY
        // we are not handling / reaching playlist from CategoryGridFeedFragment, so don't handle Category.Playlist case
        val id = when (category) {
            is Category.Album -> {
                type = Type.ALBUM
                category.id
            }
            is Category.Artist -> {
                type = Type.ARTIST
                category.id
            }
            else -> Type.EMPTY
        }
        fragmentManager?.let {
            it.beginTransaction()
                .replace(R.id.gridFeedRootContainer, SongFeedFragment.newInstance(id, type))
                .addToBackStack(null)
                .commit()
        }
    }

}
