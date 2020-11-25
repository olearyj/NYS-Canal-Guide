package com.ayc.canalguide.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.ApiSyncDate
import java.util.*

@Dao
interface ApiSyncDateDao: BaseDao<ApiSyncDate> {


    @Query("SELECT * FROM api_sync_date WHERE apiName=:apiName")
    suspend fun getRecord(apiName: String): ApiSyncDate

    @Query("SELECT lastModifiedDate FROM api_sync_date WHERE apiName=:apiName")
    suspend fun getLastModifiedDate(apiName: String): Date

//    @Query("SELECT lastSyncDate FROM api_sync_date WHERE apiName=:apiName")
//    suspend fun getLastSyncDate(apiName: String): Date

//    @Query("SELECT MIN(lastSyncDate) FROM api_sync_date")
//    suspend fun getOldestSyncDate(apiName: String): Date

    /**
     * Called when apiName table has been refreshed. Update last modified date and last sync date in database.
     *
     * For an apiName:
     *  If lastModifiedDate from the response is not null, update the date
     *  Update last sync date to current date
     */
    @Transaction
    suspend fun updateApiSyncDates(apiName: String, newDataLastModifiedDate: Date?) {
        val record = getRecord(apiName).apply {
            if (newDataLastModifiedDate != null)
                lastModifiedDate = newDataLastModifiedDate
            lastSyncDate = Date()
        }
        update(record)
    }


}