package com.trackwell.app.notification.medicine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.trackwell.app.MainActivity
import com.trackwell.app.R
import com.trackwell.app.domain.medicine.model.PendingMedicineReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    init {
        createNotificationChannels()
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = notificationManager ?: return

            // 1. Normal Medicine Reminder Channel
            val normalChannel = NotificationChannel(
                CHANNEL_MEDICINE_REMINDERS,
                "Medicine reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Scheduled due-time reminders for your medicines"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 350, 150, 350)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // 2. High-Priority 15-Minute Follow-Up Channel
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val followUpChannel = NotificationChannel(
                CHANNEL_MEDICINE_FOLLOW_UP,
                "Medicine follow-up reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority reminder 15 minutes after scheduled dose"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(defaultSoundUri, audioAttributes)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            nm.createNotificationChannel(normalChannel)
            nm.createNotificationChannel(followUpChannel)
        }
    }

    /**
     * Posts the standard scheduled medicine reminder notification with "Took it" and "Skipped" actions.
     */
    fun postNormalReminderNotification(
        reminder: PendingMedicineReminder,
        isLockScreenPrivacyEnabled: Boolean = false
    ) {
        val nm = notificationManager ?: return

        // Tap content intent -> opens MainActivity & launches matching Medicine dialog
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, "medicine")
            putExtra(EXTRA_PENDING_REMINDER_ID, reminder.pendingReminderId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            reminder.notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Took it
        val tookIntent = Intent(context, MedicineActionReceiver::class.java).apply {
            action = MedicineActionReceiver.ACTION_TOOK_IT
            putExtra(MedicineActionReceiver.EXTRA_REMINDER_ID, reminder.pendingReminderId)
            putExtra(MedicineActionReceiver.EXTRA_MEDICINE_ID, reminder.medicineId)
            putExtra(MedicineActionReceiver.EXTRA_NOTIFICATION_ID, reminder.notificationId)
            putExtra(MedicineActionReceiver.EXTRA_FOLLOW_UP_NOTIFICATION_ID, reminder.followUpNotificationId)
        }
        val tookPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.notificationId + 100000,
            tookIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Skipped
        val skippedIntent = Intent(context, MedicineActionReceiver::class.java).apply {
            action = MedicineActionReceiver.ACTION_SKIPPED
            putExtra(MedicineActionReceiver.EXTRA_REMINDER_ID, reminder.pendingReminderId)
            putExtra(MedicineActionReceiver.EXTRA_MEDICINE_ID, reminder.medicineId)
            putExtra(MedicineActionReceiver.EXTRA_NOTIFICATION_ID, reminder.notificationId)
            putExtra(MedicineActionReceiver.EXTRA_FOLLOW_UP_NOTIFICATION_ID, reminder.followUpNotificationId)
        }
        val skippedPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.notificationId + 200000,
            skippedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isLockScreenPrivacyEnabled) "Medicine reminder" else "Time for ${reminder.medicineName}"
        val body = if (isLockScreenPrivacyEnabled) "Tap to open reminder" else "${reminder.doseFormatted} · Scheduled for ${reminder.scheduledTime}"

        val builder = NotificationCompat.Builder(context, CHANNEL_MEDICINE_REMINDERS)
            .setSmallIcon(R.drawable.ic_nav_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setSubText("TrackWell")
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setColor(0xFF0284C7.toInt())
            .addAction(
                android.R.drawable.checkbox_on_background,
                "Took it",
                tookPendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "Skipped",
                skippedPendingIntent
            )

        nm.notify(reminder.notificationId, builder.build())
    }

    /**
     * Posts the high-priority follow-up notification 15 minutes after scheduled dose.
     */
    fun postFollowUpNotification(
        reminder: PendingMedicineReminder,
        isLockScreenPrivacyEnabled: Boolean = false
    ) {
        val nm = notificationManager ?: return

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, "medicine")
            putExtra(EXTRA_PENDING_REMINDER_ID, reminder.pendingReminderId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            reminder.followUpNotificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Took it
        val tookIntent = Intent(context, MedicineActionReceiver::class.java).apply {
            action = MedicineActionReceiver.ACTION_TOOK_IT
            putExtra(MedicineActionReceiver.EXTRA_REMINDER_ID, reminder.pendingReminderId)
            putExtra(MedicineActionReceiver.EXTRA_MEDICINE_ID, reminder.medicineId)
            putExtra(MedicineActionReceiver.EXTRA_NOTIFICATION_ID, reminder.notificationId)
            putExtra(MedicineActionReceiver.EXTRA_FOLLOW_UP_NOTIFICATION_ID, reminder.followUpNotificationId)
        }
        val tookPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.followUpNotificationId + 100000,
            tookIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Skipped
        val skippedIntent = Intent(context, MedicineActionReceiver::class.java).apply {
            action = MedicineActionReceiver.ACTION_SKIPPED
            putExtra(MedicineActionReceiver.EXTRA_REMINDER_ID, reminder.pendingReminderId)
            putExtra(MedicineActionReceiver.EXTRA_MEDICINE_ID, reminder.medicineId)
            putExtra(MedicineActionReceiver.EXTRA_NOTIFICATION_ID, reminder.notificationId)
            putExtra(MedicineActionReceiver.EXTRA_FOLLOW_UP_NOTIFICATION_ID, reminder.followUpNotificationId)
        }
        val skippedPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.followUpNotificationId + 200000,
            skippedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Medicine reminder"
        val subtitle = if (isLockScreenPrivacyEnabled) "Still waiting for response" else "${reminder.medicineName} · ${reminder.doseFormatted}"
        val expandedText = if (isLockScreenPrivacyEnabled) {
            "Tap to respond to your reminder"
        } else {
            "Still waiting for your response\n15 minutes past ${reminder.scheduledTime}"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_MEDICINE_FOLLOW_UP)
            .setSmallIcon(R.drawable.ic_nav_bell)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setSubText("TrackWell")
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(0xFFD97706.toInt())
            .addAction(
                android.R.drawable.checkbox_on_background,
                "Took it",
                tookPendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "Skipped",
                skippedPendingIntent
            )

        nm.notify(reminder.followUpNotificationId, builder.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager?.cancel(notificationId)
    }

    companion object {
        const val CHANNEL_MEDICINE_REMINDERS = "medicine_reminders"
        const val CHANNEL_MEDICINE_FOLLOW_UP = "medicine_follow_up"

        const val EXTRA_NAVIGATE_TO = "extra_navigate_to"
        const val EXTRA_PENDING_REMINDER_ID = "extra_pending_reminder_id"
    }
}
