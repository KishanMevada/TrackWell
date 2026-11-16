package com.trackwell.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.trackwell.app.MainActivity
import com.trackwell.app.TrackWellApp
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        when (action) {
            ACTION_SUGAR_REMINDER -> {
                val slotName = intent.getStringExtra(EXTRA_SLOT_NAME) ?: SugarSlot.BEFORE_BREAKFAST.name
                val slot = try { SugarSlot.valueOf(slotName) } catch (e: Exception) { SugarSlot.BEFORE_BREAKFAST }
                val notifId = NOTIF_BASE_SUGAR + slot.ordinal

                // Open App intent
                val contentIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val contentPendingIntent = PendingIntent.getActivity(
                    context,
                    notifId,
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Inline Reply
                val remoteInput = RemoteInput.Builder(InlineReplyReceiver.KEY_TEXT_REPLY)
                    .setLabel("Enter sugar reading (mg/dL)")
                    .build()

                val replyIntent = Intent(context, InlineReplyReceiver::class.java).apply {
                    this.action = InlineReplyReceiver.ACTION_INLINE_REPLY_SUGAR
                    putExtra(InlineReplyReceiver.EXTRA_NOTIFICATION_ID, notifId)
                    putExtra(InlineReplyReceiver.EXTRA_SLOT_NAME, slot.name)
                }
                val replyPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notifId,
                    replyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

                val replyAction = NotificationCompat.Action.Builder(
                    android.R.drawable.ic_input_add,
                    "Log Reading",
                    replyPendingIntent
                ).addRemoteInput(remoteInput).build()

                val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_SUGAR_REMINDER)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Time for ${slot.title}")
                    .setContentText("Check and record your blood sugar & insulin dose.")
                    .setContentIntent(contentPendingIntent)
                    .addAction(replyAction)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                notificationManager.notify(notifId, notification)
            }

            ACTION_WEIGHT_REMINDER -> {
                val notifId = NOTIF_WEIGHT

                val contentIntent = Intent(context, MainActivity::class.java)
                val contentPendingIntent = PendingIntent.getActivity(
                    context,
                    notifId,
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_WEIGHT_REMINDER)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Morning Weight Reminder")
                    .setContentText("Record your weight after waking to monitor fluid balance.")
                    .setContentIntent(contentPendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                notificationManager.notify(notifId, notification)
            }

            ACTION_FLUID_NUDGE -> {
                val notifId = NOTIF_FLUID

                // Action to quick log +200ml
                val log200Intent = Intent(context, InlineReplyReceiver::class.java).apply {
                    this.action = InlineReplyReceiver.ACTION_INLINE_LOG_FLUID
                    putExtra(InlineReplyReceiver.EXTRA_NOTIFICATION_ID, notifId)
                    putExtra(InlineReplyReceiver.EXTRA_FLUID_AMOUNT, 200)
                    putExtra(InlineReplyReceiver.EXTRA_FLUID_TYPE, FluidType.INTAKE.name)
                }
                val log200Pending = PendingIntent.getBroadcast(
                    context,
                    notifId + 1,
                    log200Intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val contentIntent = Intent(context, MainActivity::class.java)
                val contentPendingIntent = PendingIntent.getActivity(
                    context,
                    notifId,
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, TrackWellApp.CHANNEL_FLUID_NUDGE)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Hydration Check")
                    .setContentText("Stay on track with your daily fluid target.")
                    .setContentIntent(contentPendingIntent)
                    .addAction(android.R.drawable.ic_input_add, "+200 ml Water", log200Pending)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(notifId, notification)
            }
        }
    }

    companion object {
        const val ACTION_SUGAR_REMINDER = "com.trackwell.app.ACTION_SUGAR_REMINDER"
        const val ACTION_WEIGHT_REMINDER = "com.trackwell.app.ACTION_WEIGHT_REMINDER"
        const val ACTION_FLUID_NUDGE = "com.trackwell.app.ACTION_FLUID_NUDGE"

        const val EXTRA_SLOT_NAME = "extra_slot_name"

        const val NOTIF_BASE_SUGAR = 1000
        const val NOTIF_WEIGHT = 2000
        const val NOTIF_FLUID = 3000
    }
}
