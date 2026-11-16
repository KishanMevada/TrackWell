package com.trackwell.app.data.medicine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineScheduleDao {

    @Transaction
    @Query("SELECT * FROM medicine_schedules ORDER BY createdAt ASC")
    fun observeAllSchedulesWithTimes(): Flow<List<MedicineScheduleWithTimes>>

    @Transaction
    @Query("SELECT * FROM medicine_schedules WHERE id = :id")
    suspend fun getScheduleWithTimesById(id: String): MedicineScheduleWithTimes?

    @Transaction
    @Query("SELECT * FROM medicine_schedules WHERE isEnabled = 1")
    suspend fun getEnabledSchedulesWithTimes(): List<MedicineScheduleWithTimes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MedicineScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderTimes(times: List<MedicineReminderTimeEntity>)

    @Update
    suspend fun updateSchedule(schedule: MedicineScheduleEntity)

    @Query("UPDATE medicine_schedules SET isEnabled = :isEnabled, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateScheduleEnabled(id: String, isEnabled: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM medicine_reminder_times WHERE medicineId = :medicineId")
    suspend fun deleteReminderTimesForMedicine(medicineId: String)

    @Query("DELETE FROM medicine_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: String)

    @Transaction
    suspend fun saveScheduleWithTimes(
        schedule: MedicineScheduleEntity,
        times: List<MedicineReminderTimeEntity>
    ) {
        insertSchedule(schedule)
        deleteReminderTimesForMedicine(schedule.id)
        if (times.isNotEmpty()) {
            insertReminderTimes(times)
        }
    }

    // -------------------------------------------------------------------------
    // Temporary Pending Reminders (Deleted immediately when resolved / skipped)
    // -------------------------------------------------------------------------

    @Query("SELECT * FROM pending_reminders WHERE state = 'PENDING' ORDER BY scheduledAt ASC")
    fun observePendingReminders(): Flow<List<PendingReminderEntity>>

    @Query("SELECT * FROM pending_reminders WHERE pendingReminderId = :id")
    suspend fun getPendingReminderById(id: String): PendingReminderEntity?

    @Query("SELECT * FROM pending_reminders WHERE medicineId = :medicineId")
    suspend fun getPendingRemindersForMedicine(medicineId: String): List<PendingReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingReminder(pending: PendingReminderEntity)

    @Query("DELETE FROM pending_reminders WHERE pendingReminderId = :id")
    suspend fun deletePendingReminderById(id: String)

    @Query("DELETE FROM pending_reminders WHERE medicineId = :medicineId")
    suspend fun deletePendingRemindersByMedicineId(medicineId: String)

    @Query("DELETE FROM pending_reminders WHERE state = 'RESOLVED'")
    suspend fun deleteResolvedPendingReminders()
}
