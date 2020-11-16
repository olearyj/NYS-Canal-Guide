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
    var immerseMode = preferences.fullscreenMapModeIndex == "2"

    // True when immerse mode set to Toggle on Tap
    var toggleImmerseMode = preferences.fullscreenMapModeIndex == "1"
        private set

    // Used by settinngs fragment
    fun setImmerseMode(value: String) {
        when (value) {
            "0" -> {
                immerseMode = false
                toggleImmerseMode = false
            }
            "1" -> toggleImmerseMode = true
            "2" -> {
                immerseMode = true
                toggleImmerseMode = false
            }
            else -> Log.wtf("MainViewModel", "Unexpected setImmerseMode parameter")
        }
    }

}