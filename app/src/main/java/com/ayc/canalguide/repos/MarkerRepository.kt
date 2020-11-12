package com.ayc.canalguide.repos

import androidx.lifecycle.liveData
import com.ayc.canalguide.Constants
import com.ayc.canalguide.data.AppRoomDatabase
import com.ayc.canalguide.data.entities.*
import com.ayc.canalguide.network.CanalsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarkerRepository @Inject constructor(
    private val appDatabase: AppRoomDatabase,
    private val canalsApiService: CanalsApiService
) : BaseRepository() {


    fun loadMapMarker(markerId: Int, javaClassSimpleName: String) =
            when (javaClassSimpleName) {
                MarinaMarker::class.java.simpleName -> appDatabase.marinaDao().getMarker(markerId)
                LockMarker::class.java.simpleName -> appDatabase.lockDao().getMarker(markerId)
                LaunchMarker::class.java.simpleName -> appDatabase.launchDao().getMarker(markerId)
                CruiseMarker::class.java.simpleName -> appDatabase.cruiseDao().getMarker(markerId)
                BridgeGateMarker::class.java.simpleName -> appDatabase.bridgeGateDao().getMarker(markerId)
                NavInfoMarker::class.java.simpleName -> appDatabase.navInfoDao().getMarker(markerId)
                else -> throw Exception("loadMapMarker parameter is not a MapMarker class name: $javaClassSimpleName")
            }

    fun loadBridgeGateMarkers() = liveData {
        val dao = appDatabase.bridgeGateDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val liftBridges = async { safeApiCall( { canalsApiService.getLiftBridges() }, "error") }
            val guardGates = async { safeApiCall( { canalsApiService.getGuardGates() }, "error") }

            // Await markers and return if either are null since we don't want to delete the table unless we get a result for both
            val liftBridgeMarkers = liftBridges.await()?.liftBridges ?: return@withContext
            val guardGateMarkers = guardGates.await()?.guardGates ?: return@withContext

            dao.deleteAllAndInsert(liftBridgeMarkers + guardGateMarkers)
        }
    }

    fun loadLockMarkers() = liveData {
        val dao = appDatabase.lockDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val markers = safeApiCall( { canalsApiService.getLocks() }, "error")?.markers ?: return@withContext
            dao.deleteAllAndInsert(markers)
        }
    }

    fun loadMarinaMarkers() = liveData {
        val dao = appDatabase.marinaDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val markers = safeApiCall( { canalsApiService.getMarinas() }, "error")?.markers ?: return@withContext
            dao.deleteAllAndInsert(markers)
        }
    }

    fun loadLaunchMarkers() = liveData {
        val dao = appDatabase.launchDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val markers = safeApiCall( { canalsApiService.getBoatLaunches() }, "error")?.markers ?: return@withContext
            dao.deleteAllAndInsert(markers)
        }
    }

    fun loadCruiseMarkers() = liveData {
        val dao = appDatabase.cruiseDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val markers = safeApiCall( { canalsApiService.getRentalsCruises() }, "error")?.markers ?: return@withContext
            dao.deleteAllAndInsert(markers)
        }
    }

    fun loadNavInfoMarkers() = liveData {
        val dao = appDatabase.navInfoDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val results = Constants.navInfoRegions.map {
                regionName -> async { safeApiCall( { canalsApiService.getNavInfo(regionName) }, "error") }
            }.awaitAll()

            // TODO - use last update date field

            // Return if either are null since we don't want to delete the table unless we get a result for both
            val markers = results.flatMap { navInfoMarkers -> navInfoMarkers?.markers ?: return@withContext }

            dao.deleteAllAndInsert(markers)
        }
    }

}