package com.muralex.achiever.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context != null && intent != null) {
      val alarms = OneTimeWorkRequestBuilder<NotifyWorker>()
      WorkManager.getInstance(context).enqueue(alarms.build())
    }
  }
}
