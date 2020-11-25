package com.ayc.canalguide.utils.converters

import androidx.core.text.HtmlCompat
import com.tickaroo.tikxml.TypeConverter


class HtmlEscapeStringConverter : TypeConverter<String?> {

    @Throws(Exception::class)
    override fun read(s: String?): String {
        return if(s != null) HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim() else ""
    }

    @Throws(Exception::class)
    override fun write(s: String?): String {
        return if(s != null) HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim() else ""
    }

}