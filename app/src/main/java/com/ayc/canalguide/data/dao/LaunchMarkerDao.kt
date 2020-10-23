package com.ayc.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.LaunchMarker


@Dao
interface LaunchMarkerDao: BaseDao<LaunchMarker> {

    @Query("SELECT * FROM launch_marker")
    fun getMarkers(): LiveData<List<LaunchMarker>>

    @Query("DELETE FROM launch_marker")
    suspend fun deleteAll()


    @Transaction
    suspend fun deleteAllAndInsert(objects: List<LaunchMarker>) {
        deleteAll()
        insert(objects)
    }

}