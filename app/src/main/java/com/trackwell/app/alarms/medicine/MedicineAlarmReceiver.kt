package com.trackwell.app.alarms.medicine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trackwell.app.data.medicine.MedicineRepository
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.medicine.model.PendingMedicineReminder
import com.trackwell.app.notification.medicine.MedicineNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MedicineAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: MedicineRepository

    @Inject
    lateinit var notificationManager: MedicineNotificationManager

    @Inject
    lateinit var scheduler: MedicineReminderScheduler

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val medicineId = intent.getStringExtra(EXTRA_MEDICINE_ID) ?: return
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Medicine"
        val doseFormatted = intent.getStringExtra(EXTRA_DOSE_FORMATTED) ?: ""
        val scheduledTime = intent.getStringExtra(EXTRA_SCHEDULED_TIME) ?: ""
        val triggerMillis = intent.getLongExtra(EXTRA_TRIGGER_MILLIS, System.currentTimeMillis())
        val followUpRequestCode = intent.getIntExtra(EXTRA_FOLLOW_UP_REQUEST_CODE, -1)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appSettings = userPreferences.settingsFlow.firstOrNull()
                val isLockScreenPrivacy = false // Default private label check if preference added

                when (action) {
                    ACTION_MEDICINE_DUE -> {
                        // 1. Verify schedule is still valid and enabled
                        val schedule = repository.getSchedule(medicineId)
                            ?: repository.getEnabledSchedules().find { it.name.equals(medicineName, ignoreCase = true) }
                            ?: repository.getEnabledSchedules().firstOrNull()
                        if (schedule == null || !schedule.isEnabled) return@launch
                        val effectiveMedId = schedule.id
                        val effectiveMedName = schedule.name
                        val effectiveDose = schedule.formattedDose

                        // 2. Generate temporary pending reminder
                        val pendingReminderId = UUID.randomUUID().toString()
                        val notifId = 4000000 + ((effectiveMedId.hashCode() and 0x7FFFFFFF) % 100000)
                        val followUpNotifId = 5000000 + ((effectiveMedId.hashCode() and 0x7FFFFFFF) % 100000)

                        val pendingReminder = PendingMedicineReminder(
                            pendingReminderId = pendingReminderId,
                            medicineId = effectiveMedId,
                            medicineName = effectiveMedName,
                            doseFormatted = effectiveDose,
                            scheduledTime = scheduledTime.ifBlank { "8:00 AM" },
                            scheduledAt = triggerMillis,
                            followUpAt = triggerMillis + (15 * 60 * 1000),
                            state = "PENDING",
                            notificationId = notifId,
                            followUpNotificationId = followUpNotifId,
                            isFollowUp = false
                        )

                        // 3. Persist temporary pending reminder so in-app host and deep link observe it
                        repository.createPendingReminder(pendingReminder)

                        // 4. Post native system notification
                        notificationManager.postNormalReminderNotification(
                            reminder = pendingReminder,
                            isLockScreenPrivacyEnabled = isLockScreenPrivacy
                        )

                        // 5. Schedule 15-minute escalation follow-up alarm
                        if (followUpRequestCode != -1) {
                            scheduler.scheduleFollowUp(
                                medicineId = medicineId,
                                medicineName = medicineName,
                                doseFormatted = doseFormatted,
                                scheduledTime = scheduledTime,
                                followUpTriggerMillis = triggerMillis + (15 * 60 * 1000),
                                followUpRequestCode = followUpRequestCode,
                                pendingReminderId = pendingReminderId,
                                notificationId = notifId
                            )
                        }

                        // 6. Schedule next occurrence for upcoming cycle
                        scheduler.scheduleMedicine(schedule)
                    }

                    ACTION_MEDICINE_FOLLOW_UP -> {
                        val pendingReminderId = intent.getStringExtra(EXTRA_PENDING_REMINDER_ID) ?: return@launch
                        val notifId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)

                        // Check if reminder is still pending and unresolved
                        val pending = repository.getPendingReminder(pendingReminderId)
                        if (pending != null && pending.state == "PENDING") {
                            // Dismiss normal notification and escalate with follow-up notification
                            if (notifId != -1) {
                                notificationManager.cancelNotification(notifId)
                            }

                            val followUpReminder = pending.copy(isFollowUp = true)
                            repository.createPendingReminder(followUpReminder)

                            notificationManager.postFollowUpNotification(
                                reminder = followUpReminder,
                                isLockScreenPrivacyEnabled = isLockScreenPrivacy
                            )
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_MEDICINE_DUE = "com.trackwell.app.ACTION_MEDICINE_DUE"
        const val ACTION_MEDICINE_FOLLOW_UP = "com.trackwell.app.ACTION_MEDICINE_FOLLOW_UP"

        const val EXTRA_MEDICINE_ID = "extra_medicine_id"
        const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
        const val EXTRA_DOSE_FORMATTED = "extra_dose_formatted"
        const val EXTRA_SCHEDULED_TIME = "extra_scheduled_time"
        const val EXTRA_TRIGGER_MILLIS = "extra_trigger_millis"
        const val EXTRA_REQUEST_CODE = "extra_request_code"
        const val EXTRA_FOLLOW_UP_REQUEST_CODE = "extra_follow_up_request_code"
        const val EXTRA_PENDING_REMINDER_ID = "extra_pending_reminder_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_IS_FOLLOW_UP = "extra_is_follow_up"
    }
}
