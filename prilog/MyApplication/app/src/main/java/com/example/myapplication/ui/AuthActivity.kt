package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAuthBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.UserDao
import android.util.Log
import com.example.myapplication.R
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var adapter: AuthPagerAdapter
    private var isAdminTabAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AuthPagerAdapter(this)
        binding.viewPager.adapter = adapter

        setupTabs()
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Вход"
                    tab.setIcon(R.drawable.ic_login)
                }
                1 -> {
                    tab.text = "Регистрация"
                    tab.setIcon(R.drawable.ic_register)
                }
                2 -> if (isAdminTabAdded) {
                    tab.text = "Администрация"
                    tab.setIcon(R.drawable.ic_admin)
                }
                else -> {}
            }
        }.attach()
    }

    fun addAdminTab() {
        if (!isAdminTabAdded) {
            Log.d("AuthActivity", "Adding Admin Tab")

            adapter.addAdminTab()
            binding.viewPager.adapter = adapter
            isAdminTabAdded = true

            setupTabs()
        }
    }
}