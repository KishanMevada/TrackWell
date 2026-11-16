package com.trackwell.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.FluidEntryEntity
import com.trackwell.app.data.local.entity.ImportBatchEntity
import com.trackwell.app.data.local.entity.MedicationEntity
import com.trackwell.app.data.local.entity.OptionalMetricEntity
import com.trackwell.app.data.local.entity.SugarEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyLog(date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    fun observeDailyLog(date: String): Flow<DailyLogEntity?>

    @Query("SELECT * FROM daily_logs WHERE date < :date AND morningWeight IS NOT NULL ORDER BY date DESC LIMIT 1")
    suspend fun getPreviousWeightLog(date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getDailyLogsInRange(startDate: String, endDate: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getDailyLogsListInRange(startDate: String, endDate: String): List<DailyLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dailyLog: DailyLogEntity)

    @Query("UPDATE daily_logs SET morningWeight = :weight WHERE date = :date")
    suspend fun updateMorningWeight(date: String, weight: Double)

    @Query("DELETE FROM daily_logs WHERE date = :date")
    suspend fun delete(date: String)

    @Query("DELETE FROM daily_logs")
    suspend fun clearAll()
}

@Dao
interface FluidEntryDao {
    @Query("SELECT * FROM fluid_entries WHERE date = :date ORDER BY timestamp ASC")
    fun observeEntriesForDate(date: String): Flow<List<FluidEntryEntity>>

    @Query("SELECT * FROM fluid_entries WHERE date = :date ORDER BY timestamp ASC")
    suspend fun getEntriesForDate(date: String): List<FluidEntryEntity>

    @Query("SELECT * FROM fluid_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp ASC")
    fun getEntriesInRange(startDate: String, endDate: String): Flow<List<FluidEntryEntity>>

    @Query("SELECT * FROM fluid_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp ASC")
    suspend fun getEntriesListInRange(startDate: String, endDate: String): List<FluidEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FluidEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FluidEntryEntity>)

    @Update
    suspend fun update(entry: FluidEntryEntity)

    @Delete
    suspend fun delete(entry: FluidEntryEntity)

    @Query("DELETE FROM fluid_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM fluid_entries WHERE importBatchId = :batchId")
    suspend fun deleteByBatchId(batchId: Long)

    @Query("DELETE FROM fluid_entries WHERE date = :date")
    suspend fun deleteForDate(date: String)

    @Query("DELETE FROM fluid_entries")
    suspend fun clearAll()
}

@Dao
interface SugarEntryDao {
    @Query("SELECT * FROM sugar_entries WHERE date = :date")
    fun observeEntriesForDate(date: String): Flow<List<SugarEntryEntity>>

    @Query("SELECT * FROM sugar_entries WHERE date = :date")
    suspend fun getEntriesForDate(date: String): List<SugarEntryEntity>

    @Query("SELECT * FROM sugar_entries WHERE date = :date AND slot = :slot LIMIT 1")
    suspend fun getEntryForSlot(date: String, slot: String): SugarEntryEntity?

    @Query("SELECT * FROM sugar_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, timestamp ASC")
    fun getEntriesInRange(startDate: String, endDate: String): Flow<List<SugarEntryEntity>>

    @Query("SELECT * FROM sugar_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, timestamp ASC")
    suspend fun getEntriesListInRange(startDate: String, endDate: String): List<SugarEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: SugarEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<SugarEntryEntity>)

    @Delete
    suspend fun delete(entry: SugarEntryEntity)

    @Query("DELETE FROM sugar_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sugar_entries WHERE importBatchId = :batchId")
    suspend fun deleteByBatchId(batchId: Long)

    @Query("DELETE FROM sugar_entries WHERE date = :date")
    suspend fun deleteForDate(date: String)

    @Query("DELETE FROM sugar_entries")
    suspend fun clearAll()
}

@Dao
interface OptionalMetricDao {
    @Query("SELECT * FROM optional_metrics WHERE date = :date LIMIT 1")
    fun observeForDate(date: String): Flow<OptionalMetricEntity?>

    @Query("SELECT * FROM optional_metrics WHERE date = :date LIMIT 1")
    suspend fun getForDate(date: String): OptionalMetricEntity?

    @Query("SELECT * FROM optional_metrics WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getInRange(startDate: String, endDate: String): Flow<List<OptionalMetricEntity>>

    @Query("SELECT * FROM optional_metrics ORDER BY date ASC")
    suspend fun getAllList(): List<OptionalMetricEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(metric: OptionalMetricEntity): Long

    @Delete
    suspend fun delete(metric: OptionalMetricEntity)

    @Query("DELETE FROM optional_metrics")
    suspend fun clearAll()
}

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE date = :date ORDER BY scheduledTime ASC")
    fun observeForDate(date: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE date = :date ORDER BY scheduledTime ASC")
    suspend fun getForDate(date: String): List<MedicationEntity>

    @Query("SELECT * FROM medications ORDER BY date ASC, scheduledTime ASC")
    suspend fun getAllList(): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MedicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MedicationEntity>)

    @Update
    suspend fun update(item: MedicationEntity)

    @Delete
    suspend fun delete(item: MedicationEntity)

    @Query("UPDATE medications SET isTaken = :isTaken WHERE id = :id")
    suspend fun updateStatus(id: Long, isTaken: Boolean)

    @Query("DELETE FROM medications")
    suspend fun clearAll()
}

@Dao
interface ImportBatchDao {
    @Query("SELECT * FROM import_batches ORDER BY timestamp DESC")
    fun observeAllBatches(): Flow<List<ImportBatchEntity>>

    @Query("SELECT * FROM import_batches WHERE id = :id LIMIT 1")
    suspend fun getBatchById(id: Long): ImportBatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: ImportBatchEntity): Long

    @Update
    suspend fun update(batch: ImportBatchEntity)

    @Delete
    suspend fun delete(batch: ImportBatchEntity)
}
