package com.muralex.achiever.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.muralex.achiever.R
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.presentation.utils.SettingsHelper
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object AlarmScheduler {

    private const val ALARM_REQUEST_CODE = 100

    fun checkAlarmsAfterReboot(context: Context) {
        recheckAlarms(context)
    }

    fun checkAlarmsScheduleFromSettings(context: Context, time: String) {
        if (notificationsAreEnabled(context)) {
            scheduleAlarmsForReminderWithTime(context, time)
        }
    }

    fun enableAlarmNotificationFromSettings(context: Context, active: Boolean) {
        var receiverEnabledState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED

        if (active) {
            scheduleAlarmsForReminder(context)
        } else {
            receiverEnabledState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            cancelAlarm(context)
        }

        val receiver = ComponentName(context, BootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            receiverEnabledState,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun recheckAlarms(context: Context) {
        if (notificationsAreEnabled(context)) {
            scheduleAlarmsForReminder(context)
        }
    }

    private fun notificationsAreEnabled(context: Context) =
        SettingsHelper(context).isNotificationEnabled()

    private fun scheduleAlarmsForReminder(context: Context) {
        val hour = getTimeFromSettings(context)
        scheduleAlarmWithTime(context, hour)
    }

    private fun scheduleAlarmsForReminderWithTime(context: Context, time: String) {
        val hour = getTimeFromString(context, time)
        scheduleAlarmWithTime(context, hour)
    }

    private fun scheduleAlarmWithTime(context: Context, hour: Int) {
        cancelAlarm(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context)
        scheduleAlarm(alarmIntent, alarmManager, hour)
    }

    private fun getTimeFromSettings(context: Context): Int {
        val timeOption = SettingsHelper(context).getNotificationTime()
        return getTimeFromString(context, timeOption)
    }

    private fun getTimeFromString(
        context: Context,
        timeOption: String?,
    ): Int {
        val options = context.resources.getStringArray(R.array.notification_options_values)
        val hour = when (timeOption) {
            options[2] -> 20  // evening
            options[1] -> 14  // afternoon
            else -> 7
        }
        return hour
    }

    private fun scheduleAlarm(
        alarmIntent: PendingIntent?,
        alarmMgr: AlarmManager,
        hour: Int,
    ) {
        val datetimeToAlarm = getAlarmDate(hour)
        if (!shouldNotifyToday(datetimeToAlarm)) datetimeToAlarm.add(Calendar.DATE, 1)
        setAlarm(alarmMgr, datetimeToAlarm, alarmIntent)

//        val dateFormat = SimpleDateFormat("yyyy.MM.dd, HH:mm:ss")
//        Timber.d("notify time: ${ dateFormat.format(datetimeToAlarm.timeInMillis)}")

    }

    private fun getAlarmDate(hour: Int): Calendar {
        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = CurrentTime().getMillis()
        datetimeToAlarm.set(Calendar.HOUR_OF_DAY, hour)
        datetimeToAlarm.set(Calendar.MINUTE, 0)
        datetimeToAlarm.set(Calendar.SECOND, 0)
        datetimeToAlarm.set(Calendar.MILLISECOND, 0)
        return datetimeToAlarm
    }

    private fun setAlarm(
        alarmMgr: AlarmManager,
        datetimeToAlarm: Calendar,
        alarmIntent: PendingIntent?,
    ) {
        val interval: Long = AlarmManager.INTERVAL_DAY
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            datetimeToAlarm.timeInMillis,
            interval,
            alarmIntent)
    }


    private fun createPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        val pendingIntentFlags =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        return PendingIntent.getBroadcast(context,
            ALARM_REQUEST_CODE,
            intent,
            pendingIntentFlags)
    }

    private fun shouldNotifyToday(datetimeToAlarm: Calendar): Boolean {
        val today = Calendar.getInstance(Locale.getDefault())
        var shouldNotify = false

        if (today.get(Calendar.HOUR_OF_DAY) <= datetimeToAlarm.get(Calendar.HOUR_OF_DAY)) {
            shouldNotify =
                !(today.get(Calendar.HOUR_OF_DAY) == datetimeToAlarm.get(Calendar.HOUR_OF_DAY) && today.get(
                    Calendar.MINUTE) > datetimeToAlarm.get(Calendar.MINUTE))
        }

        return shouldNotify
    }

    private fun cancelAlarm(context: Context) {
        val pIntent = createPendingIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pIntent)
    }

}