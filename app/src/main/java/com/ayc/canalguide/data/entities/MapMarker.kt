package com.ayc.canalguide.data.entities

import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

abstract class MapMarker (
    open val lat: Double,
    open val lng: Double,
    open val name: String,
    open val bodyOfWater: String?,
    open val mile: String?,
    @PrimaryKey(autoGenerate = true)
    open val markerId: Int = 0
) {


    open fun getTitle() = name

    open fun getSnippet() = ("$bodyOfWater, mile $mile".capitalize(Locale.ROOT))

    open fun getMarkerOptions(): MarkerOptions? = MarkerOptions()
            .title(getTitle())
            .position(LatLng(lat, lng))
            .snippet(getSnippet())

    protected open fun isNotBlank(str: String?): Boolean {
        return !(str == "" || str == null || str == " " || str == "-1")
    }

}