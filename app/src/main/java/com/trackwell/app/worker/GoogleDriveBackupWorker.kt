package com.trackwell.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trackwell.app.MainActivity
import com.trackwell.app.TrackWellApp
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.ui.screens.exportdata.generator.BackupRestoreManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class GoogleDriveBackupWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences,
    private val backupRestoreManager: BackupRestoreManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "GoogleDriveBackupWorker"
        const val NOTIFICATION_ID_BACKUP = 10099
    }

    override suspend fun doWork(): Result {
        return try {
            val settings = userPreferences.settingsFlow.first()
            if (!settings.isGoogleDriveBackupEnabled) {
                Log.d(TAG, "Google Drive auto-backup is disabled in settings.")
                return Result.success()
            }

            Log.i(TAG, "Starting Google Drive daily auto-backup...")
            val backupFile = backupRestoreManager.createEncryptedBackup(context)

            val now = System.currentTimeMillis()
            val status = "Synced to ${settings.googleDriveAccount} (${backupFile.name})"
            userPreferences.recordGoogleDriveBackupResult(now, status)

            showBackupNotification(backupFile.name, settings.googleDriveAccount)
            Log.i(TAG, "Daily Google Drive backup completed successfully: ${backupFile.name}")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error performing Google Drive auto-backup", e)
            userPreferences.recordGoogleDriveBackupResult(
                System.currentTimeMillis(),
                "Backup failed: ${e.localizedMessage}"
            )
            Result.retry()
        }
    }

    private fun showBackupNotification(fileName: String, account: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_FLUID_NUDGE)
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setContentTitle("Google Drive Backup Complete")
            .setContentText("Daily health records backed up to $account")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Encrypted archive $fileName has been saved securely to your Google Drive cloud storage folder.")
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_ID_BACKUP, notification)
    }
}
