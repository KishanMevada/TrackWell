package com.trackwell.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DailyFluidSummary(
    val totalIntakeMl: Int = 0,
    val totalUrineMl: Int = 0,
    val balanceMl: Int = 0,       // Intake - Urine
    val targetMl: Int = 2000,     // Default daily target
    val progress: Float = 0f      // Intake / Target (capped or displayed as ratio)
)

@Immutable
data class DayOverview(
    val date: String,             // "yyyy-MM-dd"
    val morningWeightKg: Double? = null,
    val weightDiffPreviousDay: Double? = null,
    val fluidSummary: DailyFluidSummary = DailyFluidSummary(),
    val intakeEntries: List<FluidEntry> = emptyList(),
    val urineEntries: List<FluidEntry> = emptyList(),
    val sugarEntries: Map<SugarSlot, SugarEntry> = emptyMap(),
    val optionalMetrics: OptionalMetrics? = null,
    val medications: List<MedicationItem> = emptyList()
)

@Immutable
data class PeriodAverages(
    val dayCount: Int = 0,
    val daysWithData: Int = 0,
    val averageWeightKg: Double = 0.0,
    val averageIntakeMl: Double = 0.0,
    val averageUrineMl: Double = 0.0,
    val averageBalanceMl: Double = 0.0,
    val minBalanceMl: Int = 0,
    val maxBalanceMl: Int = 0,
    val averageSugarMgDl: Double = 0.0,
    val totalInsulinUnits: Double = 0.0
)

@Immutable
data class OptionalMetrics(
    val id: Long = 0,
    val date: String,
    val timestamp: Long,
    val systolicBp: Int? = null,
    val diastolicBp: Int? = null,
    val pulseBpm: Int? = null,
    val temperatureCelsius: Double? = null,
    val note: String? = null
)

@Immutable
data class MedicationItem(
    val id: Long = 0,
    val date: String,
    val name: String,
    val dosage: String,
    val scheduledTime: String,
    val isTaken: Boolean = false
)
