package com.ayc.canalguide.ui.details

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ayc.canalguide.R
import com.ayc.canalguide.data.entities.NavInfoMarker
import com.ayc.canalguide.databinding.FragmentMarkerDetailsBinding
import com.ayc.canalguide.ui.MainActivity
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

        viewModel.mapMarker.observe(viewLifecycleOwner) { mapMarker ->
            (activity as MainActivity).supportActionBar?.let {
                it.title = mapMarker.getTitle()
                it.subtitle = mapMarker.getSnippet()
            }
        }

        viewModel.markerDetails.observe(viewLifecycleOwner) { details ->
            detailsLayout.removeAllViews()

            for (i in details.indices)
                addDetailsTextView(details[i], i % 2 == 0)

            if(viewModel.mapMarker.value !is NavInfoMarker)
                addDetailsTextView(requireContext().getString(R.string.text_marker_contact_info, viewModel.mapMarker.value?.name), false)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        addButtonClickListeners()
    }

    override fun onStop() {
        super.onStop()

        // Remove subtitle from actionbar
        (activity as MainActivity).supportActionBar?.subtitle = null
    }

    private fun addButtonClickListeners() {
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
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return

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