package com.trackwell.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.worker.FluidNudgeWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleAll(settings: AppSettings) {
        // Schedule Sugar reminders
        scheduleDailyAlarm(
            requestCode = 101,
            timeStr = settings.sugarReminderBreakfast,
            action = ReminderBroadcastReceiver.ACTION_SUGAR_REMINDER,
            slot = SugarSlot.BEFORE_BREAKFAST
        )
        scheduleDailyAlarm(
            requestCode = 102,
            timeStr = settings.sugarReminderLunch,
            action = ReminderBroadcastReceiver.ACTION_SUGAR_REMINDER,
            slot = SugarSlot.BEFORE_LUNCH
        )
        scheduleDailyAlarm(
            requestCode = 103,
            timeStr = settings.sugarReminderAfterLunch,
            action = ReminderBroadcastReceiver.ACTION_SUGAR_REMINDER,
            slot = SugarSlot.AFTER_LUNCH
        )
        scheduleDailyAlarm(
            requestCode = 104,
            timeStr = settings.sugarReminderDinner,
            action = ReminderBroadcastReceiver.ACTION_SUGAR_REMINDER,
            slot = SugarSlot.BEFORE_DINNER
        )

        // Schedule Weight reminder
        scheduleDailyAlarm(
            requestCode = 105,
            timeStr = settings.weightReminderMorning,
            action = ReminderBroadcastReceiver.ACTION_WEIGHT_REMINDER,
            slot = null
        )

        // Schedule WorkManager Fluid Nudge
        if (settings.fluidNudgeEnabled) {
            val nudgeRequest = PeriodicWorkRequestBuilder<FluidNudgeWorker>(2, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "work_fluid_nudge",
                ExistingPeriodicWorkPolicy.UPDATE,
                nudgeRequest
            )
        } else {
            WorkManager.getInstance(context).cancelUniqueWork("work_fluid_nudge")
        }
    }

    private fun scheduleDailyAlarm(
        requestCode: Int,
        timeStr: String,
        action: String,
        slot: SugarSlot?
    ) {
        if (alarmManager == null) return

        val parts = timeStr.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            this.action = action
            if (slot != null) {
                putExtra(ReminderBroadcastReceiver.EXTRA_SLOT_NAME, slot.name)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
