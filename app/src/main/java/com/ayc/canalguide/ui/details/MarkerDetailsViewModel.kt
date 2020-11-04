package com.ayc.canalguide.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ayc.canalguide.data.entities.*

class MarkerDetailsViewModel : ViewModel() {


    val mapMarker: LiveData<MapMarker> = TODO()

    fun hasPhoneNumber() = !phoneNumber.value.isNullOrBlank()
    val phoneNumber = Transformations.map(mapMarker) {
        when (it) {
            is LockMarker -> it.phone
            is MarinaMarker -> it.phone
            is BridgeGateMarker -> it.phone
            is CruiseMarker -> it.phone
            else -> ""
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

}