package com.pcforgeek.audiophile.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.home.playlist.AddToPlaylistFragment
import com.pcforgeek.audiophile.home.song.SongFeedFragment
import com.pcforgeek.audiophile.service.NOTHING_PLAYING
import com.pcforgeek.audiophile.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.current_playing_container.*
import timber.log.Timber
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
        setupViewpager()
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        val metadata = viewModel.nowPlaying.value ?: NOTHING_PLAYING
        if (metadata.id == NOTHING_PLAYING.id)
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
            Timber.i(
                "nowPlaying = ${metadata.id} ${metadata.artist} ${metadata.album} ${metadata.title}"
            )
            if (metadata.id != NOTHING_PLAYING.id) {
                setupCurrentPlayingUI(metadata)
            } else if (viewModel.isPlaying.value == false) {
                currentPlayingContainer.makeGone()
            }
        })

        viewModel.currentPlaybackState.observe(this, Observer { })

        viewModel.isPlaying.observe(this, Observer {
            if (it && currentPlayingContainer.isVisible) {
                playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
            } else if (!it && currentPlayingContainer.isVisible) {
                playPauseButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
            }
        })
    }

    fun openAddToPlaylistFragment(songId: String) {
        println("MainActivity - open AddToPlaylistFragment")
        tabs.makeGone()
        mainFragmentContainer.makeVisible()
        supportFragmentManager.beginTransaction()
            .add(R.id.mainFragmentContainer, AddToPlaylistFragment.newInstance(songId))
            .addToBackStack(null)
            .commit()
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
                .load(R.drawable.default_artwork)
                .into(currentMediaThumbnail)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) is AddToPlaylistFragment) {
            supportFragmentManager.popBackStackImmediate()
            mainFragmentContainer.makeGone()
            tabs.makeVisible()
        } else if (tabs.isVisible) {
            val fragment = viewpager.adapter?.instantiateItem(viewpager, viewpager.currentItem)
            if (fragment is GridFeedRootFragment) {
                val frag =
                    fragment.childFragmentManager.findFragmentById(R.id.gridFeedRootContainer)
                if (frag is SongFeedFragment)
                    fragment.childFragmentManager.popBackStackImmediate()
                else
                    super.onBackPressed()
            } else {
                super.onBackPressed()
            }
        } else
            super.onBackPressed()
    }
}
