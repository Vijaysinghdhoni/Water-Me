package com.vijaysinghdhoni.waterme.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Daily_Water_Log_Table")
data class DailyWaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val waterGoalOfDay: Int,
    val waterDrunk: Int,
    val date: String
)

@Entity(
    tableName = "Water_Intake_Table",
    foreignKeys = [ForeignKey(
        entity = DailyWaterLog::class,
        parentColumns = ["id"],
        childColumns = ["Water_Id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class WaterIntakeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val waterAmount: Int,
    val time: String,
    val date: String,
    @ColumnInfo(name = "Water_Id")
    val waterId: Long
)