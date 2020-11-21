package com.ayc.canalguide.utils

import androidx.room.TypeConverter
import com.ayc.canalguide.data.NavInfoType

class Converters {

    @TypeConverter
    fun toNavInfoType(value: Int) = NavInfoType[value]

    @TypeConverter
    fun fromNavInfoType(value: NavInfoType) = value.ordinal

}