package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ayc.canalguide.R
import com.ayc.canalguide.utils.LatLongConverter
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

/*
<channelinfo mile="145.00" shore="west" feature="marina: Springer's" feature_url="http://www.springersmarina.com" feature_color=""
channel_width="300" south_west_depth="" middle_depth="" middle_depth_url="" north_east_depth="" overhead_clearance=""
noaa_page="chart viewer" noaa_page_url="http://www.charts.noaa.gov/OnLineViewer/12348.shtml" latitude="42.637241" longitude="-73.752825"/>
 */
@Xml()
@Entity(tableName = "navinfo_marker")
data class NavInfoMarker (
    @Attribute
    @Ignore
    override val markerId: Int = 0,
    @Attribute(name = "latitude", converter = LatLongConverter::class)
    @Ignore
    override val lat: Double,
    @Attribute(name = "longitude", converter = LatLongConverter::class)
    @Ignore
    override val lng: Double,
    @Attribute(name = "feature")
    @Ignore
    override val name: String,
    @Attribute(name = "bodyofwater")
    @Ignore
    override val bodyOfWater: String?,
    @Attribute
    @Ignore
    override val mile: String,

    @Attribute
    val shore: String?,
    @Attribute(name = "feature_url")
    val featureUrl: String?,
    @Attribute(name = "feature_color")
    val featureColor: String?,
    @Attribute(name = "channel_width")
    val channelWidth: String?,
    @Attribute(name = "south_west_depth")
    val southWestDepth: String?,
    @Attribute(name = "middle_depth")
    val middleDepth: String?,
    @Attribute(name = "middle_depth_url")
    val middleDepthUrl: String?,
    @Attribute(name = "north_east_depth")
    val northEastDepth: String?,
    @Attribute(name = "overhead_clearance")
    val overheadClearance: String?,
    @Attribute(name = "noaa_page")
    val noaaPage: String?,
    @Attribute(name = "noaa_page_url")
    val noaaPageUrl: String?
): MapMarker(lat, lng, name, bodyOfWater, mile, markerId) {


    override fun getMarkerOptions(): MarkerOptions? {
        return MarkerOptions()
                .title(getTitle())
                .position(LatLng(lat, lng))
                .snippet(getSnippet())
                .icon(getBitmapDescriptor())
    }

    //val bitmapDescriptor: BitmapDescriptor? by lazy {
    fun getBitmapDescriptor(): BitmapDescriptor? {
        val isBeaconLightOrLighthouse = name.contains("beacon", ignoreCase = true)
                || name.startsWith("light", ignoreCase = true)

        return when {
            featureColor.equals("green", ignoreCase = true) ->
                when {
                    name.contains("buoy", ignoreCase = true) -> greenBuoyIcon
                    isBeaconLightOrLighthouse -> greenBeaconIcon
                    else -> null
                }
            featureColor.equals("red", ignoreCase = true) ->
                when {
                    name.contains("buoy", ignoreCase = true) -> redBuoyIcon
                    isBeaconLightOrLighthouse -> redBeaconIcon
                    else -> null
                }
            isBeaconLightOrLighthouse -> otherBeaconIcon
            name.contains("bridge", ignoreCase = true) -> bridgeIcon
            else -> null
        }
    }

    companion object {
        private val greenBuoyIcon = BitmapDescriptorFactory.fromResource(R.drawable.mmi_green_buoy)
        private val redBuoyIcon = BitmapDescriptorFactory.fromResource(R.drawable.mmi_red_buoy)
        private val greenBeaconIcon = BitmapDescriptorFactory.fromResource(R.drawable.mmi_green_beacon)
        private val redBeaconIcon = BitmapDescriptorFactory.fromResource(R.drawable.mmi_red_beacon)
        private val otherBeaconIcon = BitmapDescriptorFactory.fromResource(R.drawable.mmi_other_beacon)
        private val bridgeIcon = BitmapDescriptorFactory.fromResource(R.drawable.mmi_bridge)
    }

}