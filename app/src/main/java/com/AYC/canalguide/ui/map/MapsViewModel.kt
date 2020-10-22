package com.AYC.canalguide.ui.map

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.liveData
import com.AYC.canalguide.repos.MarkerRepository

class MapsViewModel @ViewModelInject constructor(
        markerRepository: MarkerRepository
): ViewModel() {

    //val bridgeGateMarkers = markerRepository.loadSampleBridgeGateMarkers()
    val bridgeGateMarkers = markerRepository.loadBridgeGateMarkers()

    val lockMarkers = markerRepository.loadLockMarkers()


    val bridgeGateFilter = liveData<Boolean> {
        emit(true)
    }

}