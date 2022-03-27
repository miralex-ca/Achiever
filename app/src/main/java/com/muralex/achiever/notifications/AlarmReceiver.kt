package com.muralex.achiever.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (context != null && intent != null) {
      NotificationHelper.createNotificationFromReceiver(context, intent)
    }
  }
}