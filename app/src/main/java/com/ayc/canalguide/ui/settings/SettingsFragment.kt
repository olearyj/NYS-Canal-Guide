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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


/**
 * This class will:
 *  Display preferences
 *  Add non-editable dynamic preferences for app version and app developer to the about category
 *  Listen to preferences changes to handle immerse mode
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preferences, s)

        // Add app version preference
        val appVersionPref = Preference(requireContext()).apply {
            title = context.getString(R.string.title_setting_app_version)
            summary = "v${BuildConfig.VERSION_NAME}"
        }
        val appDeveloperPref = Preference(requireContext()).apply {
            title = context.getString(R.string.title_setting_app_developer)
            summary = "James O'Leary"
        }
        findPreference<PreferenceCategory>(requireContext().getString(R.string.pref_key_about_category))?.apply {
            addPreference(appDeveloperPref)
            addPreference(appVersionPref)
        }

        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, this@SettingsFragment.javaClass.simpleName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, this@SettingsFragment.javaClass.simpleName)
        })
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
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

}