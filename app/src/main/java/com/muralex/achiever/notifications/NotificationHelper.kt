package com.muralex.achiever.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.muralex.achiever.R
import com.muralex.achiever.presentation.activities.MainActivity


object NotificationHelper {

    private const val NOTIFICATION_CHANNEL_ID = "Reminder-Notification"
    const val NOTIFICATION_CHANNEL_NAME = "Reminder"
    const val NOTIFICATION_CHANNEL_DESC = "App notification"

    fun createNotificationChannel(context: Context, importance: Int, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = NOTIFICATION_CHANNEL_ID
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotificationFromReceiver(context: Context, intent: Intent?) {
        val title = context.getString(R.string.notification_title)
        val message = context.getString(R.string.notification_message)
        createSampleDataNotification(context, title, message)
    }

    private fun createSampleDataNotification(context: Context, title: String, message: String) {

        val channelId = NOTIFICATION_CHANNEL_ID

        val notification = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_notification_pin)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            setContentIntent(pendingIntent)
        }

        val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1001, notification.build())

    }




}