package com.trackwell.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.local.database.DatabaseSeeder
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.notification.GoogleDriveBackupScheduler
import com.trackwell.app.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val reminderScheduler: ReminderScheduler,
    private val databaseSeeder: DatabaseSeeder,
    private val googleDriveBackupScheduler: GoogleDriveBackupScheduler
) : ViewModel() {

    private val _seedMessage = MutableSharedFlow<String>()
    val seedMessage: SharedFlow<String> = _seedMessage.asSharedFlow()

    fun toggleGoogleDriveAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateGoogleDriveBackupEnabled(enabled)
            googleDriveBackupScheduler.scheduleDailyBackup(settings.value.copy(isGoogleDriveBackupEnabled = enabled))
            _seedMessage.emit(if (enabled) "Google Drive daily auto-backup enabled" else "Google Drive auto-backup disabled")
        }
    }

    fun reloadClinicalDataset() {
        viewModelScope.launch {
            val success = databaseSeeder.forceReseed()
            if (success) {
                _seedMessage.emit("Clinical dataset reloaded successfully (17 weeks, 3,960 entries)!")
            } else {
                _seedMessage.emit("Failed to reload clinical dataset.")
            }
        }
    }

    val settings: StateFlow<AppSettings> = userPreferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    fun updateThresholds(low: Int, min: Int, max: Int, critical: Int) {
        viewModelScope.launch {
            userPreferences.updateThresholds(low, min, max, critical)
        }
    }

    fun updateFluidTarget(target: Int) {
        viewModelScope.launch {
            userPreferences.updateFluidTarget(target)
        }
    }

    fun updateIntakeAndUrineTargets(intake: Int, urine: Int) {
        viewModelScope.launch {
            userPreferences.updateIntakeAndUrineTargets(intake, urine)
        }
    }

    fun updateQuickPresets(presets: String) {
        viewModelScope.launch {
            userPreferences.updateQuickPresets(presets)
        }
    }

    fun updateTransplantProfile(
        transplantDate: String,
        bloodGroup: String,
        nephrologistContact: String,
        emergencyNotes: String
    ) {
        viewModelScope.launch {
            userPreferences.updateTransplantProfile(
                transplantDate,
                bloodGroup,
                nephrologistContact,
                emergencyNotes
            )
        }
    }

    fun updateDefaultWidgetAmount(amount: Int) {
        viewModelScope.launch {
            userPreferences.updateDefaultWidgetAmount(amount)
        }
    }

    fun updatePatientName(name: String) {
        viewModelScope.launch {
            userPreferences.updatePatientName(name)
        }
    }

    fun updateGeminiApiKey(key: String) {
        viewModelScope.launch {
            userPreferences.updateGeminiApiKey(key)
        }
    }

    fun updateBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateBiometricEnabled(enabled)
        }
    }

    fun toggleModule(module: String, enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.toggleModule(module, enabled)
        }
    }

    fun updateReminderTime(reminderKey: String, time: String) {
        viewModelScope.launch {
            userPreferences.updateReminderTime(reminderKey, time)
            reminderScheduler.scheduleAll(settings.value)
        }
    }

    fun toggleFluidNudge(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateFluidNudge(enabled)
            reminderScheduler.scheduleAll(settings.value.copy(fluidNudgeEnabled = enabled))
        }
    }
}
