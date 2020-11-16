package com.ayc.canalguide.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceFragmentCompat
import com.ayc.canalguide.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preferences, s)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        preferences ?: key ?: return

        when (key) {
            requireContext().getString(R.string.pref_key_fullscreen_mode) ->
                mainViewModel.setImmerseMode(preferences?.getString(key, "0")!!)
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