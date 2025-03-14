package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class Page1Database : RoomDatabase() {
    abstract fun productDao(): Page1ProductDao

    companion object {
        @Volatile
        private var INSTANCE: Page1Database? = null

        fun getInstance(context: Context): Page1Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Page1Database::class.java,
                    "page1_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}