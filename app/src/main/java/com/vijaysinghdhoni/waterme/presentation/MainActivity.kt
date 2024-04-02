package com.vijaysinghdhoni.waterme.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.tabs.TabLayoutMediator
import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.presentation.adapters.ViewPagerAdapter
import com.vijaysinghdhoni.waterme.databinding.ActivityMainBinding
import com.vijaysinghdhoni.waterme.viewmodels.WaterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<WaterViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val waterArray = arrayOf(
            "Home",
            "Statistic",
            "Setting"
        )

        val waterArrayIcons = arrayOf(
            R.drawable.home_ic,
            R.drawable.statistic_ic,
            R.drawable.settings_ic
        )

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = waterArray[position]
                    tab.icon = ContextCompat.getDrawable(this, waterArrayIcons[position])
                }
                1 -> {
                    tab.text = waterArray[position]
                    tab.icon = ContextCompat.getDrawable(this, waterArrayIcons[position])
                }

                else -> {
                    tab.text = waterArray[position]
                    tab.icon = ContextCompat.getDrawable(this, waterArrayIcons[position])
                }
            }

        }.attach()

    }
}