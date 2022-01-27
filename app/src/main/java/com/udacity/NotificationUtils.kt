package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    fileName: String,
    statusDownload: String
) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        FLAG_UPDATE_CURRENT
    )

    val checkStatusIntent = Intent(applicationContext, DetailActivity::class.java)
    checkStatusIntent.putExtra("FILENAME", fileName)
    checkStatusIntent.putExtra("STATUSDOWNLOAD", statusDownload)
    checkStatusIntent.putExtra("NOTIFY_ID", NOTIFICATION_ID)

    val checkStatusPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        checkStatusIntent,
        FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.check_status),
            checkStatusPendingIntent
        )
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())

}