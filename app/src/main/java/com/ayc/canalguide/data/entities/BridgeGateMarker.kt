package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import com.ayc.canalguide.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml()
@Entity(tableName = "bridge_gate_marker")
data class BridgeGateMarker (
    @Attribute
    @Ignore
    override val markerId: Int = 0,
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
    override val mile: String,

    @Attribute
    val location: String?,
    @Attribute(name = "phonenumber")
    val phone: String?,
    @Attribute(name = "clearance_closed")
    val clearanceClosed: String?,
    @Attribute(name = "clearance_opened")
    val clearanceOpened: String?
): MapMarker(lat, lng, name, bodyOfWater, mile, markerId) {


    override fun getMarkerOptions() = super.getMarkerOptions()?.icon(markerIcon)

    fun getClearanceSubtext(): String? {
        val cc = clearanceClosed
        val co = clearanceOpened?.replace("999", "Unlimited")

        var subText = ""
        if (cc != "-1" && cc != null) subText += "Closed clearance: $cc\n"
        if (co != "-1" && co != null) subText += "Opened clearance: $co\n"

        // Remove extra new line character
        return if(subText.isNotBlank()) subText.substring(0, subText.length - 1) else null
    }

    companion object {
        val markerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mmi_yellow_marker)
    }

}