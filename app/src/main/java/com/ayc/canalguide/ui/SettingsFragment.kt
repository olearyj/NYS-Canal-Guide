package com.ayc.canalguide.ui

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ayc.canalguide.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preferences, s)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {






        return super.onPreferenceTreeClick(preference)
    }

}