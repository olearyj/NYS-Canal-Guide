package com.ayc.canalguide.data.dao

import androidx.room.*

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
