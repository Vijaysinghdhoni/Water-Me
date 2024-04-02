package com.vijaysinghdhoni.waterme.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWaterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWaterLog(dailyWaterLog: DailyWaterLog)

    @Query("SELECT * FROM Daily_Water_Log_Table WHERE date = :date")
    suspend fun getTodayDailyWaterLog(date: String): DailyWaterLog?

    @Query("UPDATE Daily_Water_Log_Table SET waterDrunk = waterDrunk + :waterDrunk WHERE date = :date")
    suspend fun upDateWaterDrunk(waterDrunk: Int, date: String)

    @Query("UPDATE Daily_Water_Log_Table SET waterGoalOfDay = :waterGoal WHERE date = :date")
    suspend fun upDateWaterGoalOfDay(waterGoal:Int, date: String)

    @Query("SELECT * FROM Daily_Water_Log_Table WHERE date BETWEEN date('now', '-6 days') AND datetime('now', 'localtime') ORDER BY date DESC")
     fun getLastSevenDaysWaterLog() : Flow<List<DailyWaterLog>>


}