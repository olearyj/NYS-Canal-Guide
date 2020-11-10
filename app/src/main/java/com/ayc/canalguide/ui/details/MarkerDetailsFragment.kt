package com.ayc.canalguide.ui.details

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ayc.canalguide.MainActivity
import com.ayc.canalguide.R
import com.ayc.canalguide.databinding.FragmentMarkerDetailsBinding
import com.ayc.canalguide.utils.MyHelper
import com.ayc.canalguide.utils.dpToPx
import com.ayc.canalguide.utils.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_marker_details.*

@AndroidEntryPoint
class MarkerDetailsFragment : Fragment(R.layout.fragment_marker_details), OnMapReadyCallback {


    //private val args: MarkerDetailsFragmentArgs by navArgs()

    private val viewModel: MarkerDetailsViewModel by viewModels()

    private val binding by viewBinding(FragmentMarkerDetailsBinding::bind)

    private lateinit var googleMap: GoogleMap


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        ivCall.setOnClickListener {
            if (viewModel.hasPhoneNumber())
                MyHelper.makeCall(activity, viewModel.phoneNumber.value!!)
        }
        ivWebsite.setOnClickListener {
            if (viewModel.hasWebsite())
                MyHelper.openUrl(context, viewModel.website.value!!)
        }
        ivWebsiteNoaa.setOnClickListener {
            if (viewModel.hasWebsiteNoaa())
                MyHelper.openUrl(context, viewModel.websiteNoaa.value!!)
        }

        viewModel.markerDetails.observe(viewLifecycleOwner) { details ->
            detailsLayout.removeAllViews()

            for (i in details.indices)
                addDetailsTextView(details[i], i % 2 == 0)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return

        @SuppressLint("MissingPermission")
        if (MainActivity.hasPermissions(requireContext(), MainActivity.LOCATION_PERMISSION))
            googleMap.isMyLocationEnabled = true

        // Add marker to map and move camera to it's position
        viewModel.mapMarker.observe(viewLifecycleOwner) { mapMarker ->
            googleMap.addMarker( mapMarker.getMarkerOptions() )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mapMarker.lat, mapMarker.lng), 14.5f))
        }
    }

    private fun addDetailsTextView(detail: String, isHeader: Boolean) {
        detailsLayout.addView(TextView(context).apply {
            text = detail

            // Set text appearance to medium or large depending on if this should be a header
            val textAppearance = if (isHeader) android.R.style.TextAppearance_Medium else android.R.style.TextAppearance_Small
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setTextAppearance(textAppearance)
            else @Suppress("DEPRECATION") setTextAppearance(context, textAppearance)

            if (isHeader) setPadding(4.dpToPx(context), 6.dpToPx(context), 4.dpToPx(context), 4.dpToPx(context))
            else setPadding(8.dpToPx(context), 0, 4.dpToPx(context), 2.dpToPx(context))
        })
    }

}