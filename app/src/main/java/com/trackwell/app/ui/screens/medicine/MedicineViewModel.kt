package com.trackwell.app.ui.screens.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.alarms.medicine.MedicineReminderScheduler
import com.trackwell.app.data.medicine.MedicineRepository
import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.domain.medicine.model.NextMedicineReminder
import com.trackwell.app.domain.medicine.model.PendingMedicineReminder
import com.trackwell.app.notification.medicine.MedicineNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MedicineUiState(
    val schedules: List<MedicineSchedule> = emptyList(),
    val nextReminder: NextMedicineReminder? = null,
    val activeReminders: List<PendingMedicineReminder> = emptyList(),
    val dismissedReminderIdsInApp: Set<String> = emptySet(),
    val hasExactAlarmPermission: Boolean = true,
    val hasNotificationPermission: Boolean = true,
    val isLockScreenPrivacyEnabled: Boolean = false,
    val isLoading: Boolean = false
) {
    val isStrictRemindersOn: Boolean
        get() = hasExactAlarmPermission && hasNotificationPermission

    val visiblePendingReminder: PendingMedicineReminder?
        get() = activeReminders.firstOrNull { it.pendingReminderId !in dismissedReminderIdsInApp }
}

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val repository: MedicineRepository,
    private val scheduler: MedicineReminderScheduler,
    private val notificationManager: MedicineNotificationManager
) : ViewModel() {

    private val _nextReminder = MutableStateFlow<NextMedicineReminder?>(null)
    private val _dismissedReminderIds = MutableStateFlow<Set<String>>(emptySet())
    private val _permissionState = MutableStateFlow(
        Pair(scheduler.canScheduleExactAlarms(), true)
    )
    private val _isLockScreenPrivacy = MutableStateFlow(false)

    private val _settingsAndPermissions = combine(
        _permissionState,
        _isLockScreenPrivacy
    ) { perms, privacy -> Pair(perms, privacy) }

    val uiState: StateFlow<MedicineUiState> = combine(
        repository.observeSchedules(),
        _nextReminder,
        repository.observeActivePendingReminders(),
        _dismissedReminderIds,
        _settingsAndPermissions
    ) { schedules, nextReminder, activeReminders, dismissedIds, settingsPerms ->
        val (permissions, lockScreenPrivacy) = settingsPerms
        MedicineUiState(
            schedules = schedules,
            nextReminder = nextReminder,
            activeReminders = activeReminders,
            dismissedReminderIdsInApp = dismissedIds,
            hasExactAlarmPermission = permissions.first,
            hasNotificationPermission = permissions.second,
            isLockScreenPrivacyEnabled = lockScreenPrivacy,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MedicineUiState()
    )

    init {
        refreshNextReminder()
    }

    fun refreshPermissions(hasNotificationPermission: Boolean) {
        val hasExactAlarm = scheduler.canScheduleExactAlarms()
        _permissionState.value = Pair(hasExactAlarm, hasNotificationPermission)
    }

    fun setLockScreenPrivacy(enabled: Boolean) {
        _isLockScreenPrivacy.value = enabled
    }

    fun refreshNextReminder() {
        viewModelScope.launch {
            val next = scheduler.getNextUpcomingReminder()
            _nextReminder.value = next
        }
    }

    fun saveSchedule(schedule: MedicineSchedule) {
        viewModelScope.launch {
            repository.saveSchedule(schedule)
            if (schedule.isEnabled) {
                scheduler.scheduleMedicine(schedule)
            } else {
                scheduler.cancelMedicineAlarms(schedule.id)
            }
            refreshNextReminder()
        }
    }

    fun toggleScheduleEnabled(scheduleId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateScheduleEnabled(scheduleId, isEnabled)
            val updated = repository.getSchedule(scheduleId)
            if (updated != null) {
                if (isEnabled) {
                    scheduler.scheduleMedicine(updated)
                } else {
                    scheduler.cancelMedicineAlarms(scheduleId)
                }
            }
            refreshNextReminder()
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduler.cancelMedicineAlarms(scheduleId)
            repository.deleteSchedule(scheduleId)
            refreshNextReminder()
        }
    }

    fun tookIt(reminder: PendingMedicineReminder) {
        viewModelScope.launch {
            // Strictly resolve and delete pending reminder from Room - NO historical adherence log
            repository.resolvePendingReminder(reminder.pendingReminderId)
            notificationManager.cancelNotification(reminder.notificationId)
            notificationManager.cancelNotification(reminder.followUpNotificationId)
            scheduler.cancelMedicineAlarms(reminder.medicineId)

            val schedule = repository.getSchedule(reminder.medicineId)
            if (schedule != null && schedule.isEnabled) {
                scheduler.scheduleMedicine(schedule)
            }
            refreshNextReminder()
        }
    }

    fun skipped(reminder: PendingMedicineReminder) {
        viewModelScope.launch {
            // Strictly resolve and delete pending reminder from Room - NO historical adherence log
            repository.resolvePendingReminder(reminder.pendingReminderId)
            notificationManager.cancelNotification(reminder.notificationId)
            notificationManager.cancelNotification(reminder.followUpNotificationId)
            scheduler.cancelMedicineAlarms(reminder.medicineId)

            val schedule = repository.getSchedule(reminder.medicineId)
            if (schedule != null && schedule.isEnabled) {
                scheduler.scheduleMedicine(schedule)
            }
            refreshNextReminder()
        }
    }

    fun dismissInAppReminder(pendingReminderId: String) {
        _dismissedReminderIds.value = _dismissedReminderIds.value + pendingReminderId
    }

    fun isReminderDismissedInApp(pendingReminderId: String): Boolean {
        return pendingReminderId in _dismissedReminderIds.value
    }
}
