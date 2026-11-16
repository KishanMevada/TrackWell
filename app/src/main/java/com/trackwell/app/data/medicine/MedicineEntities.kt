package com.trackwell.app.data.medicine

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.trackwell.app.domain.medicine.model.MedicineDoseUnit
import com.trackwell.app.domain.medicine.model.MedicineRepeatMode
import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.domain.medicine.model.PendingMedicineReminder
import java.time.DayOfWeek

@Entity(
    tableName = "medicine_schedules",
    indices = [Index(value = ["isEnabled"])]
)
data class MedicineScheduleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val doseValue: String,
    val doseUnit: String,
    val isEnabled: Boolean = true,
    val repeatMode: String = "DAILY",
    val selectedDaysOfWeek: String = "", // Comma-separated enum names, e.g. "MONDAY,WEDNESDAY"
    val startDate: String? = null,
    val endDate: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "medicine_reminder_times",
    foreignKeys = [
        ForeignKey(
            entity = MedicineScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medicineId"])]
)
data class MedicineReminderTimeEntity(
    @PrimaryKey
    val id: String,
    val medicineId: String,
    val localTime: String, // "HH:mm"
    val sortOrder: Int = 0
)

@Entity(tableName = "pending_reminders")
data class PendingReminderEntity(
    @PrimaryKey
    val pendingReminderId: String,
    val medicineId: String,
    val medicineName: String,
    val doseFormatted: String,
    val scheduledTime: String,
    val scheduledAt: Long,
    val followUpAt: Long,
    val state: String = "PENDING",
    val notificationId: Int,
    val followUpNotificationId: Int,
    val isFollowUp: Boolean = false
) {
    fun toDomain(): PendingMedicineReminder = PendingMedicineReminder(
        pendingReminderId = pendingReminderId,
        medicineId = medicineId,
        medicineName = medicineName,
        doseFormatted = doseFormatted,
        scheduledTime = scheduledTime,
        scheduledAt = scheduledAt,
        followUpAt = followUpAt,
        state = state,
        notificationId = notificationId,
        followUpNotificationId = followUpNotificationId,
        isFollowUp = isFollowUp
    )

    companion object {
        fun fromDomain(domain: PendingMedicineReminder): PendingReminderEntity = PendingReminderEntity(
            pendingReminderId = domain.pendingReminderId,
            medicineId = domain.medicineId,
            medicineName = domain.medicineName,
            doseFormatted = domain.doseFormatted,
            scheduledTime = domain.scheduledTime,
            scheduledAt = domain.scheduledAt,
            followUpAt = domain.followUpAt,
            state = domain.state,
            notificationId = domain.notificationId,
            followUpNotificationId = domain.followUpNotificationId,
            isFollowUp = domain.isFollowUp
        )
    }
}

/**
 * Composite relation containing a schedule and its list of reminder times.
 */
data class MedicineScheduleWithTimes(
    @Embedded
    val schedule: MedicineScheduleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "medicineId"
    )
    val times: List<MedicineReminderTimeEntity>
) {
    fun toDomain(): MedicineSchedule {
        val days = if (schedule.selectedDaysOfWeek.isBlank()) {
            emptySet()
        } else {
            schedule.selectedDaysOfWeek.split(",")
                .mapNotNull {
                    try {
                        DayOfWeek.valueOf(it.trim())
                    } catch (e: Exception) {
                        null
                    }
                }.toSet()
        }

        val sortedTimes = times.sortedBy { it.sortOrder }.map { it.localTime }

        return MedicineSchedule(
            id = schedule.id,
            name = schedule.name,
            doseValue = schedule.doseValue,
            doseUnit = MedicineDoseUnit.fromLabel(schedule.doseUnit),
            isEnabled = schedule.isEnabled,
            repeatMode = MedicineRepeatMode.fromString(schedule.repeatMode),
            selectedDaysOfWeek = days,
            startDate = schedule.startDate,
            endDate = schedule.endDate,
            reminderTimes = if (sortedTimes.isEmpty()) listOf("08:00") else sortedTimes,
            createdAt = schedule.createdAt,
            updatedAt = schedule.updatedAt
        )
    }
}
