package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(intent: Intent, appContext: Context) {

    // Intent to take user to the DetailActivity when user clicks the notification
    val actionPendingIntent = PendingIntent.getActivity(
        appContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the Notification
    val builder = NotificationCompat.Builder(appContext, appContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(appContext.getString(R.string.notification_description))
        .setAutoCancel(true)
        .addAction(R.drawable.ic_download,
            appContext.getString(R.string.notification_button),
            actionPendingIntent)
    notify(NOTIFICATION_ID, builder.build())
}