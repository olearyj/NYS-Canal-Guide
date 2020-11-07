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

}