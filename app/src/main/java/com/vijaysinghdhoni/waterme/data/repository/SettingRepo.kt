package com.vijaysinghdhoni.waterme.data.repository

import com.vijaysinghdhoni.waterme.data.model.WaterIntakeitem

interface SettingRepo {

    fun getWaterGoalOfTheDay(): Int
    fun setWaterGoalOfTheDay(waterGoalOfTheDay: Int)
    fun getTimeIntervalForNotification(): Long
    fun setTimeIntervalForNotification(hours: Long)
    fun getUserSleepTime(): Long
    fun setUserSleepTime(timeInmills: Long)
    fun getUserWakeupTime(): Long
    fun setUserWakeupTime(timeInmills: Long)
    fun getListOfWaterIntakeItems(): List<WaterIntakeitem>
}