package com.ayc.canalguide.data


enum class NavInfoType {
    GreenBuoy,
    GreenBeacon,
    RedBuoy,
    RedBeacon,
    OtherBeacon,
    Bridge,
    Unknown;

    companion object {
        private val map = values().associateBy(NavInfoType::ordinal)
        operator fun get(value: Int) = map[value] ?: Unknown
    }
}