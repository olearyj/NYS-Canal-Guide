package com.ayc.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.CruiseMarker


@Dao
interface CruiseMarkerDao: BaseDao<CruiseMarker> {

    @Query("SELECT * FROM cruises_marker WHERE markerId=:id")
    fun getMarker(id: Int): LiveData<CruiseMarker>

    @Query("SELECT * FROM cruises_marker")
    fun getMarkers(): LiveData<List<CruiseMarker>>

    @Query("DELETE FROM cruises_marker")
    suspend fun deleteAll()


    @Transaction
    suspend fun deleteAllAndInsert(objects: List<CruiseMarker>) {
        deleteAll()
        insert(objects)
    }

}