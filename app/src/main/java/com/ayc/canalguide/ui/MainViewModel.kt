package com.ayc.canalguide.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ayc.canalguide.data.CanalPreferences

class MainViewModel @ViewModelInject constructor(
    preferences: CanalPreferences
): ViewModel() {


    // TODO - change var to livedata
    // True when immerse mode is set to Always On
    var immerseMode = preferences.fullscreenMapModeIndex == ImmerseMode.AlwaysOn.value

    // True when immerse mode set to Toggle on Tap
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