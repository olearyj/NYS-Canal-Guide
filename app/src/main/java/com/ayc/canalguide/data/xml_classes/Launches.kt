package com.ayc.canalguide.data.xml_classes

import com.ayc.canalguide.data.entities.LaunchMarker
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "boatlaunches")
class Launches {

    @Element(name = "boatlaunch")
    lateinit var markers: List<LaunchMarker>

}