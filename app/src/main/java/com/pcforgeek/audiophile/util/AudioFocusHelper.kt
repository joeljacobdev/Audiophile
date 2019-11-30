package com.pcforgeek.audiophile.util

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import timber.log.Timber

class AudioFocusHelper constructor(private val listener: OnAudioFocusHelper) {

    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var audioFocusRequestCompat: AudioFocusRequestCompat
    private var audioManager: AudioManager =
        (listener as Context).getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val afChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    listener.onAudioFocusGain()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    listener.onAudioFocusLoss()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                }
            }
        }

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setOnAudioFocusChangeListener(afChangeListener)
                setAudioAttributes(android.media.AudioAttributes.Builder().run {
                    setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                })
                build()
            }
        } else {
            audioFocusRequestCompat =
                AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).run {
                    setOnAudioFocusChangeListener(afChangeListener)
                    setAudioAttributes(AudioAttributesCompat.Builder().run {
                        setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                            .build()
                    })
                    build()
                }
        }
    }


    fun requestAudioFocus(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            audioManager.requestAudioFocus(
                afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        else
            audioManager.abandonAudioFocus(afChangeListener)

    }

    interface OnAudioFocusHelper {
        fun onAudioFocusGain()
        fun onAudioFocusLoss()
    }
}