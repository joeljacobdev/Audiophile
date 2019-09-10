package com.pcforgeek.audiophile.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.service.NOTHING_PLAYING
import com.pcforgeek.audiophile.util.PermissionUtils
import com.pcforgeek.audiophile.util.id
import com.pcforgeek.audiophile.util.makeGone
import com.pcforgeek.audiophile.util.makeVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.current_playing_container.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private val tabsAdapter: TabsAdapter by lazy {
        TabsAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.component.inject(this)
        volumeControlStream = AudioManager.STREAM_MUSIC
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
        } else {
            setupViewpager()
        }

        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        val metadata = viewModel.nowPlaying.value ?: NOTHING_PLAYING
        // TODO How is it possible that metadata is not null but metadata.id is null??
        if (metadata.id == null)
            currentPlayingContainer.makeGone()
        else
            setupCurrentPlayingUI(metadata)
        super.onResume()
    }

    private fun setupViewpager() {
        progress.makeGone()
        tabs.makeVisible()
        viewpager.makeVisible()
        viewpager.adapter = tabsAdapter
        viewpager.offscreenPageLimit = 2
        tabs.setupWithViewPager(viewpager)
    }

    private fun setupListeners() {
        playPauseButton.setOnClickListener {
            viewModel.playOrPause()
        }
    }

    private fun setupObservers() {
        viewModel.nowPlaying.observe(this, Observer { metadata ->
            if (metadata.id != null) {
                println("current play = ${metadata.id}")
                setupCurrentPlayingUI(metadata)
            } else {
                currentPlayingContainer.makeGone()
            }
        })

        viewModel.isPlaying.observe(this, Observer {
            if (it && currentPlayingContainer.isVisible) {
                playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
            } else if (!it && currentPlayingContainer.isVisible) {
                playPauseButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
            }
        })

        viewModel.currentPlaybackState.observe(this, Observer {
            // even thought we don;t do anything here, need to observe, otherwise Transformations code does not run
        })
    }

    private fun setupCurrentPlayingUI(metadata: MediaMetadataCompat) {
        currentPlayingContainer.makeVisible()
        currentMediaTitle.text =
            metadata.bundle.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "<unknown>"
        currentMediaArtist.text =
            metadata.bundle.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                ?: "<unknown>"
        if (metadata.description.iconUri?.path != null) {
            Glide.with(currentPlayingContainer.context)
                .load(metadata.description.iconUri?.path)
                .error(ColorDrawable(Color.DKGRAY))
                .into(currentMediaThumbnail)
        } else {
            Glide.with(currentPlayingContainer.context)
                .load(ColorDrawable(Color.DKGRAY))
                .into(currentMediaThumbnail)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == EXTERNAL_STORAGE_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                setupViewpager()
            else
                finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        val fragment = viewpager.adapter?.instantiateItem(viewpager, viewpager.currentItem)
        if (fragment is GridFeedRootFragment) {
            val frag = fragment.childFragmentManager.findFragmentById(R.id.gridFeedRootContainer)
            if (frag is FeedFragment)
                fragment.childFragmentManager.popBackStackImmediate()
            else
                super.onBackPressed()
        } else
            super.onBackPressed()
    }

    companion object {
        const val EXTERNAL_STORAGE_READ_PERMISSION = 1
    }
}
