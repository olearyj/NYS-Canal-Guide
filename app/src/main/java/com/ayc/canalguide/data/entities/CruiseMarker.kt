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
// <cruise type="rentals" company="Angler's Bay" address="103 Drive 17" company_url="http://boatoneida.com/" city="Cleveland" state="NY" zip="13042" phonenumber="(315) 820-2628" vesseltypes="Sport, fishing, and pontoon boats" cruisetype="" homeport="Cleveland" waterways="Oneida Lake, Erie Canal (eastern)" latitude="43.224025" longitude="-75.832958"/>
@Xml()
@Entity(tableName = "cruises_marker")
data class CruiseMarker (
        @Attribute
        @Ignore
        override val markerId: Int = 0,
        @Attribute(name = "latitude")
        @Ignore
        override val lat: Double,
        @Attribute(name = "longitude")
        @Ignore
        override val lng: Double,
        @Attribute(name = "company")
        @Ignore
        override val name: String,
        @Attribute(name = "waterways")
        @Ignore
        override val bodyOfWater: String,
        @Attribute
        @Ignore
        override val mile: String?,

        @Attribute
        val homeport: String?,
        @Attribute
        val type: String?,
        @Attribute
        val cruisetype: String?,
        @Attribute(name = "vesseltypes")
        val vesselTypes: String?,
//        @Attribute
//        val waterways: String?,
        @Attribute(name = "company_url")
        val url: String?,
        @Attribute
        val address: String?,
        @Attribute
        val city: String?,
        @Attribute
        val state: String?,
        @Attribute
        val zip: String?,
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
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_violet_marker)
    }

}