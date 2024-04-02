package com.vijaysinghdhoni.waterme.presentation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeLog
import com.vijaysinghdhoni.waterme.data.repository.DailyWaterLogRepository
import com.vijaysinghdhoni.waterme.data.repository.SettingRepo
import com.vijaysinghdhoni.waterme.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
//
//@AndroidEntryPoint
//class WaterWorkerBroadcastReceiver : BroadcastReceiver() {
//
//    @Inject
//    lateinit var dailyWaterLogRepository: DailyWaterLogRepository
//    @Inject
//    lateinit var settingRepo: SettingRepo
//
//    override fun onReceive(p0: Context?, intent: Intent?) {
//        if(intent?.action == Constants.DRINK_ACTION){
//            val result = dailyWaterLogRepository.getTodayDailyWaterLog(
//                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
//                    Date()
//                )
//            )
//            if (result != null) {
//                val waterIntake = WaterIntakeLog(
//                    waterAmount = waterIntakeitem.amount,
//                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
//                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
//                    waterId = result.id
//                )
//                waterIntakeRepository.insertWaterIntake(waterIntake)
//                dailyWaterLogRepository.upDateWaterDrunk(waterIntake)
//            }
//
//
//        }
//    }
//
//
//
//}