package com.pcforgeek.audiophile.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.MusicSource
import com.pcforgeek.audiophile.data.StorageMediaSource
import com.pcforgeek.audiophile.notifcation.NOW_PLAYING_NOTIFICATION
import com.pcforgeek.audiophile.notifcation.NotificationBuilder
import com.pcforgeek.audiophile.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MusicService : MediaBrowserServiceCompat(), MediaPlaybackPreparer.OnPlaylistListener {
    override fun onPlaylistCreated(list: List<MediaMetadataCompat>) {
        playlist.clear()
        playlist.addAll(list)
    }

    // A class to encapsulate a collection of attributes describing information about an audio stream.
    private val audiophyAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(audiophyAttributes, true)
        }
    }
    private val browserTree: BrowserTree by lazy {
        BrowserTree(applicationContext, mediaSource)
    }
    private lateinit var mediaSource: MusicSource
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var audioManager: AudioManager
    private lateinit var playbackPreparer: MediaPlaybackPreparer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var packageValidator: PackageValidator

    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var isForegroundService = false
    private val playlist = mutableListOf<MediaMetadataCompat>()

    override fun onCreate() {
        super.onCreate()
//        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
//                setOnAudioFocusChangeListener(afChangeListener)
//                setAudioAttributes(android.media.AudioAttributes.Builder().run {
//                    setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
//                    build()
//                })
//                build()
//            }

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        // Create a new MediaSession.
        mediaSession = MediaSessionCompat(this, "MusicService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
                setSessionToken(sessionToken)
            }

        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback())
        }

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver =
            BecomingNoisyReceiver(context = this, sessionToken = mediaSession.sessionToken)

        mediaSource = StorageMediaSource(applicationContext)
        serviceScope.launch {
            mediaSource.load()
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).also { connector ->

            val dataSourceFactory =
                DefaultDataSourceFactory(
                    applicationContext,
                    Util.getUserAgent(this, AUDIOPHILE_USER_AGENT),
                    null
                )

            playbackPreparer =
                MediaPlaybackPreparer(mediaSource, exoPlayer, dataSourceFactory, this)

            connector.setPlayer(exoPlayer)
            connector.setPlaybackPreparer(playbackPreparer)
            connector.setQueueNavigator(AudiophileQueueNavigator(mediaSession))
        }
        packageValidator = PackageValidator(this, R.xml.allowed_media_browser_callers)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)

        /**
         * By stopping playback, the player will transition to [Player.STATE_IDLE]. This will
         * cause a state change in the MediaSession, and (most importantly) call
         * [MediaControllerCallback.onPlaybackStateChanged]. Because the playback state will
         * be reported as [PlaybackStateCompat.STATE_NONE], the service will first remove
         * itself as a foreground service, and will then call [stopSelf].
         */
        exoPlayer.stop(true)
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }

        serviceJob.cancel()
    }


    override fun onGetRoot(clientPackageName: String, clientUid: Int, p2: Bundle?): BrowserRoot? {
        val isKnownCaller = packageValidator.isKnownCaller(clientPackageName, clientUid)
        return if (isKnownCaller) {
            val rootExtras = Bundle().apply {
                putBoolean(CONTENT_STYLE_SUPPORTED, true)
                putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID)
                putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST)
            }
            BrowserRoot(AUDIOPHY_ROOT_ID, rootExtras)
        } else {
            /**On Unknown caller, follow any of the two methods
             * 1) Return a root without any content, which still allows the connecting client
             * to issue commands.
             * 2) Return `null`, which will cause the system to disconnect the app.
             */
            BrowserRoot(AUDIOPHY_EMPTY_ROOT, null)
        }
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaItem>>) {
        println("MusicService onLoadChildren parentId=$parentId")
        // TODO why on removing whenReady and only keeping serviceScope.launch {} media is not playable
        mediaSource.whenReady {
            if (it) {
                serviceScope.launch {
                    val mediaItems = mediaSource.getMediaMetadataForParenId(parentId).map { metadata ->
                        val duration = metadata.duration
                        val extras = Bundle()
                        extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        val description = MediaDescriptionCompat.Builder().setExtras(extras)
                            .setIconUri(metadata.description.iconUri)
                            .setMediaId(metadata.description.mediaId)
                            .setMediaUri(metadata.description.mediaUri)
                            .setTitle(metadata.title)
                            .setSubtitle(metadata.artist)
                            .build()
                        MediaItem(description, metadata.flag)
                    }
                    println("onLoadedChildren - ${mediaItems.size}")
                    result.sendResult(mediaItems)
                }
            }
            return@whenReady
        }


        println("onLoadedChildren - resultNotReady")
        result.detach()
    }

    /**
     * Returns a list of [MediaItem]s that match the given search query
     */
    override fun onSearch(
        query: String,
        extras: Bundle?,
        result: Result<List<MediaItem>>
    ) {

        val resultsSent = mediaSource.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                val resultsList = mediaSource.search(query, extras ?: Bundle.EMPTY)
                    .map { mediaMetadata ->
                        MediaItem(mediaMetadata.description, MediaItem.FLAG_PLAYABLE)
                    }
                result.sendResult(resultsList)
            }
        }

        if (!resultsSent) {
            result.detach()
        }
    }

//
//    private val afChangeListener = AudioManager.OnAudioFocusChangeListener {
//
//    }

    private fun removeNowPlayingNotification() {
        stopForeground(true)
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let { updateNotification(it) }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { updateNotification(it) }
        }

        private fun updateNotification(state: PlaybackStateCompat) {
            val updatedState = state.state

            // Skip building a notification when state is "none" and metadata is null.
            val notification = if (mediaController.metadata != null
                && updatedState != PlaybackStateCompat.STATE_NONE
            ) {
                notificationBuilder.buildNotification(mediaSession.sessionToken)
            } else {
                null
            }

            when (updatedState) {
                PlaybackStateCompat.STATE_BUFFERING,
                PlaybackStateCompat.STATE_PLAYING -> {
                    becomingNoisyReceiver.register()

                    /**
                     * This may look strange, but the documentation for [Service.startForeground]
                     * notes that "calling this method does *not* put the service in the started
                     * state itself, even though the name sounds like it."
                     */
                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)

                        if (!isForegroundService) {
                            ContextCompat.startForegroundService(
                                applicationContext,
                                Intent(applicationContext, this@MusicService.javaClass)
                            )
                            startForeground(NOW_PLAYING_NOTIFICATION, notification)
                            isForegroundService = true
                        }
                    }
                }
                else -> {
                    becomingNoisyReceiver.unregister()

                    if (isForegroundService) {
                        stopForeground(false)
                        isForegroundService = false

                        // If playback has ended, also stop the service.
                        if (updatedState == PlaybackStateCompat.STATE_NONE) {
                            stopSelf()
                        }

                        if (notification != null) {
                            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                        } else {
                            removeNowPlayingNotification()
                        }
                    }
                }
            }
        }
    }

    private inner class AudiophileQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return playlist[windowIndex].description
        }

        override fun onSkipToNext(player: Player?, controlDispatcher: ControlDispatcher?) {
            val next = player?.nextWindowIndex ?: return
            player.seekTo(next, 0L)
        }
    }

    companion object {
        const val AUDIOPHY_ROOT_ID = "/"
        const val AUDIOPHY_EMPTY_ROOT = "@empty@"
    }

}

private class BecomingNoisyReceiver(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : BroadcastReceiver() {

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val controller = MediaControllerCompat(context, sessionToken)

    private var registered = false

    fun register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            controller.transportControls.pause()
        }
    }
}

private const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
private const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
private const val CONTENT_STYLE_LIST = 1
private const val CONTENT_STYLE_GRID = 2
private const val AUDIOPHILE_USER_AGENT = "audiophile"

