package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.Page3Database
import com.example.myapplication.database.Product
import com.example.myapplication.database.Page3ProductDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Page3ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao: Page3ProductDao = Page3Database.getInstance(application).productDao()
    private val _totalCalories = MutableStateFlow(0)
    val totalCalories: StateFlow<Int> = _totalCalories.asStateFlow()
    val totalCaloriesLiveData: LiveData<Int> = totalCalories.asLiveData()

    val productList: LiveData<List<Product>> = productDao.getAllProducts()

    init {
        // Initialize total calories from checked products
        viewModelScope.launch(Dispatchers.IO) {
            val products = productDao.getAllProductsSync()
            val total = products.filter { it.isChecked }.sumOf { it.calories * it.quantity }
            _totalCalories.value = total
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.insert(product)
            if (product.isChecked) {
                _totalCalories.value += product.calories * product.quantity
            }
        }
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            if (product.isChecked) {
                _totalCalories.value -= product.calories * product.quantity
            }
            productDao.delete(product)
        }
    }

    fun updateProductQuantity(product: Product, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (product.isChecked) {
                val oldCalories = product.calories * product.quantity
                val newCalories = product.calories * quantity
                _totalCalories.value = _totalCalories.value - oldCalories + newCalories
            }
            
            val updatedProduct = product.copy(quantity = quantity)
            productDao.update(updatedProduct)
        }
    }

    fun updateProductCheckedStatus(product: Product, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val caloriesChange = product.calories * product.quantity
            if (isChecked) {
                _totalCalories.value += caloriesChange
            } else {
                _totalCalories.value -= caloriesChange
            }
            
            val updatedProduct = product.copy(isChecked = isChecked)
            productDao.update(updatedProduct)
        }
    }

    fun decrementProductQuantity(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            if (product.quantity > 1) {
                val newQuantity = product.quantity - 1
                _totalCalories.value += product.calories // Всегда добавляем калории за один продукт
                val updatedProduct = product.copy(quantity = newQuantity)
                productDao.update(updatedProduct)
            }
        }
    }

    fun clearAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _totalCalories.value = 0
            productDao.deleteAll()
        }
    }
}