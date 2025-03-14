package com.example.myapplication.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface Page3ProductDao {

    @Query("SELECT * FROM products")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products")
    suspend fun getAllProductsSync(): List<Product>

    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}