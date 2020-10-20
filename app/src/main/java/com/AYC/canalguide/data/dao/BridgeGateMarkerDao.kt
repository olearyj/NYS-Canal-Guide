package com.AYC.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.AYC.canalguide.data.entities.BridgeGateMarker


@Dao
interface BridgeGateMarkerDao: BaseDao<BridgeGateMarker> {

    @Query("SELECT * FROM BridgeGateMarker")
    fun getMarkers(): LiveData<List<BridgeGateMarker>>

}