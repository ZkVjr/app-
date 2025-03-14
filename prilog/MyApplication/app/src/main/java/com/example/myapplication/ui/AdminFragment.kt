package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.User
import com.example.myapplication.database.UserDao
import com.example.myapplication.databinding.FragmentAdminBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminFragment : Fragment() {
    private lateinit var userDao: UserDao
    private lateinit var userCountTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAdminBinding.inflate(inflater, container, false)

        userDao = AppDatabase.getInstance(requireContext()).userDao()
        userCountTextView = binding.userCount
        recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userAdapter = UserAdapter(
            onDeleteClick = { user -> deleteUser(user) },
            onEditClick = { user -> editUser(user) }
        )
        recyclerView.adapter = userAdapter

        loadUsers()

        return binding.root
    }

    private fun loadUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val users = userDao.getAllUsers()

            withContext(Dispatchers.Main) {
                userAdapter.submitList(users)
                userCountTextView.text = "Всего пользователей: ${users.size}"
            }
        }
    }

    private fun deleteUser(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.delete(user)

            withContext(Dispatchers.Main) {
                loadUsers()
            }
        }
    }

    private fun editUser(user: User) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        val dialogBinding = com.example.myapplication.databinding.DialogEditUserBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)

        dialogBinding.loginEditText.setText(user.login)
        dialogBinding.passwordEditText.setText(user.password)

        builder.setPositiveButton("Save") { _, _ ->
            val newLogin = dialogBinding.loginEditText.text.toString()
            val newPassword = dialogBinding.passwordEditText.text.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                userDao.updateUser(User(user.id, newLogin, user.name, newPassword))

                withContext(Dispatchers.Main) {
                    loadUsers()
                }
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}