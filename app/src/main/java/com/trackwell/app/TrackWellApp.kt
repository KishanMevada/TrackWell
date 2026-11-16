package com.trackwell.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TrackWellApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sugarChannel = NotificationChannel(
                CHANNEL_SUGAR_REMINDER,
                "Blood Sugar & Insulin Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to log blood sugar and insulin doses"
                enableVibration(true)
            }

            val fluidChannel = NotificationChannel(
                CHANNEL_FLUID_NUDGE,
                "Fluid Intake Nudges",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle alerts to stay hydrated and hit your fluid target"
            }

            val weightChannel = NotificationChannel(
                CHANNEL_WEIGHT_REMINDER,
                "Morning Weight Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to weigh yourself in the morning"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannels(listOf(sugarChannel, fluidChannel, weightChannel))
        }
    }

    companion object {
        const val CHANNEL_SUGAR_REMINDER = "channel_sugar_reminder"
        const val CHANNEL_FLUID_NUDGE = "channel_fluid_nudge"
        const val CHANNEL_WEIGHT_REMINDER = "channel_weight_reminder"
    }
}
