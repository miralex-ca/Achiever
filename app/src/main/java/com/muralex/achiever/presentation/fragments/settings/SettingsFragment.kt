package com.muralex.achiever.presentation.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.R
import com.muralex.achiever.notifications.AlarmScheduler.checkAlarmsScheduleFromSettings
import com.muralex.achiever.notifications.AlarmScheduler.enableAlarmNotificationFromSettings
import com.muralex.achiever.presentation.components.ThemeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val nightModePref = preferenceManager.findPreference<ListPreference>(
            getString(R.string.dark_mode_key)
        )

        nightModePref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _: Preference?, value: Any? ->
                lifecycleScope.launch {
                    delay(200)
                    ThemeHelper.setThemeOption(requireContext(), value.toString())
                }
                true
            }

        val notifyPref = preferenceManager.findPreference<SwitchPreferenceCompat>("notifications_mode")

        notifyPref?.setOnPreferenceChangeListener { _, newValue ->
            enableAlarmNotificationFromSettings(requireContext(), newValue as Boolean)
            true
        }

        val notifyTimePref = preferenceManager.findPreference<ListPreference>("notifications_time")
            notifyTimePref?.setOnPreferenceChangeListener { _, newValue ->
                checkAlarmsScheduleFromSettings(requireContext(), newValue.toString())
                true
            }
    }


    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?,
    ): RecyclerView {
        val list = super.onCreateRecyclerView(inflater, parent,
            savedInstanceState)
        ViewCompat.setNestedScrollingEnabled(list, false)
        return list
    }
}