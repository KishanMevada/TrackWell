package com.trackwell.app.ui.screens.today.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.util.DateTimeUtils

/**
 * Backward compatibility wrapper delegating to [FluidEntryBottomSheet].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddBottomSheet(
    initialType: FluidType = FluidType.INTAKE,
    selectedDateIso: String = DateTimeUtils.getTodayIso(),
    customPresets: List<Int>? = null,
    onDismiss: () -> Unit,
    onSave: (amountMl: Int, type: FluidType, timestamp: Long) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    FluidEntryBottomSheet(
        initialType = initialType,
        selectedDateIso = selectedDateIso,
        initialAmountMl = 200,
        customPresets = customPresets,
        onDismiss = onDismiss,
        onSave = onSave,
        sheetState = sheetState
    )
}
