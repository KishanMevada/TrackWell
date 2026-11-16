package com.trackwell.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.FluidEntryDao
import com.trackwell.app.data.local.dao.ImportBatchDao
import com.trackwell.app.data.local.dao.MedicationDao
import com.trackwell.app.data.local.dao.OptionalMetricDao
import com.trackwell.app.data.local.dao.SugarEntryDao
import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.FluidEntryEntity
import com.trackwell.app.data.local.entity.ImportBatchEntity
import com.trackwell.app.data.local.entity.MedicationEntity
import com.trackwell.app.data.local.entity.OptionalMetricEntity
import com.trackwell.app.data.local.entity.SugarEntryEntity
import com.trackwell.app.data.medicine.MedicineReminderTimeEntity
import com.trackwell.app.data.medicine.MedicineScheduleDao
import com.trackwell.app.data.medicine.MedicineScheduleEntity
import com.trackwell.app.data.medicine.PendingReminderEntity

@Database(
    entities = [
        DailyLogEntity::class,
        FluidEntryEntity::class,
        SugarEntryEntity::class,
        OptionalMetricEntity::class,
        MedicationEntity::class,
        ImportBatchEntity::class,
        MedicineScheduleEntity::class,
        MedicineReminderTimeEntity::class,
        PendingReminderEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TrackWellDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun fluidEntryDao(): FluidEntryDao
    abstract fun sugarEntryDao(): SugarEntryDao
    abstract fun optionalMetricDao(): OptionalMetricDao
    abstract fun medicationDao(): MedicationDao
    abstract fun importBatchDao(): ImportBatchDao
    abstract fun medicineScheduleDao(): MedicineScheduleDao
}
