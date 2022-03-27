package com.muralex.achiever.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.muralex.achiever.notifications.AlarmScheduler.checkAlarmsAfterReboot

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            checkAlarmsAfterReboot(context)
        }
    }
}