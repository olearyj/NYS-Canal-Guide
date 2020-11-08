package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import com.ayc.canalguide.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
// <marina bodyofwater="hudson" mile="131.48" shore="west" marina="Donovan's Shady Harbor Marina" marina_url="http://www.shadyharbormarina.com/" phonenumber="(518) 756-8001" vhf="9,16,68" fuel="GD" repair="EHM" facilities="EWPRSLIC" latitude="42.450930" longitude="-73.786800"/>
@Xml()
@Entity(tableName = "marina_marker")
data class MarinaMarker(
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


    override fun getMarkerOptions() = super.getMarkerOptions()?.icon(markerIcon)

    fun getFuelText() = if (!fuel.isNullOrBlank())
        when {
            fuel.contains("G") && fuel.contains("D") -> "Gas & Diesel"
            fuel.contains("G") -> "Gas"
            fuel.contains("D") -> "Diesel"
            else -> null
    } else null

    fun getRepairText(): String? {
        repair ?: return null

        var repairText = ""
        if( repair.contains("E") ) repairText += "Electrical\n"
        if( repair.contains("H") ) repairText += "Hull\n"
        if( repair.contains("M") ) repairText += "Mechanical\n"
        if( repair.contains("S") ) repairText += "Mast Stepping\n"
        if( repair.contains("T") ) repairText += "Towing\n"

        // Remove extra new line character
        return if (repairText.isNotEmpty()) repairText.substring(0, repairText.length - 1) else null
    }

    fun getFacilitiesText(): String? {
        facilities ?: return null
        var facilitiesText = ""
        val chars = charArrayOf('E', 'W', 'P', 'R', 'S', 'L', 'I', 'C')
        val strings = arrayOf("Electrical", "Water", "Pumpout", "Restrooms", "Showers", "Laundry", "Wi-Fi", "Cable")
        for (i in chars.indices)
            if (facilities.contains(chars[i]))
                facilitiesText += strings[i] + "\n"

        // Remove extra new line character
        return if (facilitiesText.isNotEmpty()) facilitiesText.substring(0, facilitiesText.length - 1) else null
    }

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_blue_marker)
    }

}