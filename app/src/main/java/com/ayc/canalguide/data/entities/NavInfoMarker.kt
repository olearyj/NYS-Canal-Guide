package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.TypeConverters
import com.ayc.canalguide.R
import com.ayc.canalguide.data.Constants
import com.ayc.canalguide.data.NavInfoType
import com.ayc.canalguide.utils.converters.NavInfoTypeConverter
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/*
<channelinfo mile="145.00" shore="west" feature="marina: Springer's" feature_url="http://www.springersmarina.com" feature_color=""
channel_width="300" south_west_depth="" middle_depth="" middle_depth_url="" north_east_depth="" overhead_clearance=""
noaa_page="chart viewer" noaa_page_url="http://www.charts.noaa.gov/OnLineViewer/12348.shtml" latitude="42.637241" longitude="-73.752825"/>
 */
@Entity(tableName = "navinfo_marker")
@TypeConverters(NavInfoTypeConverter::class)
data class NavInfoMarker (
        @Ignore
        override val markerId: Int = 0,
        @Ignore
        override val lat: Double,
        @Ignore
        override val lng: Double,
        @Ignore
        override val name: String,
        @Ignore
        override val bodyOfWater: String?,
        @Ignore
        override val mile: String,

        val shore: String?,
        val featureUrl: String?,
        val featureColor: String?,
        val channelWidth: String?,
        val southWestDepth: String?,
        val middleDepth: String?,
        val middleDepthUrl: String?,
        val northEastDepth: String?,
        val overheadClearance: String?,
        val noaaPage: String?,
        val noaaPageUrl: String?,
        val navInfoType: NavInfoType,
        val apiId: Int
): MapMarker(lat, lng, name, bodyOfWater, mile, markerId) {


    @Ignore
    val waterFlowEastWest = Constants.navInfoRegionsEastWestWaterflow.contains(apiId)


    fun getDepthSubtext(): String? {
        var subText = ""
        if (!northEastDepth.isNullOrBlank()) subText += "${if (waterFlowEastWest) "North" else "East"} Depth: $northEastDepth\n"
        if (!southWestDepth.isNullOrBlank()) subText += "${if (waterFlowEastWest) "South" else "West"} Depth: $southWestDepth\n"

        // Remove extra new line character
        return if(subText.isNotBlank()) subText.substring(0, subText.length - 1) else null
    }

    override fun getSnippet() = "Mile " + mile +  //(isNotBlank(southWestDepth) ? ", SW Depth=" + southWestDepth : "") +
            (if (isNotBlank(middleDepth)) ", Middle Depth=$middleDepth" else "") +  //(isNotBlank(northEastDepth) ? ", NE Depth=" + northEastDepth : "") +
            if (isNotBlank(overheadClearance)) ", Overhead Clearance=$overheadClearance" else ""

    override fun getMarkerOptions() = super.getMarkerOptions()?.icon( getBitmapDescriptor() )

    //val bitmapDescriptor: BitmapDescriptor? by lazy {
    private fun getBitmapDescriptor(): BitmapDescriptor? = when (navInfoType) {
        NavInfoType.GreenBuoy -> greenBuoyIcon
        NavInfoType.GreenBeacon -> greenBeaconIcon
        NavInfoType.RedBuoy -> redBuoyIcon
        NavInfoType.RedBeacon -> redBeaconIcon
        NavInfoType.OtherBeacon -> otherBeaconIcon
        NavInfoType.Bridge -> bridgeIcon
        NavInfoType.Unknown -> null
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