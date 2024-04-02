package com.vijaysinghdhoni.waterme.data.repository

import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import kotlinx.coroutines.flow.Flow

interface WaterIntakeRepository {

    suspend fun insertWaterIntake(waterIntakeLog: WaterIntakeLog)

    suspend fun getLastWaterIntake(
        date:String
    ): WaterIntakeLog?

    fun getLastSevenDaysWaterIntake() : Flow<List<WaterIntakeLog>>

}