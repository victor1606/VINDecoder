package com.example.vinexplorer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vinexplorer.data.model.DecodedVinEntity

/**
 * Room database for storing decoded VIN information
 */
@Database(
    entities = [DecodedVinEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VinDatabase : RoomDatabase() {

    abstract fun vinDao(): VinDao

    companion object {
        @Volatile
        private var INSTANCE: VinDatabase? = null

        fun getDatabase(context: Context): VinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VinDatabase::class.java,
                    "vin_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

