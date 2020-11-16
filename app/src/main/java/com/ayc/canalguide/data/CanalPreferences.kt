package com.ayc.canalguide.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.ayc.canalguide.R
import com.ayc.canalguide.ui.settings.ImmerseMode

class CanalPreferences(
    private val prefs: SharedPreferences,
    context: Context
) {


    // Preference keys
    private val defaultMapTypeIndexKey = context.getString(R.string.pref_key_default_map_type)
    private val defaultCacheFiltersKey = context.getString(R.string.pref_key_cache_filters)
    private val fullscreenModeIndexKey = context.getString(R.string.pref_key_fullscreen_mode)

    private val lockFilterStateKey = context.getString(R.string.pref_key_filter_state_lock)
    private val bridgeFilterStateKey = context.getString(R.string.pref_key_filter_state_bridge)
    private val launchFilterStateKey = context.getString(R.string.pref_key_filter_state_launch)
    private val marinaFilterStateKey = context.getString(R.string.pref_key_filter_state_marina)
    private val cruiseFilterStateKey = context.getString(R.string.pref_key_filter_state_cruise)
    private val navinfoFilterStateKey = context.getString(R.string.pref_key_filter_state_navinfo)


    // Preferences set in settings fragment
    val defaultMapTypeIndex: String
        get() = prefs.getString(defaultMapTypeIndexKey, "0")!!

    val fullscreenMapModeIndex: String
        get() = prefs.getString(fullscreenModeIndexKey, ImmerseMode.Off.value)!!

    private val cacheFilters: Boolean
        get() = prefs.getBoolean(defaultCacheFiltersKey, false)


    // Cached filter states
    val cachedLockFilterState: Boolean?
        get() = if (cacheFilters) prefs.getBoolean(lockFilterStateKey, true) else null
        //private set(value) = prefs.edit().putBoolean(lockFilterStateKey, value).apply()

    val cachedBridgeFilterState: Boolean?
        get() = if (cacheFilters) prefs.getBoolean(bridgeFilterStateKey, true) else null

    val cachedLaunchFilterState: Boolean?
        get() = if (cacheFilters) prefs.getBoolean(launchFilterStateKey, true) else null

    val cachedMarinaFilterState: Boolean?
        get() = if (cacheFilters) prefs.getBoolean(marinaFilterStateKey, true) else null

    val cachedCruiseFilterState: Boolean?
        get() = if (cacheFilters) prefs.getBoolean(cruiseFilterStateKey, true) else null

    val cachedNavinfoFilterState: Boolean?
        get() = if (cacheFilters) prefs.getBoolean(navinfoFilterStateKey, false) else null


    fun updateCachedFilterStates(lock: Boolean, bridge: Boolean, launch: Boolean, marina: Boolean, cruise: Boolean, navinfo: Boolean) =
        prefs.edit(commit = true) {
            putBoolean(lockFilterStateKey, lock)
            putBoolean(bridgeFilterStateKey, bridge)
            putBoolean(launchFilterStateKey, launch)
            putBoolean(marinaFilterStateKey, marina)
            putBoolean(cruiseFilterStateKey, cruise)
            putBoolean(navinfoFilterStateKey, navinfo)
        }

}