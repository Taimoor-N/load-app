package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.MainActivity
import com.udacity.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(message: String, appContext: Context) {

    // Intent to take user to the MainActivity when user clicks the notification
    val contentIntent = Intent(appContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        appContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    // Build the Notification
    val builder = NotificationCompat.Builder(appContext, appContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())
}