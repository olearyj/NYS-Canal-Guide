package com.ayc.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.NavInfoMarker


@Dao
interface NavInfoMarkerDao: BaseDao<NavInfoMarker> {

    @Query("SELECT * FROM navinfo_marker WHERE markerId=:id")
    fun getMarker(id: Int): LiveData<NavInfoMarker>

    @Query("SELECT * FROM navinfo_marker")
    fun getMarkers(): LiveData<List<NavInfoMarker>>

    @Query("DELETE from navinfo_marker WHERE apiId=:apiId")
    suspend fun delete(apiId: Int)

    @Transaction
    suspend fun deleteApiRecordsAndInsert(apiId: Int, objects: List<NavInfoMarker>) {
        delete(apiId)
        insert(objects)
    }

}