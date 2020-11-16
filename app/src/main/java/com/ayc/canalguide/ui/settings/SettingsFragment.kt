package com.ayc.canalguide.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.ayc.canalguide.BuildConfig
import com.ayc.canalguide.R
import com.ayc.canalguide.ui.MainViewModel


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preferences, s)

        // Add app version preference
        val appVersionPref = Preference(context).apply {
            title = context.getString(R.string.title_setting_app_version)
            summary = "v${BuildConfig.VERSION_NAME}"
        }
        val appDeveloperPref = Preference(context).apply {
            title = context.getString(R.string.title_setting_app_developer)
            summary = "James O'Leary"
        }
        findPreference<PreferenceCategory>(requireContext().getString(R.string.pref_key_about_category))?.apply {
            addPreference(appDeveloperPref)
            addPreference(appVersionPref)
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        preferences ?: key ?: return

        when (key) {
            requireContext().getString(R.string.pref_key_fullscreen_mode) ->
                mainViewModel.setImmerseMode(preferences?.getString(key, ImmerseMode.Off.value)!!)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

}