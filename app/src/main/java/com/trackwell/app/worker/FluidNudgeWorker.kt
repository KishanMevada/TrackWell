package com.trackwell.app.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.notification.ReminderBroadcastReceiver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class FluidNudgeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val settings = userPreferences.settingsFlow.first()
        if (!settings.fluidNudgeEnabled) return Result.success()

        // Only nudge during daytime (9 AM to 8 PM)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour in 9..20) {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                action = ReminderBroadcastReceiver.ACTION_FLUID_NUDGE
            }
            context.sendBroadcast(intent)
        }

        return Result.success()
    }
}
