package com.ayc.canalguide.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ayc.canalguide.data.entities.LockMarker


@Dao
interface LockMarkerDao: BaseDao<LockMarker> {

    @Query("SELECT * FROM lock_marker WHERE markerId=:id")
    fun getMarker(id: Int): LiveData<LockMarker>

    @Query("SELECT * FROM lock_marker")
    fun getMarkers(): LiveData<List<LockMarker>>

    @Query("DELETE FROM lock_marker")
    suspend fun deleteAll()


    @Transaction
    suspend fun deleteAllAndInsert(objects: List<LockMarker>) {
        deleteAll()
        insert(objects)
    }

}