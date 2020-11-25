package com.ayc.canalguide.utils

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long): Date = Date(dateLong)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time
}