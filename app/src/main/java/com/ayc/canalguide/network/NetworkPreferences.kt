package com.ayc.canalguide.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.os.ConfigurationCompat
import java.text.SimpleDateFormat
import java.util.*


class NetworkPreferences(
    private val prefs: SharedPreferences,
    context: Context
) {


    val canalDateFormat = SimpleDateFormat("E, dd MMM yyyy hh:mm:ss zzz", ConfigurationCompat.getLocales(context.resources.configuration)[0])

    // Preference keys
    private val lockLastModifiedKey = "LOCK_LAST_MODIFIED_PREFS_KEY"//context.getString(R.string.pref_key_default_map_type)
    private val liftBridgeLastModifiedKey = "LIFTBRIDGE_LAST_MODIFIED_PREFS_KEY"
    private val guardGateLastModifiedKey = "GUARDGATE_LAST_MODIFIED_PREFS_KEY"


    // Cached filter states
    var lockLastModified: String
        get() = prefs.getString(lockLastModifiedKey, "Fri, 03 Jan 2000 16:49:05 GMT")!!
        set(value) = prefs.edit().putString(lockLastModifiedKey, value).apply()

    var lockLastModifiedDate: Date?
        get() = canalDateFormat.parse( prefs.getString(lockLastModifiedKey, "Fri, 03 Jan 2000 16:49:05 GMT")!! )
        set(value) = if (value != null) prefs.edit().putString(lockLastModifiedKey, canalDateFormat.format(value)).apply() else Unit

    var liftBridgeLastModifiedDate: Date?
        get() = canalDateFormat.parse( prefs.getString(liftBridgeLastModifiedKey, "Fri, 03 Jan 2000 16:49:05 GMT")!! )
        set(value) = if (value != null) prefs.edit().putString(liftBridgeLastModifiedKey, canalDateFormat.format(value)).apply() else Unit

    var guardGateLastModifiedDate: Date?
        get() = canalDateFormat.parse( prefs.getString(guardGateLastModifiedKey, "Fri, 03 Jan 2000 16:49:05 GMT")!! )
        set(value) = if (value != null) prefs.edit().putString(guardGateLastModifiedKey, canalDateFormat.format(value)).apply() else Unit



}