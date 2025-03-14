package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager2: ViewPager2
    private lateinit var fragmentAdapter: FragmentStateAdapter

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        Log.d(TAG, "onCreate: MainActivity started")

        viewPager2 = findViewById(R.id.dashboard_fragment_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        fragmentAdapter = MyFragmentStateAdapter(this)
        viewPager2.adapter = fragmentAdapter

        Log.d(TAG, "ViewPager2 initialized with adapter")

        updateIconColors()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_page1 -> {
                    Log.d("MainActivity", "Navigating to Page 1")
                    if (viewPager2.currentItem != 0) {
                        viewPager2.post {
                            viewPager2.setCurrentItem(0, true)
                            Log.d("MainActivity", "Page selected (post): position = 0")
                        }
                    }
                    true
                }
                R.id.nav_page2 -> {
                    Log.d("MainActivity", "Navigating to Page 2")
                    if (viewPager2.currentItem != 1) {
                        viewPager2.post {
                            viewPager2.setCurrentItem(1, true)
                            Log.d("MainActivity", "Page selected (post): position = 1")
                        }
                    }
                    true
                }
                R.id.nav_page3 -> {
                    Log.d("MainActivity", "Navigating to Page 3")
                    if (viewPager2.currentItem != 2) {
                        viewPager2.post {
                            viewPager2.setCurrentItem(2, true)
                            Log.d("MainActivity", "Page selected (post): position = 2")
                        }
                    }
                    true
                }
                R.id.nav_page4 -> {
                    Log.d("MainActivity", "Navigating to Page 4")
                    if (viewPager2.currentItem != 3) {
                        viewPager2.post {
                            viewPager2.setCurrentItem(3, true)
                            Log.d("MainActivity", "Page selected (post): position = 3")
                        }
                    }
                    true
                }
                R.id.nav_page5 -> {
                    Log.d("MainActivity", "Navigating to Settings")
                    if (viewPager2.currentItem != 4) {
                        viewPager2.post {
                            viewPager2.setCurrentItem(4, true)
                            Log.d("MainActivity", "Page selected (post): position = 4")
                        }
                    }
                    true
                }
                else -> false
            }
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Log.d("MainActivity", "Page selected (viewPager callback): position = $position")

                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.nav_page1
                    1 -> R.id.nav_page2
                    2 -> R.id.nav_page3
                    3 -> R.id.nav_page4
                    4 -> R.id.nav_page5
                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
        })
    }

    fun updateIconColors() {
        val isDarkMode = getSharedPreferences("app_prefs", MODE_PRIVATE).getBoolean("dark_mode", false)
        val iconColorRes = R.color.purple_700

        val colorStateList = ContextCompat.getColorStateList(this, iconColorRes)
        if (colorStateList != null) {
            bottomNavigationView.itemIconTintList = colorStateList
        }
    }
}