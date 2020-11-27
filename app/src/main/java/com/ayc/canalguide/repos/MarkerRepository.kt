package com.ayc.canalguide.repos

import android.util.Log
import androidx.lifecycle.liveData
import androidx.room.withTransaction
import com.ayc.canalguide.data.AppRoomDatabase
import com.ayc.canalguide.data.Constants
import com.ayc.canalguide.data.entities.*
import com.ayc.canalguide.network.CanalsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * This class will:
 *  Get liveData from database
 *  Get data from API, compare response header Last-Modified date to local Last-Modified date.
 *      If there are updates then update database
 *  Update last modified date in the database
 */
class MarkerRepository @Inject constructor(
    private val appDatabase: AppRoomDatabase,
    private val canalsApiService: CanalsApiService,
    private val canalDateFormat: SimpleDateFormat
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

        // Fetch data from two endpoints, liftbridges and guardgates
        withContext(Dispatchers.IO) {
            val bridgesDeferred = async { safeApiCallPair( { canalsApiService.getLiftBridges() }, "error in getLiftBridges") }
            val gatesDeferred = async { safeApiCallPair( { canalsApiService.getGuardGates() }, "error in getGuardGates") }
            val (bridges, bridgesLastModified) = bridgesDeferred.await() ?: return@withContext
            val (gates, gatesLastModified) = gatesDeferred.await() ?: return@withContext
            val bridgesNewLastModifiedDate = canalDateFormat.parse(bridgesLastModified)
            val gatesNewLastModifiedDate = canalDateFormat.parse(gatesLastModified)
            val bridgeAppLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiLiftBridges)
            val gatesAppLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiGuardGates)

            // If there are updates to liftBridges or guardGates, refresh table and updateApiSyncDates
            if (bridgesNewLastModifiedDate?.after(bridgeAppLastModifiedDate) != false
                    || gatesNewLastModifiedDate?.after(gatesAppLastModifiedDate) != false) appDatabase.withTransaction {
                Log.i("Repository","Updating liftbridges and guardgates")

                dao.deleteAllAndInsert(bridges.liftBridges + gates.guardGates)
                appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiLiftBridges, bridgesNewLastModifiedDate)
                appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiGuardGates, gatesNewLastModifiedDate)
            }
        }
    }

    fun loadLockMarkers() = liveData {
        val dao = appDatabase.lockDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val (locks, lastModified) = safeApiCallPair( { canalsApiService.getLocks() }, "error in getLocks") ?: return@withContext
            val newDataLastModifiedDate = canalDateFormat.parse(lastModified)
            val appDataLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiLocks)

            // If there are updates, refresh marker table and update dates in apiSyncDate table
            if (newDataLastModifiedDate?.after(appDataLastModifiedDate) != false) appDatabase.withTransaction {
                Log.i("Repository","Updating Locks")

                dao.deleteAllAndInsert(locks.markers)
                appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiLocks, newDataLastModifiedDate)
            }
        }
    }

    fun loadMarinaMarkers() = liveData {
        val dao = appDatabase.marinaDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val (marinas, lastModified) = safeApiCallPair( { canalsApiService.getMarinas() }, "error in getMarinas") ?: return@withContext
            val newDataLastModifiedDate = canalDateFormat.parse(lastModified)
            val appDataLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiMarinas)

            // If there are updates, refresh marker table and update dates in apiSyncDate table
            if (newDataLastModifiedDate?.after(appDataLastModifiedDate) != false) appDatabase.withTransaction {
                Log.i("Repository","Updating Marinas")

                dao.deleteAllAndInsert(marinas.markers)
                appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiMarinas, newDataLastModifiedDate)
            }
        }
    }

    fun loadLaunchMarkers() = liveData {
        val dao = appDatabase.launchDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val (launches, lastModified) = safeApiCallPair( { canalsApiService.getBoatLaunches() }, "error in getBoatLaunches") ?: return@withContext
            val newDataLastModifiedDate = canalDateFormat.parse(lastModified)
            val appDataLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiCanalWaterTrail)

            // If there are updates, refresh marker table and update dates in apiSyncDate table
            if (newDataLastModifiedDate?.after(appDataLastModifiedDate) != false) appDatabase.withTransaction {
                Log.i("Repository","Updating Launches")

                dao.deleteAllAndInsert(launches.markers)
                appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiCanalWaterTrail, newDataLastModifiedDate)
            }
        }
    }

    fun loadCruiseMarkers() = liveData {
        val dao = appDatabase.cruiseDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.IO) {
            val (cruises, lastModified) = safeApiCallPair( { canalsApiService.getRentalsCruises() }, "error in getRentalsCruises") ?: return@withContext
            val newDataLastModifiedDate = canalDateFormat.parse(lastModified)
            val appDataLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiBoatsForHire)

            // If there are updates, refresh marker table and update dates in apiSyncDate table
            if (newDataLastModifiedDate?.after(appDataLastModifiedDate) != false) appDatabase.withTransaction {
                Log.i("Repository","Updating Cruises")

                dao.deleteAllAndInsert(cruises.markers)
                appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiBoatsForHire, newDataLastModifiedDate)
            }
        }
    }

    fun loadNavInfoMarkers() = liveData {
        val dao = appDatabase.navInfoDao()
        emitSource( dao.getMarkers() )

        withContext(Dispatchers.Default) {
            // For each region launch a coroutine to sync each region's data
            Constants.navInfoRegions.forEachIndexed { regionIndex, regionName ->
                launch {
                    // Get the last modified date from the HEAD api
                    val newDataLastModified = safeApiHeadLastModified( { canalsApiService.navInfoHead(regionName) }, "error in navInfoHead($regionName)") ?: return@launch
                    val newDataLastModifiedDate = canalDateFormat.parse(newDataLastModified)
                    val appDataLastModifiedDate = appDatabase.apiSyncDateDao().getLastModifiedDate(Constants.apiNavInfo(regionName))

                    // If there are no updates available then return
                    if (newDataLastModifiedDate?.after(appDataLastModifiedDate) == false) return@launch
                    Log.i("Repository","Updating navinfo-$regionName")

                    // Get the fresh data for the region
                    val networkNavInfos = safeApiCall( { canalsApiService.getNavInfo(regionName) }, "error in getNavInfo($regionName)")?.markers ?: return@launch
                    val markers = networkNavInfos.map { it.toNavInfoMarker(regionIndex) }

                    // In one transaction, refresh this region's data and update date in apiSyncDate table
                    appDatabase.withTransaction {
                        dao.deleteApiRecordsAndInsert(regionIndex, markers)
                        appDatabase.apiSyncDateDao().updateApiSyncDates(Constants.apiNavInfo(regionName), newDataLastModifiedDate)
                    }
                }
            }
        }
    }

}