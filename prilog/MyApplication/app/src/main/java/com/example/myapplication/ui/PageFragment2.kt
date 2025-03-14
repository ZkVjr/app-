package com.example.myapplication.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentPage2Binding
import com.example.myapplication.database.Product
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.example.myapplication.R

class PageFragment2 : Fragment(), FontSizeUpdatable {
    private lateinit var saveButton: Button
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var scannerButton: Button
    private lateinit var addProductButton: Button
    private lateinit var clearAllButton: Button
    private lateinit var totalPriceTextView: TextView
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var totalPrice = 0.0
    private lateinit var productViewModel: Page2ProductViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_CODE = 1234

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), "Сканирование отменено", Toast.LENGTH_SHORT).show()
        } else {
            val barcode = result.contents
            val (productName, price) = getProductData(barcode)

            if (price > 0) {
                productNameEditText.setText(productName)
                productPriceEditText.setText(price.toString())
            } else {
                Toast.makeText(requireContext(), "Товар не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d("PageFragment2", "onCreateView called")
        val binding = FragmentPage2Binding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(requireActivity()).get(Page2ProductViewModel::class.java)

        productNameEditText = binding.productNameEditText
        productPriceEditText = binding.productPriceEditText
        scannerButton = binding.scannerButton
        addProductButton = binding.addProductButton
        clearAllButton = binding.clearAllButton
        totalPriceTextView = binding.totalPriceTextView
        productListRecyclerView = binding.productListRecyclerView

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PageFragment2", "Метод onViewCreated вызван")

        applyFontSize()

        productAdapter = ProductAdapter(
            onDelete = { product -> removeProduct(product) },
            onCheck = { product -> handleCheck(product) },
            onMinus = { product -> handleMinus(product) },
            layoutType = PAGE_TYPE_2
        )
        productListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productListRecyclerView.adapter = productAdapter

        productViewModel.productList.observe(viewLifecycleOwner, { productList ->
            Log.d("PageFragment2", "Список продуктов обновлен, размер: ${productList.size}")
            productAdapter.submitList(productList)
            productAdapter.notifyDataSetChanged()
            updateTotalPrice(productList)
        })

        productNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        productPriceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        scannerButton.setOnClickListener {
            startScanner()
        }

        clearAllButton.setOnClickListener {
            clearAllProducts()
        }

        addProductButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val price = productPriceEditText.text.toString().toDoubleOrNull()
            val quantity = 1

            if (price != null) {
                val product = Product(name = productName, quantity = quantity, price = price)
                addProduct(product)
                clearInputFields()
            } else {
                Toast.makeText(requireContext(), "Ошибка: неверная цена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun applyFontSize() {
        val fontSize = sharedPreferences.getInt("font_size", 14).toFloat()
        val rootView: ViewGroup = requireView() as ViewGroup
        updateFontSizeInViewGroup(rootView, fontSize)
    }

    private fun updateFontSizeInViewGroup(viewGroup: ViewGroup, fontSize: Float) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)

            when (childView) {
                is EditText -> childView.textSize = fontSize
                is Button -> childView.textSize = fontSize
                is RadioButton -> childView.textSize = fontSize
                is TextView -> childView.textSize = fontSize
            }

            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
    }

    private fun validateInput() {
        val isInputValid = productPriceEditText.text.isNotBlank()
        addProductButton.isEnabled = isInputValid
    }

    private fun addProduct(product: Product) {
        Log.d("PageFragment2", "Adding product: ${product.name}, Price: ${product.price}")
        productViewModel.addProduct(product)
        updateTotalPrice(productViewModel.productList.value ?: emptyList())
    }

    private fun startScanner() {
        val options = ScanOptions()
        options.setPrompt("Сканируйте штрихкод")
        options.setBeepEnabled(true)
        barcodeLauncher.launch(options)
    }

    private fun getProductData(barcode: String): Pair<String, Double> {
        val productDatabase = mapOf(
            "2200000000019" to Pair("Дырокол маленький Expert", 420.00),
            "2200000000020" to Pair("Товар 2", 350.00),
            "2200000000021" to Pair("Товар 3", 500.00)
        )
        return productDatabase[barcode] ?: Pair("Неизвестный товар", 0.0)
    }

    private fun removeProduct(product: Product) {
        Log.d("PageFragment2", "Удаление продукта: ${product.name}")
        productViewModel.removeProduct(product)
        val updatedList = productViewModel.productList.value ?: emptyList()
        updateTotalPrice(updatedList)
        productAdapter.notifyDataSetChanged()
    }

    private fun handleCheck(product: Product) {
        Log.d("PageFragment2", "Проверка продукта: ${product.name}")
        productAdapter.notifyDataSetChanged()
    }

    private fun handleMinus(product: Product) {
        Log.d("PageFragment2", "Уменьшение количества продукта: ${product.name}")
        if (product.quantity > 1) {
            product.quantity -= 1
            productViewModel.updateProductQuantity(product, product.quantity)
        }
    }

    private fun updateTotalPrice(productList: List<Product>) {
        totalPrice = productList.sumOf { it.price * it.quantity }
        totalPriceTextView.text = String.format("%.2f руб", totalPrice)
    }

    private fun clearInputFields() {
        productNameEditText.text.clear()
        productPriceEditText.text.clear()
        productNameEditText.requestFocus()
    }

    private fun clearAllProducts() {
        productViewModel.clearAllProducts()
        updateTotalPrice(emptyList())
        Toast.makeText(requireContext(), "Список очищен", Toast.LENGTH_SHORT).show()
    }
}