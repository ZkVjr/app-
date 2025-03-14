package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemProductBinding
import com.example.myapplication.databinding.ItemProductPage1Binding
import com.example.myapplication.databinding.ItemProductPage2Binding
import com.example.myapplication.database.Product
import com.example.myapplication.R
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import java.util.Calendar
import java.util.Locale

const val PAGE_TYPE_1 = 1
const val PAGE_TYPE_2 = 2
const val PAGE_TYPE_3 = 3

class ProductAdapter(
    private val onDelete: (Product) -> Unit,
    private val onCheck: (Product) -> Unit,
    private val onMinus: (Product) -> Unit,
    private val layoutType: Int,
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private var totalCalories = 0
    private var lastResetDate: Long = System.currentTimeMillis()
    private var currentFontSize: Float = 16f
    private var decrementCounts: MutableMap<String, Int> = mutableMapOf()

    class ProductViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onDelete: (Product) -> Unit, onCheck: (Product) -> Unit, onMinus: (Product) -> Unit, fontSize: Float, decrementCount: Int) {
            when (binding) {
                is ItemProductPage1Binding -> {
                    binding.productNameTextView.text = product.name
                    binding.quantityTextView.text = "${product.quantity} шт"
                    binding.productNameTextView.textSize = fontSize
                    binding.quantityTextView.textSize = fontSize

                    binding.deleteButton.setOnClickListener { onDelete(product) }
                }

                is ItemProductPage2Binding -> {
                    binding.productNameTextView.text = product.name
                    binding.priceTextView.text = "${product.price} руб"
                    binding.productNameTextView.textSize = fontSize
                    binding.priceTextView.textSize = fontSize
                    binding.deleteButton.setOnClickListener { onDelete(product) }
                }

                is ItemProductBinding -> {
                    binding.productNameTextView.text = product.name
                    binding.quantityTextView.text = "${product.quantity} шт"
                    binding.productNameTextView.textSize = fontSize
                    binding.quantityTextView.textSize = fontSize
                    binding.caloriesTextView.textSize = fontSize * 0.9f
                    binding.expirationTextView.textSize = fontSize * 0.9f

                    if (product.quantity <= 1) {
                        binding.minusButton.setBackgroundColor(Color.GRAY)
                    } else {
                        binding.minusButton.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.purple_500))
                    }

                    val expirationDays = getDaysUntilExpiration(product.expirationDate)
                    if (expirationDays <= -7) {
                        onDelete(product)
                    }
                    if (expirationDays <= 0) {
                        binding.expirationTextView.text = "Просрочено"
                        binding.productContainer.setBackgroundColor(Color.RED)
                    } else {
                        binding.expirationTextView.text = "$expirationDays дней"
                    }

                    if (product.calories > 0) {
                        val totalCalories = product.calories * product.quantity
                        binding.caloriesTextView.text = binding.root.context.getString(R.string.calories_hint) + ": ${product.calories} " + binding.root.context.getString(R.string.calories_suffix) + " (всего: $totalCalories)"
                        binding.caloriesTextView.visibility = View.VISIBLE
                    } else {
                        binding.caloriesTextView.visibility = View.GONE
                    }

                    val productType = getProductType(product.name)
                    val iconResource = when (productType) {
                        "Fruit" -> R.drawable.fruit_icon
                        "Vegetable" -> R.drawable.vegetable_icon
                        else -> R.drawable.default_product_icon
                    }
                    binding.productIcon.setImageResource(iconResource)

                    if (product.isChecked) {
                        binding.productContainer.setBackgroundColor(Color.GREEN)
                    } else {
                        if (expirationDays <= 0) {
                            binding.expirationTextView.text = "Просрочено"
                            binding.productContainer.setBackgroundColor(Color.RED)
                        } else {
                            binding.expirationTextView.text = "$expirationDays дней"
                            binding.productContainer.setBackgroundColor(Color.WHITE)
                        }
                    }

                    binding.deleteButton.setOnClickListener { onDelete(product) }
                    binding.checkButton.setOnClickListener { onCheck(product) }
                    binding.minusButton.setOnClickListener { onMinus(product) }
                }
            }
        }

        private fun getDaysUntilExpiration(expirationDate: Long): Int {
            val currentTime = System.currentTimeMillis()
            val diffInMillis = expirationDate - currentTime
            return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        }

        private fun getProductType(productName: String): String {
            val fruitKeywords = listOf("яблоко", "банан", "груша", "вишня", "персик", "fruit", "apple", "banana", "pear", "cherry", "peach")
            val vegetableKeywords = listOf("морковь", "картофель", "помидор", "огурец", "капуста", "carrot", "potato", "tomato", "cucumber", "cabbage")

            val lowerCaseProductName = productName.lowercase(Locale.getDefault())

            if (fruitKeywords.any { lowerCaseProductName.contains(it) }) {
                return "Fruit"
            }

            if (vegetableKeywords.any { lowerCaseProductName.contains(it) }) {
                return "Vegetable"
            }

            return "Other"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (layoutType) {
            PAGE_TYPE_1 -> ItemProductPage1Binding.inflate(inflater, parent, false)
            PAGE_TYPE_2 -> ItemProductPage2Binding.inflate(inflater, parent, false)
            else -> ItemProductBinding.inflate(inflater, parent, false)
        }
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, onDelete, onCheck, onMinus, currentFontSize, decrementCounts[product.name] ?: 0)
    }

    fun updateFontSize(fontSize: Float) {
        currentFontSize = fontSize
        notifyDataSetChanged()
    }

    internal fun resetDailyFields() {
        val currentDate = System.currentTimeMillis()
        val currentDay = getDayOfYear(currentDate)
        val lastDay = getDayOfYear(lastResetDate)

        if (currentDay != lastDay) {
            currentList.forEach {
                it.isChecked = false
                it.calories = 0
            }
            submitList(currentList)
            lastResetDate = currentDate
        }
    }

    private fun getDayOfYear(date: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun updateCalories(calories: Int) {
        totalCalories += calories
    }

    fun removeProduct(product: Product) {
        val currentList = currentList.toMutableList()
        currentList.remove(product)
        submitList(currentList)
        decrementCounts.remove(product.name)
    }

    fun addProduct(product: Product) {
        val currentList = currentList.toMutableList()
        currentList.add(product)
        submitList(currentList)
    }

    fun incrementDecrementCount(product: Product) {
        val currentCount = decrementCounts[product.name] ?: 0
        decrementCounts[product.name] = currentCount + 1
        notifyDataSetChanged()
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}