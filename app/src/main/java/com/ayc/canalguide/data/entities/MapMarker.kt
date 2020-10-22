package com.ayc.canalguide.data.entities

import com.google.android.gms.maps.model.MarkerOptions

abstract class MapMarker (
    open val lat: Double,
    open val lng: Double,
    open val name: String,
    open val bodyOfWater: String,
    open val mile: Double
) {

    fun getSnippet() = ("$bodyOfWater, mile $mile")

    abstract fun getMarkerOptions(): MarkerOptions?

}