package com.muralex.achiever.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.muralex.achiever.R
import java.util.*
import java.util.Calendar.*

object AlarmScheduler {

    private const val ALARM_REQUEST_CODE = 100

    fun initAlarmsOnInstall(context: Context) {
        scheduleAlarmsForReminder(context)
    }

    fun checkAlarmsAfterReboot(context: Context) {
        recheckAlarms(context)
    }

    fun checkAlarmsScheduleFromSettings(context: Context, time: String) {
        if (notificationsAreEnabled(context)) {
            scheduleAlarmsForReminderWithTime(context, time)
        }
    }

    fun enableAlarmNotificationFromSettings(context: Context, active: Boolean) {
        if (active) {
            scheduleAlarmsForReminder(context)
        } else {
            cancelAlarm(context)
        }
    }

    private fun recheckAlarms(context: Context) {
        val notificationAreEnabled = notificationsAreEnabled(context)
        if (notificationAreEnabled) scheduleAlarmsForReminder(context)
    }

    private fun notificationsAreEnabled(context: Context): Boolean {
        val settings = getPreferencesFromContext(context)
        return settings.getBoolean("notifications_mode",
            context.resources.getBoolean(R.bool.notification_enabled_default))
    }

    private fun scheduleAlarmsForReminder(context: Context) {
        val hour = getTimeFromSettings(context)
        scheduleAlarmWithTime(context, hour)
    }

    private fun scheduleAlarmsForReminderWithTime(context: Context, time: String) {
        val hour = getTimeFromString(context, time)
        scheduleAlarmWithTime(context, hour)
    }

    private fun scheduleAlarmWithTime(context: Context, hour: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context)
        scheduleAlarm(alarmIntent, alarmManager, hour)
    }

    private fun getTimeFromSettings(context: Context): Int {
        val settings = getPreferencesFromContext(context)
        val timeOption: String? = settings.getString("notifications_time",
            context.getString(R.string.notification_options_value_default))

        return getTimeFromString(context, timeOption)
    }

    private fun getTimeFromString (
        context: Context,
        timeOption: String?,
    ): Int {
        val options = context.resources.getStringArray(R.array.notification_options_values)
        val hour = when (timeOption) {
            options[2] -> 20  // evening
            options[1] -> 13  // afternoon
            else -> 6
        }
        return hour
    }

    private fun getPreferencesFromContext(context: Context) =
        context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    private fun scheduleAlarm(
        alarmIntent: PendingIntent?,
        alarmMgr: AlarmManager,
        hour: Int
    ) {

        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())

        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(HOUR_OF_DAY, hour)
        datetimeToAlarm.set(MINUTE, 0)
        datetimeToAlarm.set(SECOND, 0)
        datetimeToAlarm.set(MILLISECOND, 0)

        val today = Calendar.getInstance(Locale.getDefault())

       // val dateFormat = SimpleDateFormat("yyyy.MM.dd, HH:mm:ss")

        if (shouldNotifyToday(today, datetimeToAlarm)) {

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24).toLong(), alarmIntent)
            return
        }

        datetimeToAlarm.roll(DAY_OF_WEEK, 1)

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
            datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24).toLong(), alarmIntent)

    }

    private fun createPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun shouldNotifyToday(today: Calendar, datetimeToAlarm: Calendar): Boolean {

        var shouldNotify = false

        if (today.get(HOUR_OF_DAY) <= datetimeToAlarm.get(HOUR_OF_DAY)) {
            shouldNotify =
                !(today.get(HOUR_OF_DAY) == datetimeToAlarm.get(HOUR_OF_DAY) && today.get(MINUTE) > datetimeToAlarm.get(MINUTE))
        }

        return shouldNotify
    }

    private fun cancelAlarm(context: Context) {
        val pIntent = createPendingIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pIntent)
    }

}