package com.example.myapplication.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.database.Product

class ProductViewModel : ViewModel() {
    val productList = MutableLiveData<MutableList<Product>>(mutableListOf())
    var totalCalories = 0

    fun addProduct(product: Product) {
        val currentList = productList.value ?: mutableListOf()
        currentList.add(product)
        productList.value = currentList
    }

    fun removeProduct(product: Product) {
        val currentList = productList.value ?: mutableListOf()
        currentList.remove(product)
        productList.value = currentList
    }

    fun addCaloriesToTotal(calories: Int) {
        totalCalories += calories
    }

    fun removeCaloriesFromTotal(calories: Int) {
        totalCalories -= calories
    }

    fun updateProductQuantity(product: Product, quantity: Int) {
        val updatedList = productList.value?.toMutableList() ?: mutableListOf()
        val index = updatedList.indexOf(product)
        if (index != -1) {
            updatedList[index].quantity = quantity
        }
        productList.value = updatedList
    }
}
