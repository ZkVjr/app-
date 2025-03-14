package com.example.myapplication.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.myapplication.database.Product
import com.example.myapplication.databinding.FragmentPage4Binding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.Locale

class PageFragment4 : Fragment(), FontSizeUpdatable {

    private lateinit var productNameEditText: EditText
    private lateinit var expirationDateEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var saveProductButton: Button
    private lateinit var scannerButton: Button

    private lateinit var productViewModel: Page3ProductViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), "Сканирование отменено", Toast.LENGTH_SHORT).show()
        } else {
            val scannedProductInfo = result.contents.split(";")
            if (scannedProductInfo.size >= 2) {
                productNameEditText.setText(scannedProductInfo[0])
                expirationDateEditText.setText(scannedProductInfo[1])
            } else {
                Toast.makeText(requireContext(), "Ошибка: неверный штрихкод", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPage4Binding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(requireActivity()).get(Page3ProductViewModel::class.java)

        productNameEditText = binding.productNameEditText
        expirationDateEditText = binding.expirationDateEditText
        caloriesEditText = binding.caloriesEditText
        quantityEditText = binding.quantityEditText
        saveProductButton = binding.saveProductButton
        scannerButton = binding.scannerButton

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        setupTextWatchers()
        setupClickListeners()

        return binding.root
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        }

        productNameEditText.addTextChangedListener(textWatcher)
        expirationDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
                val dateText = editable.toString()
                if (dateText.length == 2 || dateText.length == 5) {
                    expirationDateEditText.append(".")
                }
            }
        })
        caloriesEditText.addTextChangedListener(textWatcher)
        quantityEditText.addTextChangedListener(textWatcher)
    }

    private fun setupClickListeners() {
        saveProductButton.setOnClickListener {
            saveProduct()
        }

        scannerButton.setOnClickListener {
            startScanner()
        }
    }

    override fun onResume() {
        super.onResume()
        applyFontSize()
    }

    override fun applyFontSize() {
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
            }
            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
    }

    private fun validateInput() {
        val isValid = productNameEditText.text.isNotBlank() &&
                expirationDateEditText.text.isNotBlank()
        saveProductButton.isEnabled = isValid
    }

    private fun saveProduct() {
        val name = productNameEditText.text.toString()
        val expirationDateStr = expirationDateEditText.text.toString()
        val calories = caloriesEditText.text.toString().toIntOrNull()
        val quantity = quantityEditText.text.toString().toIntOrNull()

        if (name.isBlank() || expirationDateStr.isBlank()) {
            Toast.makeText(requireContext(), "Ошибка: все обязательные поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            return
        }

        val expirationDate = parseExpirationDate(expirationDateStr)
        if (expirationDate == null) {
            Toast.makeText(requireContext(), "Ошибка: неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(name = name, expirationDate = expirationDate, calories = calories ?: 0, quantity = quantity ?: 1)
        productViewModel.addProduct(product)

        Toast.makeText(requireContext(), "Продукт сохранен!", Toast.LENGTH_SHORT).show()
        clearInputFields()
    }

    private fun parseExpirationDate(dateString: String): Long? {
        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time
        } catch (e: Exception) {
            null
        }
    }

    private fun startScanner() {
        val options = ScanOptions()
        options.setPrompt("Сканируйте штрихкод")
        options.setBeepEnabled(true)
        barcodeLauncher.launch(options)
    }

    private fun clearInputFields() {
        productNameEditText.text.clear()
        expirationDateEditText.text.clear()
        caloriesEditText.text.clear()
        quantityEditText.text.clear()
        productNameEditText.requestFocus()
    }
}