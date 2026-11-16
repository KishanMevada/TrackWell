package com.trackwell.app.ui.screens.exportdata

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
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
    private val googleDriveBackupScheduler: GoogleDriveBackupScheduler
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
            _exportState.value = ExportState.Generating("Creating Google Drive Backup...")
            try {
                val backupFile = backupRestoreManager.createEncryptedBackup(context)
                val now = System.currentTimeMillis()
                val status = "Synced to ${settings.value.googleDriveAccount} (${backupFile.name})"
                userPreferences.recordGoogleDriveBackupResult(now, status)

                _exportState.value = ExportState.ReadyToShare(
                    file = backupFile,
                    mimeType = "application/octet-stream",
                    title = "Google Drive Cloud Backup: ${backupFile.name}"
                )
                _restoreMessage.emit("Backup saved! Choose Google Drive from the share sheet to upload instantly.")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Google Drive backup failed: ${e.localizedMessage}")
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
