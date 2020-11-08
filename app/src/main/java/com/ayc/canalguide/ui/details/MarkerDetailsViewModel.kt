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

            /*

		if( !isBlank(marina.getFuel()) ){
        	addTextView("Fuel");
        	addTextView(getFuelString(marina.getFuel()));
    	}

    	if( !isBlank(marina.getVhf()) ){
        	addTextView("Vhf Channels");
        	addTextView(marina.getVhf().replaceAll(", *", ", "));
    	}

    	if( !isBlank(marina.getFacilities()) ){
        	addTextView("Facilities");
        	addTextView(getFacilitiesString(marina.getFacilities()));
    	}

    	if(  !isBlank(marina.getRepair()) ){
        	addTextView("Repair");
        	addTextView(getRepairString(marina.getRepair()));
    	}
             */

            when (mapMarker) {
                //is LockMarker -> it.phone
                is MarinaMarker -> {
                }
//                is CruiseMarker -> it.url
//                is BridgeGateMarker -> it.phone
//                is LaunchMarker -> {}
//                is NavInfoMarker -> it.featureUrl
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