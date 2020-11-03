package com.ayc.canalguide.ui.map

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ayc.canalguide.MainActivity
import com.ayc.canalguide.R
import com.ayc.canalguide.data.entities.MapMarker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Be sure to know the difference between the two types with the word "marker" in it:
 * MapMarker(com.ayc.canalguide.data.entities.MapMarker) - data class to store data from the nys canal corp, stored in viewModel
 * Marker(com.google.android.gms.maps.model.Marker) - Reference to the icon that's placed on the google map view, stored in this class
 */
class MapsFragment : Fragment(R.layout.fragment_maps) {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    // List of references to the markers on the map by category
    private val lockMarkers = mutableListOf<Marker>()
    private val bridgeGateMarkers = mutableListOf<Marker>()
    private val marinaMarkers = mutableListOf<Marker>()
    private val cruiseMarkers = mutableListOf<Marker>()
    private val launchMarkers = mutableListOf<Marker>()
    private val navInfoMarkers = mutableListOf<Marker>()

    private lateinit var map: GoogleMap


    /**
     * When the map is ready, move to start camera location, observe filter states & data from view model, set custom info window
     */
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        // Move camera to starting position
        val saratoga = LatLng(43.0616419,-73.7719178)
        val startZoom = 8.0f
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(saratoga, startZoom) )
        if (MainActivity.hasPermissions(requireContext(), MainActivity.LOCATION_PERMISSION))
            map.isMyLocationEnabled = true

        // Set custom info window so an icon appears on the right - TODO
        //googleMap.setInfoWindowAdapter(CanalInfoWindowAdapter())

        // When the user clicks on the info window open the details page
        googleMap.setOnInfoWindowClickListener {
            Toast.makeText(context, "CLICKED INFO WINDOW", Toast.LENGTH_SHORT).show()
            val action = MapsFragmentDirections.actionMarkerDetails(3)
            findNavController().navigate(action)
        }

        fun createMarkerObserver(markerList: MutableList<Marker>, filterStateIsChecked: LiveData<Boolean>): Observer<List<MapMarker>> =
                Observer<List<MapMarker>> { markers ->
                    if (filterStateIsChecked.value != true) return@Observer

                    // Refresh the marker list with the updated list
                    markerList.removeMarkersFromMapAndClearList()
                    for (marker in markers)
                        markerList += googleMap.addMarker( marker.getMarkerOptions() )
                }

        fun <T: MapMarker> createFilterStateObserver(markerList: MutableList<Marker>, mapMarkers: LiveData<List<T>>): Observer<Boolean> =
                Observer { isChecked ->
                    if (isChecked)  // Add markers to map
                        for (marker in mapMarkers.value ?: return@Observer)
                            markerList += googleMap.addMarker( marker.getMarkerOptions() )
                    else
                        markerList.removeMarkersFromMapAndClearList()
                }

        // Observe MapMarkers in viewModel
        mapsViewModel.lockMarkers.observe(viewLifecycleOwner, createMarkerObserver(lockMarkers, mapsViewModel.lockFilterState))
        mapsViewModel.bridgeGateMarkers.observe(viewLifecycleOwner, createMarkerObserver(bridgeGateMarkers, mapsViewModel.bridgeGateFilterState))
        mapsViewModel.marinaMarkers.observe(viewLifecycleOwner, createMarkerObserver(marinaMarkers, mapsViewModel.marinaFilterState))
        mapsViewModel.cruiseMarkers.observe(viewLifecycleOwner, createMarkerObserver(cruiseMarkers, mapsViewModel.cruiseFilterState))
        mapsViewModel.launchMarkers.observe(viewLifecycleOwner, createMarkerObserver(launchMarkers, mapsViewModel.launchFilterState))
        mapsViewModel.navInfoMarkers.observe(viewLifecycleOwner, createMarkerObserver(navInfoMarkers, mapsViewModel.navInfoFilterState))

        // Observe filter states
        mapsViewModel.lockFilterState.observe(viewLifecycleOwner, createFilterStateObserver(lockMarkers, mapsViewModel.lockMarkers))
        mapsViewModel.bridgeGateFilterState.observe(viewLifecycleOwner, createFilterStateObserver(bridgeGateMarkers, mapsViewModel.bridgeGateMarkers))
        mapsViewModel.marinaFilterState.observe(viewLifecycleOwner, createFilterStateObserver(marinaMarkers, mapsViewModel.marinaMarkers))
        mapsViewModel.cruiseFilterState.observe(viewLifecycleOwner, createFilterStateObserver(cruiseMarkers, mapsViewModel.cruiseMarkers))
        mapsViewModel.launchFilterState.observe(viewLifecycleOwner, createFilterStateObserver(launchMarkers, mapsViewModel.launchMarkers))
        mapsViewModel.navInfoFilterState.observe(viewLifecycleOwner, createFilterStateObserver(navInfoMarkers, mapsViewModel.navInfoMarkers))
    }

    private fun MutableList<Marker>.removeMarkersFromMapAndClearList() {
        for (marker in this)
            marker.remove()
        this.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
            if (requestCode == MainActivity.REQUEST_CODE)
                map.isMyLocationEnabled = true
            //Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): MapsFragment {
            return MapsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

}