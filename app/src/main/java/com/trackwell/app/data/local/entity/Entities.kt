package com.trackwell.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey
    val date: String,             // "yyyy-MM-dd"
    val morningWeight: Double? = null,
    val note: String? = null
)

@Entity(
    tableName = "fluid_entries",
    indices = [Index(value = ["date"]), Index(value = ["timestamp"])]
)
data class FluidEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,             // "yyyy-MM-dd"
    val timestamp: Long,          // Epoch millis
    val amountMl: Int,
    val type: String,             // "INTAKE" or "URINE"
    val note: String? = null,
    val importBatchId: Long? = null
)

@Entity(
    tableName = "sugar_entries",
    indices = [
        Index(value = ["date", "slot"], unique = true),
        Index(value = ["date"])
    ]
)
data class SugarEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,             // "yyyy-MM-dd"
    val slot: String,             // BEFORE_BREAKFAST, etc.
    val sugarMgDl: Int,
    val insulinUnits: Double,
    val timestamp: Long,
    val note: String? = null,
    val importBatchId: Long? = null
)

@Entity(
    tableName = "optional_metrics",
    indices = [Index(value = ["date"])]
)
data class OptionalMetricEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val timestamp: Long,
    val systolic: Int? = null,
    val diastolic: Int? = null,
    val pulse: Int? = null,
    val temperatureCelsius: Double? = null,
    val note: String? = null
)

@Entity(
    tableName = "medications",
    indices = [Index(value = ["date"])]
)
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val name: String,
    val dosage: String,
    val scheduledTime: String,
    val isTaken: Boolean = false
)

@Entity(tableName = "import_batches")
data class ImportBatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val source: String,           // "IMAGE_OCR", "CSV", "EXCEL", "GEMINI"
    val recordCount: Int,
    val isUndone: Boolean = false,
    val summary: String = ""
)
