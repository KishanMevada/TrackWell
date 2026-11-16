package com.trackwell.app.ui.screens.medicine.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackwell.app.ui.screens.medicine.MedicineViewModel

/**
 * Global host composable that automatically displays the active medicine reminder bottom sheet
 * whenever a reminder is due and awaiting user action ("Took it" / "Skipped").
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ActiveReminderHost(
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeReminder = uiState.visiblePendingReminder

    if (activeReminder != null) {
        MedicineReminderBottomSheet(
            reminder = activeReminder,
            onTookIt = { viewModel.tookIt(activeReminder) },
            onSkipped = { viewModel.skipped(activeReminder) },
            onDismiss = { viewModel.dismissInAppReminder(activeReminder.pendingReminderId) }
        )
    }
}
