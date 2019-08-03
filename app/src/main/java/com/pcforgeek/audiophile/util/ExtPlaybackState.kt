package com.pcforgeek.audiophile.util

import android.support.v4.media.session.PlaybackStateCompat


inline val PlaybackStateCompat.isPrepared
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING) ||
            (state == PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.isPlaying
    get() = (state == PlaybackStateCompat.STATE_PLAYING) ||
            (state == PlaybackStateCompat.STATE_BUFFERING)

inline val PlaybackStateCompat.isPlayEnabled
    // (actions and PlaybackStateCompat.ACTION_PLAY != 0L) when both action and ACTION_PLAY have different value
    // it will not be equal to 0L, if it equal to 0L then it is playing
    // why no use (actions == PlaybackStateCompat.ACTION_PLAY)
    get() = (state == PlaybackStateCompat.STATE_PAUSED) ||
            (actions == PlaybackStateCompat.ACTION_PLAY_PAUSE) || // can pause and play
            (actions == PlaybackStateCompat.ACTION_PLAY) // can play

inline val PlaybackStateCompat.isSkipToNextEnabled
    get() = (actions == PlaybackStateCompat.ACTION_SKIP_TO_NEXT)

inline val PlaybackStateCompat.isSkipToPreviousEnabled
    get() = (actions == PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

