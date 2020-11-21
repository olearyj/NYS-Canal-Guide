package com.ayc.canalguide.data.xml_classes

import com.ayc.canalguide.data.entities.MarinaMarker
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "marinas")
class Marinas {

    @Element(name = "marina")
    lateinit var markers: List<MarinaMarker>

}