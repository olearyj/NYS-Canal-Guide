package com.ayc.canalguide.ui.map

import android.app.Activity
import android.view.View
import com.ayc.canalguide.databinding.ViewInfowindowBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CanalInfoWindowAdapter(
        private val activity: Activity
): GoogleMap.InfoWindowAdapter {


    override fun getInfoContents(marker: Marker?): View {
        val binding = ViewInfowindowBinding.inflate(activity.layoutInflater, null, false).apply {
            textTitle.text = marker?.title
            textSnippet.text = marker?.snippet
        }

        return binding.root
    }

    // Use default InfoWindow frame
    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

}