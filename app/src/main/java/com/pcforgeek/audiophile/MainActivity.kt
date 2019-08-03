package com.pcforgeek.audiophile

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcforgeek.audiophile.db.MediaItem
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.home.MediaFeedAdapter
import com.pcforgeek.audiophile.util.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MediaFeedAdapter.OnClick {

    //private val mediaManager = MediaManager(this)
    private lateinit var mediaFeedAdapter: MediaFeedAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!PermissionUtils.isPermissionGranted(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_READ_PERMISSION
            )
        }
        App.component.inject(this)
        volumeControlStream = AudioManager.STREAM_MUSIC

        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.mediaList.observe(this, Observer { list ->
            if (PermissionUtils.isPermissionGranted(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                )
            ) {
                mediaFeedAdapter.addData(list)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_READ_PERMISSION
                )
            }
        })
        viewModel.rootMediaId.observe(this, Observer { rootId ->
            println("rootId - $rootId")
        })
    }

    private fun setupRecyclerView() {
        mediaFeedAdapter = MediaFeedAdapter(mutableListOf(), this)
        songFeed.also {
            it.adapter = mediaFeedAdapter
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun mediaItemClicked(mediaItem: MediaItem) {
        viewModel.mediaItemClicked(mediaItem)
    }

//    private lateinit var currentState: PlaybackStateCompat
//    private fun updatePlaybackState(state: PlaybackStateCompat) {
//        currentState = state
//        if (state.isPlayEnabled || state.isPrepared) {
//            playPauseButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
//        } else if (state.isPlaying) {
//            playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
//        }
//
//    }

//    private fun updateMetadata(metadata: MediaMetadataCompat) {
//        currentMediaTitle.text = metadata.title
//        currentMediaArtist.text = metadata.artist
//        //currentMediaThumbnail.setImageBitmap(BitmapFactory.decodeFile(metadata.artUri))
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == EXTERNAL_STORAGE_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //mediaFeedAdapter.addData(viewModel.mediaList.value!!)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val EXTERNAL_STORAGE_READ_PERMISSION = 1
    }
}
