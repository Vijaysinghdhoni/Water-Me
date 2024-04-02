package com.vijaysinghdhoni.waterme.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterIntake(waterIntakeLog: WaterIntakeLog)

    @Query("SELECT * FROM Water_Intake_Table WHERE date =:date ORDER BY time DESC LIMIT 1")
    suspend fun getLastWaterIntake(date: String): WaterIntakeLog?

    @Query("SELECT * FROM Water_Intake_Table WHERE date BETWEEN date('now', '-6 days') AND datetime('now', 'localtime') ORDER BY date DESC")
    fun getLast7daysWaterIntake(): Flow<List<WaterIntakeLog>>

}