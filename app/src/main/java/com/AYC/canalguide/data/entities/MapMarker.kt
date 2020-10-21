package com.AYC.canalguide.data.entities

import androidx.room.PrimaryKey

abstract class MapMarker (
    open val lat: Double,
    open val lng: Double,
    open val name: String,
    open val bodyOfWater: String,
    open val mile: Double
) {

    fun getSnippet() = ("$bodyOfWater, mile $mile")

}