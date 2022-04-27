package com.muralex.achiever.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.muralex.achiever.notifications.AlarmScheduler.checkAlarmsAfterReboot
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            checkAlarmsAfterReboot(context)
        }
    }
}


class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED"
            || intent.action == "android.intent.action.TIME_SET"
        ) {
            checkAlarmsAfterReboot(context)
        }

    }
}