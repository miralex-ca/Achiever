package com.muralex.achiever.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.notifications.AlarmScheduler
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class SettingsHelper @Inject constructor(
    @ActivityContext private val context: Context,
) {
    private var settings: SharedPreferences? = null

    init {
        settings = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isSwipeEnabled(): Boolean {
        val default = context.resources.getBoolean(R.bool.enable_swipe_default)
        var confirmDeletion = default
        settings?.let {
            confirmDeletion = it.getBoolean(context.getString(R.string.enable_swipe_key), default)
        }
        return confirmDeletion
    }

    fun isDeleteConfirmEnabled(): Boolean {

        val default = context.resources.getBoolean(R.bool.task_delete_confirm_default)
        var confirmDeletion = default
        settings?.let {
            confirmDeletion =
                it.getBoolean(context.getString(R.string.task_delete_confirm_key), default)
        }
        return confirmDeletion
    }

    fun isNotificationEnabled(): Boolean {
        val default = context.resources.getBoolean(R.bool.notification_enabled_default)
        var enabled = default
        settings?.let {
            enabled = it.getBoolean("notifications_mode", default)
        }
        return enabled
    }

    fun getNotificationTime(): String {
        val default = context.getString(R.string.notification_options_value_default)
        var timeOption = default
        settings?.let {
            timeOption = it.getString("notifications_time", default).toString()
        }
        return timeOption
    }

}
