package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class Page3Database : RoomDatabase() {
    abstract fun productDao(): Page3ProductDao

    companion object {
        @Volatile
        private var INSTANCE: Page3Database? = null

        fun getInstance(context: Context): Page3Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Page3Database::class.java,
                    "page3_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}