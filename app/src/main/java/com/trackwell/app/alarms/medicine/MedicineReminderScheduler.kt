package com.trackwell.app.alarms.medicine

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.trackwell.app.data.medicine.MedicineRepository
import com.trackwell.app.domain.medicine.model.MedicineRepeatMode
import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.domain.medicine.model.NextMedicineReminder
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MedicineRepository
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    /**
     * Checks whether the app currently has permission to schedule exact alarms.
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() ?: false
        } else {
            true
        }
    }

    /**
     * Schedules the next due-time reminder and its 15-minute follow-up alarm for a medicine.
     */
    suspend fun scheduleMedicine(schedule: MedicineSchedule) {
        if (!schedule.isEnabled || schedule.reminderTimes.isEmpty()) {
            cancelMedicineAlarms(schedule.id)
            return
        }

        schedule.reminderTimes.forEachIndexed { index, timeStr ->
            val nextOccurrence = calculateNextOccurrence(schedule, timeStr) ?: return@forEachIndexed
            val requestCode = getRequestCode(schedule.id, index, isFollowUp = false)
            val followUpRequestCode = getRequestCode(schedule.id, index, isFollowUp = true)

            val displayTime = formatTimeForDisplay(nextOccurrence.timeInMillis)

            // 1. Normal Reminder Alarm
            val normalIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = MedicineAlarmReceiver.ACTION_MEDICINE_DUE
                putExtra(MedicineAlarmReceiver.EXTRA_MEDICINE_ID, schedule.id)
                putExtra(MedicineAlarmReceiver.EXTRA_MEDICINE_NAME, schedule.name)
                putExtra(MedicineAlarmReceiver.EXTRA_DOSE_FORMATTED, schedule.formattedDose)
                putExtra(MedicineAlarmReceiver.EXTRA_SCHEDULED_TIME, displayTime)
                putExtra(MedicineAlarmReceiver.EXTRA_TRIGGER_MILLIS, nextOccurrence.timeInMillis)
                putExtra(MedicineAlarmReceiver.EXTRA_REQUEST_CODE, requestCode)
                putExtra(MedicineAlarmReceiver.EXTRA_FOLLOW_UP_REQUEST_CODE, followUpRequestCode)
                putExtra(MedicineAlarmReceiver.EXTRA_IS_FOLLOW_UP, false)
            }

            val normalPendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                normalIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            setAlarmSafely(nextOccurrence.timeInMillis, normalPendingIntent)
        }
    }

    /**
     * Schedules the follow-up alarm 15 minutes after the due-time trigger.
     */
    fun scheduleFollowUp(
        medicineId: String,
        medicineName: String,
        doseFormatted: String,
        scheduledTime: String,
        followUpTriggerMillis: Long,
        followUpRequestCode: Int,
        pendingReminderId: String,
        notificationId: Int
    ) {
        val followUpIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = MedicineAlarmReceiver.ACTION_MEDICINE_FOLLOW_UP
            putExtra(MedicineAlarmReceiver.EXTRA_MEDICINE_ID, medicineId)
            putExtra(MedicineAlarmReceiver.EXTRA_MEDICINE_NAME, medicineName)
            putExtra(MedicineAlarmReceiver.EXTRA_DOSE_FORMATTED, doseFormatted)
            putExtra(MedicineAlarmReceiver.EXTRA_SCHEDULED_TIME, scheduledTime)
            putExtra(MedicineAlarmReceiver.EXTRA_TRIGGER_MILLIS, followUpTriggerMillis)
            putExtra(MedicineAlarmReceiver.EXTRA_REQUEST_CODE, followUpRequestCode)
            putExtra(MedicineAlarmReceiver.EXTRA_PENDING_REMINDER_ID, pendingReminderId)
            putExtra(MedicineAlarmReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(MedicineAlarmReceiver.EXTRA_IS_FOLLOW_UP, true)
        }

        val followUpPendingIntent = PendingIntent.getBroadcast(
            context,
            followUpRequestCode,
            followUpIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarmSafely(followUpTriggerMillis, followUpPendingIntent)
    }

    /**
     * Cancels all scheduled normal and follow-up alarms for a given medicine.
     */
    fun cancelMedicineAlarms(medicineId: String) {
        for (index in 0 until 10) {
            val normalCode = getRequestCode(medicineId, index, isFollowUp = false)
            val followUpCode = getRequestCode(medicineId, index, isFollowUp = true)

            cancelAlarmByRequestCode(normalCode)
            cancelAlarmByRequestCode(followUpCode)
        }
    }

    /**
     * Cancels a specific follow-up alarm when a dose is resolved.
     */
    fun cancelFollowUpAlarm(followUpRequestCode: Int) {
        cancelAlarmByRequestCode(followUpRequestCode)
    }

    private fun cancelAlarmByRequestCode(requestCode: Int) {
        val intent = Intent(context, MedicineAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Reschedules all active enabled medicine reminders across the system.
     */
    suspend fun rescheduleAll() = withContext(Dispatchers.IO) {
        val enabledSchedules = repository.getEnabledSchedules()
        for (schedule in enabledSchedules) {
            scheduleMedicine(schedule)
        }
    }

    /**
     * Finds the next upcoming medicine reminder across all enabled schedules for display on the "Today" card.
     */
    suspend fun getNextUpcomingReminder(): NextMedicineReminder? = withContext(Dispatchers.IO) {
        val enabledSchedules = repository.getEnabledSchedules()
        if (enabledSchedules.isEmpty()) return@withContext null

        var earliestReminder: NextMedicineReminder? = null
        var earliestMillis = Long.MAX_VALUE

        for (schedule in enabledSchedules) {
            for (timeStr in schedule.reminderTimes) {
                val nextOccur = calculateNextOccurrence(schedule, timeStr) ?: continue
                if (nextOccur.timeInMillis < earliestMillis) {
                    earliestMillis = nextOccur.timeInMillis
                    earliestReminder = NextMedicineReminder(
                        medicineId = schedule.id,
                        medicineName = schedule.name,
                        doseFormatted = schedule.formattedDose,
                        scheduledTime = formatTimeForDisplay(nextOccur.timeInMillis),
                        triggerEpochMillis = nextOccur.timeInMillis
                    )
                }
            }
        }

        earliestReminder
    }

    /**
     * Accurately calculates the next occurrence of a schedule based on local time and repeat rules.
     */
    fun calculateNextOccurrence(schedule: MedicineSchedule, timeStr: String): Calendar? {
        val parts = timeStr.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val now = Calendar.getInstance()
        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Loop up to 14 days ahead to find the next valid occurrence
        for (offset in 0..14) {
            val checkCal = (candidate.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, offset)
            }

            // Must be in the future (at least 5 seconds from now)
            if (checkCal.timeInMillis <= now.timeInMillis + 5000) {
                continue
            }

            // Check start/end date constraints if present
            val checkIso = DateTimeUtils.formatDateToIso(checkCal.time)
            if (schedule.startDate != null && checkIso < schedule.startDate) {
                continue
            }
            if (schedule.endDate != null && checkIso > schedule.endDate) {
                continue
            }

            // Check repeat rule
            when (schedule.repeatMode) {
                MedicineRepeatMode.DAILY -> {
                    return checkCal
                }
                MedicineRepeatMode.SELECTED_DAYS -> {
                    val currentDayOfWeek = when (checkCal.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> DayOfWeek.MONDAY
                        Calendar.TUESDAY -> DayOfWeek.TUESDAY
                        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                        Calendar.THURSDAY -> DayOfWeek.THURSDAY
                        Calendar.FRIDAY -> DayOfWeek.FRIDAY
                        Calendar.SATURDAY -> DayOfWeek.SATURDAY
                        Calendar.SUNDAY -> DayOfWeek.SUNDAY
                        else -> DayOfWeek.MONDAY
                    }

                    if (schedule.selectedDaysOfWeek.isEmpty() || schedule.selectedDaysOfWeek.contains(currentDayOfWeek)) {
                        return checkCal
                    }
                }
            }
        }

        return null
    }

    private fun setAlarmSafely(triggerMillis: Long, pendingIntent: PendingIntent) {
        val am = alarmManager ?: return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Safe fallback if exact alarm permission was revoked
            try {
                am.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } catch (ignored: Exception) {}
        }
    }

    private fun getRequestCode(medicineId: String, timeIndex: Int, isFollowUp: Boolean): Int {
        val base = (medicineId.hashCode() and 0x7FFFFFFF) % 100000 * 10 + (timeIndex % 10)
        return if (isFollowUp) 3000000 + base else 2000000 + base
    }

    private fun formatTimeForDisplay(millis: Long): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
    }
}
