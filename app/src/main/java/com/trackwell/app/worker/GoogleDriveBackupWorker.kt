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
import com.trackwell.app.data.remote.GoogleDriveService
import com.trackwell.app.ui.screens.exportdata.generator.BackupRestoreManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class GoogleDriveBackupWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences,
    private val backupRestoreManager: BackupRestoreManager,
    private val googleDriveService: GoogleDriveService
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
            val accountName = settings.googleDriveAccount.ifBlank {
                googleDriveService.getLastSignedInAccount()?.email ?: "Google Drive"
            }

            updateProgressNotification(0, 0, backupFile.length(), accountName)

            val uploadResult = googleDriveService.uploadBackupFile(backupFile) { percent, bytes, total ->
                updateProgressNotification(percent, bytes, total, accountName)
            }

            if (uploadResult.isSuccess) {
                val metadata = uploadResult.getOrNull()
                val status = "Synced to $accountName (${metadata?.fileName ?: backupFile.name})"
                userPreferences.recordGoogleDriveBackupResult(now, status)
                updateProgressNotification(100, backupFile.length(), backupFile.length(), accountName, isComplete = true)
                Log.i(TAG, "Daily Google Drive backup completed successfully: ${metadata?.fileId}")
            } else {
                val errorMsg = uploadResult.exceptionOrNull()?.localizedMessage ?: "Upload failed"
                val status = "Upload failed: $errorMsg"
                userPreferences.recordGoogleDriveBackupResult(now, status)
                updateProgressNotification(0, 0, 0, accountName, isComplete = false, errorMsg = errorMsg)
                Log.w(TAG, "Google Drive cloud upload failed: $errorMsg")
            }

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

    private fun updateProgressNotification(
        progressPercent: Int,
        bytesTransferred: Long,
        totalBytes: Long,
        account: String,
        isComplete: Boolean = false,
        errorMsg: String? = null
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (errorMsg != null) {
            val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_FLUID_NUDGE)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle("Google Drive Backup Failed")
                .setContentText("Upload failed: $errorMsg")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setProgress(0, 0, false)
                .build()
            notificationManager.notify(NOTIFICATION_ID_BACKUP, notification)
            return
        }

        if (isComplete) {
            val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_FLUID_NUDGE)
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentTitle("Google Drive Backup Complete (100%)")
                .setContentText("Daily health records backed up to $account")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Encrypted archive TrackWell_Backup.twbackup (100%) has been saved securely to your Google Drive cloud storage folder.")
                )
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setOngoing(false)
                .setProgress(0, 0, false)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager.notify(NOTIFICATION_ID_BACKUP, notification)
            return
        }

        val transferredStr = formatBytes(bytesTransferred)
        val totalStr = formatBytes(totalBytes)
        val progressText = if (totalBytes > 0) "$progressPercent% ($transferredStr / $totalStr)" else "$progressPercent%"

        val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_FLUID_NUDGE)
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setContentTitle("Backing up to Google Drive... $progressPercent%")
            .setContentText(progressText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Uploading encrypted backup to $account: $progressText")
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progressPercent, false)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BACKUP, notification)
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        return String.format(java.util.Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}
