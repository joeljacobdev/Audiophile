package com.pcforgeek.audiophile.home


import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.BuildConfig
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.MediaItem
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.util.PermissionUtils
import kotlinx.android.synthetic.main.fragment_feed_grid.*
import javax.inject.Inject

class FeedGridFragment : Fragment(), MediaFeedAdapter.OnClick {

    private lateinit var mediaId: String
    private lateinit var mediaFeedAdapter: MediaFeedAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FeedViewModel by viewModels { viewModelFactory }

    fun setMediaId(id: String) {
        mediaId = id
    }

    companion object {
        fun newInstance(mediaId: String): FeedGridFragment {

            return FeedGridFragment().apply {
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
        viewModel.setMediaId(mediaId)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.mediaList.observe(this, Observer { list ->
            if (PermissionUtils.isPermissionGranted(
                    activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                )
            ) {
                mediaFeedAdapter.addData(list)
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.EXTERNAL_STORAGE_READ_PERMISSION
                )
            }
        })
        viewModel.rootMediaId.observe(this, Observer { rootId ->
            println("rootId - $rootId")
        })
    }

    private fun setupRecyclerView() {
        mediaFeedAdapter = MediaFeedAdapter(mutableListOf(), this)
        mediaFeedAdapter.isViewTypeGrid(true)
        gridRecyclerView.also {
            it.layoutManager = GridLayoutManager(context, 2)
            it.adapter = mediaFeedAdapter
        }
    }

    override fun mediaItemClicked(mediaItem: MediaItem, browsable: Boolean) {
        if (browsable) {
            if (BuildConfig.DEBUG)
                println("Browsed to mediaItemID=${mediaItem.id} title=${mediaItem.title}")
            fragmentManager?.let {
                it.beginTransaction().replace(R.id.gridFeedRootContainer, FeedFragment.newInstance(mediaItem.id)).addToBackStack(null)
                    .commit()
            }
        } else
            viewModel.mediaItemClicked(mediaItem)
    }

}
