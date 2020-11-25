package com.ayc.canalguide.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "api_sync_date")
data class ApiSyncDate (
    val apiName: String,
    var lastModifiedDate: Date,
    var lastSyncDate: Date,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)