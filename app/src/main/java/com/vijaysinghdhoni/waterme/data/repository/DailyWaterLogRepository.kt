package com.vijaysinghdhoni.waterme.data.repository

import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import kotlinx.coroutines.flow.Flow

interface DailyWaterLogRepository {

    suspend fun insertDailyWaterLog(dailyWaterLog: DailyWaterLog)

    suspend fun getTodayDailyWaterLog(date: String): DailyWaterLog?

    suspend fun upDateWaterDrunk(waterIntakeLog: WaterIntakeLog)

    suspend fun upDateGoalOftheDay(goal: Int, date: String)

    fun getLast7DaysWaterLog(): Flow<List<DailyWaterLog>>
}