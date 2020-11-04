package com.ayc.canalguide.data.entities

import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.MarkerOptions

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

    fun getSnippet() = ("$bodyOfWater, mile $mile")

    abstract fun getMarkerOptions(): MarkerOptions?

}