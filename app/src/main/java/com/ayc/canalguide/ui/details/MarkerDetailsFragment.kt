package com.ayc.canalguide.ui.details

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ayc.canalguide.MainActivity
import com.ayc.canalguide.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.marker_details_fragment.*

class MarkerDetailsFragment : Fragment(R.layout.marker_details_fragment), OnMapReadyCallback {


    private val viewModel: MarkerDetailsViewModel by viewModels()

    


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //(map as SupportMapFragment).getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        if (MainActivity.hasPermissions(requireContext(), MainActivity.LOCATION_PERMISSION))
            googleMap.isMyLocationEnabled = true
    }

}