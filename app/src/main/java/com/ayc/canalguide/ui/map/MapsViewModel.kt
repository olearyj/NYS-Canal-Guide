package com.ayc.canalguide.ui.map

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ayc.canalguide.repos.MarkerRepository
import com.google.android.gms.maps.GoogleMap

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

    // Map type (satellite, Terrain, etc.) variables
    private val mapTypes = arrayOf(GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN)
    val mapTypeNames = arrayOf("Normal", "Hybrid", "Satellite", "Terrain")

    val mapTypePosition = MutableLiveData(0)
    val selectedMapType = Transformations.map(mapTypePosition) { position ->
        mapTypes[position]
    }

}