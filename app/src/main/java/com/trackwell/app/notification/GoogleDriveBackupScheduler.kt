package com.trackwell.app.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.worker.GoogleDriveBackupWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveBackupScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val UNIQUE_WORK_NAME = "trackwell_google_drive_daily_backup"
    }

    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailyBackup(settings: AppSettings) {
        if (!settings.isGoogleDriveBackupEnabled) {
            workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val initialDelayMinutes = computeInitialDelayMinutes(settings.googleDriveBackupTime)

        val periodicWork = PeriodicWorkRequestBuilder<GoogleDriveBackupWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .addTag(UNIQUE_WORK_NAME)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWork
        )
    }

    fun triggerImmediateBackup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateWork = OneTimeWorkRequestBuilder<GoogleDriveBackupWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(immediateWork)
    }

    private fun computeInitialDelayMinutes(timeStr: String): Long {
        val parts = timeStr.split(":")
        val targetHour = parts.getOrNull(0)?.toIntOrNull() ?: 2
        val targetMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val diffMillis = target.timeInMillis - now.timeInMillis
        return (diffMillis / (1000 * 60)).coerceAtLeast(0)
    }
}
