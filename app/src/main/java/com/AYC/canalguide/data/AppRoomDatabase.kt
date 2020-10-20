package com.AYC.canalguide.data


import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.AYC.canalguide.data.dao.BridgeGateMarkerDao
import com.AYC.canalguide.data.entities.BridgeGateMarker


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [
        BridgeGateMarker::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun markerDao(): BridgeGateMarkerDao

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