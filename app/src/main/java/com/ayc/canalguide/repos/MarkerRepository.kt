package com.ayc.canalguide.repos

import androidx.lifecycle.liveData
import com.ayc.canalguide.Constants
import com.ayc.canalguide.data.AppRoomDatabase
import com.ayc.canalguide.data.entities.BridgeGateMarker
import com.ayc.canalguide.network.CanalsApiService
import kotlinx.coroutines.*
import javax.inject.Inject

class MarkerRepository @Inject constructor(
    private val appDatabase: AppRoomDatabase,
    private val canalsApiService: CanalsApiService
) : BaseRepository() {

    fun loadSampleBridgeGateMarkers() = liveData {
        emit(BridgeGateMarker.sampleData)
    }

    fun loadBridgeGateMarkers() = liveData {
        val dao = appDatabase.bridgeGateDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            launch {
                val liftBridges = async { safeApiCall( { canalsApiService.getLiftBridges() }, "error") }
                val guardGates = async { safeApiCall( { canalsApiService.getGuardGates() }, "error") }

                // Await markers and concatenate
                // Return if either are null since we don't want to delete the table unless we get a result for both
                val markers1 = liftBridges.await()?.liftBridges ?: return@launch
                val markers2 = guardGates.await()?.guardGates ?: return@launch

                dao.deleteAllAndInsert(markers1 + markers2)
            }
        }
    }

    fun loadLockMarkers() = liveData {
        val dao = appDatabase.lockDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            launch {
                val markers = safeApiCall( { canalsApiService.getLocks() }, "error")?.markers ?: return@launch
                dao.deleteAllAndInsert(markers)
            }
        }
    }

    fun loadMarinaMarkers() = liveData {
        val dao = appDatabase.marinaDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            launch {
                val markers = safeApiCall( { canalsApiService.getMarinas() }, "error")?.markers ?: return@launch
                dao.deleteAllAndInsert(markers)
            }
        }
    }

    fun loadLaunchMarkers() = liveData {
        val dao = appDatabase.launchDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            launch {
                val markers = safeApiCall( { canalsApiService.getBoatLaunches() }, "error")?.markers ?: return@launch
                dao.deleteAllAndInsert(markers)
            }
        }
    }

    fun loadCruiseMarkers() = liveData {
        val dao = appDatabase.cruiseDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            launch {
                val markers = safeApiCall( { canalsApiService.getRentalsCruises() }, "error")?.markers ?: return@launch
                dao.deleteAllAndInsert(markers)
            }
        }
    }


    fun loadNavInfoMarkers() = liveData {
        val dao = appDatabase.navInfoDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            launch {
                val results = Constants.navInfoRegions.map {
                    regionName -> async { safeApiCall( { canalsApiService.getNavInfo(regionName) }, "error") }
                }.awaitAll()

                // TODO - use last update date field

                // Return if either are null since we don't want to delete the table unless we get a result for both
                val markers = results.flatMap { navInfoMarkers -> navInfoMarkers?.markers ?: return@launch }

                dao.deleteAllAndInsert(markers)
            }
        }
    }

}