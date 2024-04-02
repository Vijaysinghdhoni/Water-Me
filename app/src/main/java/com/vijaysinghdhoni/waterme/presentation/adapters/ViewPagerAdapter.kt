package com.vijaysinghdhoni.waterme.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vijaysinghdhoni.waterme.presentation.fragments.HomeFragment
import com.vijaysinghdhoni.waterme.presentation.fragments.SettingFragment
import com.vijaysinghdhoni.waterme.presentation.fragments.StatisticFragment

private const val NUM_TABS = 3

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return StatisticFragment()
        }
        return SettingFragment()
    }
}

