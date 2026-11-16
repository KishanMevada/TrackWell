package com.trackwell.app.data.repository

import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.FluidEntryDao
import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.FluidEntryEntity
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DailyFluidSummary
import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.usecase.CalculateDailySummaryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FluidRepositoryImpl @Inject constructor(
    private val fluidEntryDao: FluidEntryDao,
    private val dailyLogDao: DailyLogDao,
    private val userPreferences: UserPreferences,
    private val calculateDailySummaryUseCase: CalculateDailySummaryUseCase
) : FluidRepository {

    override fun observeEntriesForDate(date: String): Flow<List<FluidEntry>> {
        return fluidEntryDao.observeEntriesForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getEntriesForDate(date: String): List<FluidEntry> {
        return fluidEntryDao.getEntriesForDate(date).map { it.toDomain() }
    }

    override fun observeDailySummary(date: String): Flow<DailyFluidSummary> {
        return combine(
            observeEntriesForDate(date),
            userPreferences.settingsFlow
        ) { entries, settings ->
            calculateDailySummaryUseCase.execute(entries, settings.fluidTargetMl)
        }
    }

    override suspend fun logFluid(
        date: String,
        timestamp: Long,
        amountMl: Int,
        type: FluidType,
        note: String?
    ): Long {
        // Ensure daily log row exists
        val currentLog = dailyLogDao.getDailyLog(date)
        if (currentLog == null) {
            dailyLogDao.insertOrUpdate(DailyLogEntity(date = date))
        }

        val entity = FluidEntryEntity(
            date = date,
            timestamp = timestamp,
            amountMl = amountMl,
            type = type.name,
            note = note
        )
        return fluidEntryDao.insert(entity)
    }

    override suspend fun updateEntry(entry: FluidEntry) {
        fluidEntryDao.update(
            FluidEntryEntity(
                id = entry.id,
                date = entry.date,
                timestamp = entry.timestamp,
                amountMl = entry.amountMl,
                type = entry.type.name,
                note = entry.note
            )
        )
    }

    override suspend fun deleteEntry(id: Long) {
        fluidEntryDao.deleteById(id)
    }

    override fun getEntriesInRange(startDate: String, endDate: String): Flow<List<FluidEntry>> {
        return fluidEntryDao.getEntriesInRange(startDate, endDate).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getEntriesListInRange(startDate: String, endDate: String): List<FluidEntry> {
        return fluidEntryDao.getEntriesListInRange(startDate, endDate).map { it.toDomain() }
    }

    private fun FluidEntryEntity.toDomain(): FluidEntry {
        return FluidEntry(
            id = id,
            date = date,
            timestamp = timestamp,
            amountMl = amountMl,
            type = FluidType.valueOf(type),
            note = note
        )
    }
}
