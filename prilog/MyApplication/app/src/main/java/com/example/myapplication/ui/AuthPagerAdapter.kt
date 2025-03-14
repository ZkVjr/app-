package com.example.myapplication.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AuthPagerAdapter(activity: AuthActivity) : FragmentStateAdapter(activity) {
    private val fragments = mutableListOf<Fragment>(LoginFragment(), RegisterFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addAdminTab() {
        fragments.add(AdminFragment())
        notifyItemInserted(fragments.size - 1)
    }
}