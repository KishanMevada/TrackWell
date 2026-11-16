package com.trackwell.app.ui.screens.today.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.PlausibleRanges

@Composable
fun EditWeightDialog(
    initialWeight: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    val context = LocalContext.current
    var weightText by remember { mutableStateOf(initialWeight?.let { String.format("%.2f", it) } ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Morning Weight",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Consistent morning weighing (after first urination, before eating) helps monitor fluid retention in transplant recovery.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { input ->
                        weightText = input
                        errorMessage = null
                    },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("e.g. 68.50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = weightText.toDoubleOrNull()
                    if (parsed != null && PlausibleRanges.isPlausibleWeight(parsed)) {
                        HapticFeedbackHelper.performSuccessHaptic(context)
                        onConfirm(parsed)
                    } else {
                        errorMessage = "Please enter a valid weight (30 - 250 kg)"
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EditTargetDialog(
    initialIntakeTarget: Int = 3500,
    initialUrineTarget: Int = 3200,
    onDismiss: () -> Unit,
    onConfirm: (intakeTarget: Int, urineTarget: Int) -> Unit
) {
    val context = LocalContext.current
    var intakeText by remember { mutableStateOf(initialIntakeTarget.toString()) }
    var urineText by remember { mutableStateOf(initialUrineTarget.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clinical Fluid Targets",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Post-transplant protocol prescribes separate goals for daily intake (graft flushing) and minimum urine diuresis (accounting for ~300-500 ml insensible loss).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = intakeText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            intakeText = input
                            errorMessage = null
                        }
                    },
                    label = { Text("Daily Intake Target (ml)") },
                    placeholder = { Text("3500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = urineText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            urineText = input
                            errorMessage = null
                        }
                    },
                    label = { Text("Daily Urine Target (ml)") },
                    placeholder = { Text("3200") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedIntake = intakeText.toIntOrNull()
                    val parsedUrine = urineText.toIntOrNull()
                    if (parsedIntake != null && parsedIntake in 500..8000 &&
                        parsedUrine != null && parsedUrine in 500..8000
                    ) {
                        HapticFeedbackHelper.performSuccessHaptic(context)
                        onConfirm(parsedIntake, parsedUrine)
                    } else {
                        errorMessage = "Please enter valid values between 500 ml and 8000 ml"
                    }
                }
            ) {
                Text("Save Targets")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
