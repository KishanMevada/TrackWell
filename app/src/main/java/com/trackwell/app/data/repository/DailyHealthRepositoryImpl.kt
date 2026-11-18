package com.trackwell.app.data.repository

import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.FluidEntryDao
import com.trackwell.app.data.local.dao.MedicationDao
import com.trackwell.app.data.local.dao.OptionalMetricDao
import com.trackwell.app.data.local.dao.SugarEntryDao
import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.FluidEntryEntity
import com.trackwell.app.data.local.entity.MedicationEntity
import com.trackwell.app.data.local.entity.OptionalMetricEntity
import com.trackwell.app.data.local.entity.SugarEntryEntity
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.MedicationItem
import com.trackwell.app.domain.model.OptionalMetrics
import com.trackwell.app.domain.model.PeriodAverages
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.usecase.CalculateDailySummaryUseCase
import com.trackwell.app.domain.usecase.CalculatePeriodAveragesUseCase
import com.trackwell.app.domain.util.DateTimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyHealthRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val fluidEntryDao: FluidEntryDao,
    private val sugarEntryDao: SugarEntryDao,
    private val optionalMetricDao: OptionalMetricDao,
    private val medicationDao: MedicationDao,
    private val userPreferences: UserPreferences,
    private val calculateDailySummaryUseCase: CalculateDailySummaryUseCase,
    private val calculatePeriodAveragesUseCase: CalculatePeriodAveragesUseCase
) : DailyHealthRepository {

    override fun observeDayOverview(date: String): Flow<DayOverview> {
        val fluidAndLogFlow = combine(
            dailyLogDao.observeDailyLog(date),
            fluidEntryDao.observeEntriesForDate(date),
            userPreferences.settingsFlow
        ) { dailyLog, fluidEntities, settings ->
            Triple(dailyLog, fluidEntities.map { it.toDomain() }, settings.fluidTargetMl)
        }

        val sugarAndOthersFlow = combine(
            sugarEntryDao.observeEntriesForDate(date),
            optionalMetricDao.observeForDate(date),
            medicationDao.observeForDate(date)
        ) { sugarEntities, optMetric, meds ->
            val sugarMap = sugarEntities.associate { entity ->
                SugarSlot.fromString(entity.slot) to entity.toDomain()
            }
            Triple(sugarMap, optMetric?.toDomain(), meds.map { it.toDomain() })
        }

        return combine(fluidAndLogFlow, sugarAndOthersFlow) { (dailyLog, fluidEntries, targetMl), (sugarMap, optMetric, meds) ->
            val intakeList = fluidEntries.filter { it.type == FluidType.INTAKE }
            val urineList = fluidEntries.filter { it.type == FluidType.URINE }
            val fluidSummary = calculateDailySummaryUseCase.execute(fluidEntries, targetMl)

            val prevLog = dailyLogDao.getPreviousWeightLog(date)
            val weightDiff = calculateDailySummaryUseCase.calculateWeightDifference(
                dailyLog?.morningWeight,
                prevLog?.morningWeight
            )

            DayOverview(
                date = date,
                morningWeightKg = dailyLog?.morningWeight,
                weightDiffPreviousDay = weightDiff,
                fluidSummary = fluidSummary,
                intakeEntries = intakeList,
                urineEntries = urineList,
                sugarEntries = sugarMap,
                optionalMetrics = optMetric,
                medications = meds
            )
        }
    }

    override suspend fun getDayOverview(date: String): DayOverview {
        val dailyLog = dailyLogDao.getDailyLog(date)
        val fluidEntities = fluidEntryDao.getEntriesForDate(date)
        val sugarEntities = sugarEntryDao.getEntriesForDate(date)
        val optMetric = optionalMetricDao.getForDate(date)
        val meds = medicationDao.getForDate(date)

        val fluidEntries = fluidEntities.map { it.toDomain() }
        val intakeList = fluidEntries.filter { it.type == FluidType.INTAKE }
        val urineList = fluidEntries.filter { it.type == FluidType.URINE }

        val fluidSummary = calculateDailySummaryUseCase.execute(fluidEntries, 2000)

        val sugarMap = sugarEntities.mapNotNull { entity ->
            try {
                SugarSlot.valueOf(entity.slot) to entity.toDomain()
            } catch (e: Exception) {
                null
            }
        }.toMap()

        val prevLog = dailyLogDao.getPreviousWeightLog(date)
        val weightDiff = calculateDailySummaryUseCase.calculateWeightDifference(
            dailyLog?.morningWeight,
            prevLog?.morningWeight
        )

        return DayOverview(
            date = date,
            morningWeightKg = dailyLog?.morningWeight,
            weightDiffPreviousDay = weightDiff,
            fluidSummary = fluidSummary,
            intakeEntries = intakeList,
            urineEntries = urineList,
            sugarEntries = sugarMap,
            optionalMetrics = optMetric?.toDomain(),
            medications = meds.map { it.toDomain() }
        )
    }

    override suspend fun updateMorningWeight(date: String, weightKg: Double) {
        val currentLog = dailyLogDao.getDailyLog(date)
        val rounded = Math.round(weightKg * 100.0) / 100.0
        if (currentLog != null) {
            dailyLogDao.updateMorningWeight(date, rounded)
        } else {
            dailyLogDao.insertOrUpdate(DailyLogEntity(date = date, morningWeight = rounded))
        }
    }

    override suspend fun deleteMorningWeight(date: String) {
        val currentLog = dailyLogDao.getDailyLog(date)
        if (currentLog != null) {
            dailyLogDao.insertOrUpdate(currentLog.copy(morningWeight = null))
        }
    }

    override fun getAllDaysInRange(startDate: String, endDate: String): Flow<List<DayOverview>> {
        return combine(
            dailyLogDao.getDailyLogsInRange(startDate, endDate),
            fluidEntryDao.getEntriesInRange(startDate, endDate),
            sugarEntryDao.getEntriesInRange(startDate, endDate)
        ) { dailyLogs, fluidEntities, sugarEntities ->
            val allDates = DateTimeUtils.getDaysListBetween(startDate, endDate)
            val logsByDate = dailyLogs.associateBy { it.date }
            val fluidsByDate = fluidEntities.groupBy { it.date }
            val sugarsByDate = sugarEntities.groupBy { it.date }

            var lastKnownWeight: Double? = null

            allDates.map { date ->
                val log = logsByDate[date]
                val fluids = (fluidsByDate[date] ?: emptyList()).map { it.toDomain() }
                val sugars = (sugarsByDate[date] ?: emptyList()).associate { entity ->
                    SugarSlot.fromString(entity.slot) to entity.toDomain()
                }

                val summary = calculateDailySummaryUseCase.execute(fluids, 2000)
                val currentWeight = log?.morningWeight
                val weightDiff = if (currentWeight != null && lastKnownWeight != null) {
                    calculateDailySummaryUseCase.calculateWeightDifference(currentWeight, lastKnownWeight)
                } else null

                if (currentWeight != null) {
                    lastKnownWeight = currentWeight
                }

                DayOverview(
                    date = date,
                    morningWeightKg = currentWeight,
                    weightDiffPreviousDay = weightDiff,
                    fluidSummary = summary,
                    intakeEntries = fluids.filter { it.type == FluidType.INTAKE },
                    urineEntries = fluids.filter { it.type == FluidType.URINE },
                    sugarEntries = sugars
                )
            }
        }
    }

    override suspend fun getAllDaysListInRange(startDate: String, endDate: String): List<DayOverview> {
        val dailyLogs = dailyLogDao.getDailyLogsListInRange(startDate, endDate)
        val fluidEntities = fluidEntryDao.getEntriesListInRange(startDate, endDate)
        val sugarEntities = sugarEntryDao.getEntriesListInRange(startDate, endDate)

        val allDates = DateTimeUtils.getDaysListBetween(startDate, endDate)
        val logsByDate = dailyLogs.associateBy { it.date }
        val fluidsByDate = fluidEntities.groupBy { it.date }
        val sugarsByDate = sugarEntities.groupBy { it.date }

        var lastKnownWeight: Double? = null

        return allDates.map { date ->
            val log = logsByDate[date]
            val fluids = (fluidsByDate[date] ?: emptyList()).map { it.toDomain() }
            val sugars = (sugarsByDate[date] ?: emptyList()).mapNotNull { entity ->
                try {
                    SugarSlot.valueOf(entity.slot) to entity.toDomain()
                } catch (e: Exception) {
                    null
                }
            }.toMap()

            val summary = calculateDailySummaryUseCase.execute(fluids, 2000)
            val currentWeight = log?.morningWeight
            val weightDiff = if (currentWeight != null && lastKnownWeight != null) {
                calculateDailySummaryUseCase.calculateWeightDifference(currentWeight, lastKnownWeight)
            } else null

            if (currentWeight != null) {
                lastKnownWeight = currentWeight
            }

            DayOverview(
                date = date,
                morningWeightKg = currentWeight,
                weightDiffPreviousDay = weightDiff,
                fluidSummary = summary,
                intakeEntries = fluids.filter { it.type == FluidType.INTAKE },
                urineEntries = fluids.filter { it.type == FluidType.URINE },
                sugarEntries = sugars
            )
        }
    }

    override fun getPeriodAverages(startDate: String, endDate: String): Flow<PeriodAverages> {
        return getAllDaysInRange(startDate, endDate).map { days ->
            calculatePeriodAveragesUseCase.execute(days)
        }
    }

    override fun observeOptionalMetrics(date: String): Flow<OptionalMetrics?> {
        return optionalMetricDao.observeForDate(date).map { it?.toDomain() }
    }

    override suspend fun saveOptionalMetrics(metrics: OptionalMetrics): Long {
        val entity = OptionalMetricEntity(
            id = metrics.id,
            date = metrics.date,
            timestamp = metrics.timestamp,
            systolic = metrics.systolicBp,
            diastolic = metrics.diastolicBp,
            pulse = metrics.pulseBpm,
            temperatureCelsius = metrics.temperatureCelsius,
            note = metrics.note
        )
        return optionalMetricDao.insertOrUpdate(entity)
    }

    override fun observeMedications(date: String): Flow<List<MedicationItem>> {
        return medicationDao.observeForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addMedication(item: MedicationItem): Long {
        return medicationDao.insert(
            MedicationEntity(
                date = item.date,
                name = item.name,
                dosage = item.dosage,
                scheduledTime = item.scheduledTime,
                isTaken = item.isTaken
            )
        )
    }

    override suspend fun toggleMedication(id: Long, isTaken: Boolean) {
        medicationDao.updateStatus(id, isTaken)
    }

    override suspend fun getEarliestRecordDate(): String {
        val d1 = dailyLogDao.getEarliestDate()
        val d2 = fluidEntryDao.getEarliestDate()
        val d3 = sugarEntryDao.getEarliestDate()
        val dates = listOfNotNull(d1, d2, d3).filter { it.isNotBlank() }
        val earliest = dates.minOrNull() ?: DateTimeUtils.getTodayIso()
        val today = DateTimeUtils.getTodayIso()
        return if (earliest > today) today else earliest
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

    private fun OptionalMetricEntity.toDomain(): OptionalMetrics {
        return OptionalMetrics(
            id = id,
            date = date,
            timestamp = timestamp,
            systolicBp = systolic,
            diastolicBp = diastolic,
            pulseBpm = pulse,
            temperatureCelsius = temperatureCelsius,
            note = note
        )
    }

    private fun MedicationEntity.toDomain(): MedicationItem {
        return MedicationItem(
            id = id,
            date = date,
            name = name,
            dosage = dosage,
            scheduledTime = scheduledTime,
            isTaken = isTaken
        )
    }
}
