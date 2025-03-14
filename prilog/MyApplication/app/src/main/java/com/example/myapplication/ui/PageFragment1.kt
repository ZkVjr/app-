package com.example.myapplication.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentPage1Binding
import com.example.myapplication.database.Product

class PageFragment1 : Fragment() {

    private lateinit var saveButton: Button
    private lateinit var productNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productViewModel: Page1ProductViewModel

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage1Binding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(requireActivity()).get(Page1ProductViewModel::class.java)

        saveButton = binding.saveButton
        productNameEditText = binding.productNameEditText
        quantityEditText = binding.quantityEditText
        productListRecyclerView = binding.productListRecyclerView

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyFontSize()

        productAdapter = ProductAdapter(
            onDelete = { product -> removeProduct(product) },
            onCheck = { product -> handleCheck(product) },
            onMinus = { product -> handleMinus(product) },
            layoutType = PAGE_TYPE_1
        )
        productListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productListRecyclerView.adapter = productAdapter

        productViewModel.productList.observe(viewLifecycleOwner) { productList ->
            Log.d("PageFragment1", "Product list updated, size: ${productList.size}")
            productAdapter.submitList(productList)
            productAdapter.notifyDataSetChanged()
        }

        productNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        saveButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val quantityText = quantityEditText.text.toString()
            val quantity = quantityText.toIntOrNull()

            if (productName.isNotBlank() && quantity != null) {
                Log.d("PageFragment1", "Saving product: $productName with quantity: $quantity")
                val product = Product(name = productName, quantity = quantity, price = 0.0)
                productViewModel.addProduct(product) // Добавляем продукт в базу данных
                saveButton.text = "Сохранено"
            } else {
                Log.d("PageFragment1", "Invalid input: productName or quantity is blank or incorrect")
                Toast.makeText(requireContext(), "Ошибка: неверные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyFontSize() {
        val fontSize = sharedPreferences.getInt("font_size", 16).toFloat()

        val rootView: ViewGroup = requireView() as ViewGroup
        updateFontSizeInViewGroup(rootView, fontSize)
    }

    private fun updateFontSizeInViewGroup(viewGroup: ViewGroup, fontSize: Float) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)

            when (childView) {
                is EditText -> childView.textSize = fontSize
                is Button -> childView.textSize = fontSize
                is TextView -> childView.textSize = fontSize
                is RadioButton -> childView.textSize = fontSize
            }

            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
    }

    private fun validateInput() {
        val productName = productNameEditText.text.toString()
        val quantityText = quantityEditText.text.toString()
        val isInputValid = productName.isNotBlank() && quantityText.isNotBlank() && quantityText.toIntOrNull() != null

        saveButton.isEnabled = isInputValid
        Log.d("PageFragment1", "Validation status: $isInputValid")
    }

    private fun removeProduct(product: Product) {
        Log.d("PageFragment1", "Removing product: ${product.name}")
        productViewModel.removeProduct(product)
    }

    private fun handleCheck(product: Product) {
        Log.d("PageFragment1", "Handle check for product: ${product.name}")
        productAdapter.notifyDataSetChanged()
    }

    private fun handleMinus(product: Product) {
        Log.d("PageFragment1", "Handle minus for product: ${product.name}")
        if (product.quantity > 1) {
            product.quantity -= 1
            productAdapter.notifyDataSetChanged()
        }
    }
}