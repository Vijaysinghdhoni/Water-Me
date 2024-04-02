package com.vijaysinghdhoni.waterme.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.vijaysinghdhoni.waterme.data.db.DailyWaterDao
import com.vijaysinghdhoni.waterme.data.db.WaterDB
import com.vijaysinghdhoni.waterme.data.db.WaterIntakeDao
import com.vijaysinghdhoni.waterme.data.repository.*
import com.vijaysinghdhoni.waterme.data.userprefernce.SettingCache
import com.vijaysinghdhoni.waterme.util.Constants.INTRO_SP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun providesWaterDb(@ApplicationContext app: Context): WaterDB {
        return Room.databaseBuilder(
            app, WaterDB::class.java, "Water_Rem_Db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providesDailyWaterDao(waterDB: WaterDB): DailyWaterDao {
        return waterDB.dailyWaterDao
    }

    @Provides
    @Singleton
    fun providesWaterIntakeDao(waterDB: WaterDB): WaterIntakeDao {
        return waterDB.waterIntakeDao
    }

    @Provides
    @Singleton
    fun providesDailyWaterLogRepo(dailyWaterDao: DailyWaterDao): DailyWaterLogRepository {
        return DailyWaterLogRepositoryImpl(dailyWaterDao)
    }

    @Provides
    @Singleton
    fun providesWaterIntakeRepo(waterIntakeDao: WaterIntakeDao): WaterIntakeRepository {
        return WaterIntakeRepositoryImpl(waterIntakeDao)
    }

    @Provides
    @Singleton
    fun providesIntroductionSharedPrefernce(
        @ApplicationContext app: Context
    ): SharedPreferences = app.getSharedPreferences(INTRO_SP, MODE_PRIVATE)


    @Provides
    @Singleton
    fun providesSettingCache(
        sharedPreferences: SharedPreferences
    ): SettingCache = SettingCache(sharedPreferences)


    @Provides
    @Singleton
    fun providesSettingRepo(
        settingCache: SettingCache
    ): SettingRepo = SettingRepoImpl(settingCache)


}