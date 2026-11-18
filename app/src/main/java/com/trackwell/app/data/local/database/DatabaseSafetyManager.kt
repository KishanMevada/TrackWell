package com.trackwell.app.data.local.database

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSafetyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "DatabaseSafetyManager"
        private const val DB_NAME = "trackwell.db"
        private const val SAFETY_VAULT_DIR = "db_safety_vault"
        private const val SAFETY_BACKUP_NAME = "trackwell_safety_backup.db"
    }

    private val dbFile: File
        get() = context.getDatabasePath(DB_NAME)

    private val vaultDir: File
        get() = File(context.filesDir, SAFETY_VAULT_DIR).apply { mkdirs() }

    private val safetyBackupFile: File
        get() = File(vaultDir, SAFETY_BACKUP_NAME)

    /**
     * Creates an automatic physical snapshot copy of the SQLite database files
     * (db, db-wal, db-shm) before any Room migrations or app updates occur.
     */
    suspend fun performPreLaunchSafetyBackup(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!dbFile.exists() || dbFile.length() == 0L) {
                Log.d(TAG, "No existing database found; skipping pre-launch safety snapshot.")
                return@withContext false
            }

            Log.i(TAG, "Creating physical pre-launch safety backup of database (${dbFile.length()} bytes)...")

            // Snapshot main DB file
            copyFile(dbFile, safetyBackupFile)

            // Snapshot WAL and SHM files if present
            val walFile = File(dbFile.path + "-wal")
            val shmFile = File(dbFile.path + "-shm")

            if (walFile.exists() && walFile.length() > 0) {
                copyFile(walFile, File(vaultDir, "$SAFETY_BACKUP_NAME-wal"))
            }
            if (shmFile.exists() && shmFile.length() > 0) {
                copyFile(shmFile, File(vaultDir, "$SAFETY_BACKUP_NAME-shm"))
            }

            Log.i(TAG, "Successfully created physical database safety backup in vault: ${safetyBackupFile.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create physical database safety backup", e)
            false
        }
    }

    /**
     * Emergency auto-recovery: Restores database files from safety vault if a migration error occurs.
     */
    suspend fun restoreFromSafetyVault(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!safetyBackupFile.exists() || safetyBackupFile.length() == 0L) {
                Log.w(TAG, "Cannot restore from safety vault: No backup snapshot exists.")
                return@withContext false
            }

            Log.w(TAG, "Restoring database from physical safety vault to prevent data loss...")

            copyFile(safetyBackupFile, dbFile)

            val backupWal = File(vaultDir, "$SAFETY_BACKUP_NAME-wal")
            val backupShm = File(vaultDir, "$SAFETY_BACKUP_NAME-shm")
            val targetWal = File(dbFile.path + "-wal")
            val targetShm = File(dbFile.path + "-shm")

            if (backupWal.exists()) {
                copyFile(backupWal, targetWal)
            }
            if (backupShm.exists()) {
                copyFile(backupShm, targetShm)
            }

            Log.i(TAG, "Database successfully restored from physical safety vault!")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring database from safety vault", e)
            false
        }
    }

    private fun copyFile(src: File, dest: File) {
        val fis = FileInputStream(src)
        val fos = FileOutputStream(dest)
        fis.channel.use { inChannel ->
            fos.channel.use { outChannel ->
                inChannel.transferTo(0, inChannel.size(), outChannel)
            }
        }
        fis.close()
        fos.close()
    }
}
