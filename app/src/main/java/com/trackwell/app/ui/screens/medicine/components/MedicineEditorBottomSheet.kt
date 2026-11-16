package com.trackwell.app.ui.screens.medicine.components

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import com.trackwell.app.ui.home.components.homeCardShadow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.domain.medicine.model.MedicineDoseUnit
import com.trackwell.app.domain.medicine.model.MedicineRepeatMode
import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.theme.InterFontFamily
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineEditorBottomSheet(
    existingSchedule: MedicineSchedule? = null,
    onSave: (MedicineSchedule) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val isEditMode = existingSchedule != null

    var name by remember { mutableStateOf(existingSchedule?.name ?: "") }
    var doseValue by remember { mutableStateOf(existingSchedule?.doseValue ?: "1") }
    var selectedUnit by remember { mutableStateOf(existingSchedule?.doseUnit ?: MedicineDoseUnit.MG) }
    var isEnabled by remember { mutableStateOf(existingSchedule?.isEnabled ?: true) }
    var repeatMode by remember { mutableStateOf(existingSchedule?.repeatMode ?: MedicineRepeatMode.DAILY) }
    var selectedDays by remember { mutableStateOf(existingSchedule?.selectedDaysOfWeek ?: setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)) }
    var reminderTimes by remember { mutableStateOf(existingSchedule?.reminderTimes ?: listOf("08:00")) }
    var isSaving by remember { mutableStateOf(false) }

    // Validation
    val isNameValid = name.trim().isNotBlank()
    val isDoseValid = doseValue.toDoubleOrNull()?.let { it > 0.0 } ?: false
    val isTimeValid = reminderTimes.isNotEmpty()
    val isRepeatValid = repeatMode == MedicineRepeatMode.DAILY || selectedDays.isNotEmpty()
    val isFormValid = isNameValid && isDoseValid && isTimeValid && isRepeatValid && !isSaving

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = Color(0xFFF8FBFF),
        scrimColor = Color.Black.copy(alpha = 0.40f),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .imePadding()
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Elevated 3D Capsule Icon Container
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .homeCardShadow(
                                cornerRadius = 16.dp,
                                elevation = 6.dp,
                                spotColor = Color(0x300284C7),
                                ambientColor = Color(0x180284C7)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFF0F9FF))
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFBAE6FD))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon3DCapsule(size = 32.dp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isEditMode) "Edit medicine" else "Add medicine",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.3).sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Set a reminder schedule",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }

                // 3D Elevated Circular Close 'X' Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = Color(0x20000000),
                            ambientColor = Color(0x10000000)
                        )
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onDismiss()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 1. Medicine Name Field
            SectionLabel("Medicine name")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g. Tacrolimus", color = Color(0xFF94A3B8)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0284C7),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Dose & Unit Row
            SectionLabel("Dose & unit")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = doseValue,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1) {
                            doseValue = input
                        }
                    },
                    placeholder = { Text("1", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.weight(0.42f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                // Unit selection chips (horizontal scrolling / wrapping)
                Row(
                    modifier = Modifier
                        .weight(0.58f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(MedicineDoseUnit.MG, MedicineDoseUnit.MCG, MedicineDoseUnit.TABLET).forEach { unit ->
                        val isSelected = selectedUnit == unit
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedUnit = unit
                                    HapticFeedbackHelper.performTickHaptic(context)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color.White else Color.Transparent,
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unit.label,
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Reminder Times
            SectionLabel("Reminder time(s)")
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminderTimes.forEachIndexed { index, timeStr ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val parts = timeStr.split(":")
                                val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 8
                                val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val formatted = String.format(Locale.US, "%02d:%02d", hourOfDay, minute)
                                        val updated = reminderTimes.toMutableList()
                                        updated[index] = formatted
                                        reminderTimes = updated.sorted()
                                        HapticFeedbackHelper.performTickHaptic(context)
                                    },
                                    initialHour,
                                    initialMinute,
                                    false
                                ).show()
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = formatTimeToDisplay12Hour(timeStr),
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                            }

                            if (reminderTimes.size > 1) {
                                IconButton(
                                    onClick = {
                                        reminderTimes = reminderTimes.filterIndexed { i, _ -> i != index }
                                        HapticFeedbackHelper.performTickHaptic(context)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove time",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Add time button
                if (reminderTimes.size < 6) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                val newTime = "12:00"
                                if (!reminderTimes.contains(newTime)) {
                                    reminderTimes = (reminderTimes + newTime).sorted()
                                    HapticFeedbackHelper.performTickHaptic(context)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF0F9FF),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add another time",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF0284C7)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Repeat Schedule Segmented Control
            SectionLabel("Repeat schedule")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Every day tab
                val isDaily = repeatMode == MedicineRepeatMode.DAILY
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable {
                            repeatMode = MedicineRepeatMode.DAILY
                            HapticFeedbackHelper.performTickHaptic(context)
                        },
                    shape = RoundedCornerShape(11.dp),
                    color = if (isDaily) Color.White else Color.Transparent,
                    shadowElevation = if (isDaily) 2.dp else 0.dp
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Every day",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = if (isDaily) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.5.sp,
                                color = if (isDaily) Color(0xFF0284C7) else Color(0xFF64748B)
                            )
                        )
                    }
                }

                // Choose days tab
                val isCustomDays = repeatMode == MedicineRepeatMode.SELECTED_DAYS
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable {
                            repeatMode = MedicineRepeatMode.SELECTED_DAYS
                            HapticFeedbackHelper.performTickHaptic(context)
                        },
                    shape = RoundedCornerShape(11.dp),
                    color = if (isCustomDays) Color.White else Color.Transparent,
                    shadowElevation = if (isCustomDays) 2.dp else 0.dp
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Choose days",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = if (isCustomDays) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.5.sp,
                                color = if (isCustomDays) Color(0xFF0284C7) else Color(0xFF64748B)
                            )
                        )
                    }
                }
            }

            // Day Chips (if Choose days)
            AnimatedVisibility(visible = repeatMode == MedicineRepeatMode.SELECTED_DAYS) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    val days = listOf(
                        DayOfWeek.MONDAY to "M",
                        DayOfWeek.TUESDAY to "T",
                        DayOfWeek.WEDNESDAY to "W",
                        DayOfWeek.THURSDAY to "T",
                        DayOfWeek.FRIDAY to "F",
                        DayOfWeek.SATURDAY to "S",
                        DayOfWeek.SUNDAY to "S"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        days.forEach { (day, label) ->
                            val isSelected = selectedDays.contains(day)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedDays = if (isSelected) {
                                            if (selectedDays.size > 1) selectedDays - day else selectedDays
                                        } else {
                                            selectedDays + day
                                        }
                                        HapticFeedbackHelper.performTickHaptic(context)
                                    },
                                shape = CircleShape,
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        style = TextStyle(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else Color(0xFF0F172A)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Reminder Enabled Switch
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Reminder active",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEnabled) "Notifications will fire at scheduled times" else "Schedule is paused",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0284C7)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Save Button
            Button(
                onClick = {
                    if (isFormValid) {
                        isSaving = true
                        HapticFeedbackHelper.performSuccessHaptic(context)
                        val schedule = existingSchedule?.copy(
                            name = name.trim(),
                            doseValue = doseValue.trim(),
                            doseUnit = selectedUnit,
                            isEnabled = isEnabled,
                            repeatMode = repeatMode,
                            selectedDaysOfWeek = selectedDays,
                            reminderTimes = reminderTimes.sorted(),
                            updatedAt = System.currentTimeMillis()
                        ) ?: MedicineSchedule(
                            id = UUID.randomUUID().toString(),
                            name = name.trim(),
                            doseValue = doseValue.trim(),
                            doseUnit = selectedUnit,
                            isEnabled = isEnabled,
                            repeatMode = repeatMode,
                            selectedDaysOfWeek = selectedDays,
                            reminderTimes = reminderTimes.sorted(),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(schedule)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x350284C7)),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF0284C7).copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                enabled = isFormValid
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Save changes" else "Save medicine",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label,
        style = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        ),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

private fun formatTimeToDisplay12Hour(timeStr: String): String {
    return try {
        val parsed = SimpleDateFormat("HH:mm", Locale.US).parse(timeStr)
        if (parsed != null) {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(parsed)
        } else {
            timeStr
        }
    } catch (e: Exception) {
        timeStr
    }
}
