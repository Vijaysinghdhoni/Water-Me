package com.vijaysinghdhoni.waterme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeitem
import com.vijaysinghdhoni.waterme.data.repository.DailyWaterLogRepository
import com.vijaysinghdhoni.waterme.data.repository.SettingRepo
import com.vijaysinghdhoni.waterme.data.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WaterViewModel @Inject constructor(
    private val dailyWaterLogRepository: DailyWaterLogRepository,
    private val waterIntakeRepository: WaterIntakeRepository,
    private val settingRepo: SettingRepo
) : ViewModel() {

    private val _todayDailyWaterLog = MutableSharedFlow<DailyWaterLog?>()
    val todayDailyWaterLog = _todayDailyWaterLog.asSharedFlow()

    private val _lastWaterIntake = MutableStateFlow<WaterIntakeLog?>(null)
    val lastWaterIntake = _lastWaterIntake.asStateFlow()

    private val _userSleepTime = MutableStateFlow<Long>(0)
    val userSleepTime = _userSleepTime.asStateFlow()

    private val _userWakeupTime = MutableStateFlow<Long>(0)
    val userWakeupTime = _userWakeupTime.asStateFlow()

    private val _lastSevenDaysWaterLog = MutableStateFlow<List<DailyWaterLog>>(emptyList())
    val lastSevenDaysWaterLog = _lastSevenDaysWaterLog.asStateFlow()

    private val _last7daysWaterIntake = MutableStateFlow<List<WaterIntakeLog>>(emptyList())
    val last7daysWaterIntake = _last7daysWaterIntake.asStateFlow()

    fun getWaterGoalOfTheDay() = settingRepo.getWaterGoalOfTheDay()

    fun setWaterGoalOfTheDay(waterGoalOfTheDay: Int) {
        viewModelScope.launch {
            settingRepo.setWaterGoalOfTheDay(waterGoalOfTheDay)
            dailyWaterLogRepository.upDateGoalOftheDay(
                goal = waterGoalOfTheDay,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date()
                )
            )
        }

    }

    fun getTimeIntervalForNotification() = settingRepo.getTimeIntervalForNotification()
    fun setTimeIntervalForNotification(hours: Long) =
        settingRepo.setTimeIntervalForNotification(hours)

    fun getUserSleepTime() {
        viewModelScope.launch {
            _userSleepTime.value = settingRepo.getUserSleepTime()
        }
        Log.d("viewModel sleep ", settingRepo.getUserSleepTime().toString())
    }

    fun getUserWakeupTime() {
        viewModelScope.launch {
            _userWakeupTime.value = settingRepo.getUserWakeupTime()
        }
    }

    fun setUserSleepTime(timeInmills: Long) = settingRepo.setUserSleepTime(timeInmills)
    fun setUserWakeupTime(timeInmills: Long) = settingRepo.setUserWakeupTime(timeInmills)


    fun insertDailyWaterLog(dailyWaterLog: DailyWaterLog) {
        viewModelScope.launch {
            dailyWaterLogRepository.insertDailyWaterLog(dailyWaterLog)
        }
    }

    fun insertWaterIntake(waterIntakeAmount: Int) {
        viewModelScope.launch {
            val result = dailyWaterLogRepository.getTodayDailyWaterLog(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date()
                )
            )
            if (result != null) {
                val waterIntake = WaterIntakeLog(
                    waterAmount = waterIntakeAmount,
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    waterId = result.id
                )
                waterIntakeRepository.insertWaterIntake(waterIntake)
                dailyWaterLogRepository.upDateWaterDrunk(waterIntake)
                getUserLastWaterIntake()
                getTodayDailyWaterLog()
            }
        }
    }

    fun getTodayDailyWaterLog() {
        viewModelScope.launch {
            val result = dailyWaterLogRepository.getTodayDailyWaterLog(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date()
                )
            )
            Log.d("mytag", "daily water is $result")
            _todayDailyWaterLog.emit(result)
        }
    }

    fun getUserLastWaterIntake() {
        viewModelScope.launch {
            val result = waterIntakeRepository.getLastWaterIntake(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date()
                )
            )
            Log.d("last", "last is ${result?.waterAmount}")
            _lastWaterIntake.value = result
        }
    }

    fun getLastSevenDaysDailyWaterLog() {
        viewModelScope.launch {
            dailyWaterLogRepository.getLast7DaysWaterLog().collectLatest {
                _lastSevenDaysWaterLog.value = it
            }
        }
    }

    fun getLast7DaysWaterIntakes() {
        viewModelScope.launch {
            waterIntakeRepository.getLastSevenDaysWaterIntake().collectLatest {
                _last7daysWaterIntake.value = it
            }
        }
    }

    fun getListOfWaterIntakeItems(): List<WaterIntakeitem> {
        return settingRepo.getListOfWaterIntakeItems()
    }


}