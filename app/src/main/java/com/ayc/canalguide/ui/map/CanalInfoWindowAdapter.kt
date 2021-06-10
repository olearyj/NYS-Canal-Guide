package com.ayc.canalguide.ui.map

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import com.ayc.canalguide.R
import com.ayc.canalguide.databinding.ViewInfowindowBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CanalInfoWindowAdapter(
        private val activity: Activity
): GoogleMap.InfoWindowAdapter {


    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker?): View {
        val binding = ViewInfowindowBinding.inflate(activity.layoutInflater, null, false)

        return activity.layoutInflater.inflate(R.layout.view_infowindow, null).apply {
            binding.textTitle.text = marker?.title
            binding.textSnippet.text = marker?.snippet
        }
    }

    // Use default InfoWindow frame
    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

}