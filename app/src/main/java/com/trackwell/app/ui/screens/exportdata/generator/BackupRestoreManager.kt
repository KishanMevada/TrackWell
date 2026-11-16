package com.trackwell.app.ui.screens.exportdata.generator

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
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
import com.trackwell.app.domain.util.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

data class AppBackupData(
    val version: Int = 1,
    val exportDate: String = DateTimeUtils.getTodayIso(),
    val dailyLogs: List<DailyLogEntity>,
    val fluidEntries: List<FluidEntryEntity>,
    val sugarEntries: List<SugarEntryEntity>,
    val optionalMetrics: List<OptionalMetricEntity>,
    val medications: List<MedicationEntity>
)

@Singleton
class BackupRestoreManager @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val fluidEntryDao: FluidEntryDao,
    private val sugarEntryDao: SugarEntryDao,
    private val optionalMetricDao: OptionalMetricDao,
    private val medicationDao: MedicationDao
) {
    private val gson = Gson()
    // 256-bit fixed seed key for app backups
    private val defaultKeyBytes = "TrackWellSecureBackupKey2026Transplant".toByteArray(StandardCharsets.UTF_8).copyOf(32)

    suspend fun createEncryptedBackup(context: Context): File = withContext(Dispatchers.IO) {
        val dailyLogs = dailyLogDao.getDailyLogsListInRange("2000-01-01", "2099-12-31")
        val fluidEntries = fluidEntryDao.getEntriesListInRange("2000-01-01", "2099-12-31")
        val sugarEntries = sugarEntryDao.getEntriesListInRange("2000-01-01", "2099-12-31")
        val optionalMetrics = optionalMetricDao.getAllList()
        val medications = medicationDao.getAllList()

        val backupData = AppBackupData(
            dailyLogs = dailyLogs,
            fluidEntries = fluidEntries,
            sugarEntries = sugarEntries,
            optionalMetrics = optionalMetrics,
            medications = medications
        )

        val jsonString = gson.toJson(backupData)
        val plaintextBytes = jsonString.toByteArray(StandardCharsets.UTF_8)

        // AES-GCM encryption
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val secretKey = SecretKeySpec(defaultKeyBytes, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val ciphertext = cipher.doFinal(plaintextBytes)

        val backupDir = File(context.cacheDir, "backups").apply { mkdirs() }
        val backupFile = File(backupDir, "TrackWell_Backup_${DateTimeUtils.getTodayIso()}.twbackup")
        val fos = FileOutputStream(backupFile)
        fos.write(iv)
        fos.write(ciphertext)
        fos.close()

        // Also save a copy to the persistent cloud/Drive sync folder
        saveToDriveSyncFolder(context, backupFile)

        backupFile
    }

    private fun saveToDriveSyncFolder(context: Context, backupFile: File) {
        try {
            val driveFolder = File(context.getExternalFilesDir(null), "GoogleDriveBackups").apply { mkdirs() }
            val destFile = File(driveFolder, backupFile.name)
            backupFile.copyTo(destFile, overwrite = true)
        } catch (_: Exception) {}
    }

    suspend fun restoreEncryptedBackup(backupFile: File): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val fis = FileInputStream(backupFile)
            val result = restoreFromInputStream(fis)
            fis.close()
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFromUri(context: Context, uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open file from cloud storage"))
            val result = restoreFromInputStream(inputStream)
            inputStream.close()
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFromInputStream(inputStream: InputStream): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val iv = ByteArray(12)
            val bytesRead = inputStream.read(iv)
            if (bytesRead < 12) {
                return@withContext Result.failure(Exception("Invalid backup file header"))
            }
            val ciphertext = inputStream.readBytes()

            val secretKey = SecretKeySpec(defaultKeyBytes, "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(ciphertext)
            val jsonString = String(decryptedBytes, StandardCharsets.UTF_8)

            val backupData = gson.fromJson(jsonString, AppBackupData::class.java)

            // Restore all records into Room SQLite
            for (log in backupData.dailyLogs) {
                dailyLogDao.insertOrUpdate(log)
            }
            if (backupData.fluidEntries.isNotEmpty()) {
                backupData.fluidEntries.chunked(500).forEach { chunk ->
                    fluidEntryDao.insertAll(chunk)
                }
            }
            if (backupData.sugarEntries.isNotEmpty()) {
                backupData.sugarEntries.chunked(200).forEach { chunk ->
                    sugarEntryDao.insertAll(chunk)
                }
            }
            if (!backupData.optionalMetrics.isNullOrEmpty()) {
                for (metric in backupData.optionalMetrics) {
                    optionalMetricDao.insertOrUpdate(metric)
                }
            }
            if (!backupData.medications.isNullOrEmpty()) {
                medicationDao.insertAll(backupData.medications)
            }

            val totalRestored = (backupData.dailyLogs.size +
                    backupData.fluidEntries.size +
                    backupData.sugarEntries.size +
                    (backupData.optionalMetrics?.size ?: 0) +
                    (backupData.medications?.size ?: 0))

            Result.success(totalRestored)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
