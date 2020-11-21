package com.ayc.canalguide.data.xml_classes

import com.ayc.canalguide.data.entities.CruiseMarker
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "cruises")
class Cruises {

    @Element(name = "cruise")
    lateinit var markers: List<CruiseMarker>

}