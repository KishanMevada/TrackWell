package com.trackwell.app.data.repository

import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.FluidEntryDao
import com.trackwell.app.data.local.dao.ImportBatchDao
import com.trackwell.app.data.local.dao.SugarEntryDao
import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.FluidEntryEntity
import com.trackwell.app.data.local.entity.ImportBatchEntity
import com.trackwell.app.data.local.entity.SugarEntryEntity
import com.trackwell.app.data.model.DuplicateResolution
import com.trackwell.app.data.model.ParsedDayRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepository @Inject constructor(
    private val importBatchDao: ImportBatchDao,
    private val dailyLogDao: DailyLogDao,
    private val fluidEntryDao: FluidEntryDao,
    private val sugarEntryDao: SugarEntryDao
) {

    fun observeImportBatches(): Flow<List<ImportBatchEntity>> = importBatchDao.observeAllBatches()

    suspend fun saveImportBatch(
        records: List<ParsedDayRecord>,
        source: String
    ): Long {
        val totalItems = records.sumOf { it.fluidItems.size + it.sugarItems.size }
        val batch = ImportBatchEntity(
            timestamp = System.currentTimeMillis(),
            source = source,
            recordCount = totalItems,
            summary = "${records.size} days processed ($totalItems readings)"
        )
        val batchId = importBatchDao.insert(batch)

        for (record in records) {
            val date = record.date

            if (record.isDuplicate && record.duplicateResolution == DuplicateResolution.SKIP) {
                continue
            }

            if (record.duplicateResolution == DuplicateResolution.OVERWRITE) {
                fluidEntryDao.deleteForDate(date)
                sugarEntryDao.deleteForDate(date)
            }

            // Morning weight
            if (record.morningWeightKg != null) {
                val currentLog = dailyLogDao.getDailyLog(date)
                if (currentLog != null) {
                    dailyLogDao.updateMorningWeight(date, record.morningWeightKg!!)
                } else {
                    dailyLogDao.insertOrUpdate(DailyLogEntity(date = date, morningWeight = record.morningWeightKg))
                }
            }

            // Fluid Entries
            record.fluidItems.forEachIndexed { idx, item ->
                val entity = FluidEntryEntity(
                    date = date,
                    timestamp = item.timestamp ?: (System.currentTimeMillis() + idx * 60000),
                    amountMl = item.amountMl,
                    type = item.type.name,
                    importBatchId = batchId
                )
                fluidEntryDao.insert(entity)
            }

            // Sugar Entries
            record.sugarItems.forEach { (slot, item) ->
                val entity = SugarEntryEntity(
                    date = date,
                    slot = slot.name,
                    sugarMgDl = item.sugarMgDl,
                    insulinUnits = item.insulinUnits,
                    timestamp = System.currentTimeMillis(),
                    importBatchId = batchId
                )
                sugarEntryDao.insertOrUpdate(entity)
            }
        }

        return batchId
    }

    suspend fun undoImportBatch(batchId: Long) {
        val batch = importBatchDao.getBatchById(batchId) ?: return
        fluidEntryDao.deleteByBatchId(batchId)
        sugarEntryDao.deleteByBatchId(batchId)
        importBatchDao.update(batch.copy(isUndone = true))
    }
}
