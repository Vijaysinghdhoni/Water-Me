package com.vijaysinghdhoni.waterme.data.repository

import com.vijaysinghdhoni.waterme.data.db.DailyWaterDao
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyWaterLogRepositoryImpl @Inject constructor(private val dailyWaterDao: DailyWaterDao) :
    DailyWaterLogRepository {

    override suspend fun insertDailyWaterLog(dailyWaterLog: DailyWaterLog) {
        dailyWaterDao.insertDailyWaterLog(dailyWaterLog)
    }

    override suspend fun getTodayDailyWaterLog(date: String): DailyWaterLog? {
        return dailyWaterDao.getTodayDailyWaterLog(date)
    }

    override suspend fun upDateWaterDrunk(waterIntakeLog: WaterIntakeLog) {
        dailyWaterDao.upDateWaterDrunk(waterIntakeLog.waterAmount, waterIntakeLog.date)
    }

    override suspend fun upDateGoalOftheDay(goal: Int, date: String) {
        dailyWaterDao.upDateWaterGoalOfDay(waterGoal = goal, date = date)
    }

    override fun getLast7DaysWaterLog(): Flow<List<DailyWaterLog>> {
        return dailyWaterDao.getLastSevenDaysWaterLog()
    }

}