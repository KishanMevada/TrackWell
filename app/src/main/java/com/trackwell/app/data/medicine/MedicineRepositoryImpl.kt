package com.trackwell.app.data.medicine

import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.domain.medicine.model.PendingMedicineReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineRepositoryImpl @Inject constructor(
    private val dao: MedicineScheduleDao
) : MedicineRepository {

    override fun observeSchedules(): Flow<List<MedicineSchedule>> {
        return dao.observeAllSchedulesWithTimes().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getSchedule(id: String): MedicineSchedule? {
        return dao.getScheduleWithTimesById(id)?.toDomain()
    }

    override suspend fun getEnabledSchedules(): List<MedicineSchedule> {
        return dao.getEnabledSchedulesWithTimes().map { it.toDomain() }
    }

    override suspend fun saveSchedule(schedule: MedicineSchedule): String {
        val daysString = schedule.selectedDaysOfWeek.joinToString(",") { it.name }

        val scheduleEntity = MedicineScheduleEntity(
            id = schedule.id,
            name = schedule.name.trim(),
            doseValue = schedule.doseValue.trim(),
            doseUnit = schedule.doseUnit.label,
            isEnabled = schedule.isEnabled,
            repeatMode = schedule.repeatMode.name,
            selectedDaysOfWeek = daysString,
            startDate = schedule.startDate,
            endDate = schedule.endDate,
            createdAt = schedule.createdAt,
            updatedAt = System.currentTimeMillis()
        )

        val timesEntities = schedule.reminderTimes.mapIndexed { index, timeStr ->
            MedicineReminderTimeEntity(
                id = "${schedule.id}_time_$index",
                medicineId = schedule.id,
                localTime = timeStr.trim(),
                sortOrder = index
            )
        }

        dao.saveScheduleWithTimes(scheduleEntity, timesEntities)
        return schedule.id
    }

    override suspend fun updateScheduleEnabled(id: String, isEnabled: Boolean) {
        dao.updateScheduleEnabled(id, isEnabled)
    }

    override suspend fun deleteSchedule(id: String) {
        dao.deleteScheduleById(id)
        dao.deleteReminderTimesForMedicine(id)
        dao.deletePendingRemindersByMedicineId(id)
    }

    override fun observeActivePendingReminders(): Flow<List<PendingMedicineReminder>> {
        return dao.observePendingReminders().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getPendingReminder(id: String): PendingMedicineReminder? {
        return dao.getPendingReminderById(id)?.toDomain()
    }

    override suspend fun createPendingReminder(reminder: PendingMedicineReminder) {
        dao.insertPendingReminder(PendingReminderEntity.fromDomain(reminder))
    }

    override suspend fun resolvePendingReminder(pendingReminderId: String) {
        // As per product rule: Delete immediately upon resolution, never retain history log
        dao.deletePendingReminderById(pendingReminderId)
    }

    override suspend fun deletePendingRemindersForMedicine(medicineId: String) {
        dao.deletePendingRemindersByMedicineId(medicineId)
    }
}
