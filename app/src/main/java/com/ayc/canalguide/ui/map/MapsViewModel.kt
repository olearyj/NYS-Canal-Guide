package com.ayc.canalguide.ui.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ayc.canalguide.data.CanalPreferences
import com.ayc.canalguide.data.entities.NavInfoMarker
import com.ayc.canalguide.repos.MarkerRepository
import com.google.android.gms.maps.GoogleMap

class MapsViewModel @ViewModelInject constructor(
        markerRepository: MarkerRepository,
        preferences: CanalPreferences
): ViewModel() {

    // Get markers from repository
    val bridgeGateMarkers = markerRepository.loadBridgeGateMarkers()
    val lockMarkers = markerRepository.loadLockMarkers()
    val marinaMarkers = markerRepository.loadMarinaMarkers()
    val launchMarkers = markerRepository.loadLaunchMarkers()
    val cruiseMarkers = markerRepository.loadCruiseMarkers()
    val navInfoMarkers = Transformations.map(markerRepository.loadNavInfoMarkers()) { markers ->
            // Remove any markers that don't fall into a category
            markers.filter { marker -> marker.getNavInfoType() != NavInfoMarker.Type.Unknown }
    }

    // Filter states
    val lockFilterState = MutableLiveData(preferences.cachedLockFilterState ?: true)
    val bridgeGateFilterState = MutableLiveData(preferences.cachedBridgeFilterState ?: true)
    val marinaFilterState = MutableLiveData(preferences.cachedMarinaFilterState ?: true)
    val launchFilterState = MutableLiveData(preferences.cachedLaunchFilterState ?: true)
    val cruiseFilterState = MutableLiveData(preferences.cachedCruiseFilterState ?: true)
    val navInfoFilterState = MutableLiveData(preferences.cachedNavinfoFilterState ?: false)

    // Map type (satellite, Terrain, etc.) variables
    private val mapTypes = arrayOf(GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN)

    val mapTypePosition = MutableLiveData(preferences.defaultMapTypeIndex.toInt())
    val selectedMapType = Transformations.map(mapTypePosition) { position ->
        mapTypes[position]
    }

}