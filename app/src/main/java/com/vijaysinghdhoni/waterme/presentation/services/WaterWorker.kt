package com.vijaysinghdhoni.waterme.presentation.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.presentation.activity.MainActivity
import com.vijaysinghdhoni.waterme.util.Constants.NOTIFICATION_ID
import com.vijaysinghdhoni.waterme.util.Constants.WATER_CHANNEL_ID

class WaterWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            showNotification()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        val builder = NotificationCompat.Builder(context, WATER_CHANNEL_ID)
            .setSmallIcon(R.drawable.waterme_notifi_icon)
            .setContentTitle(context.getText(R.string.water_notification_title))
            .setContentText(context.getText(R.string.water_notification_title))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getText(R.string.water_notification_content))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val channel = createNotificationChannel()
        channel?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel(): NotificationManager? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_water_reminder)
            val descriptionText = context.getString(R.string.channel_water_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(WATER_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager
        } else null
    }


}