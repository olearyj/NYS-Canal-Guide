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
// <marina bodyofwater="hudson" mile="131.48" shore="west" marina="Donovan's Shady Harbor Marina" marina_url="http://www.shadyharbormarina.com/" phonenumber="(518) 756-8001" vhf="9,16,68" fuel="GD" repair="EHM" facilities="EWPRSLIC" latitude="42.450930" longitude="-73.786800"/>
@Xml()
@Entity(tableName = "marina_marker")
data class MarinaMarker (
    @Attribute
    @Ignore
    override val markerId: Int = 0,
    @Attribute(name = "latitude")
    @Ignore
    override val lat: Double,
    @Attribute(name = "longitude")
    @Ignore
    override val lng: Double,
    @Attribute(name = "marina")
    @Ignore
    override val name: String,
    @Attribute(name = "bodyofwater")
    @Ignore
    override val bodyOfWater: String,
    @Attribute
    @Ignore
    override val mile: String,

    @Attribute(name = "marina_url")
    val url: String?,
    @Attribute
    val vhf: String?,
    @Attribute
    val fuel: String?,
    @Attribute
    val repair: String?,
    @Attribute
    val facilities: String?,
    @Attribute(name = "phonenumber")
    val phone: String?
): MapMarker(lat, lng, name, bodyOfWater, mile, markerId) {


    override fun getMarkerOptions(): MarkerOptions? {
        return MarkerOptions()
                .title(getTitle())
                .position(LatLng(lat, lng))
                .snippet(getSnippet())
                .icon(markerIcon)
    }

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_blue_marker)
    }

}