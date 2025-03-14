package com.example.myapplication.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.UserDao
import com.example.myapplication.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.myapplication.R

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDao: UserDao
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getInstance(requireContext())
        userDao = db.userDao()
        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", 0)

        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        updateThemeButton(isDarkMode)

        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)

        binding.themeToggleButton.setOnClickListener {
            val newMode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }

            AppCompatDelegate.setDefaultNightMode(newMode)
            sharedPreferences.edit().putBoolean("dark_mode", !isDarkMode).apply()
            updateThemeButton(!isDarkMode)
        }

        binding.showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            binding.password.inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.password.setSelection(binding.password.text.length)
        }

        binding.loginButton.setOnClickListener {
            val login = "@" + binding.login.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (login == "@admin" && password == "admin123") {
                Toast.makeText(requireContext(), "Добро пожаловать, Администратор!", Toast.LENGTH_SHORT).show()
                (activity as? AuthActivity)?.addAdminTab()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val user = userDao.authenticate(login, password)
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            Toast.makeText(requireContext(), "Вход успешен!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Ошибка: неправильные данные!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateThemeButton(isDarkMode: Boolean) {
        val themeIcon = if (isDarkMode) R.drawable.ic_moon else R.drawable.ic_sun
        binding.themeToggleButton.setImageResource(themeIcon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}