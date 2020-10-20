package com.AYC.canalguide.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(objects:List<T>)

    /** Update an object in the database */
    @Update
    suspend fun update(obj: T)

    /** Delete an object in the database */
    @Delete
    suspend fun delete(obj: T)

}
