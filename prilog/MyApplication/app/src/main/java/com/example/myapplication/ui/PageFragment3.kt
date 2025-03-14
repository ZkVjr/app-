package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.Product
import com.example.myapplication.databinding.FragmentPage3Binding
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import android.widget.Button
import android.widget.Toast
import com.example.myapplication.R
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle

class PageFragment3 : Fragment(), FontSizeUpdatable {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var totalCaloriesTextView: TextView
    private lateinit var productViewModel: Page3ProductViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage3Binding.inflate(inflater, container, false)

        productListRecyclerView = binding.productListRecyclerView
        totalCaloriesTextView = binding.totalCaloriesTextView

        productViewModel = ViewModelProvider(requireActivity()).get(Page3ProductViewModel::class.java)

        productAdapter = ProductAdapter(
            onDelete = { product -> removeProduct(product) },
            onCheck = { product -> toggleProductCheck(product) },
            onMinus = { product -> decrementProductQuantity(product) },
            layoutType = PAGE_TYPE_3
        )
        productListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productListRecyclerView.adapter = productAdapter

        productViewModel.productList.observe(viewLifecycleOwner, { productList ->
            productAdapter.submitList(productList)
        })

        // Observe total calories changes using Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.totalCalories.collect { calories ->
                    totalCaloriesTextView.text = getString(R.string.total_calories_text, calories)
                }
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clearAllButton = view.findViewById<Button>(R.id.clearAllButton)
        clearAllButton.setOnClickListener {
            productViewModel.clearAllProducts()
            Toast.makeText(requireContext(), getString(R.string.list_cleared), Toast.LENGTH_SHORT).show()
        }

        applyFontSize()
    }

    override fun onResume() {
        super.onResume()
        applyFontSize()
    }

    override fun applyFontSize() {
        val fontSize = sharedPreferences.getInt("font_size", 16).toFloat()
        updateFontSizeInViewGroup(requireView() as ViewGroup, fontSize)
        productAdapter.updateFontSize(fontSize)
    }

    private fun updateFontSizeInViewGroup(viewGroup: ViewGroup, fontSize: Float) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)

            when (childView) {
                is TextView -> {
                    childView.textSize = fontSize
                    childView.setLineSpacing(0f, 1.2f) // Add line spacing for better readability
                }
                is Button -> {
                    childView.textSize = fontSize * 0.8f // Make buttons slightly smaller
                }
            }

            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
    }

    private fun removeProduct(product: Product) {
        productViewModel.removeProduct(product)
    }

    private fun toggleProductCheck(product: Product) {
        val newCheckedState = !product.isChecked
        productViewModel.updateProductCheckedStatus(product, newCheckedState)
    }

    private fun decrementProductQuantity(product: Product) {
        productViewModel.decrementProductQuantity(product)
        productAdapter.incrementDecrementCount(product)
    }
}