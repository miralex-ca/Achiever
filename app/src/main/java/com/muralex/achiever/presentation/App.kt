package com.muralex.achiever.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import com.muralex.achiever.notifications.NotificationHelper
import com.muralex.achiever.notifications.NotificationHelper.NOTIFICATION_CHANNEL_DESC
import com.muralex.achiever.notifications.NotificationHelper.NOTIFICATION_CHANNEL_NAME
import com.muralex.achiever.BuildConfig
import com.muralex.achiever.R
import com.muralex.achiever.presentation.utils.ThemeHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App :  Application() {

    override fun onCreate() {

        super.onCreate()
        context = applicationContext

        setThemeOptions()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationHelper.createNotificationChannel(this,
                NotificationManager.IMPORTANCE_DEFAULT,  NOTIFICATION_CHANNEL_NAME, NOTIFICATION_CHANNEL_DESC )
        }

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

}