package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import com.ayc.canalguide.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
// <boatlaunch site_name="Corning Preserve Boat Ramp" waterway="Hudson River (tidal)" id="corning" municipality="City of Albany" launch_type="Ramp" parking="Cars (30) / Cars with Trailer (20)" overnight_parking="No" camping="No" potable_water="No" restrooms="Yes (Portable toilet)" day_use_amenities="Picnicking, Bike Path, Crew Rowing, Fishing" portage_distance="100' +/-" latitude="42.656532" longitude="-73.741678" mile="146.50" shore="west" bodyofwater="hudson"/>
@Xml()
@Entity(tableName = "launch_marker")
data class LaunchMarker (
    @Attribute
    @Ignore
    override val markerId: Int = 0,
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
    override val mile: String,

    @Attribute
    val waterway: String?,
    @Attribute(name = "id")
    val strId: String?,
    @Attribute
    val municipality: String?,
    @Attribute(name = "launch_type")
    val launchType: String?,
    @Attribute(name = "overnight_parking")
    val overnightParking: String?,
    @Attribute
    val camping: String?,
    @Attribute(name = "potable_water")
    val potableWater: String?,
    @Attribute
    val restrooms: String?,
    @Attribute(name = "day_use_amenities")
    val dayUseAmenities: String?,
    @Attribute(name = "portage_distance")
    val portageDistance: String?,
    @Attribute
    val shore: String?
): MapMarker(lat, lng, name, bodyOfWater, mile, markerId) {


    fun getParkingSubtext() = when {
        overnightParking.equals("yes", true) -> "Overnight Parking"
        overnightParking.equals("Yes, call", true) ->
            "Overnight Parking" + overnightParking!!.substring(3)
        else -> null
    }

    fun getFacilitiesSubtext(): String? {
        var subText = ""
        if (restrooms?.contains("yes", true) == true)
            subText += "Restrooms${restrooms.substring(3)}\n"
        if (potableWater?.contains("yes", true) == true)
            subText += "Potable Water\n"
        if (camping?.contains("yes", true) == true)
            subText += "Camping\n"

        // Remove extra new line character
        return if(subText.isNotBlank()) subText.substring(0, subText.length - 1) else null
    }

    fun getotherInfoSubtext(): String? {
        var subText = ""
        if (!municipality.isNullOrBlank())
            subText += "Municipality: $municipality\n"
        if (camping?.contains("yes", true) == true)
            subText += "Portage Distance: \n"

        // Remove extra new line character
        return if(subText.isNotBlank()) subText.substring(0, subText.length - 1) else null
    }

    override fun getTitle() = "Launch - $name"

    override fun getMarkerOptions() = super.getMarkerOptions()?.icon(markerIcon)

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_green_marker)
    }

}