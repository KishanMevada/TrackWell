package com.trackwell.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.FluidEntryDao
import com.trackwell.app.data.local.dao.ImportBatchDao
import com.trackwell.app.data.local.dao.MedicationDao
import com.trackwell.app.data.local.dao.OptionalMetricDao
import com.trackwell.app.data.local.dao.SugarEntryDao
import com.trackwell.app.data.local.database.TrackWellDatabase
import com.trackwell.app.data.medicine.MedicineScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `medicine_schedules` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `name` TEXT NOT NULL,
                    `doseValue` TEXT NOT NULL,
                    `doseUnit` TEXT NOT NULL,
                    `isEnabled` INTEGER NOT NULL,
                    `repeatMode` TEXT NOT NULL,
                    `selectedDaysOfWeek` TEXT NOT NULL,
                    `startDate` TEXT,
                    `endDate` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `medicine_reminder_times` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `medicineId` TEXT NOT NULL,
                    `localTime` TEXT NOT NULL,
                    `sortOrder` INTEGER NOT NULL,
                    FOREIGN KEY(`medicineId`) REFERENCES `medicine_schedules`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
            """.trimIndent())

            db.execSQL("""
                CREATE INDEX IF NOT EXISTS `index_medicine_schedules_isEnabled` ON `medicine_schedules` (`isEnabled`)
            """.trimIndent())

            db.execSQL("""
                CREATE INDEX IF NOT EXISTS `index_medicine_reminder_times_medicineId` ON `medicine_reminder_times` (`medicineId`)
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `pending_reminders` (
                    `pendingReminderId` TEXT NOT NULL PRIMARY KEY,
                    `medicineId` TEXT NOT NULL,
                    `medicineName` TEXT NOT NULL,
                    `doseFormatted` TEXT NOT NULL,
                    `scheduledTime` TEXT NOT NULL,
                    `scheduledAt` INTEGER NOT NULL,
                    `followUpAt` INTEGER NOT NULL,
                    `state` TEXT NOT NULL,
                    `notificationId` INTEGER NOT NULL,
                    `followUpNotificationId` INTEGER NOT NULL,
                    `isFollowUp` INTEGER NOT NULL
                )
            """.trimIndent())
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackWellDatabase {
        return Room.databaseBuilder(
            context,
            TrackWellDatabase::class.java,
            "trackwell.db"
        )
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()
    }

    @Provides
    fun provideDailyLogDao(db: TrackWellDatabase): DailyLogDao = db.dailyLogDao()

    @Provides
    fun provideFluidEntryDao(db: TrackWellDatabase): FluidEntryDao = db.fluidEntryDao()

    @Provides
    fun provideSugarEntryDao(db: TrackWellDatabase): SugarEntryDao = db.sugarEntryDao()

    @Provides
    fun provideOptionalMetricDao(db: TrackWellDatabase): OptionalMetricDao = db.optionalMetricDao()

    @Provides
    fun provideMedicationDao(db: TrackWellDatabase): MedicationDao = db.medicationDao()

    @Provides
    fun provideImportBatchDao(db: TrackWellDatabase): ImportBatchDao = db.importBatchDao()

    @Provides
    fun provideMedicineScheduleDao(db: TrackWellDatabase): MedicineScheduleDao = db.medicineScheduleDao()
}
