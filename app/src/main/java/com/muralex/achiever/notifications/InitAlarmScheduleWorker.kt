package com.muralex.achiever.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.muralex.achiever.notifications.AlarmScheduler.checkAlarmsAfterReboot

class InitAlarmScheduleWorker(
    val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        checkAlarmsAfterReboot(context)
        return Result.success()
    }

}

