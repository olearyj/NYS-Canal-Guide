package com.ayc.canalguide.ui.map

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import com.ayc.canalguide.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.view_infowindow.view.*

class CanalInfoWindowAdapter(
        private val activity: Activity
): GoogleMap.InfoWindowAdapter {


    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker?): View {
        return activity.layoutInflater.inflate(R.layout.view_infowindow, null).apply {
            textTitle.text = marker?.title
            textSnippet.text = marker?.snippet
        }
    }

    // Use default InfoWindow frame
    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

}