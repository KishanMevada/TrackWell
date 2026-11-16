package com.trackwell.app.data.repository

import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.SugarEntryDao
import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.SugarEntryEntity
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.SugarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SugarRepositoryImpl @Inject constructor(
    private val sugarEntryDao: SugarEntryDao,
    private val dailyLogDao: DailyLogDao
) : SugarRepository {

    override fun observeEntriesForDate(date: String): Flow<Map<SugarSlot, SugarEntry>> {
        return sugarEntryDao.observeEntriesForDate(date).map { list ->
            list.mapNotNull { entity ->
                try {
                    val slot = SugarSlot.valueOf(entity.slot)
                    slot to entity.toDomain()
                } catch (e: Exception) {
                    null
                }
            }.toMap()
        }
    }

    override suspend fun getEntriesForDate(date: String): Map<SugarSlot, SugarEntry> {
        return sugarEntryDao.getEntriesForDate(date).mapNotNull { entity ->
            try {
                val slot = SugarSlot.valueOf(entity.slot)
                slot to entity.toDomain()
            } catch (e: Exception) {
                null
            }
        }.toMap()
    }

    override suspend fun logSugar(
        date: String,
        slot: SugarSlot,
        sugarMgDl: Int,
        insulinUnits: Double,
        timestamp: Long,
        note: String?
    ): Long {
        val currentLog = dailyLogDao.getDailyLog(date)
        if (currentLog == null) {
            dailyLogDao.insertOrUpdate(DailyLogEntity(date = date))
        }

        val existing = sugarEntryDao.getEntryForSlot(date, slot.name)
        val entity = SugarEntryEntity(
            id = existing?.id ?: 0,
            date = date,
            slot = slot.name,
            sugarMgDl = sugarMgDl,
            insulinUnits = insulinUnits,
            timestamp = timestamp,
            note = note
        )
        return sugarEntryDao.insertOrUpdate(entity)
    }

    override suspend fun deleteEntry(id: Long) {
        sugarEntryDao.deleteById(id)
    }

    override fun getEntriesInRange(startDate: String, endDate: String): Flow<List<SugarEntry>> {
        return sugarEntryDao.getEntriesInRange(startDate, endDate).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getEntriesListInRange(startDate: String, endDate: String): List<SugarEntry> {
        return sugarEntryDao.getEntriesListInRange(startDate, endDate).map { it.toDomain() }
    }

    private fun SugarEntryEntity.toDomain(): SugarEntry {
        return SugarEntry(
            id = id,
            date = date,
            slot = SugarSlot.fromString(slot),
            sugarMgDl = sugarMgDl,
            insulinUnits = insulinUnits,
            timestamp = timestamp,
            note = note
        )
    }
}
