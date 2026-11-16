package com.trackwell.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trackwell.app.data.preferences.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var googleDriveBackupScheduler: GoogleDriveBackupScheduler

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = userPreferences.settingsFlow.first()
                    reminderScheduler.scheduleAll(settings)
                    googleDriveBackupScheduler.scheduleDailyBackup(settings)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
