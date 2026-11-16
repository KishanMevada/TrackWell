package com.trackwell.app.domain.medicine.model

import java.time.DayOfWeek
import java.util.UUID

/**
 * Supported units of measurement for medicine doses.
 */
enum class MedicineDoseUnit(val label: String) {
    MG("mg"),
    MCG("mcg"),
    G("g"),
    ML("ml"),
    TABLET("tablet"),
    CAPSULE("capsule");

    companion object {
        fun fromLabel(label: String): MedicineDoseUnit {
            return entries.find { it.label.equals(label, ignoreCase = true) } ?: MG
        }
    }
}

/**
 * Recurrence rule for medicine reminders.
 */
enum class MedicineRepeatMode(val label: String) {
    DAILY("Every day"),
    SELECTED_DAYS("Choose days");

    companion object {
        fun fromString(value: String): MedicineRepeatMode {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                DAILY
            }
        }
    }
}

/**
 * Domain representation of a persistent medicine schedule.
 * Does NOT contain adherence logs or history.
 */
data class MedicineSchedule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val doseValue: String,
    val doseUnit: MedicineDoseUnit,
    val isEnabled: Boolean = true,
    val repeatMode: MedicineRepeatMode = MedicineRepeatMode.DAILY,
    val selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    val startDate: String? = null,
    val endDate: String? = null,
    val reminderTimes: List<String> = listOf("08:00"), // Formatted as "HH:mm"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val formattedDose: String
        get() = "$doseValue ${doseUnit.label}"

    val formattedRepeat: String
        get() = when (repeatMode) {
            MedicineRepeatMode.DAILY -> "Every day"
            MedicineRepeatMode.SELECTED_DAYS -> {
                if (selectedDaysOfWeek.isEmpty()) {
                    "Every day"
                } else if (selectedDaysOfWeek.size == 7) {
                    "Every day"
                } else {
                    val orderedDays = listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY,
                        DayOfWeek.SUNDAY
                    )
                    selectedDaysOfWeek.sortedBy { orderedDays.indexOf(it) }
                        .joinToString(", ") { day ->
                            day.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                        }
                }
            }
        }
}

/**
 * Temporary pending reminder state awaiting user action (Took it / Skipped).
 * Deleted immediately upon resolution.
 */
data class PendingMedicineReminder(
    val pendingReminderId: String = UUID.randomUUID().toString(),
    val medicineId: String,
    val medicineName: String,
    val doseFormatted: String,
    val scheduledTime: String, // e.g. "8:00 AM"
    val scheduledAt: Long,     // Target epoch millis
    val followUpAt: Long,      // Target epoch millis + 15 min
    val state: String = "PENDING", // PENDING or RESOLVED
    val notificationId: Int,
    val followUpNotificationId: Int,
    val isFollowUp: Boolean = false
)

/**
 * Summary representation of the next upcoming reminder dose for the "Today" card.
 */
data class NextMedicineReminder(
    val medicineId: String,
    val medicineName: String,
    val doseFormatted: String,
    val scheduledTime: String,
    val triggerEpochMillis: Long
)
