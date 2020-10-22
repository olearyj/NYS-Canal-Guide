package com.AYC.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.AYC.canalguide.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml()
@Entity(tableName = "BridgeGateMarker")
data class BridgeGateMarker (
    @Attribute(name = "latitude")
    @Ignore
    override val lat: Double,
    @Attribute(name = "longitude")
    @Ignore
    override val lng: Double,
    @Attribute
    @Ignore
    override val name: String,
    @Attribute(name = "bodyofwater")
    @Ignore
    override val bodyOfWater: String,
    @Attribute
    @Ignore
    override val mile: Double,

    @Attribute
    val location: String?,
    @Attribute(name = "phonenumber")
    val phone: String?,
    @Attribute(name = "clearance_closed")
    val clearanceClosed: String?,
    @Attribute(name = "clearance_opened")
    val clearanceOpened: String?,
    @Attribute
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): MapMarker(lat, lng, name, bodyOfWater, mile) {


    override fun getMarkerOptions(): MarkerOptions? {
        return MarkerOptions()
            .title(name)
            .position(LatLng(lat, lng))
            .snippet(getSnippet())
            .icon(markerIcon) //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
    }

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_yellow_marker)

        val sampleData = listOf(
            BridgeGateMarker(43.18955, -73.58102, "Guard Gate Crockers Reef", "Ft Miller", 31.3, "CHamplain", "518-555-5555", "3.2", "2.3"),
            BridgeGateMarker(42.80340, -73.70890, "Guard Gate fake data", "Ft Miller!!", 31.2, "CHamplain", "518-555-5555", "3.2", "2.3")
        )
    }

}