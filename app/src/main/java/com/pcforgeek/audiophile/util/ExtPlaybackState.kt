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
    get() = (state == PlaybackStateCompat.STATE_PAUSED) ||
            (actions == PlaybackStateCompat.ACTION_PLAY_PAUSE) ||
            (actions == PlaybackStateCompat.ACTION_PLAY)

inline val PlaybackStateCompat.isSkipToNextEnabled
    get() = (actions == PlaybackStateCompat.ACTION_SKIP_TO_NEXT)

inline val PlaybackStateCompat.isSkipToPreviousEnabled
    get() = (actions == PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

