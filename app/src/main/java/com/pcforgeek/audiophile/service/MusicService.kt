package com.pcforgeek.audiophile.service

import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.os.Bundle
import android.text.TextUtils
import androidx.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.media.AudioManager
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.support.v4.media.MediaDescriptionCompat
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.Player
import com.pcforgeek.audiophile.notifcation.NOW_PLAYING_NOTIFICATION
import com.pcforgeek.audiophile.notifcation.NotificationBuilder
import com.pcforgeek.audiophile.util.*


class MusicService : MediaBrowserServiceCompat() {
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

    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var isForegroundService = false


//    private val callback = object : MediaSessionCompat.Callback() {
//        override fun onPlay() {
//        }
//
//        override fun onPause() {
//            super.onPause()
//        }
//
//        override fun onStop() {
//            super.onStop()
//        }
//
//        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
//            super.onPlayFromMediaId(mediaId, extras)
//            println("onPlayFromMediaId - $mediaId")
//        }
//
//
//    }

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
                setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                            or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                )
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }
        sessionToken = mediaSession.sessionToken

//        mediaSession = MediaSessionCompat(applicationContext, "audiophile").apply {
//            // TODO What is transport control?
//            setFlags(
//                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
//                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
//            )
//            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
//            // what is the difference between ACTION_PAUSE and ACTION_PLAY_PAUSE
//            stateBuilder = PlaybackStateCompat.Builder()
//                .setActions(
//                    PlaybackStateCompat.ACTION_PLAY
//                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
//                )
//            setPlaybackState(stateBuilder.build())
//
//            isActive = true
//
//            // We will set it to null since we don't want a MediaPlayer button to start our app if it has been stopped
//            setMediaButtonReceiver(null)
//
//            // MySessionCallback() has methods that handle callbacks from a media controller
//            setCallback(callback)
//
//            // Set the session's token so that client activities can communicate with it.
//            setSessionToken(sessionToken)
//        }

        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback())
        }

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        println("session token - ${mediaSession.sessionToken.token}")
        becomingNoisyReceiver =
            BecomingNoisyReceiver(context = this, sessionToken = mediaSession.sessionToken)

        mediaSource = StorageMediaSource(applicationContext)
        serviceScope.launch {
            mediaSource.load()
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).also { connector ->

            val dataSourceFactory = DefaultDataSourceFactory(applicationContext, "Exoplayer-local")

            playbackPreparer = MediaPlaybackPreparer(mediaSource, exoPlayer, dataSourceFactory)

            connector.setPlayer(exoPlayer)
            connector.setPlaybackPreparer(playbackPreparer)
//            connector.setQueueNavigator(UampQueueNavigator(mediaSession))
        }
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
        val isKnownCaller = TextUtils.equals(clientPackageName, packageName)
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

    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        println("MusicService onLoadChildren parentId=$parentId")
        val resultReady = mediaSource.whenReady { isLoaded ->
            if (isLoaded) {
                val mediaItems = browserTree[parentId]?.map {
                    MediaItem(it.description, it.flag)
                }
                println("onLoadedChildren - ${mediaItems?.size}")
                result.sendResult(mediaItems)
            } else {
                result.sendResult(null)
            }
        }

        if (!resultReady) {
            println("onLoadedChildren - resultNotReady")
            result.detach()
        }
    }

    private fun prepareMediaItemfromSource(): MutableList<MediaBrowserCompat.MediaItem> {
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
        mediaSource.forEach { track ->
            val mediaId = track.id ?: "empty"
            //Artist song
            val title = track.displayTitle
            //Artist name
            val subTitle = track.description.subtitle.toString()
            //Artist album
            val description = track.description.description.toString()
            //Song duration
            val duration = track.duration
            val songDuration = Bundle()
            songDuration.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            //songDuration.putParcelable(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, track.mediaUri)

            val desc = MediaDescriptionCompat.Builder()
                .setTitle(title)
                .setSubtitle(subTitle)
                .setMediaUri(track.mediaUri)
                .setMediaId(mediaId)
                .setExtras(songDuration)
                .build()

            val songList = MediaBrowserCompat.MediaItem(
                desc,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
            mediaItems.add(songList)
        }
        return mediaItems
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

