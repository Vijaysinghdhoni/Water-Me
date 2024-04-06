package com.vijaysinghdhoni.waterme.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DailyWaterDaoTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var waterDB: WaterDB
    private lateinit var dailyWaterDao: DailyWaterDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        waterDB = Room.inMemoryDatabaseBuilder(context, WaterDB::class.java)
            .allowMainThreadQueries()
            .build()
        dailyWaterDao = waterDB.dailyWaterDao
    }

    @After
    fun teardown() {
        waterDB.close()
    }


    @Test
    fun insertDaily_WaterLog() {
        runBlocking {
            val dailyWaterLog = DailyWaterLog(1, 2000, 1000, "2024-04-06")
            dailyWaterDao.insertDailyWaterLog(dailyWaterLog)

            val retrievedLog = dailyWaterDao.getTodayDailyWaterLog("2024-04-06")
            assertNotNull(retrievedLog)
            assertEquals(dailyWaterLog.waterGoalOfDay, retrievedLog?.waterGoalOfDay)
            assertEquals(dailyWaterLog.waterDrunk, retrievedLog?.waterDrunk)
            assertEquals(dailyWaterLog.date, retrievedLog?.date)
        }
    }


    @Test
    fun getTodayDailyWaterLogtest() {
        runBlocking {

            val date = "2024-04-06"
            val expectedLog = DailyWaterLog(1, 2000, 1000, date)
            dailyWaterDao.insertDailyWaterLog(expectedLog)


            val retrievedLog = dailyWaterDao.getTodayDailyWaterLog(date)

            assertNotNull(retrievedLog)
            assertEquals(expectedLog, retrievedLog)
        }
    }

    @Test
    fun upDateWaterDrunk() {
        runBlocking {

            val date = "2024-04-06"
            val initialLog = DailyWaterLog(1, 2000, 1000, date)
            dailyWaterDao.insertDailyWaterLog(initialLog)
            val waterDrunkToAdd = 500


            dailyWaterDao.upDateWaterDrunk(waterDrunkToAdd, date)


            val updatedLog = dailyWaterDao.getTodayDailyWaterLog(date)
            assertNotNull(updatedLog)
            assertEquals(initialLog.waterDrunk + waterDrunkToAdd, updatedLog?.waterDrunk)
        }
    }

    @Test
    fun updateGoalOfTheDay() {
        runBlocking {

            val date = "2024-04-06"
            val initialLog = DailyWaterLog(1, 2000, 1000, date)
            dailyWaterDao.insertDailyWaterLog(initialLog)
            val newWaterGoal = 2500


            dailyWaterDao.upDateWaterGoalOfDay(newWaterGoal, date)


            val updatedLog = dailyWaterDao.getTodayDailyWaterLog(date)
            assertNotNull(updatedLog)
            assertEquals(newWaterGoal, updatedLog?.waterGoalOfDay)
        }
    }


    @Test
    fun getLastSevenDaysWaterLog() {
        runBlocking {

            val logsToInsert = listOf(
                DailyWaterLog(1, 2000, 1000, "2024-04-06"),
                DailyWaterLog(2, 2000, 1500, "2024-04-05"),
                DailyWaterLog(3, 2000, 1200, "2024-04-04"),
                DailyWaterLog(4, 2000, 1000, "2024-04-03"),
                DailyWaterLog(5, 2000, 1500, "2024-04-02"),
                DailyWaterLog(6, 2000, 1200, "2024-04-01"),
                DailyWaterLog(7, 2000, 1300, "2024-03-31"),
                DailyWaterLog(8, 2000, 1300, "2024-03-28"),
                DailyWaterLog(9, 2000, 1300, "2024-03-25"),
            )
            logsToInsert.forEach { dailyWaterDao.insertDailyWaterLog(it) }


            val lastSevenDaysLogs = dailyWaterDao.getLastSevenDaysWaterLog().first()


            assertEquals(7, lastSevenDaysLogs.size)
        }
    }

}