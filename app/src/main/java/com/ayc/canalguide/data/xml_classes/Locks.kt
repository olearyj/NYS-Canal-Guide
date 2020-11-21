package com.ayc.canalguide.data.xml_classes

import com.ayc.canalguide.data.entities.LockMarker
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "locks")
class Locks {

    @Element(name = "lock")
    lateinit var markers: List<LockMarker>

}