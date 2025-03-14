package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.adminMessageTextView.text = "Добро пожаловать, Администратор!"

        setupUserList()
    }

    private fun setupUserList() {
        val users = listOf("User1", "User2", "User3")
        val adapter = UserListAdapter(users)
        binding.recyclerView.adapter = adapter
    }
}