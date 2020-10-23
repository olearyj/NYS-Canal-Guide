package com.ayc.canalguide.ui.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ayc.canalguide.R
import com.ayc.canalguide.data.entities.LockMarker
import com.ayc.canalguide.data.entities.MapMarker

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(R.layout.fragment_maps) {


    private val mapsViewModel: MapsViewModel by activityViewModels()

    private val lockMarkers = mutableListOf<Marker>()


    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        //val sydney = LatLng(-34.0, 151.0)
        //googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // Move camera to starting position
        val saratoga = LatLng(43.0616419,-73.7719178)
        val startZoom = 8.0f
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(saratoga, startZoom) )

        fun createMarkerObserver(markerList: MutableList<Marker>): Observer<List<MapMarker>> =
                Observer<List<MapMarker>> { markers ->
                    // Refresh the marker list with the updated list
                    markerList.removeMarkersFromMapAndClearList()
                    for (marker in markers)
                        markerList += googleMap.addMarker( marker.getMarkerOptions() )
                }

        fun createFilterStateObserver(markerList: MutableList<Marker>, mapMarkers: List<MapMarker>?): Observer<Boolean> =
                Observer { isChecked ->
                    if (isChecked)  // Add markers to map
                        for (marker in mapMarkers ?: return@Observer)
                            markerList += googleMap.addMarker( marker.getMarkerOptions() )
                    else
                        markerList.removeMarkersFromMapAndClearList()
                }

        mapsViewModel.bridgeGateMarkers.observe(viewLifecycleOwner) { markers ->
            for (marker in markers)
                googleMap.addMarker( marker.getMarkerOptions() )
        }

        mapsViewModel.lockMarkers.observe(viewLifecycleOwner, createMarkerObserver(lockMarkers))
        mapsViewModel.lockFilterState.observe(viewLifecycleOwner, createFilterStateObserver(lockMarkers, mapsViewModel.lockMarkers.value))

        // Set custom info window so an icon appears on the right - TODO
        //googleMap.setInfoWindowAdapter(CanalInfoWindowAdapter())

        // When the user clicks on the info window open the details page
        googleMap.setOnInfoWindowClickListener {
            Toast.makeText(context, "CLICKED INFO WINDOW", Toast.LENGTH_SHORT).show()
        }
    }

    private fun MutableList<Marker>.removeMarkersFromMapAndClearList() {
        for (marker in lockMarkers)
            marker.remove()
        this.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
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