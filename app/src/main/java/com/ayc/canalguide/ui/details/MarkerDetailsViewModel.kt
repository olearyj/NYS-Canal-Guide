package com.ayc.canalguide.ui.details

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ayc.canalguide.data.entities.*
import com.ayc.canalguide.repos.MarkerRepository

class MarkerDetailsViewModel @ViewModelInject constructor(
        markerRepo: MarkerRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


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

    val markerDetails = Transformations.map(mapMarker) { mapMarker ->
        mutableListOf<String>().apply {
            add(mapMarker.getTitle())
            add(if (mapMarker !is NavInfoMarker) mapMarker.getSnippet() else mapMarker.getSnippet().replace(", ", "\n"))

            getAddress(mapMarker)?.let { address ->
                add("Address")
                add(address)
            }

            when (mapMarker) {
                //is LockMarker -> it.phone
                is MarinaMarker -> {
                    mapMarker.getFuelText()?.let {
                        add("Fuel")
                        add(it)
                    }
                    mapMarker.vhf?.let {
                        add("Vhf Channels")
                        add(it) //.replace(", *", ", "))
                    }
                    mapMarker.getFacilitiesText()?.let {
                        add("Facilities")
                        add(it)
                    }
                    mapMarker.getRepairText()?.let {
                        add("Repair")
                        add(it)
                    }
                }
                is CruiseMarker -> {
                    mapMarker.bodyOfWater.let {
                        add("Waterways")
                        add(it)
                    }
                }
                is BridgeGateMarker -> {
                    mapMarker.location?.let {
                        add("Location")
                        add(it)
                    }
                    mapMarker.getClearanceSubtext()?.let {
                        add("Clearance")
                        add(it)
                    }
                }
                is LaunchMarker -> {
                    mapMarker.launchType?.let {
                        add("Launch Type")
                        add(it)
                    }
                    mapMarker.getParkingSubtext()?.let {
                        add("Parking")
                        add(it)
                    }
                    mapMarker.dayUseAmenities?.let {
                        add("Day Use Amenities")
                        add(it.replace(", ", "\n"))
                    }
                    mapMarker.getFacilitiesSubtext()?.let {
                        add("Facilities / Utilities")
                        add(it)
                    }
                    mapMarker.getotherInfoSubtext()?.let {
                        add("Other Information")
                        add(it)
                    }
                }
                is NavInfoMarker -> {
                    mapMarker.getDepthSubtext()?.let {
                        add("Depths")
                        add(it)
                    }
                }
                else -> {}
            }
        }.toList()
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

}