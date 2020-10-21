package com.AYC.canalguide.repos

import android.util.Log
import androidx.lifecycle.liveData
import com.AYC.canalguide.data.AppRoomDatabase
import com.AYC.canalguide.data.entities.BridgeGateMarker
import com.AYC.canalguide.network.CanalsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarkerRepository @Inject constructor(
    private val appDatabase: AppRoomDatabase,
    private val canalsApiService: CanalsApiService
) : BaseRepository() {

    //fun loadBridgeGateMarkers() = appDatabase.markerDao().getMarkers()
    fun loadBridgeGateMarkers() = liveData {
        emit(BridgeGateMarker.sampleData)
    }


    fun loadBridgeGateMarkers2() = liveData {
        emitSource( appDatabase.markerDao().getMarkers() )
        withContext(Dispatchers.IO) {
            launch {

                Log.i("", "IM HEREEEEE")
                //val a = canalsApiService.getLiftBridgesText()
                //Log.i("", canalsApiService.getLiftBridgesText())

                val result = safeApiCall( { canalsApiService.getLiftBridges() }, "error")
                //logi("API result count is ${result?.count() ?: "null"}")

                if (result != null)
                    appDatabase.markerDao().insert(result.liftBridges)
            }
        }
    }

}