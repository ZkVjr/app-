package com.example.myapplication.ui

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import java.util.*
import android.content.Intent
import android.widget.ImageButton

class SettingsFragment : Fragment() {

    private lateinit var themeToggleButton: ImageButton
    private lateinit var russianRadioButton: RadioButton
    private lateinit var englishRadioButton: RadioButton
    private lateinit var techSupportButton: Button
    private lateinit var smallFontSizeRadioButton: RadioButton
    private lateinit var mediumFontSizeRadioButton: RadioButton
    private lateinit var largeFontSizeRadioButton: RadioButton

    private lateinit var sharedPreferences: SharedPreferences

    private val TAG = "SettingsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        smallFontSizeRadioButton = view.findViewById(R.id.smallFontSizeRadioButton)
        mediumFontSizeRadioButton = view.findViewById(R.id.mediumFontSizeRadioButton)
        largeFontSizeRadioButton = view.findViewById(R.id.largeFontSizeRadioButton)

        val savedFontSize = sharedPreferences.getInt("font_size", 16)
        setFontSize(savedFontSize)

        smallFontSizeRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setFontSize(12)
            }
        }

        mediumFontSizeRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setFontSize(16)
            }
        }

        largeFontSizeRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setFontSize(20)
            }
        }

        russianRadioButton = view.findViewById(R.id.russianRadioButton)
        englishRadioButton = view.findViewById(R.id.englishRadioButton)
        val language = sharedPreferences.getString("language", "ru")
        if (language == "ru") {
            russianRadioButton.isChecked = true
        } else {
            englishRadioButton.isChecked = true
        }

        russianRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLanguage("ru")
            }
        }

        englishRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLanguage("en")
            }
        }

        techSupportButton = view.findViewById(R.id.techSupportButton)
        techSupportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/UraedaBot"))
            startActivity(intent)
        }

        themeToggleButton = view.findViewById(R.id.themeToggleButton)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        themeToggleButton.setImageResource(
            if (isDarkMode) R.drawable.ic_moon else R.drawable.ic_sun
        )

        themeToggleButton.setOnClickListener {
            val newMode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }

            AppCompatDelegate.setDefaultNightMode(newMode)
            sharedPreferences.edit().putBoolean("dark_mode", !isDarkMode).apply()

            themeToggleButton.setImageResource(
                if (isDarkMode) R.drawable.ic_sun else R.drawable.ic_moon
            )

            requireActivity().recreate()
        }
    }

    private fun setFontSize(size: Int) {
        sharedPreferences.edit().putInt("font_size", size).apply()

        applyFontSize()

        updateFontSizeInAllFragments()
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
                is RadioButton -> childView.textSize = fontSize
                is Button -> childView.textSize = fontSize
            }

            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
    }

    private fun updateFontSizeInAllFragments() {
        val activity = requireActivity()
        val fragments = activity.supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is FontSizeUpdatable) {
                fragment.applyFontSize()
            }
        }
    }

    private fun setLanguage(language: String) {
        val locale = if (language == "ru") Locale("ru") else Locale("en")
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        val context = requireContext()
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        sharedPreferences.edit().putString("language", language).apply()
        
        requireActivity().recreate()
    }
}