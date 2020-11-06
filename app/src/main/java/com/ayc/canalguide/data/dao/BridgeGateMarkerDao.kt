package com.ayc.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.BridgeGateMarker


@Dao
interface BridgeGateMarkerDao: BaseDao<BridgeGateMarker> {

    @Query("SELECT * FROM bridge_gate_marker WHERE markerId=:id")
    fun getMarker(id: Int): LiveData<BridgeGateMarker>

    @Query("SELECT * FROM bridge_gate_marker")
    fun getMarkers(): LiveData<List<BridgeGateMarker>>

    @Query("DELETE FROM bridge_gate_marker")
    suspend fun deleteAll()


    @Transaction
    suspend fun deleteAllAndInsert(objects: List<BridgeGateMarker>) {
        deleteAll()
        insert(objects)
    }

}