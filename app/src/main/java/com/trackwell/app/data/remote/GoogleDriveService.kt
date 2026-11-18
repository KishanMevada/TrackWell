package com.trackwell.app.data.remote

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File as DriveFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class DriveBackupMetadata(
    val fileId: String,
    val fileName: String,
    val modifiedTimeMs: Long,
    val sizeBytes: Long
)

@Singleton
class GoogleDriveService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "GoogleDriveService"
        const val BACKUP_FILE_NAME = "TrackWell_Backup.twbackup"
        private const val BACKUP_MIME_TYPE = "application/octet-stream"
    }

    /**
     * Build GoogleSignInClient requesting standard email and appDataFolder drive scopes.
     */
    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Check if user is signed in and has Drive permissions.
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return if (account != null && GoogleSignIn.hasPermissions(account, Scope(DriveScopes.DRIVE_APPDATA))) {
            account
        } else {
            null
        }
    }

    /**
     * Construct authenticated Drive API Service client.
     */
    private fun getDriveService(): Drive? {
        val account = getLastSignedInAccount() ?: return null
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_APPDATA)
        ).apply {
            selectedAccount = account.account
        }

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("TrackWell").build()
    }

    /**
     * Search for existing backup file in appDataFolder.
     */
    private suspend fun findBackupFileInAppData(drive: Drive): DriveFile? = withContext(Dispatchers.IO) {
        try {
            val result = drive.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = '$BACKUP_FILE_NAME' and trashed = false")
                .setFields("files(id, name, modifiedTime, size)")
                .execute()

            result.files?.firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error listing files in appDataFolder", e)
            null
        }
    }

    /**
     * Upload or update backup archive in Google Drive AppData folder with progress listener.
     */
    suspend fun uploadBackupFile(
        localBackupFile: File,
        onProgress: ((progressPercent: Int, bytesTransferred: Long, totalBytes: Long) -> Unit)? = null
    ): Result<DriveBackupMetadata> = withContext(Dispatchers.IO) {
        val drive = getDriveService()
            ?: return@withContext Result.failure(IllegalStateException("Google Account is not connected or missing Drive permissions."))

        try {
            val existingFile = findBackupFileInAppData(drive)
            val mediaContent = FileContent(BACKUP_MIME_TYPE, localBackupFile)
            val totalBytes = localBackupFile.length()

            val progressListener = MediaHttpUploaderProgressListener { uploader ->
                val percent = when (uploader.uploadState) {
                    MediaHttpUploader.UploadState.INITIATION_STARTED -> 0
                    MediaHttpUploader.UploadState.INITIATION_COMPLETE -> 5
                    MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> (uploader.progress * 100).toInt().coerceIn(5, 99)
                    MediaHttpUploader.UploadState.MEDIA_COMPLETE -> 100
                    else -> 0
                }
                val numBytes = try { uploader.numBytesUploaded } catch (_: Exception) { (totalBytes * (percent / 100.0)).toLong() }
                onProgress?.invoke(percent, numBytes, totalBytes)
            }

            val uploadedFile: DriveFile = if (existingFile != null) {
                val fileMetadata = DriveFile().apply {
                    name = BACKUP_FILE_NAME
                }
                val request = drive.files().update(existingFile.id, fileMetadata, mediaContent)
                request.mediaHttpUploader.setProgressListener(progressListener)
                request.setFields("id, name, modifiedTime, size").execute()
            } else {
                val fileMetadata = DriveFile().apply {
                    name = BACKUP_FILE_NAME
                    parents = listOf("appDataFolder")
                }
                val request = drive.files().create(fileMetadata, mediaContent)
                request.mediaHttpUploader.setProgressListener(progressListener)
                request.setFields("id, name, modifiedTime, size").execute()
            }

            onProgress?.invoke(100, totalBytes, totalBytes)

            val modifiedMs = uploadedFile.modifiedTime?.value ?: System.currentTimeMillis()
            val size = (uploadedFile.size ?: localBackupFile.length()).toLong()

            val metadata = DriveBackupMetadata(
                fileId = uploadedFile.id,
                fileName = uploadedFile.name ?: BACKUP_FILE_NAME,
                modifiedTimeMs = modifiedMs,
                sizeBytes = size
            )

            Log.i(TAG, "Successfully uploaded backup to Drive AppData: ${metadata.fileId}")
            Result.success(metadata)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload backup to Google Drive", e)
            Result.failure(e)
        }
    }

    /**
     * Download backup archive from Google Drive AppData folder to local target file with progress listener.
     */
    suspend fun downloadBackupFile(
        destFile: File,
        onProgress: ((progressPercent: Int, bytesTransferred: Long, totalBytes: Long) -> Unit)? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val drive = getDriveService()
            ?: return@withContext Result.failure(IllegalStateException("Google Account is not connected."))

        try {
            val remoteFile = findBackupFileInAppData(drive)
                ?: return@withContext Result.failure(IllegalStateException("No backup found in Google Drive AppData."))

            val totalBytes = (remoteFile.size ?: 0L).toLong()
            val request = drive.files().get(remoteFile.id)
            request.mediaHttpDownloader.setProgressListener { downloader ->
                val percent = when (downloader.downloadState) {
                    MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS -> (downloader.progress * 100).toInt().coerceIn(0, 99)
                    MediaHttpDownloader.DownloadState.MEDIA_COMPLETE -> 100
                    else -> 0
                }
                val numBytes = try { downloader.numBytesDownloaded } catch (_: Exception) { (totalBytes * (percent / 100.0)).toLong() }
                onProgress?.invoke(percent, numBytes, totalBytes)
            }

            val outputStream = FileOutputStream(destFile)
            request.executeMediaAndDownloadTo(outputStream)
            outputStream.close()

            onProgress?.invoke(100, totalBytes, totalBytes)

            Log.i(TAG, "Successfully downloaded backup from Drive AppData: ${destFile.absolutePath}")
            Result.success(destFile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download backup from Google Drive", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieve metadata of the last backup in Google Drive AppData folder.
     */
    suspend fun getBackupMetadata(): Result<DriveBackupMetadata?> = withContext(Dispatchers.IO) {
        val drive = getDriveService()
            ?: return@withContext Result.failure(IllegalStateException("Google Account is not connected."))

        try {
            val remoteFile = findBackupFileInAppData(drive)
            if (remoteFile == null) {
                return@withContext Result.success(null)
            }

            val modifiedMs = remoteFile.modifiedTime?.value ?: System.currentTimeMillis()
            val size = (remoteFile.size ?: 0L).toLong()

            val metadata = DriveBackupMetadata(
                fileId = remoteFile.id,
                fileName = remoteFile.name ?: BACKUP_FILE_NAME,
                modifiedTimeMs = modifiedMs,
                sizeBytes = size
            )
            Result.success(metadata)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch backup metadata", e)
            Result.failure(e)
        }
    }
}
