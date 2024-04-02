package com.vijaysinghdhoni.waterme.data.repository

import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeitem
import com.vijaysinghdhoni.waterme.data.userprefernce.SettingCache
import javax.inject.Inject

class SettingRepoImpl @Inject constructor(private val settingCache: SettingCache) : SettingRepo {
    override fun getWaterGoalOfTheDay(): Int {
        return settingCache.getWaterGoalOfTheDay()
    }

    override fun setWaterGoalOfTheDay(waterGoalOfTheDay: Int) {
        settingCache.setWaterGoalOfTheDay(waterGoalOfTheDay)
    }

    override fun getTimeIntervalForNotification(): Long {
        return settingCache.getTimeIntervalOfNotification()
    }

    override fun setTimeIntervalForNotification(hours: Long) {
        settingCache.setTimeIntervalOfNotification(hours)
    }

    override fun getUserSleepTime(): Long {
        return settingCache.getUserSleepTime()
    }

    override fun setUserSleepTime(timeInmills: Long) {
        settingCache.setUserSleepTime(timeInmills)
    }

    override fun getUserWakeupTime(): Long {
        return settingCache.getUserWakeupTime()
    }

    override fun setUserWakeupTime(timeInmills: Long) {
        settingCache.setUserWakeupTime(timeInmills)
    }

    override fun getListOfWaterIntakeItems(): List<WaterIntakeitem> {
        return listOf(
            WaterIntakeitem(50, R.drawable.small_cup_ic),
            WaterIntakeitem(100, R.drawable.water_glass),
            WaterIntakeitem(200, R.drawable.water_jug),
            WaterIntakeitem(300, R.drawable.water_bottle),
            WaterIntakeitem(400, R.drawable.big_wtr_bttl),
            WaterIntakeitem(500, R.drawable.wtr_big_jug),
        )
    }

}