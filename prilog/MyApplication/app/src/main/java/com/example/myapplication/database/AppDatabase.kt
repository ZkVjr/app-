package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Создаем таблицу пользователей
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `users` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `login` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `password` TEXT NOT NULL
            )
        """)

        // Создаем таблицу продуктов
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `products` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `quantity` INTEGER NOT NULL DEFAULT 1,
                `price` REAL NOT NULL DEFAULT 0.0,
                `expirationDate` INTEGER NOT NULL DEFAULT 0,
                `calories` INTEGER NOT NULL DEFAULT 0,
                `icon` INTEGER NOT NULL DEFAULT 0,
                `isChecked` INTEGER NOT NULL DEFAULT 0,
                `type` TEXT NOT NULL DEFAULT 'Unknown'
            )
        """)

        // Добавляем администратора
        database.execSQL("""
            INSERT INTO users (login, name, password)
            VALUES ('@admin', 'Administrator', 'admin123')
        """)
    }
}

@Database(entities = [User::class, Product::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}