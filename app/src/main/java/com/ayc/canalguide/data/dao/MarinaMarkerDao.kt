package com.ayc.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.MarinaMarker


@Dao
interface MarinaMarkerDao: BaseDao<MarinaMarker> {

    @Query("SELECT * FROM marina_marker")
    fun getMarkers(): LiveData<List<MarinaMarker>>

    @Query("DELETE FROM marina_marker")
    suspend fun deleteAll()


    @Transaction
    suspend fun deleteAllAndInsert(objects: List<MarinaMarker>) {
        deleteAll()
        insert(objects)
    }

}