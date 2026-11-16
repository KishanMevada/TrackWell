package com.trackwell.app.data.local.database

import android.content.Context
import android.util.Log
import com.google.gson.JsonParser
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyLogDao: DailyLogDao,
    private val fluidEntryDao: FluidEntryDao,
    private val sugarEntryDao: SugarEntryDao,
    private val optionalMetricDao: OptionalMetricDao,
    private val medicationDao: MedicationDao,
    private val userPreferences: UserPreferences
) {

    companion object {
        private const val TAG = "DatabaseSeeder"
        private const val SEED_FILE = "seed_data.json"
        private const val CHECK_DATE = "2026-11-15"
    }

    /**
     * Seeds clinical dataset if the database does not already contain the clinical logs.
     */
    suspend fun seedIfNeeded(): Boolean = withContext(Dispatchers.IO) {
        try {
            val hasClinicalHistory = dailyLogDao.getDailyLog("2026-11-14") != null &&
                fluidEntryDao.getEntriesForDate("2026-11-14").isNotEmpty()

            if (hasClinicalHistory) {
                Log.d(TAG, "Database already cleanly seeded with clinical dataset. Skipping seed.")
                return@withContext false
            }

            Log.i(TAG, "Database is unseeded. Seeding clinical dataset from $SEED_FILE...")
            executeSeed()
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking/seeding database", e)
            return@withContext false
        }
    }

    /**
     * Forces reseeding of the database with the authentic 17-week clinical dataset.
     */
    suspend fun forceReseed(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Force reseeding clinical dataset...")
            cleanAndReseed()
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error during force reseed", e)
            return@withContext false
        }
    }

    private suspend fun cleanAndReseed() {
        // Backup today's live user data to guarantee zero data loss
        val todayLogs = dailyLogDao.getDailyLog(CHECK_DATE)
        val todayFluids = fluidEntryDao.getEntriesForDate(CHECK_DATE)
        val todaySugars = sugarEntryDao.getEntriesForDate(CHECK_DATE)

        fluidEntryDao.clearAll()
        sugarEntryDao.clearAll()
        dailyLogDao.clearAll()
        optionalMetricDao.clearAll()
        medicationDao.clearAll()
        executeSeed()

        // Restore today's user data (prioritize user's live logs over seed defaults)
        if (todayFluids.size >= 20) {
            fluidEntryDao.deleteForDate(CHECK_DATE)
            fluidEntryDao.insertAll(todayFluids)
        }
        if (todaySugars.isNotEmpty()) {
            sugarEntryDao.deleteForDate(CHECK_DATE)
            sugarEntryDao.insertAll(todaySugars)
        }
        if (todayLogs != null) {
            dailyLogDao.insertOrUpdate(todayLogs)
        }
    }

    private suspend fun executeSeed() {
        val jsonString = context.assets.open(SEED_FILE).bufferedReader().use { it.readText() }
        val root = JsonParser.parseString(jsonString).asJsonObject

        val patientName = root.get("patientName")?.asString ?: "Kishan"
        val fluidTargetMl = root.get("fluidTargetMl")?.asInt ?: 3000

        // 1. Daily logs
        val dailyLogsArray = root.getAsJsonArray("dailyLogs")
        val dailyLogEntities = mutableListOf<DailyLogEntity>()
        for (i in 0 until dailyLogsArray.size()) {
            val obj = dailyLogsArray.get(i).asJsonObject
            val date = obj.get("date").asString
            val morningWeight = if (obj.has("morningWeight") && !obj.get("morningWeight").isJsonNull) {
                obj.get("morningWeight").asDouble
            } else null
            val note = if (obj.has("note") && !obj.get("note").isJsonNull) {
                obj.get("note").asString
            } else null

            dailyLogEntities.add(
                DailyLogEntity(
                    date = date,
                    morningWeight = morningWeight,
                    note = note
                )
            )
        }
        for (log in dailyLogEntities) {
            dailyLogDao.insertOrUpdate(log)
        }
        Log.i(TAG, "Inserted ${dailyLogEntities.size} daily logs.")

        // 2. Fluid entries
        val fluidArray = root.getAsJsonArray("fluidEntries")
        val fluidEntities = mutableListOf<FluidEntryEntity>()
        for (i in 0 until fluidArray.size()) {
            val obj = fluidArray.get(i).asJsonObject
            val date = obj.get("date").asString
            val timestamp = obj.get("timestamp").asLong
            val amountMl = obj.get("amountMl").asInt
            val type = obj.get("type").asString
            val note = if (obj.has("note") && !obj.get("note").isJsonNull) {
                obj.get("note").asString
            } else null

            fluidEntities.add(
                FluidEntryEntity(
                    date = date,
                    timestamp = timestamp,
                    amountMl = amountMl,
                    type = type,
                    note = note
                )
            )
        }
        // Insert in batches of 500 for SQLite performance
        fluidEntities.chunked(500).forEach { chunk ->
            fluidEntryDao.insertAll(chunk)
        }
        Log.i(TAG, "Inserted ${fluidEntities.size} fluid entries.")

        // 3. Sugar entries
        val sugarArray = root.getAsJsonArray("sugarEntries")
        val sugarEntities = mutableListOf<SugarEntryEntity>()
        for (i in 0 until sugarArray.size()) {
            val obj = sugarArray.get(i).asJsonObject
            val date = obj.get("date").asString
            val slot = obj.get("slot").asString
            val sugarMgDl = obj.get("sugarMgDl").asInt
            val insulinUnits = if (obj.has("insulinUnits") && !obj.get("insulinUnits").isJsonNull) {
                obj.get("insulinUnits").asDouble
            } else 0.0
            val timestamp = obj.get("timestamp").asLong
            val note = if (obj.has("note") && !obj.get("note").isJsonNull) {
                obj.get("note").asString
            } else null

            sugarEntities.add(
                SugarEntryEntity(
                    date = date,
                    slot = slot,
                    sugarMgDl = sugarMgDl,
                    insulinUnits = insulinUnits,
                    timestamp = timestamp,
                    note = note
                )
            )
        }
        sugarEntities.chunked(200).forEach { chunk ->
            sugarEntryDao.insertAll(chunk)
        }
        Log.i(TAG, "Inserted ${sugarEntities.size} sugar entries.")

        // 4. Optional metrics
        if (root.has("optionalMetrics")) {
            val metricsArray = root.getAsJsonArray("optionalMetrics")
            for (i in 0 until metricsArray.size()) {
                val obj = metricsArray.get(i).asJsonObject
                val date = obj.get("date").asString
                val timestamp = obj.get("timestamp").asLong
                val systolic = if (obj.has("systolic") && !obj.get("systolic").isJsonNull) obj.get("systolic").asInt else null
                val diastolic = if (obj.has("diastolic") && !obj.get("diastolic").isJsonNull) obj.get("diastolic").asInt else null
                val pulse = if (obj.has("pulse") && !obj.get("pulse").isJsonNull) obj.get("pulse").asInt else null
                val tempC = if (obj.has("temperatureCelsius") && !obj.get("temperatureCelsius").isJsonNull) obj.get("temperatureCelsius").asDouble else null
                val note = if (obj.has("note") && !obj.get("note").isJsonNull) obj.get("note").asString else null

                optionalMetricDao.insertOrUpdate(
                    OptionalMetricEntity(
                        date = date,
                        timestamp = timestamp,
                        systolic = systolic,
                        diastolic = diastolic,
                        pulse = pulse,
                        temperatureCelsius = tempC,
                        note = note
                    )
                )
            }
        }

        // 5. Medications
        if (root.has("medications")) {
            val medsArray = root.getAsJsonArray("medications")
            val medEntities = mutableListOf<MedicationEntity>()
            for (i in 0 until medsArray.size()) {
                val obj = medsArray.get(i).asJsonObject
                val date = obj.get("date").asString
                val name = obj.get("name").asString
                val dosage = obj.get("dosage").asString
                val scheduledTime = obj.get("scheduledTime").asString
                val isTaken = if (obj.has("isTaken") && !obj.get("isTaken").isJsonNull) obj.get("isTaken").asBoolean else false

                medEntities.add(
                    MedicationEntity(
                        date = date,
                        name = name,
                        dosage = dosage,
                        scheduledTime = scheduledTime,
                        isTaken = isTaken
                    )
                )
            }
            medicationDao.insertAll(medEntities)
        }

        // 6. Update user preferences with patient details
        val intakeTarget = if (root.has("intakeTargetMl")) root.get("intakeTargetMl").asInt else 3500
        val urineTarget = if (root.has("urineTargetMl")) root.get("urineTargetMl").asInt else 2500
        userPreferences.updatePatientName(patientName)
        userPreferences.updateIntakeAndUrineTargets(intakeTarget, urineTarget)
        Log.i(TAG, "Completed clinical data seed successfully for patient '$patientName'.")
    }
}
