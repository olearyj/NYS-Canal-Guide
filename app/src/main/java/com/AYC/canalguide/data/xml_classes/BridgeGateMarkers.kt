package com.AYC.canalguide.data.xml_classes

import com.AYC.canalguide.data.entities.BridgeGateMarker
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "liftbridges")
class LiftBridges {

    @Element(name = "liftbridge")
    lateinit var liftBridges: List<BridgeGateMarker>

}

@Xml(name = "guardgates")
class GuardGates {

    @Element(name = "guardgate")
    lateinit var guardGates: List<BridgeGateMarker>

}