package com.ayc.canalguide.ui.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ayc.canalguide.MainActivity
import com.ayc.canalguide.R
import com.ayc.canalguide.utils.MyHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_marker_details.*


class MarkerDetailsFragment : Fragment(R.layout.fragment_marker_details), OnMapReadyCallback {


    private val args: MarkerDetailsFragmentArgs by navArgs()

    private val viewModel: MarkerDetailsViewModel by viewModels()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i("TEST", "args = ${args.markerId}")


        ivCall.setOnClickListener {
            if (viewModel.hasPhoneNumber())
                MyHelper.makeCall(activity, viewModel.phoneNumber.value!!)
        }

        ivWebsite.setOnClickListener {
            if (viewModel.hasWebsite())
                MyHelper.openUrl(context, viewModel.website.value!!)
        }

        //(map as SupportMapFragment).getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        if (MainActivity.hasPermissions(requireContext(), MainActivity.LOCATION_PERMISSION))
            googleMap.isMyLocationEnabled = true
    }


}