package com.ayc.canalguide.utils.converters

import androidx.room.TypeConverter
import com.ayc.canalguide.data.NavInfoType

class NavInfoTypeConverter {
    @TypeConverter
    fun toNavInfoType(value: Int) = NavInfoType[value]

    @TypeConverter
    fun fromNavInfoType(value: NavInfoType) = value.ordinal
}