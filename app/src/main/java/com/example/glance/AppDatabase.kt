package com.example.glance
import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [Source::class, Item::class],
    version = 1,
    exportSchema=false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder<AppDatabase>(
                context.applicationContext,
                "app_database"
            )
                .setDriver(AndroidSQLiteDriver())
                .build()
        }
    }


}