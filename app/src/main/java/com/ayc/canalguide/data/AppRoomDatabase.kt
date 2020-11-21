package com.ayc.canalguide.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ayc.canalguide.data.dao.*
import com.ayc.canalguide.data.entities.*


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [
        BridgeGateMarker::class,
        LockMarker::class,
        MarinaMarker::class,
        CruiseMarker::class,
        LaunchMarker::class,
        NavInfoMarker::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun bridgeGateDao(): BridgeGateMarkerDao
    abstract fun lockDao(): LockMarkerDao
    abstract fun marinaDao(): MarinaMarkerDao
    abstract fun cruiseDao(): CruiseMarkerDao
    abstract fun launchDao(): LaunchMarkerDao
    abstract fun navInfoDao(): NavInfoMarkerDao

    companion object {

        @Volatile
        private var instance: AppRoomDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppRoomDatabase::class.java, "nys_canal_guide_database.db").apply {

                // TODO - use preloaded database?

            }.build()

    }
}