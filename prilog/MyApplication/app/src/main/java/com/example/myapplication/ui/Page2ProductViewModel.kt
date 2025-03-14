package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.Product
import com.example.myapplication.database.ProductDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Page2ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao: ProductDao = AppDatabase.getInstance(application).productDao()

    val productList: LiveData<List<Product>> = productDao.getAllProducts()

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.insert(product)
        }
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.delete(product)
        }
    }

    fun updateProductQuantity(product: Product, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedProduct = product.copy(quantity = quantity)
            productDao.update(updatedProduct)
        }
    }

    fun clearAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.deleteAll()
        }
    }
}