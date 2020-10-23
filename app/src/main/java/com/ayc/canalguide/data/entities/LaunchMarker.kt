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
// <boatlaunch site_name="Corning Preserve Boat Ramp" waterway="Hudson River (tidal)" id="corning" municipality="City of Albany" launch_type="Ramp" parking="Cars (30) / Cars with Trailer (20)" overnight_parking="No" camping="No" potable_water="No" restrooms="Yes (Portable toilet)" day_use_amenities="Picnicking, Bike Path, Crew Rowing, Fishing" portage_distance="100' +/-" latitude="42.656532" longitude="-73.741678" mile="146.50" shore="west" bodyofwater="hudson"/>
@Xml()
@Entity(tableName = "launch_marker")
data class LaunchMarker (
        @Attribute(name = "latitude")
        @Ignore
        override val lat: Double,
        @Attribute(name = "longitude")
        @Ignore
        override val lng: Double,
        @Attribute(name = "site_name")
        @Ignore
        override val name: String,
        @Attribute(name = "bodyofwater")
        @Ignore
        override val bodyOfWater: String,
        @Attribute
        @Ignore
        override val mile: Double,

        @Attribute
        val waterway: String?,
        @Attribute
        val id: String?,
        @Attribute
        val municipality: String?,
        @Attribute
        val launch_type: String?,
        @Attribute
        val overnight_parking: String?,
        @Attribute
        val camping: String?,
        @Attribute
        val potable_water: String?,
        @Attribute
        val restrooms: String?,
        @Attribute
        val day_use_amenities: String?,
        @Attribute
        val shore: String?,
        @Attribute
        @PrimaryKey(autoGenerate = true)
        val launchId: Int = 0
): MapMarker(lat, lng, name, bodyOfWater, mile) {


    override fun getMarkerOptions(): MarkerOptions? {
        return MarkerOptions()
                .title(name)
                .position(LatLng(lat, lng))
                .snippet(getSnippet())
                .icon(markerIcon)
    }

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_green_marker)
    }

}