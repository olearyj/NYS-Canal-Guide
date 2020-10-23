package com.ayc.canalguide.ui.map

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.ayc.canalguide.repos.MarkerRepository

class MapsViewModel @ViewModelInject constructor(
        markerRepository: MarkerRepository
): ViewModel() {

    //val bridgeGateMarkers = markerRepository.loadSampleBridgeGateMarkers()
    val bridgeGateMarkers = markerRepository.loadBridgeGateMarkers()

    val lockMarkers = markerRepository.loadLockMarkers()


    val lockFilterState = MutableLiveData(true)

}