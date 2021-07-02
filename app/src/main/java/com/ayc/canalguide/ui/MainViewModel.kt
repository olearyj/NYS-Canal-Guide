package com.ayc.canalguide.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ayc.canalguide.data.CanalPreferences
import com.ayc.canalguide.ui.settings.ImmerseMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * This class will handle immerse mode states
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    preferences: CanalPreferences
): ViewModel() {


    // True when immerse mode preference is set to Always On
    var immerseMode = preferences.fullscreenMapModeIndex == ImmerseMode.AlwaysOn.value

    // True when immerse mode preference set to Toggle on Tap
    var toggleImmerseMode = preferences.fullscreenMapModeIndex == ImmerseMode.TapToToggle.value
        private set

    // Used by settinngs fragment
    fun setImmerseMode(fullscreenMapModeIndex: String) {
        when (fullscreenMapModeIndex) {
            ImmerseMode.Off.value -> {
                immerseMode = false
                toggleImmerseMode = false
            }
            ImmerseMode.TapToToggle.value -> toggleImmerseMode = true
            ImmerseMode.AlwaysOn.value -> {
                immerseMode = true
                toggleImmerseMode = false
            }
            else -> Log.wtf("MainViewModel", "Unexpected setImmerseMode parameter")
        }
    }

}