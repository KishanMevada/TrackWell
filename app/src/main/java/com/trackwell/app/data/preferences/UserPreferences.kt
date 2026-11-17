package com.trackwell.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trackwell.app.domain.model.ThresholdSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "trackwell_prefs")

data class AppSettings(
    val fluidTargetMl: Int = 3500, // Kept for legacy widget compatibility
    val intakeTargetMl: Int = 3500,
    val urineTargetMl: Int = 3200,
    val quickPresetsMl: String = "150,200,250,500",
    val defaultWidgetAmountMl: Int = 200,
    val thresholdSettings: ThresholdSettings = ThresholdSettings(),
    val isBiometricEnabled: Boolean = false,
    val geminiApiKey: String = "",
    val patientName: String = "",
    val healthContext: String = "",
    val transplantDate: String = "",
    val bloodGroup: String = "",
    val nephrologistContact: String = "",
    val emergencyNotes: String = "",
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val isBpEnabled: Boolean = true,
    val isPulseEnabled: Boolean = true,
    val isTempEnabled: Boolean = true,
    val isMedicationEnabled: Boolean = true,
    val sugarReminderBreakfast: String = "07:30",
    val sugarReminderLunch: String = "13:00",
    val sugarReminderAfterLunch: String = "16:30",
    val sugarReminderDinner: String = "20:00",
    val weightReminderMorning: String = "07:00",
    val fluidNudgeEnabled: Boolean = true,
    val isGoogleDriveBackupEnabled: Boolean = false,
    val googleDriveAccount: String = "",
    val googleDriveBackupTime: String = "02:00",
    val lastGoogleDriveBackupTimestamp: Long = 0L,
    val lastGoogleDriveBackupStatus: String = "Not Configured",
    // FTUE & Modular Dashboard Preferences
    val hasCompletedInitialSetup: Boolean = false,
    val selectedModulesCsv: String = "FLUID,WEIGHT,SUGAR,MEDS,VITALS",
    val isPreviewModeActive: Boolean = false,
    val isSetupChecklistDismissed: Boolean = false,
    val seenTooltipsCsv: String = "",
    val hasLoggedFirstAction: Boolean = false
) {
    fun isModuleSelected(moduleKey: String): Boolean {
        return selectedModulesCsv.split(",").map { it.trim().uppercase() }.contains(moduleKey.uppercase())
    }

    fun hasSeenTooltip(tooltipId: String): Boolean {
        return seenTooltipsCsv.split(",").contains(tooltipId)
    }
}

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val FLUID_TARGET = intPreferencesKey("fluid_target")
        val INTAKE_TARGET = intPreferencesKey("intake_target")
        val URINE_TARGET = intPreferencesKey("urine_target")
        val QUICK_PRESETS = stringPreferencesKey("quick_presets")
        val TRANSPLANT_DATE = stringPreferencesKey("transplant_date")
        val BLOOD_GROUP = stringPreferencesKey("blood_group")
        val NEPHROLOGIST_CONTACT = stringPreferencesKey("nephrologist_contact")
        val EMERGENCY_NOTES = stringPreferencesKey("emergency_notes")
        val DEFAULT_WIDGET_AMOUNT = intPreferencesKey("default_widget_amount")
        val SUGAR_LOW = intPreferencesKey("sugar_low")
        val SUGAR_TARGET_MIN = intPreferencesKey("sugar_target_min")
        val SUGAR_TARGET_MAX = intPreferencesKey("sugar_target_max")
        val SUGAR_CRITICAL_HIGH = intPreferencesKey("sugar_critical_high")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val PATIENT_NAME = stringPreferencesKey("patient_name")
        val HEALTH_CONTEXT = stringPreferencesKey("health_context")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val MODULE_BP = booleanPreferencesKey("module_bp")
        val MODULE_PULSE = booleanPreferencesKey("module_pulse")
        val MODULE_TEMP = booleanPreferencesKey("module_temp")
        val MODULE_MEDS = booleanPreferencesKey("module_meds")
        val REMINDER_BREAKFAST = stringPreferencesKey("reminder_breakfast")
        val REMINDER_LUNCH = stringPreferencesKey("reminder_lunch")
        val REMINDER_AFTER_LUNCH = stringPreferencesKey("reminder_after_lunch")
        val REMINDER_DINNER = stringPreferencesKey("reminder_dinner")
        val REMINDER_WEIGHT = stringPreferencesKey("reminder_weight")
        val FLUID_NUDGE_ENABLED = booleanPreferencesKey("fluid_nudge_enabled")
        val GOOGLE_DRIVE_BACKUP_ENABLED = booleanPreferencesKey("google_drive_backup_enabled")
        val GOOGLE_DRIVE_ACCOUNT = stringPreferencesKey("google_drive_account")
        val GOOGLE_DRIVE_BACKUP_TIME = stringPreferencesKey("google_drive_backup_time")
        val LAST_GOOGLE_DRIVE_BACKUP_TS = longPreferencesKey("last_google_drive_backup_ts")
        val LAST_GOOGLE_DRIVE_BACKUP_STATUS = stringPreferencesKey("last_google_drive_backup_status")

        // FTUE
        val HAS_COMPLETED_INITIAL_SETUP = booleanPreferencesKey("has_completed_initial_setup")
        val SELECTED_MODULES_CSV = stringPreferencesKey("selected_modules_csv")
        val IS_PREVIEW_MODE_ACTIVE = booleanPreferencesKey("is_preview_mode_active")
        val IS_SETUP_CHECKLIST_DISMISSED = booleanPreferencesKey("is_setup_checklist_dismissed")
        val SEEN_TOOLTIPS_CSV = stringPreferencesKey("seen_tooltips_csv")
        val HAS_LOGGED_FIRST_ACTION = booleanPreferencesKey("has_logged_first_action")
    }

    val settingsFlow: Flow<AppSettings> = dataStore.data.map { prefs ->
        val fluidTarget = prefs[PreferencesKeys.FLUID_TARGET] ?: 3500
        AppSettings(
            fluidTargetMl = fluidTarget,
            intakeTargetMl = prefs[PreferencesKeys.INTAKE_TARGET] ?: fluidTarget,
            urineTargetMl = prefs[PreferencesKeys.URINE_TARGET] ?: 3200,
            quickPresetsMl = prefs[PreferencesKeys.QUICK_PRESETS] ?: "150,200,250,500",
            defaultWidgetAmountMl = prefs[PreferencesKeys.DEFAULT_WIDGET_AMOUNT] ?: 200,
            thresholdSettings = ThresholdSettings(
                lowThreshold = prefs[PreferencesKeys.SUGAR_LOW] ?: 70,
                targetMin = prefs[PreferencesKeys.SUGAR_TARGET_MIN] ?: 80,
                targetMax = prefs[PreferencesKeys.SUGAR_TARGET_MAX] ?: 140,
                criticalHigh = prefs[PreferencesKeys.SUGAR_CRITICAL_HIGH] ?: 250
            ),
            isBiometricEnabled = prefs[PreferencesKeys.BIOMETRIC_ENABLED] ?: false,
            geminiApiKey = prefs[PreferencesKeys.GEMINI_API_KEY] ?: "",
            patientName = prefs[PreferencesKeys.PATIENT_NAME] ?: "",
            healthContext = prefs[PreferencesKeys.HEALTH_CONTEXT] ?: "",
            transplantDate = prefs[PreferencesKeys.TRANSPLANT_DATE] ?: "",
            bloodGroup = prefs[PreferencesKeys.BLOOD_GROUP] ?: "",
            nephrologistContact = prefs[PreferencesKeys.NEPHROLOGIST_CONTACT] ?: "",
            emergencyNotes = prefs[PreferencesKeys.EMERGENCY_NOTES] ?: "",
            themeMode = prefs[PreferencesKeys.THEME_MODE] ?: "SYSTEM",
            isBpEnabled = prefs[PreferencesKeys.MODULE_BP] ?: true,
            isPulseEnabled = prefs[PreferencesKeys.MODULE_PULSE] ?: true,
            isTempEnabled = prefs[PreferencesKeys.MODULE_TEMP] ?: true,
            isMedicationEnabled = prefs[PreferencesKeys.MODULE_MEDS] ?: true,
            sugarReminderBreakfast = prefs[PreferencesKeys.REMINDER_BREAKFAST] ?: "07:30",
            sugarReminderLunch = prefs[PreferencesKeys.REMINDER_LUNCH] ?: "13:00",
            sugarReminderAfterLunch = prefs[PreferencesKeys.REMINDER_AFTER_LUNCH] ?: "16:30",
            sugarReminderDinner = prefs[PreferencesKeys.REMINDER_DINNER] ?: "20:00",
            weightReminderMorning = prefs[PreferencesKeys.REMINDER_WEIGHT] ?: "07:00",
            fluidNudgeEnabled = prefs[PreferencesKeys.FLUID_NUDGE_ENABLED] ?: true,
            isGoogleDriveBackupEnabled = prefs[PreferencesKeys.GOOGLE_DRIVE_BACKUP_ENABLED] ?: false,
            googleDriveAccount = prefs[PreferencesKeys.GOOGLE_DRIVE_ACCOUNT] ?: "",
            googleDriveBackupTime = prefs[PreferencesKeys.GOOGLE_DRIVE_BACKUP_TIME] ?: "02:00",
            lastGoogleDriveBackupTimestamp = prefs[PreferencesKeys.LAST_GOOGLE_DRIVE_BACKUP_TS] ?: 0L,
            lastGoogleDriveBackupStatus = prefs[PreferencesKeys.LAST_GOOGLE_DRIVE_BACKUP_STATUS] ?: "Not Configured",
            hasCompletedInitialSetup = prefs[PreferencesKeys.HAS_COMPLETED_INITIAL_SETUP] ?: false,
            selectedModulesCsv = prefs[PreferencesKeys.SELECTED_MODULES_CSV] ?: "FLUID,WEIGHT,SUGAR,MEDS,VITALS",
            isPreviewModeActive = prefs[PreferencesKeys.IS_PREVIEW_MODE_ACTIVE] ?: false,
            isSetupChecklistDismissed = prefs[PreferencesKeys.IS_SETUP_CHECKLIST_DISMISSED] ?: false,
            seenTooltipsCsv = prefs[PreferencesKeys.SEEN_TOOLTIPS_CSV] ?: "",
            hasLoggedFirstAction = prefs[PreferencesKeys.HAS_LOGGED_FIRST_ACTION] ?: false
        )
    }

    suspend fun completeInitialSetup(selectedModulesCsv: String, isPreview: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.HAS_COMPLETED_INITIAL_SETUP] = true
            it[PreferencesKeys.SELECTED_MODULES_CSV] = selectedModulesCsv
            it[PreferencesKeys.IS_PREVIEW_MODE_ACTIVE] = isPreview
        }
    }

    suspend fun updateSelectedModules(selectedModulesCsv: String) {
        dataStore.edit {
            it[PreferencesKeys.SELECTED_MODULES_CSV] = selectedModulesCsv
        }
    }

    suspend fun togglePreviewMode(active: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_PREVIEW_MODE_ACTIVE] = active
        }
    }

    suspend fun dismissSetupChecklist() {
        dataStore.edit {
            it[PreferencesKeys.IS_SETUP_CHECKLIST_DISMISSED] = true
        }
    }

    suspend fun markTooltipSeen(tooltipId: String) {
        dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.SEEN_TOOLTIPS_CSV] ?: ""
            val list = current.split(",").filter { it.isNotBlank() }.toMutableSet()
            list.add(tooltipId)
            prefs[PreferencesKeys.SEEN_TOOLTIPS_CSV] = list.joinToString(",")
        }
    }

    suspend fun markFirstActionLogged() {
        dataStore.edit {
            it[PreferencesKeys.HAS_LOGGED_FIRST_ACTION] = true
        }
    }

    suspend fun updateCareProfile(
        patientName: String,
        healthContext: String,
        bloodGroup: String,
        transplantDate: String,
        emergencyNotes: String,
        nephrologistContact: String
    ) {
        dataStore.edit {
            it[PreferencesKeys.PATIENT_NAME] = patientName.trim()
            it[PreferencesKeys.HEALTH_CONTEXT] = healthContext.trim()
            it[PreferencesKeys.BLOOD_GROUP] = bloodGroup.trim()
            it[PreferencesKeys.TRANSPLANT_DATE] = transplantDate.trim()
            it[PreferencesKeys.EMERGENCY_NOTES] = emergencyNotes.trim()
            it[PreferencesKeys.NEPHROLOGIST_CONTACT] = nephrologistContact.trim()
        }
    }

    suspend fun updateFluidTarget(targetMl: Int) {
        dataStore.edit {
            it[PreferencesKeys.FLUID_TARGET] = targetMl
            it[PreferencesKeys.INTAKE_TARGET] = targetMl
        }
    }

    suspend fun updateIntakeAndUrineTargets(intakeTargetMl: Int, urineTargetMl: Int) {
        dataStore.edit {
            it[PreferencesKeys.FLUID_TARGET] = intakeTargetMl
            it[PreferencesKeys.INTAKE_TARGET] = intakeTargetMl
            it[PreferencesKeys.URINE_TARGET] = urineTargetMl
        }
    }

    suspend fun updateQuickPresets(presets: String) {
        dataStore.edit { it[PreferencesKeys.QUICK_PRESETS] = presets }
    }

    suspend fun updateTransplantProfile(
        transplantDate: String,
        bloodGroup: String,
        nephrologistContact: String,
        emergencyNotes: String
    ) {
        dataStore.edit {
            it[PreferencesKeys.TRANSPLANT_DATE] = transplantDate
            it[PreferencesKeys.BLOOD_GROUP] = bloodGroup
            it[PreferencesKeys.NEPHROLOGIST_CONTACT] = nephrologistContact
            it[PreferencesKeys.EMERGENCY_NOTES] = emergencyNotes
        }
    }

    suspend fun updateDefaultWidgetAmount(amountMl: Int) {
        dataStore.edit { it[PreferencesKeys.DEFAULT_WIDGET_AMOUNT] = amountMl }
    }

    suspend fun updateThresholds(low: Int, targetMin: Int, targetMax: Int, criticalHigh: Int) {
        dataStore.edit {
            it[PreferencesKeys.SUGAR_LOW] = low
            it[PreferencesKeys.SUGAR_TARGET_MIN] = targetMin
            it[PreferencesKeys.SUGAR_TARGET_MAX] = targetMax
            it[PreferencesKeys.SUGAR_CRITICAL_HIGH] = criticalHigh
        }
    }

    suspend fun updateBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun updateGeminiApiKey(key: String) {
        dataStore.edit { it[PreferencesKeys.GEMINI_API_KEY] = key.trim() }
    }

    suspend fun updatePatientName(name: String) {
        dataStore.edit { it[PreferencesKeys.PATIENT_NAME] = name.trim() }
    }

    suspend fun updateThemeMode(mode: String) {
        dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode }
    }

    suspend fun toggleModule(module: String, enabled: Boolean) {
        dataStore.edit {
            when (module) {
                "bp" -> it[PreferencesKeys.MODULE_BP] = enabled
                "pulse" -> it[PreferencesKeys.MODULE_PULSE] = enabled
                "temp" -> it[PreferencesKeys.MODULE_TEMP] = enabled
                "meds" -> it[PreferencesKeys.MODULE_MEDS] = enabled
            }
        }
    }

    suspend fun updateReminderTime(reminderKey: String, time: String) {
        dataStore.edit {
            when (reminderKey) {
                "breakfast" -> it[PreferencesKeys.REMINDER_BREAKFAST] = time
                "lunch" -> it[PreferencesKeys.REMINDER_LUNCH] = time
                "after_lunch" -> it[PreferencesKeys.REMINDER_AFTER_LUNCH] = time
                "dinner" -> it[PreferencesKeys.REMINDER_DINNER] = time
                "weight" -> it[PreferencesKeys.REMINDER_WEIGHT] = time
            }
        }
    }

    suspend fun updateFluidNudge(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.FLUID_NUDGE_ENABLED] = enabled }
    }

    suspend fun updateGoogleDriveBackupEnabled(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.GOOGLE_DRIVE_BACKUP_ENABLED] = enabled }
    }

    suspend fun updateGoogleDriveAccount(account: String) {
        dataStore.edit { it[PreferencesKeys.GOOGLE_DRIVE_ACCOUNT] = account.trim() }
    }

    suspend fun updateGoogleDriveBackupTime(time: String) {
        dataStore.edit { it[PreferencesKeys.GOOGLE_DRIVE_BACKUP_TIME] = time }
    }

    suspend fun recordGoogleDriveBackupResult(timestamp: Long, status: String) {
        dataStore.edit {
            it[PreferencesKeys.LAST_GOOGLE_DRIVE_BACKUP_TS] = timestamp
            it[PreferencesKeys.LAST_GOOGLE_DRIVE_BACKUP_STATUS] = status
        }
    }
}
