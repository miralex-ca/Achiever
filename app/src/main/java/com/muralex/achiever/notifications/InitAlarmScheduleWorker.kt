package com.muralex.achiever.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.muralex.achiever.notifications.AlarmScheduler.initAlarmsOnInstall

class InitAlarmScheduleWorker(
    val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        initAlarmsOnInstall(context)
        return Result.success()
    }

}

