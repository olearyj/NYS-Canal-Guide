package com.ayc.canalguide.ui.map

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.ayc.canalguide.repos.MarkerRepository

class MapsViewModel @ViewModelInject constructor(
        markerRepository: MarkerRepository
): ViewModel() {

    // Get markers from repo
    //val bridgeGateMarkers = markerRepository.loadSampleBridgeGateMarkers()
    val bridgeGateMarkers = markerRepository.loadBridgeGateMarkers()
    val lockMarkers = markerRepository.loadLockMarkers()
    val marinaMarkers = markerRepository.loadMarinaMarkers()
    val launchMarkers = markerRepository.loadLaunchMarkers()
    val cruiseMarkers = markerRepository.loadCruiseMarkers()
    val navInfoMarkers = Transformations.map(markerRepository.loadNavInfoMarkers()) { markers ->
        // Remove any markers that don't fall into a category
        markers.filter { marker -> marker.getBitmapDescriptor() != null }
    }

    // Filter states
    val lockFilterState = MutableLiveData(true)
    val bridgeGateFilterState = MutableLiveData(true)
    val marinaFilterState = MutableLiveData(true)
    val launchFilterState = MutableLiveData(true)
    val cruiseFilterState = MutableLiveData(true)
    val navInfoFilterState = MutableLiveData(false)

}