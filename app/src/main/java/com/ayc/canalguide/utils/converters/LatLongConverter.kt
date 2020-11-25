package com.ayc.canalguide.utils.converters

import com.tickaroo.tikxml.TypeConverter

/**
 * Some lat / long data from the endpoint has trailing whitespace so a custom converter from string to double is a must
 */
class LatLongConverter: TypeConverter<Double> {

    override fun read(value: String?): Double {
        return value!!.replace("[^0-9.\\-]".toRegex(),"").toDouble()
    }

    override fun write(value: Double?): String {
        return value.toString()
    }

}