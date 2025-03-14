package com.example.myapplication.ui

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.appcompat.app.AppCompatActivity

class MyFragmentStateAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        Log.d("FragmentStateAdapter", "getItemCount called, returning 5")
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("FragmentStateAdapter", "createFragment called, position: $position")

        return when (position) {
            0 -> {
                Log.d("FragmentStateAdapter", "Creating PageFragment1")
                PageFragment1()
            }
            1 -> {
                Log.d("FragmentStateAdapter", "Creating PageFragment2")
                PageFragment2()
            }
            2 -> {
                Log.d("FragmentStateAdapter", "Creating PageFragment3")
                PageFragment3()
            }
            3 -> {
                Log.d("FragmentStateAdapter", "Creating PageFragment4")
                PageFragment4()
            }
            4 -> {
                Log.d("FragmentStateAdapter", "Creating SettingsFragment")
                SettingsFragment()
            }
            else -> {
                Log.e("FragmentStateAdapter", "Unexpected position: $position")
                throw IllegalStateException("Unexpected position $position")
            }
        }
    }
}