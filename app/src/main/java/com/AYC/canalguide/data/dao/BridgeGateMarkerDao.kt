package com.AYC.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.AYC.canalguide.data.entities.BridgeGateMarker


@Dao
interface BridgeGateMarkerDao: BaseDao<BridgeGateMarker> {

    @Query("SELECT * FROM BridgeGateMarker")
    fun getMarkers(): LiveData<List<BridgeGateMarker>>

    @Query("DELETE FROM BridgeGateMarker")
    suspend fun deleteAll()


    @Transaction
    suspend fun deleteAllAndInsert(objects: List<BridgeGateMarker>) {
        deleteAll()
        insert(objects)
    }

}