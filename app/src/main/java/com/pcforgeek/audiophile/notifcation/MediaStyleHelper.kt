package com.pcforgeek.audiophile.notifcation

import android.support.v4.media.MediaMetadataCompat


object MediaStyleHelper {
    /**
     * Build a notification using the information from the given media session. Makes heavy use
     * of [MediaMetadataCompat.getDescription] to extract the appropriate information.
     * @param context Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
//    fun from(
//        context: Context, mediaSession: MediaSessionCompat
//    ): NotificationCompat {
//        val controller = mediaSession.controller
//        val mediaMetadata = controller.metadata
//        val description = mediaMetadata.description
//
//        val builder = NotificationBuilder.Builder(context)
//        builder
//            .setContentTitle(description.title)
//            .setContentText(description.subtitle)
//            .setSubText(description.description)
//            .setLargeIcon(description.iconBitmap)
//            .setContentIntent(controller.sessionActivity)
//            .setDeleteIntent(
//                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)
//            )
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        return builder
//    }
}