package com.example.myapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "Unknown",
    var quantity: Int = 1,
    val price: Double = 0.0,
    val expirationDate: Long = 0L,
    var calories: Int = 0,
    val icon: Int = 0,
    var isChecked: Boolean = false,
    val type: String = "Unknown",
)