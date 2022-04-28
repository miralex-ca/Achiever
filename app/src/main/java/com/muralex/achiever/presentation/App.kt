package com.muralex.achiever.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.muralex.achiever.notifications.NotificationHelper
import com.muralex.achiever.notifications.NotificationHelper.NOTIFICATION_CHANNEL_DESC
import com.muralex.achiever.notifications.NotificationHelper.NOTIFICATION_CHANNEL_NAME
import com.muralex.achiever.BuildConfig
import com.muralex.achiever.R
import com.muralex.achiever.notifications.AlarmScheduler.checkAlarmsAfterReboot
import com.muralex.achiever.notifications.InitAlarmScheduleWorker
import com.muralex.achiever.presentation.utils.ThemeHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App :  Application(), Configuration.Provider {

    override fun onCreate() {

        super.onCreate()
        context = applicationContext

        setThemeOptions()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationHelper.createNotificationChannel(this,
                NotificationManager.IMPORTANCE_DEFAULT,  NOTIFICATION_CHANNEL_NAME, NOTIFICATION_CHANNEL_DESC )
        }

        val alarms = OneTimeWorkRequestBuilder<InitAlarmScheduleWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(alarms)

    }

    private fun setThemeOptions() {
        val settings =  PreferenceManager.getDefaultSharedPreferences(this)
        val darkMode: String? = settings.getString(getString(R.string.dark_mode_key), getString(R.string.dark_mode_disabled))
        darkMode?.let { ThemeHelper.setThemeOption(this, it) }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            //.setMinimumLoggingLevel(Log.DEBUG)
            .build()

}