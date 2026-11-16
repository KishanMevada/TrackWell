package com.trackwell.app.domain.repository

import com.trackwell.app.domain.model.DailyFluidSummary
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.MedicationItem
import com.trackwell.app.domain.model.OptionalMetrics
import com.trackwell.app.domain.model.PeriodAverages
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarSlot
import kotlinx.coroutines.flow.Flow

interface FluidRepository {
    fun observeEntriesForDate(date: String): Flow<List<FluidEntry>>
    suspend fun getEntriesForDate(date: String): List<FluidEntry>
    fun observeDailySummary(date: String): Flow<DailyFluidSummary>
    suspend fun logFluid(date: String, timestamp: Long, amountMl: Int, type: FluidType, note: String? = null): Long
    suspend fun updateEntry(entry: FluidEntry)
    suspend fun deleteEntry(id: Long)
    fun getEntriesInRange(startDate: String, endDate: String): Flow<List<FluidEntry>>
    suspend fun getEntriesListInRange(startDate: String, endDate: String): List<FluidEntry>
}

interface SugarRepository {
    fun observeEntriesForDate(date: String): Flow<Map<SugarSlot, SugarEntry>>
    suspend fun getEntriesForDate(date: String): Map<SugarSlot, SugarEntry>
    suspend fun logSugar(date: String, slot: SugarSlot, sugarMgDl: Int, insulinUnits: Double, timestamp: Long, note: String? = null): Long
    suspend fun deleteEntry(id: Long)
    fun getEntriesInRange(startDate: String, endDate: String): Flow<List<SugarEntry>>
    suspend fun getEntriesListInRange(startDate: String, endDate: String): List<SugarEntry>
}

interface DailyHealthRepository {
    fun observeDayOverview(date: String): Flow<DayOverview>
    suspend fun getDayOverview(date: String): DayOverview
    suspend fun updateMorningWeight(date: String, weightKg: Double)
    suspend fun deleteMorningWeight(date: String)
    fun getPeriodAverages(startDate: String, endDate: String): Flow<PeriodAverages>
    fun getAllDaysInRange(startDate: String, endDate: String): Flow<List<DayOverview>>
    suspend fun getAllDaysListInRange(startDate: String, endDate: String): List<DayOverview>
    
    fun observeOptionalMetrics(date: String): Flow<OptionalMetrics?>
    suspend fun saveOptionalMetrics(metrics: OptionalMetrics): Long
    
    fun observeMedications(date: String): Flow<List<MedicationItem>>
    suspend fun addMedication(item: MedicationItem): Long
    suspend fun toggleMedication(id: Long, isTaken: Boolean)
}
