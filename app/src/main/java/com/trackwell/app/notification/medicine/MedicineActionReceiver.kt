package com.trackwell.app.notification.medicine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trackwell.app.alarms.medicine.MedicineReminderScheduler
import com.trackwell.app.data.medicine.MedicineRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MedicineActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: MedicineRepository

    @Inject
    lateinit var scheduler: MedicineReminderScheduler

    @Inject
    lateinit var notificationManager: MedicineNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingReminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val medicineId = intent.getStringExtra(EXTRA_MEDICINE_ID) ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val followUpNotificationId = intent.getIntExtra(EXTRA_FOLLOW_UP_NOTIFICATION_ID, -1)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (action) {
                    ACTION_TOOK_IT, ACTION_SKIPPED -> {
                        // 1. Resolve and delete the active pending reminder (No history log stored!)
                        repository.resolvePendingReminder(pendingReminderId)

                        // 2. Dismiss notifications
                        if (notificationId != -1) {
                            notificationManager.cancelNotification(notificationId)
                        }
                        if (followUpNotificationId != -1) {
                            notificationManager.cancelNotification(followUpNotificationId)
                        }

                        // 3. Cancel any remaining follow-up alarms for this medicine
                        scheduler.cancelMedicineAlarms(medicineId)

                        // 4. Reschedule the next valid occurrence for this medicine schedule
                        val schedule = repository.getSchedule(medicineId)
                        if (schedule != null && schedule.isEnabled) {
                            scheduler.scheduleMedicine(schedule)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TOOK_IT = "com.trackwell.app.ACTION_MEDICINE_TOOK_IT"
        const val ACTION_SKIPPED = "com.trackwell.app.ACTION_MEDICINE_SKIPPED"

        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_MEDICINE_ID = "extra_medicine_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_FOLLOW_UP_NOTIFICATION_ID = "extra_follow_up_notification_id"
    }
}
