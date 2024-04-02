package com.vijaysinghdhoni.waterme.data.userprefernce

import android.content.SharedPreferences
import com.vijaysinghdhoni.waterme.util.Constants.DEFAULT_TIME_INTERVAL
import com.vijaysinghdhoni.waterme.util.Constants.SLEEP_TIME_KEY
import com.vijaysinghdhoni.waterme.util.Constants.TIME_INTERVAL
import com.vijaysinghdhoni.waterme.util.Constants.WAKEUP_TIME_KEY
import com.vijaysinghdhoni.waterme.util.Constants.WATER_GOAL_OF_THE_DAY_KEY
import com.vijaysinghdhoni.waterme.util.Constants.default_water_goal_of_day
import javax.inject.Inject

class SettingCache @Inject constructor(private val sharedPrefernces: SharedPreferences) {

    fun getWaterGoalOfTheDay() =
        sharedPrefernces.getInt(WATER_GOAL_OF_THE_DAY_KEY, default_water_goal_of_day)

    fun setWaterGoalOfTheDay(waterGoalOfTheDay: Int) =
        sharedPrefernces.edit().putInt(WATER_GOAL_OF_THE_DAY_KEY, waterGoalOfTheDay).apply()

    fun getTimeIntervalOfNotification() =
        sharedPrefernces.getLong(TIME_INTERVAL, DEFAULT_TIME_INTERVAL.toLong())

    fun setTimeIntervalOfNotification(hours: Long) =
        sharedPrefernces.edit().putLong(TIME_INTERVAL, hours).apply()

    fun getUserSleepTime() = sharedPrefernces.getLong(SLEEP_TIME_KEY, 0L)

    fun setUserSleepTime(timeInmills: Long) =
        sharedPrefernces.edit().putLong(SLEEP_TIME_KEY, timeInmills).apply()

    fun getUserWakeupTime() = sharedPrefernces.getLong(WAKEUP_TIME_KEY, 0L)

    fun setUserWakeupTime(timeInmills: Long) =
        sharedPrefernces.edit().putLong(WAKEUP_TIME_KEY, timeInmills).apply()


}