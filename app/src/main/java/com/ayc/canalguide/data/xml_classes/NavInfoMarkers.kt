package com.ayc.canalguide.data.xml_classes

import com.ayc.canalguide.data.entities.LockMarker
import com.ayc.canalguide.data.entities.NavInfoMarker
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/*

<navigationinfo xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="navinfo-schema.xsd">
<lastupdatetime>2020-01-03</lastupdatetime>
<canal>Hudson River (mileages from NYC)</canal>
<depth>13</depth>
<clearance>26</clearance>
<channelinfo....
 */
@Xml(name = "navigationinfo")
class NavInfoMarkers {

    @PropertyElement(name = "lastupdatetime")
    lateinit var lastUpdateDate: String

    @PropertyElement()
    lateinit var canal: String

    @PropertyElement()
    lateinit var depth: String

    @PropertyElement()
    lateinit var clearance: String

    @Element(name = "channelinfo")
    lateinit var markers: List<NavInfoMarker>

}