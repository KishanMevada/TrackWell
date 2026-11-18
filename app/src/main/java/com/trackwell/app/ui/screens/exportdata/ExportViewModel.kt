package com.trackwell.app.ui.screens.exportdata

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.data.remote.GoogleDriveService
import com.trackwell.app.domain.fhir.FhirQrCodeGenerator
import com.trackwell.app.domain.fhir.FhirR4Exporter
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.notification.GoogleDriveBackupScheduler
import com.trackwell.app.ui.screens.exportdata.generator.BackupRestoreManager
import com.trackwell.app.ui.screens.exportdata.generator.ExcelWorkbookGenerator
import com.trackwell.app.ui.screens.exportdata.generator.PdfReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

enum class ExportRange(val days: Int, val label: String) {
    SEVEN_DAYS(7, "Last 7 Days"),
    FOURTEEN_DAYS(14, "Last 14 Days"),
    THIRTY_DAYS(30, "Last 30 Days"),
    NINETY_DAYS(90, "Last 90 Days"),
    ALL_TIME(125, "All Records (17W)")
}

sealed class ExportState {
    data object Idle : ExportState()
    data class Generating(val message: String) : ExportState()
    data class ReadyToShare(val file: File, val mimeType: String, val title: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyHealthRepository: DailyHealthRepository,
    private val userPreferences: UserPreferences,
    private val backupRestoreManager: BackupRestoreManager,
    private val googleDriveBackupScheduler: GoogleDriveBackupScheduler,
    val googleDriveService: GoogleDriveService,
    private val fhirR4Exporter: FhirR4Exporter,
    private val fhirQrCodeGenerator: FhirQrCodeGenerator
) : ViewModel() {

    val settings: StateFlow<AppSettings> = userPreferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    private val _restoreMessage = MutableSharedFlow<String>()
    val restoreMessage: SharedFlow<String> = _restoreMessage.asSharedFlow()

    private val _selectedRange = MutableStateFlow(ExportRange.ALL_TIME)
    val selectedRange: StateFlow<ExportRange> = _selectedRange.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _qrCodeBitmap = MutableStateFlow<ImageBitmap?>(null)
    val qrCodeBitmap: StateFlow<ImageBitmap?> = _qrCodeBitmap.asStateFlow()

    fun handleGoogleSignInResult(intent: Intent?) {
        viewModelScope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account = task.getResult(ApiException::class.java)
                val email = account.email ?: account.account?.name ?: "Google Account"
                userPreferences.updateGoogleDriveAccount(email)
                userPreferences.updateGoogleDriveBackupEnabled(true)
                googleDriveBackupScheduler.scheduleDailyBackup(
                    settings.value.copy(isGoogleDriveBackupEnabled = true, googleDriveAccount = email)
                )
                _restoreMessage.emit("Connected to Google Drive as $email")
            } catch (e: ApiException) {
                _restoreMessage.emit("Google Sign-In failed (Code ${e.statusCode}): ${e.localizedMessage}")
            } catch (e: Exception) {
                _restoreMessage.emit("Google Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

    fun toggleGoogleDriveAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateGoogleDriveBackupEnabled(enabled)
            googleDriveBackupScheduler.scheduleDailyBackup(settings.value.copy(isGoogleDriveBackupEnabled = enabled))
            val status = if (enabled) "Enabled daily auto-backup to Google Drive" else "Disabled Google Drive auto-backup"
            _restoreMessage.emit(status)
        }
    }

    fun updateGoogleDriveAccount(account: String) {
        viewModelScope.launch {
            userPreferences.updateGoogleDriveAccount(account)
            _restoreMessage.emit("Google Drive account set to: $account")
        }
    }

    fun updateGoogleDriveBackupTime(time: String) {
        viewModelScope.launch {
            userPreferences.updateGoogleDriveBackupTime(time)
            googleDriveBackupScheduler.scheduleDailyBackup(settings.value.copy(googleDriveBackupTime = time))
            _restoreMessage.emit("Auto-backup scheduled daily at $time")
        }
    }

    fun backupToGoogleDriveNow() {
        viewModelScope.launch {
            var account = settings.value.googleDriveAccount
            if (account.isBlank()) {
                val signedInAccount = googleDriveService.getLastSignedInAccount()
                if (signedInAccount != null && !signedInAccount.email.isNullOrBlank()) {
                    account = signedInAccount.email!!
                    userPreferences.updateGoogleDriveAccount(account)
                }
            }

            if (account.isBlank()) {
                _restoreMessage.emit("Please tap 'Tap to Sign in with Google' above to connect your account first.")
                return@launch
            }

            _exportState.value = ExportState.Generating("Encrypting database & syncing to Google Drive...")
            userPreferences.recordGoogleDriveBackupResult(System.currentTimeMillis(), "Syncing to Google Drive...")
            try {
                val backupFile = backupRestoreManager.createEncryptedBackup(context)
                val uploadResult = googleDriveService.uploadBackupFile(backupFile)
                val now = System.currentTimeMillis()

                if (uploadResult.isSuccess) {
                    val metadata = uploadResult.getOrNull()
                    val status = "Synced to $account (${metadata?.fileName ?: backupFile.name})"
                    userPreferences.recordGoogleDriveBackupResult(now, status)
                    _exportState.value = ExportState.Idle
                    _restoreMessage.emit("Backup complete! Encrypted archive synced to Google Drive.")
                } else {
                    val error = uploadResult.exceptionOrNull()?.localizedMessage ?: "Upload failed"
                    userPreferences.recordGoogleDriveBackupResult(now, "Upload failed: $error")
                    _exportState.value = ExportState.Error("Cloud upload failed: $error")
                }
            } catch (e: Exception) {
                userPreferences.recordGoogleDriveBackupResult(System.currentTimeMillis(), "Backup error: ${e.localizedMessage}")
                _exportState.value = ExportState.Error("Google Drive backup failed: ${e.localizedMessage}")
            }
        }
    }

    fun restoreFromGoogleDriveCloud() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Downloading backup from Google Drive...")
            userPreferences.recordGoogleDriveBackupResult(System.currentTimeMillis(), "Downloading from Google Drive...")
            try {
                val tempFile = File(context.cacheDir, "drive_restore.twbackup")
                val downloadResult = googleDriveService.downloadBackupFile(tempFile)
                if (downloadResult.isSuccess) {
                    _exportState.value = ExportState.Generating("Decrypting & Restoring records...")
                    val restoreResult = backupRestoreManager.restoreEncryptedBackup(tempFile)
                    val now = System.currentTimeMillis()
                    if (restoreResult.isSuccess) {
                        val count = restoreResult.getOrNull() ?: 0
                        val account = settings.value.googleDriveAccount.ifBlank { "Google Drive" }
                        userPreferences.recordGoogleDriveBackupResult(now, "Restored $count records ($account)")
                        _exportState.value = ExportState.Idle
                        _restoreMessage.emit("Successfully restored $count records from Google Drive!")
                    } else {
                        val error = restoreResult.exceptionOrNull()?.localizedMessage ?: "Restore failed"
                        userPreferences.recordGoogleDriveBackupResult(now, "Restore failed: $error")
                        _exportState.value = ExportState.Error("Restore failed: $error")
                    }
                } else {
                    val error = downloadResult.exceptionOrNull()?.localizedMessage ?: "Download failed"
                    userPreferences.recordGoogleDriveBackupResult(System.currentTimeMillis(), "Download failed: $error")
                    _exportState.value = ExportState.Error("Could not download backup from Google Drive: $error")
                }
            } catch (e: Exception) {
                userPreferences.recordGoogleDriveBackupResult(System.currentTimeMillis(), "Restore error: ${e.localizedMessage}")
                _exportState.value = ExportState.Error("Google Drive restore error: ${e.localizedMessage}")
            }
        }
    }

    fun restoreFromDriveUri(uri: Uri) {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Decrypting & Restoring from Google Drive...")
            try {
                val result = backupRestoreManager.restoreFromUri(context, uri)
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    _exportState.value = ExportState.Idle
                    _restoreMessage.emit("Successfully restored $count records from Google Drive backup!")
                } else {
                    val error = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                    _exportState.value = ExportState.Error("Restore failed: $error")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Restore error: ${e.localizedMessage}")
            }
        }
    }

    fun exportFhirR4() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Generating HL7 FHIR R4 Bundle...")
            try {
                val file = File(context.cacheDir, "TrackWell_FHIR_R4_Bundle.json")
                fhirR4Exporter.exportFhirBundleToFile(file)
                _exportState.value = ExportState.ReadyToShare(
                    file = file,
                    mimeType = "application/json",
                    title = "HL7 FHIR R4 Medical Export"
                )
                _restoreMessage.emit("Generated HL7 FHIR R4 medical bundle!")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("FHIR Export failed: ${e.localizedMessage}")
            }
        }
    }

    fun generateClinicQrCode() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Generating Clinic QR Code...")
            try {
                val json = fhirR4Exporter.generateFhirBundleJson()
                val bitmap = fhirQrCodeGenerator.generateQrImageBitmap(json, size = 600)
                _qrCodeBitmap.value = bitmap
                _exportState.value = ExportState.Idle
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("QR Code generation failed: ${e.localizedMessage}")
            }
        }
    }

    fun clearClinicQrCode() {
        _qrCodeBitmap.value = null
    }

    fun selectRange(range: ExportRange) {
        _selectedRange.value = range
    }

    fun exportPdf() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Generating Doctor PDF Report...")
            try {
                val range = _selectedRange.value
                val (startDate, endDate) = DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
                val days = dailyHealthRepository.getAllDaysListInRange(startDate, endDate)
                val averages = dailyHealthRepository.getPeriodAverages(startDate, endDate).first()
                val settings = userPreferences.settingsFlow.first()

                val pdfFile = PdfReportGenerator.generateDoctorReport(
                    context = context,
                    patientName = settings.patientName,
                    startDate = startDate,
                    endDate = endDate,
                    days = days,
                    averages = averages,
                    thresholds = settings.thresholdSettings
                )

                _exportState.value = ExportState.ReadyToShare(
                    file = pdfFile,
                    mimeType = "application/pdf",
                    title = "Doctor Routine PDF Report"
                )
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Failed to generate PDF: ${e.localizedMessage}")
            }
        }
    }

    fun exportExcel() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Generating Multi-sheet Excel (.xlsx)...")
            try {
                val range = _selectedRange.value
                val (startDate, endDate) = DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
                val days = dailyHealthRepository.getAllDaysListInRange(startDate, endDate)
                val averages = dailyHealthRepository.getPeriodAverages(startDate, endDate).first()

                val excelFile = ExcelWorkbookGenerator.generateXlsx(
                    context = context,
                    startDate = startDate,
                    endDate = endDate,
                    days = days,
                    averages = averages
                )

                _exportState.value = ExportState.ReadyToShare(
                    file = excelFile,
                    mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    title = "Excel Health Log"
                )
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Failed to generate Excel: ${e.localizedMessage}")
            }
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Exporting CSV...")
            try {
                val range = _selectedRange.value
                val (startDate, endDate) = DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
                val days = dailyHealthRepository.getAllDaysListInRange(startDate, endDate)

                val csvFile = ExcelWorkbookGenerator.generateCsv(
                    context = context,
                    startDate = startDate,
                    endDate = endDate,
                    days = days
                )

                _exportState.value = ExportState.ReadyToShare(
                    file = csvFile,
                    mimeType = "text/csv",
                    title = "CSV Health Log"
                )
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Failed to export CSV: ${e.localizedMessage}")
            }
        }
    }

    fun createEncryptedBackup() {
        viewModelScope.launch {
            _exportState.value = ExportState.Generating("Creating Encrypted Backup...")
            try {
                val backupFile = backupRestoreManager.createEncryptedBackup(context)
                _exportState.value = ExportState.ReadyToShare(
                    file = backupFile,
                    mimeType = "application/octet-stream",
                    title = "TrackWell Encrypted Backup"
                )
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Failed to create backup: ${e.localizedMessage}")
            }
        }
    }

    fun shareFile(file: File, mimeType: String, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "com.trackwell.app.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share with").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun resetState() {
        _exportState.value = ExportState.Idle
    }
}
