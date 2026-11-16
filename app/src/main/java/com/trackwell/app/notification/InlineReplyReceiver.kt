package com.trackwell.app.notification

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.repository.SugarRepository
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InlineReplyReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sugarRepository: SugarRepository

    @Inject
    lateinit var fluidRepository: FluidRepository

    @Inject
    lateinit var dailyHealthRepository: DailyHealthRepository

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        when (action) {
            ACTION_INLINE_REPLY_SUGAR -> {
                val results = RemoteInput.getResultsFromIntent(intent) ?: return
                val replyText = results.getCharSequence(KEY_TEXT_REPLY)?.toString() ?: return
                val slotName = intent.getStringExtra(EXTRA_SLOT_NAME) ?: SugarSlot.BEFORE_BREAKFAST.name
                val slot = try { SugarSlot.valueOf(slotName) } catch (e: Exception) { SugarSlot.BEFORE_BREAKFAST }

                val reading = replyText.toIntOrNull()
                if (reading != null && reading > 0) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            sugarRepository.logSugar(
                                date = DateTimeUtils.getTodayIso(),
                                slot = slot,
                                sugarMgDl = reading,
                                insulinUnits = 0.0,
                                timestamp = System.currentTimeMillis()
                            )
                            // Update notification to confirmed
                            val updatedNotification = NotificationCompat.Builder(context, CHANNEL_SUGAR_REMINDER)
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle("Blood Sugar Logged")
                                .setContentText("${slot.title}: $reading mg/dL recorded")
                                .setTimeoutAfter(5000)
                                .build()
                            notificationManager?.notify(notificationId, updatedNotification)
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }

            ACTION_INLINE_LOG_FLUID -> {
                val amount = intent.getIntExtra(EXTRA_FLUID_AMOUNT, 200)
                val typeName = intent.getStringExtra(EXTRA_FLUID_TYPE) ?: FluidType.INTAKE.name
                val type = FluidType.valueOf(typeName)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        fluidRepository.logFluid(
                            date = DateTimeUtils.getTodayIso(),
                            timestamp = System.currentTimeMillis(),
                            amountMl = amount,
                            type = type
                        )
                        val updatedNotification = NotificationCompat.Builder(context, CHANNEL_FLUID_NUDGE)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle("Fluid Logged")
                            .setContentText("Added $amount ml ${if (type == FluidType.INTAKE) "Intake" else "Urine"}")
                            .setTimeoutAfter(4000)
                            .build()
                        notificationManager?.notify(notificationId, updatedNotification)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_INLINE_REPLY_SUGAR = "com.trackwell.app.ACTION_INLINE_REPLY_SUGAR"
        const val ACTION_INLINE_LOG_FLUID = "com.trackwell.app.ACTION_INLINE_LOG_FLUID"
        const val KEY_TEXT_REPLY = "key_text_reply"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_SLOT_NAME = "extra_slot_name"
        const val EXTRA_FLUID_AMOUNT = "extra_fluid_amount"
        const val EXTRA_FLUID_TYPE = "extra_fluid_type"
        const val CHANNEL_SUGAR_REMINDER = "channel_sugar_reminder"
        const val CHANNEL_FLUID_NUDGE = "channel_fluid_nudge"
    }
}
