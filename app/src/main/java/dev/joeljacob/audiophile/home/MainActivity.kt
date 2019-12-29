package dev.joeljacob.audiophile.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import dev.joeljacob.audiophile.App
import dev.joeljacob.audiophile.R
import dev.joeljacob.audiophile.di.ViewModelFactory
import dev.joeljacob.audiophile.home.option.SettingFragment
import dev.joeljacob.audiophile.home.playlist.AddToPlaylistFragment
import dev.joeljacob.audiophile.home.song.SongFeedFragment
import dev.joeljacob.audiophile.service.NOTHING_PLAYING
import dev.joeljacob.audiophile.util.*
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
        viewpager.offscreenPageLimit = 3
        tabs.setupWithViewPager(viewpager)
    }

    private fun setupListeners() {
        playPauseButton.setOnClickListener {
            viewModel.playOrPause()
        }

        overflowMenu.setOnClickListener {
            val popupMenu = PopupMenu(it.context, overflowMenu)
            popupMenu.inflate(R.menu.main_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.setting -> {
                        showSetting()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
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

    private fun showSetting() {
        tabs.makeGone()
        mainFragmentContainer.makeVisible()
        supportFragmentManager.beginTransaction()
            .add(R.id.mainFragmentContainer, SettingFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    fun openAddToPlaylistFragment(songId: String) {
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
        } else if (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) is SettingFragment) {
            val fragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer)
            if ((fragment as SettingFragment).isBlacklistUpdated) viewModel.onBlacklistUpdated()
            supportFragmentManager.popBackStackImmediate()
            tabs.makeVisible()
            mainFragmentContainer.makeGone()
        } else if (tabs.isVisible) {
            val fragment = viewpager.adapter?.instantiateItem(viewpager, viewpager.currentItem)
            if (fragment is ContainerRootFragment) {
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
