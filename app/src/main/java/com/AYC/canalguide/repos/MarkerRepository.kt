package com.AYC.canalguide.repos

import android.util.Log
import androidx.lifecycle.liveData
import com.AYC.canalguide.data.AppRoomDatabase
import com.AYC.canalguide.data.entities.BridgeGateMarker
import com.AYC.canalguide.data.xml_classes.LiftBridges
import com.AYC.canalguide.network.CanalsApiService
import com.tickaroo.tikxml.TikXml
import kotlinx.coroutines.*
import okio.buffer
import okio.source
import javax.inject.Inject

class MarkerRepository @Inject constructor(
    private val appDatabase: AppRoomDatabase,
    private val canalsApiService: CanalsApiService
) : BaseRepository() {

    fun loadSampleBridgeGateMarkers() = liveData {
        emit(BridgeGateMarker.sampleData)
    }

    fun loadBridgeGateMarkers() = liveData {
        emitSource( appDatabase.markerDao().getMarkers() )
        withContext(Dispatchers.IO) {
            launch {
                val liftBridges = async { safeApiCall( { canalsApiService.getLiftBridges() }, "error") }
                val guardGates = async { safeApiCall( { canalsApiService.getGuardGates() }, "error") }

                // Await markers and concatenate
                // Return if either are null since we don't want to delete the table unless we get a result for both
                val markers1 = liftBridges.await()?.liftBridges ?: return@launch
                val markers2 = guardGates.await()?.guardGates ?: return@launch

                appDatabase.markerDao().deleteAllAndInsert(markers1 + markers2)
            }
        }
    }

}