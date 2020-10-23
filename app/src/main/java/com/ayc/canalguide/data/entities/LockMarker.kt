package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ayc.canalguide.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
// <lock latitude="42.75145" longitude="-73.68540" name="Troy Federal Lock*" lift="14±2.5' ↑NB/↓SB" attachment="Pipes & Cables" address="1 Bond St" city="Troy" state="NY" zip="12180" mile="153.90" bodyofwater="Hudson River" phonenumber="(518) 272-6442"/>
@Xml()
@Entity(tableName = "lock_marker")
data class LockMarker (
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
    val lift: String?,
    @Attribute
    val address: String?,
    @Attribute
    val city: String?,
    @Attribute
    val state: String?,
    @Attribute
    val zip: String?,
    @Attribute(name = "phonenumber")
    val phone: String?,
    @Attribute
    @PrimaryKey(autoGenerate = true)
    val lockId: Int = 0
): MapMarker(lat, lng, name, bodyOfWater, mile) {


    override fun getMarkerOptions(): MarkerOptions? {
        return MarkerOptions()
            .title(name)
            .position(LatLng(lat, lng))
            .snippet(getSnippet())
            .icon(markerIcon)
    }

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_red_marker)

        val sampleData = listOf(
            BridgeGateMarker(43.18955, -73.58102, "Guard Gate Crockers Reef", "Ft Miller", 31.3, "CHamplain", "518-555-5555", "3.2", "2.3"),
            BridgeGateMarker(42.80340, -73.70890, "Guard Gate fake data", "Ft Miller!!", 31.2, "CHamplain", "518-555-5555", "3.2", "2.3")
        )
    }

}