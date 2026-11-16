package com.trackwell.app.data.medicine

import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.domain.medicine.model.PendingMedicineReminder
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    fun observeSchedules(): Flow<List<MedicineSchedule>>
    suspend fun getSchedule(id: String): MedicineSchedule?
    suspend fun getEnabledSchedules(): List<MedicineSchedule>
    suspend fun saveSchedule(schedule: MedicineSchedule): String
    suspend fun updateScheduleEnabled(id: String, isEnabled: Boolean)
    suspend fun deleteSchedule(id: String)

    fun observeActivePendingReminders(): Flow<List<PendingMedicineReminder>>
    suspend fun getPendingReminder(id: String): PendingMedicineReminder?
    suspend fun createPendingReminder(reminder: PendingMedicineReminder)
    suspend fun resolvePendingReminder(pendingReminderId: String)
    suspend fun deletePendingRemindersForMedicine(medicineId: String)
}
