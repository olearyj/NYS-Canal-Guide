package com.ayc.canalguide.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.ayc.canalguide.R
import com.ayc.canalguide.data.entities.MapMarker
import com.ayc.canalguide.databinding.FragmentMapsBinding
import com.ayc.canalguide.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Be sure to know the difference between the two types with the word "marker" in it:
 * MapMarker(com.ayc.canalguide.data.entities.MapMarker) - data class to store data from the nys canal corp, stored in viewModel
 * Marker(com.google.android.gms.maps.model.Marker) - Reference to the icon that's placed on the google map view, stored in this fragment
 *
 * This class will:
 *  Handle location permissions and display location on map
 *  Display snackbar if location services are turned off
 *  Display filter FAB
 *  Initialize map
 *  Observe all MapMarkers and filter states
 *  Keep a list of references to all UI Markers displayed on map
 */
class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    private lateinit var binding: FragmentMapsBinding

    // List of references to the markers on the map by category
    private val lockMarkers = mutableListOf<Marker>()
    private val bridgeGateMarkers = mutableListOf<Marker>()
    private val marinaMarkers = mutableListOf<Marker>()
    private val cruiseMarkers = mutableListOf<Marker>()
    private val launchMarkers = mutableListOf<Marker>()
    private val navInfoMarkers = mutableListOf<Marker>()

    private lateinit var map: GoogleMap

    private var hasDismissedGpsOffSnackbar = false

    private val permReqLuncher = registerForActivityResult( ActivityResultContracts.RequestPermission() ) { granted ->
        Log.i(javaClass.name, "Location permissions${if (granted) "" else " NOT"} granted")
        if (granted) {
            enableMyLocation()
            checkForGps()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, this@MapsFragment.javaClass.simpleName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, this@MapsFragment.javaClass.simpleName)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabFilters.setOnClickListener {
            val action = MapsFragmentDirections.actionOptionsDialog()
            val extras = FragmentNavigatorExtras(binding.fabFilters to getString(R.string.shared_container_transition_name_filters))
            findNavController().navigate(action, extras)
        }

        // Initialize the map if it is not already initialized
        if (!this::map.isInitialized) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }

        handleLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        checkForGps()
    }

    /**
     * When the map is ready, move to start camera location, observe filter states & data from view model, set custom info window
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return

        // Move camera to starting position
        val saratoga = LatLng(43.0616419,-73.7719178)
        val startZoom = 8.0f
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(saratoga, startZoom) )

        enableMyLocation()

        // Hide the street view and directions button when a marker is clicked
        googleMap.uiSettings.isMapToolbarEnabled = false

        // Set custom info window so info icon appears to the right of window
        googleMap.setInfoWindowAdapter( CanalInfoWindowAdapter(requireActivity()) )

        // When the user clicks on the info window open the details page
        googleMap.setOnInfoWindowClickListener { marker ->
            val mapMarker = marker.tag as MapMarker
            val action = MapsFragmentDirections.actionMarkerDetails(mapMarker.markerId, mapMarker.javaClass.simpleName)
            findNavController().navigate(action)
        }

        googleMap.setOnCameraMoveListener {
            binding.fabFilters.shrink()
        }
        googleMap.setOnMapClickListener {
            (activity as MainActivity).toggleImmerseMode()
        }

        // Add viewModel observers
        observeMarkers(googleMap)
        observeFilterStates(googleMap)
        mapsViewModel.selectedMapType.observeForever { mapType ->
            googleMap.mapType = mapType
        }
    }

    private fun observeMarkers(googleMap: GoogleMap) {
        fun createMarkerObserver(markerList: MutableList<Marker>, filterStateIsChecked: LiveData<Boolean>): Observer<List<MapMarker>> =
            Observer<List<MapMarker>> { markers ->
                if (filterStateIsChecked.value != true) return@Observer
                Log.i("TEST", "There are ${markers.count()} ${if(markers.count() > 0) markers[0].javaClass.simpleName else "markers"}")

                // Refresh the marker list with the updated list
                markerList.removeMarkersFromMapAndClearList()
                for (marker in markers)
                    markerList += googleMap.addMarker( marker.getMarkerOptions() ).apply { tag = marker }
            }

        mapsViewModel.lockMarkers.observeForever(createMarkerObserver(lockMarkers, mapsViewModel.lockFilterState))
        mapsViewModel.bridgeGateMarkers.observeForever(createMarkerObserver(bridgeGateMarkers, mapsViewModel.bridgeGateFilterState))
        mapsViewModel.marinaMarkers.observeForever(createMarkerObserver(marinaMarkers, mapsViewModel.marinaFilterState))
        mapsViewModel.cruiseMarkers.observeForever(createMarkerObserver(cruiseMarkers, mapsViewModel.cruiseFilterState))
        mapsViewModel.launchMarkers.observeForever(createMarkerObserver(launchMarkers, mapsViewModel.launchFilterState))
        mapsViewModel.navInfoMarkers.observeForever(createMarkerObserver(navInfoMarkers, mapsViewModel.navInfoFilterState))
    }

    private fun observeFilterStates(googleMap: GoogleMap) {
        fun <T: MapMarker> createFilterStateObserver(markerList: MutableList<Marker>, mapMarkers: LiveData<List<T>>): Observer<Boolean> =
            Observer { isChecked ->
                if (isChecked)  // Add markers to map
                    for (marker in mapMarkers.value ?: return@Observer)
                        markerList += googleMap.addMarker( marker.getMarkerOptions() ).apply { tag = marker }
                else
                    markerList.removeMarkersFromMapAndClearList()
            }

        mapsViewModel.lockFilterState.observeForever(createFilterStateObserver(lockMarkers, mapsViewModel.lockMarkers))
        mapsViewModel.bridgeGateFilterState.observeForever(createFilterStateObserver(bridgeGateMarkers, mapsViewModel.bridgeGateMarkers))
        mapsViewModel.marinaFilterState.observeForever(createFilterStateObserver(marinaMarkers, mapsViewModel.marinaMarkers))
        mapsViewModel.cruiseFilterState.observeForever(createFilterStateObserver(cruiseMarkers, mapsViewModel.cruiseMarkers))
        mapsViewModel.launchFilterState.observeForever(createFilterStateObserver(launchMarkers, mapsViewModel.launchMarkers))
        mapsViewModel.navInfoFilterState.observeForever(createFilterStateObserver(navInfoMarkers, mapsViewModel.navInfoMarkers))
    }

    private fun MutableList<Marker>.removeMarkersFromMapAndClearList() {
        for (marker in this)
            marker.remove()
        this.clear()
    }

    /**
     * If the device has location services disabled show a snackbar to allow the user to go to location settings
     */
    private fun checkForGps() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasLocationPermission() && !hasDismissedGpsOffSnackbar)
            showGpsOffSnackbar()
    }

    private fun showGpsOffSnackbar() =
        Snackbar.make(binding.mapContainer, getString(R.string.snackbar_text_turn_on_gps), Snackbar.LENGTH_INDEFINITE)
            //.setAnchorView(fabFilters)    // Attempt to show snackbar above FAB per material design specs
            .setAction(getString(R.string.title_settings)) { startActivity( Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) ) }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event != DISMISS_EVENT_ACTION)  // Clicking the FAB is a good test for this
                        hasDismissedGpsOffSnackbar = true
                    super.onDismissed(transientBottomBar, event)
                }
            })
            .show()

    private fun hasLocationPermission() =
        checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun enableMyLocation() {
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            if (this::map.isInitialized)
                map.isMyLocationEnabled = true
    }

    /**
     * Check for location permissions and request if not available
     */
    private fun handleLocationPermission() {
        val hasLocationPermission = checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            enableMyLocation()
            checkForGps()
        } else
            permReqLuncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

}