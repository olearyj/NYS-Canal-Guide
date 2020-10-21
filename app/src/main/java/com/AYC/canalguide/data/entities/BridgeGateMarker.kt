package com.AYC.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Entity(tableName = "BridgeGateMarker")
data class BridgeGateMarker (
    @Ignore
    override val lat: Double,
    @Ignore
    override val lng: Double,
    @Ignore
    override val name: String,
    @Ignore
    override val bodyOfWater: String,
    @Ignore
    override val mile: Double,

    val location: String?,
    val phone: String?,
    val clearanceClosed: Double?,
    val clearanceOpened: Double?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): MapMarker(lat, lng, name, bodyOfWater, mile) {


    fun getMarkerOptions(): MarkerOptions? {
        return MarkerOptions()
            .title(name)
            .position(LatLng(lat, lng))
            .snippet(getSnippet())
            // TODO - .icon(yellowMarker) //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
    }

    companion object {
        val sampleData = listOf(
            BridgeGateMarker(43.18955, -73.58102, "Guard Gate Crockers Reef", "Ft Miller", 31.3, "CHamplain", "518-555-5555", "3.2", "2.3"),
            BridgeGateMarker(42.80340, -73.70890, "Guard Gate fake data", "Ft Miller!!", 31.2, "CHamplain", "518-555-5555", "3.2", "2.3")
        )
    }

}