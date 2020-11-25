package com.ayc.canalguide.data.xml_classes

import com.ayc.canalguide.data.NavInfoType
import com.ayc.canalguide.data.entities.MapMarker
import com.ayc.canalguide.data.entities.NavInfoMarker
import com.ayc.canalguide.utils.converters.LatLongConverter
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

/*
<channelinfo mile="145.00" shore="west" feature="marina: Springer's" feature_url="http://www.springersmarina.com" feature_color=""
channel_width="300" south_west_depth="" middle_depth="" middle_depth_url="" north_east_depth="" overhead_clearance=""
noaa_page="chart viewer" noaa_page_url="http://www.charts.noaa.gov/OnLineViewer/12348.shtml" latitude="42.637241" longitude="-73.752825"/>
 */
@Xml()
data class NetworkNavInfoMarker (
        @Attribute
        override val markerId: Int = 0,
        @Attribute(name = "latitude", converter = LatLongConverter::class)
        override val lat: Double,
        @Attribute(name = "longitude", converter = LatLongConverter::class)
        override val lng: Double,
        @Attribute(name = "feature")
        override val name: String,
        @Attribute(name = "bodyofwater")
        override val bodyOfWater: String?,
        @Attribute
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


    private fun getNavInfoType(): NavInfoType {
        val isBeaconLightOrLighthouse = name.contains("beacon", ignoreCase = true)
                || name.startsWith("light", ignoreCase = true)

        return when {
            featureColor.equals("green", ignoreCase = true) ->
                when {
                    name.contains("buoy", ignoreCase = true) -> NavInfoType.GreenBuoy
                    isBeaconLightOrLighthouse -> NavInfoType.GreenBeacon
                    else -> NavInfoType.Unknown
                }
            featureColor.equals("red", ignoreCase = true) ->
                when {
                    name.contains("buoy", ignoreCase = true) -> NavInfoType.RedBuoy
                    isBeaconLightOrLighthouse -> NavInfoType.RedBeacon
                    else -> NavInfoType.Unknown
                }
            isBeaconLightOrLighthouse -> NavInfoType.OtherBeacon
            name.contains("bridge", ignoreCase = true) -> NavInfoType.Bridge
            else -> NavInfoType.Unknown
        }
    }

    fun toNavInfoMarker(apiId: Int): NavInfoMarker {
        return NavInfoMarker(0, lat, lng, name, bodyOfWater, mile, shore, featureUrl, featureColor, channelWidth, southWestDepth, middleDepth, middleDepthUrl, northEastDepth, overheadClearance, noaaPage, noaaPageUrl, getNavInfoType(), apiId)
    }

}