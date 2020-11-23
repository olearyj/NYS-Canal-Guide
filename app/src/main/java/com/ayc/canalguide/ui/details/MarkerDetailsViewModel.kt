package com.ayc.canalguide.ui.details

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ayc.canalguide.data.entities.*
import com.ayc.canalguide.repos.MarkerRepository


/**
 * This class will:
 *  Load the individual MapMarker based on the navArgs provided by the savedStateHandle
 *  Prepare data for buttons: phone number, website, noaa pdf website
 *  Prepare address string
 *  Prepare marker details as a list of strings
 */
class MarkerDetailsViewModel @ViewModelInject constructor(
        markerRepo: MarkerRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    // These arguments come from safeArgs / navArgs
    val mapMarker = markerRepo.loadMapMarker(
            savedStateHandle.get<Int>("markerId")!!,
            savedStateHandle.get<String>("javaClassSimpleName")!!
    )

    fun hasPhoneNumber() = !phoneNumber.value.isNullOrBlank()
    val phoneNumber = Transformations.map(mapMarker) {
        when (it) {
            is MarinaMarker -> it.phone
            is LockMarker -> it.phone
            is BridgeGateMarker -> it.phone
            is CruiseMarker -> it.phone
            else -> null
        }?.replace("[^0-9]".toRegex(), "")
    }

    fun hasWebsite() = !website.value.isNullOrBlank()
    val website = Transformations.map(mapMarker) {
        when (it) {
            is MarinaMarker -> it.url
            is CruiseMarker -> it.url
            is NavInfoMarker -> it.featureUrl
            else -> ""
        }
    }

    fun hasWebsiteNoaa() = !(mapMarker.value as? NavInfoMarker)?.noaaPageUrl.isNullOrBlank()
    val websiteNoaa = Transformations.map(mapMarker) {
        (it as? NavInfoMarker)?.noaaPageUrl
    }

    private fun getAddress(mapMarker: MapMarker): String? {
        val strings = when (mapMarker) {
            is LockMarker -> arrayOf(mapMarker.address, mapMarker.city, mapMarker.zip)
            is CruiseMarker -> arrayOf(mapMarker.address, mapMarker.city, mapMarker.zip)
            else -> return null
        }
        return if ( strings.all { !it.isNullOrBlank() } )
            String.format("%s\n%s, NY %s", *strings)
        else null
    }

    /**
     * Every string will be it's own textView. Every other will be a header.
     * Handle dirty data such as nulls and empty strings when building this list of strings.
     */
    val markerDetails = Transformations.map(mapMarker) { mapMarker ->
        mapMarker ?: return@map emptyList<String>()

        mutableListOf<String>().apply {
            // Add title and snippet
            add(mapMarker.getTitle())
            add(if (mapMarker !is NavInfoMarker) mapMarker.getSnippet() else mapMarker.getSnippet().replace(", ", "\n"))

            getAddress(mapMarker)?.let {
                if (it.isBlank()) return@let
                add("Address")
                add(it)
            }

            when (mapMarker) {
                is LockMarker -> {}
                is MarinaMarker -> {
                    mapMarker.getFuelText()?.let {
                        if (it.isBlank()) return@let
                        add("Fuel")
                        add(it)
                    }
                    if(!mapMarker.vhf.isNullOrBlank()) {
                        add("Vhf Channels")
                        add(mapMarker.vhf)
                    }
                    mapMarker.getFacilitiesText()?.let {
                        if (it.isBlank()) return@let
                        add("Facilities")
                        add(it)
                    }
                    mapMarker.getRepairText()?.let {
                        if (it.isBlank()) return@let
                        add("Repair")
                        add(it)
                    }
                }
                is CruiseMarker -> {
                    add("Waterways")
                    add(mapMarker.bodyOfWater)
                }
                is BridgeGateMarker -> {
                    if(!mapMarker.location.isNullOrBlank()) {
                        add("Location")
                        add(mapMarker.location)
                    }
                    mapMarker.getClearanceSubtext()?.let {
                        if (it.isBlank()) return@let
                        add("Clearance")
                        add(it)
                    }
                }
                is LaunchMarker -> {
                    if(!mapMarker.launchType.isNullOrBlank()) {
                        add("Launch Type")
                        add(mapMarker.launchType)
                    }
                    mapMarker.getParkingSubtext()?.let {
                        if (it.isBlank()) return@let
                        add("Parking")
                        add(it)
                    }
                    if(!mapMarker.dayUseAmenities.isNullOrBlank()) {
                        add("Day Use Amenities")
                        add(mapMarker.dayUseAmenities.replace(", ", "\n"))
                    }
                    mapMarker.getFacilitiesSubtext()?.let {
                        if (it.isBlank()) return@let
                        add("Facilities / Utilities")
                        add(it)
                    }
                    mapMarker.getotherInfoSubtext()?.let {
                        if (it.isBlank()) return@let
                        add("Other Information")
                        add(it)
                    }
                }
                is NavInfoMarker -> {
                    mapMarker.getDepthSubtext()?.let {
                        if (it.isBlank()) return@let
                        add("Depths")
                        add(it)
                    }
                }
                else -> {}
            }
        }.toList()
    }

}