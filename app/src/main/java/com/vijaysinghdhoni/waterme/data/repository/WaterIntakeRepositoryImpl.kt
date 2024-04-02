package com.vijaysinghdhoni.waterme.data.repository

import com.vijaysinghdhoni.waterme.data.db.WaterIntakeDao
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WaterIntakeRepositoryImpl @Inject constructor(private val waterIntakeDao: WaterIntakeDao) :
    WaterIntakeRepository {

    override suspend fun insertWaterIntake(waterIntakeLog: WaterIntakeLog) {
        waterIntakeDao.insertWaterIntake(waterIntakeLog)
    }

    override suspend fun getLastWaterIntake(date: String): WaterIntakeLog? {
        return waterIntakeDao.getLastWaterIntake(date)
    }

    override fun getLastSevenDaysWaterIntake(): Flow<List<WaterIntakeLog>> {
        return waterIntakeDao.getLast7daysWaterIntake()
    }
}