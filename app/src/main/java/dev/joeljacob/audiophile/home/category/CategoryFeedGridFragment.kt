package dev.joeljacob.audiophile.home.category


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.data.model.Category
import dev.joeljacob.audiophile.di.ViewModelFactory
import dev.joeljacob.audiophile.home.song.SongFeedFragment
import dev.joeljacob.audiophile.util.Type
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

    override fun onDestroyView() {
        gridRecyclerView.adapter = null
        super.onDestroyView()
    }

    private fun setupObservers() {
        viewModel.categoryItemLiveData.observe(viewLifecycleOwner, Observer { list ->
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

    override fun categoryItemClicked(category: Category) {
        var type = Type.EMPTY
        // we are not handling / reaching playlist from CategoryGridFeedFragment, so don't handle Category.Playlist case
        val typeId = when (category) {
            is Category.Album -> {
                type = Type.ALBUM
                category.albumId
            }
            is Category.Artist -> {
                type = Type.ARTIST
                category.artistId
            }
            else -> Type.EMPTY
        }
        fragmentManager?.let {
            it.beginTransaction()
                .replace(R.id.gridFeedRootContainer, SongFeedFragment.newInstance(typeId, type))
                .addToBackStack(null)
                .commit()
        }
    }

}
