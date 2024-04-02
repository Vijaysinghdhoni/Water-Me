package com.vijaysinghdhoni.waterme.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog

@Database(entities = [DailyWaterLog::class, WaterIntakeLog::class], version = 1)
abstract class WaterDB : RoomDatabase() {

    abstract val dailyWaterDao: DailyWaterDao
    abstract val waterIntakeDao: WaterIntakeDao

}