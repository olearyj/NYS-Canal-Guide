package com.ayc.canalguide.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ayc.canalguide.data.dao.*
import com.ayc.canalguide.data.entities.*
import com.ayc.canalguide.utils.converters.DateConverter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [
        ApiSyncDate::class,
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
@TypeConverters(DateConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun apiSyncDateDao(): ApiSyncDateDao
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
                addCallback(roomDbCallback)
            }.build()

        /** Pre-populate the database - add records to the ApiSyncDate table */
        private val roomDbCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                GlobalScope.launch {
                    val earliestDate = Date(0L)
                    val navInfoRecords = Constants.navInfoRegions.map { regionName -> ApiSyncDate(Constants.apiNavInfo(regionName), earliestDate, earliestDate) }
                    val records = mutableListOf<ApiSyncDate>().apply {
                        add(ApiSyncDate(Constants.apiLocks, earliestDate, earliestDate))
                        add(ApiSyncDate(Constants.apiLiftBridges, earliestDate, earliestDate))
                        add(ApiSyncDate(Constants.apiGuardGates, earliestDate, earliestDate))
                        add(ApiSyncDate(Constants.apiBoatsForHire, earliestDate, earliestDate))
                        add(ApiSyncDate(Constants.apiCanalWaterTrail, earliestDate, earliestDate))
                        add(ApiSyncDate(Constants.apiMarinas, earliestDate, earliestDate))
                        addAll(navInfoRecords)
                    }

                    instance?.apiSyncDateDao()?.insert(records)
                }
            }
        }
    }
}