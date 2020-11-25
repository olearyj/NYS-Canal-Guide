package com.ayc.canalguide.data

/**
 * Constant values that won't be used in XML
 */
object Constants {
    const val BASE_URL = "http://www.canals.ny.gov/xml/"
    val navInfoRegions = listOf(
        "hudsonriver", "champlain", "fortedward", "erieeastern", "frankfortharbor",
        "uticaharbor", "oswego", "eriecentral", "onondagalake", "cayugaseneca",
        "cayugalake", "senecalake", "eriewestern", "geneseeriver", "ellicottcreek"
    )
    // These regions flow east to west
    val navInfoRegionsEastWestWaterflow = arrayOf(
            navInfoRegions.indexOf("fortedward"),
            navInfoRegions.indexOf("eriewestern"),
            navInfoRegions.indexOf("erieeastern"),
            navInfoRegions.indexOf("eriecentral")
    )
    const val apiLocks = "locks"
    const val apiLiftBridges = "liftbridges"
    const val apiGuardGates = "guardgates"
    const val apiBoatsForHire = "boatsforhire"
    const val apiCanalWaterTrail = "canalwatertrail"
    const val apiMarinas = "marinas"
    fun apiNavInfo(regionName: String) = "navinfo-$regionName"
}