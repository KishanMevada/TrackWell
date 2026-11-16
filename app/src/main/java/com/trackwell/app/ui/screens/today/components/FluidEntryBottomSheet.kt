package com.trackwell.app.ui.screens.today.components

import android.app.TimePickerDialog
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.Icon3DUrineDrop
import com.trackwell.app.ui.common.Icon3DWaterDrop
import com.trackwell.app.ui.theme.InterFontFamily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Redesigned "Add Fluid" bottom sheet matching the calm medical-glass dashboard theme.
 *
 * Supports both Fluid Intake and Urine Output entries with:
 * - Pale blue-white surface (#F8FBFF) with 30dp rounded top corners and soft scrim.
 * - Centered 40x5dp drag handle and accessible close 'X' button.
 * - Header: "Add fluid" with secondary date display ("Today · Nov 16").
 * - Segmented control: "Intake" (cyan water droplet) vs "Urine" (warm amber droplet).
 * - Amount section: subtle mode-tinted card with circular +/- stepper buttons, large bold ml display,
 *   and optional custom numeric keypad input.
 * - Quick amount presets: 4 balanced chips [100, 200, 250, 500] with mode-colored active states.
 * - Time selection row: displays "Now · [Time]" or edited time; opens Android TimePickerDialog.
 * - Primary save button: full-width rounded CTA with checkmark and dynamic copy ("Add 200 ml" / "Log 200 ml urine").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluidEntryBottomSheet(
    initialType: FluidType = FluidType.INTAKE,
    selectedDateIso: String = DateTimeUtils.getTodayIso(),
    initialAmountMl: Int = 200,
    customPresets: List<Int>? = null,
    onDismiss: () -> Unit,
    onSave: (amountMl: Int, type: FluidType, timestamp: Long) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(initialType) }
    var amountMl by remember { mutableIntStateOf(initialAmountMl) }

    // Initial timestamp based on selected date & current hour/minute
    val initialTimestamp = remember(selectedDateIso) {
        val now = Calendar.getInstance()
        val targetCal = Calendar.getInstance()
        val parsedDate = DateTimeUtils.parseIsoDate(selectedDateIso)
        if (parsedDate != null) {
            targetCal.time = parsedDate
            targetCal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
            targetCal.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
            targetCal.set(Calendar.SECOND, 0)
            targetCal.timeInMillis
        } else {
            System.currentTimeMillis()
        }
    }
    var currentTimestamp by remember(selectedDateIso) { mutableLongStateOf(initialTimestamp) }
    var isUserCustomTime by remember { mutableStateOf(false) }

    var isCustomKeypadOpen by remember { mutableStateOf(false) }
    var customInputString by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val presetChips = customPresets?.takeIf { it.isNotEmpty() } ?: listOf(100, 200, 250, 500)

    // Formatted date string (e.g. "Today · Nov 16", "Yesterday · Nov 15")
    val formattedDateText = remember(selectedDateIso) {
        val parsed = DateTimeUtils.parseIsoDate(selectedDateIso)
        if (parsed != null) {
            val monthDay = SimpleDateFormat("MMM d", Locale.getDefault()).format(parsed)
            val today = DateTimeUtils.getTodayIso()
            val yesterday = DateTimeUtils.getDaysAgoIso(1)
            val tomorrow = DateTimeUtils.getDaysAgoIso(-1)
            when (selectedDateIso) {
                today -> "Today · $monthDay"
                yesterday -> "Yesterday · $monthDay"
                tomorrow -> "Tomorrow · $monthDay"
                else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(parsed)
            }
        } else {
            selectedDateIso
        }
    }

    // Color definitions
    val isIntake = selectedType == FluidType.INTAKE
    val modePrimaryColor = if (isIntake) Color(0xFF0284C7) else Color(0xFFD97706)
    val modeSurfaceTint = if (isIntake) Color(0xFFF0F9FF) else Color(0xFFFFFBEB)
    val modeBorderColor = if (isIntake) Color(0xFFBAE6FD) else Color(0xFFFDE68A)
    val modePillActiveBg = if (isIntake) Color(0xFFE0F2FE) else Color(0xFFFEF3C7)

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
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            // -----------------------------------------------------------------
            // 1. Header: Title, Calm Date Subtitle, and Close 'X' Button
            // -----------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add fluid",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedDateText,
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // -----------------------------------------------------------------
            // 2. Fluid Type Selector: Segmented Control (Intake vs Urine)
            // -----------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Intake Tab
                val intakeBg by animateColorAsState(
                    targetValue = if (isIntake) Color.White else Color.Transparent,
                    animationSpec = tween(durationMillis = 200),
                    label = "IntakeBg"
                )
                val intakeTextCol by animateColorAsState(
                    targetValue = if (isIntake) Color(0xFF0284C7) else Color(0xFF64748B),
                    animationSpec = tween(durationMillis = 200),
                    label = "IntakeText"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Tab) {
                            if (selectedType != FluidType.INTAKE) {
                                selectedType = FluidType.INTAKE
                                HapticFeedbackHelper.performTickHaptic(context)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = intakeBg,
                    shadowElevation = if (isIntake) 3.dp else 0.dp,
                    border = if (isIntake) BorderStroke(1.dp, Color(0xFFBAE6FD)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon3DWaterDrop(size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Intake",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = if (isIntake) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.5.sp,
                                color = intakeTextCol
                            )
                        )
                    }
                }

                // Urine Tab
                val isUrine = selectedType == FluidType.URINE
                val urineBg by animateColorAsState(
                    targetValue = if (isUrine) Color.White else Color.Transparent,
                    animationSpec = tween(durationMillis = 200),
                    label = "UrineBg"
                )
                val urineTextCol by animateColorAsState(
                    targetValue = if (isUrine) Color(0xFFD97706) else Color(0xFF64748B),
                    animationSpec = tween(durationMillis = 200),
                    label = "UrineText"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Tab) {
                            if (selectedType != FluidType.URINE) {
                                selectedType = FluidType.URINE
                                HapticFeedbackHelper.performTickHaptic(context)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = urineBg,
                    shadowElevation = if (isUrine) 3.dp else 0.dp,
                    border = if (isUrine) BorderStroke(1.dp, Color(0xFFFDE68A)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon3DUrineDrop(size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Urine",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = if (isUrine) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.5.sp,
                                color = urineTextCol
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // -----------------------------------------------------------------
            // 3. Amount Section: Label, Mode-Tinted Surface, Minus/Plus Steppers
            // -----------------------------------------------------------------
            Text(
                text = "Amount",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = Color(0x10000000)),
                shape = RoundedCornerShape(22.dp),
                color = modeSurfaceTint,
                border = BorderStroke(1.2.dp, modeBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Minus Button (Step -10 ml)
                    val canDecrement = amountMl > 10
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(if (canDecrement) 2.dp else 0.dp, CircleShape)
                            .clip(CircleShape)
                            .clickable(
                                enabled = canDecrement,
                                onClick = {
                                    amountMl = maxOf(10, amountMl - 10)
                                    HapticFeedbackHelper.performTickHaptic(context)
                                }
                            ),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease amount by 10 ml",
                                tint = if (canDecrement) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Center Amount & Unit (Clickable for custom inline keypad)
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                isCustomKeypadOpen = !isCustomKeypadOpen
                                customInputString = amountMl.toString()
                            }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$amountMl",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 42.sp,
                                    color = Color(0xFF0F172A)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ml",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = modePrimaryColor
                                ),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Text(
                            text = if (isCustomKeypadOpen) "Tap numbers below" else "Tap for custom ml",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.5.sp,
                                color = modePrimaryColor.copy(alpha = 0.85f)
                            )
                        )
                    }

                    // Circular Plus Button (Step +10 ml)
                    val canIncrement = amountMl < 3500
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(if (canIncrement) 2.dp else 0.dp, CircleShape)
                            .clip(CircleShape)
                            .clickable(
                                enabled = canIncrement,
                                onClick = {
                                    amountMl = minOf(3500, amountMl + 10)
                                    HapticFeedbackHelper.performTickHaptic(context)
                                }
                            ),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase amount by 10 ml",
                                tint = if (canIncrement) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // -----------------------------------------------------------------
            // 4. Quick Amount Presets vs Custom Numeric Keypad
            // -----------------------------------------------------------------
            if (!isCustomKeypadOpen) {
                Text(
                    text = "Quick amounts",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetChips.forEach { preset ->
                        val isSelected = amountMl == preset
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    amountMl = preset
                                    HapticFeedbackHelper.performClickHaptic(context)
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) modePillActiveBg else Color(0xFFF1F5F9),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) modePrimaryColor else Color(0xFFE2E8F0)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$preset",
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 15.sp,
                                        color = if (isSelected) modePrimaryColor else Color(0xFF0F172A)
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // Custom Numeric Keypad for precise input
                NumericKeypad(
                    onDigit = { digit ->
                        if (customInputString.length < 5) {
                            val candidate = (customInputString + digit).toIntOrNull() ?: amountMl
                            if (candidate <= 5000) {
                                customInputString += digit
                                amountMl = candidate
                                HapticFeedbackHelper.performTickHaptic(context)
                            }
                        }
                    },
                    onBackspace = {
                        if (customInputString.isNotEmpty()) {
                            customInputString = customInputString.dropLast(1)
                            amountMl = customInputString.toIntOrNull() ?: 10
                            HapticFeedbackHelper.performTickHaptic(context)
                        }
                    },
                    onDone = {
                        isCustomKeypadOpen = false
                        if (amountMl <= 0) amountMl = 10
                        HapticFeedbackHelper.performClickHaptic(context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // -----------------------------------------------------------------
            // 5. Time Selection Row: Displays "Now · 11:22 AM" / Edited Time
            // -----------------------------------------------------------------
            val isNowTime = !isUserCustomTime &&
                DateTimeUtils.isToday(selectedDateIso) &&
                Math.abs(currentTimestamp - System.currentTimeMillis()) < 5 * 60 * 1000

            val timeDisplayString = if (isNowTime) {
                "Now · ${DateTimeUtils.formatTime(currentTimestamp)}"
            } else {
                DateTimeUtils.formatTime(currentTimestamp)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        val cal = Calendar.getInstance().apply { timeInMillis = currentTimestamp }
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val updatedCal = Calendar.getInstance().apply {
                                    timeInMillis = currentTimestamp
                                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    set(Calendar.MINUTE, minute)
                                    set(Calendar.SECOND, 0)
                                }
                                currentTimestamp = updatedCal.timeInMillis
                                isUserCustomTime = true
                                HapticFeedbackHelper.performTickHaptic(context)
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Entry time",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = timeDisplayString,
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.5.sp,
                                color = Color(0xFF334155)
                            )
                        )
                    }

                    Text(
                        text = "Edit",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = modePrimaryColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -----------------------------------------------------------------
            // 6. Primary Save CTA Button: Full-width, Rounded, Live Copy
            // -----------------------------------------------------------------
            val ctaLabel = if (isIntake) "Add $amountMl ml" else "Log $amountMl ml urine"

            Button(
                onClick = {
                    if (amountMl > 0 && !isSaving) {
                        isSaving = true
                        HapticFeedbackHelper.performSuccessHaptic(context)
                        onSave(amountMl, selectedType, currentTimestamp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = modePrimaryColor.copy(alpha = 0.40f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = modePrimaryColor,
                    contentColor = Color.White,
                    disabledContainerColor = modePrimaryColor.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                enabled = amountMl > 0 && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
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
                        text = ctaLabel,
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
fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("back", "0", "done")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                when (key) {
                                    "back" -> onBackspace()
                                    "done" -> onDone()
                                    else -> onDigit(key)
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (key == "done") Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (key == "done") Color(0xFFBAE6FD) else Color(0xFFE2E8F0))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            when (key) {
                                "back" -> Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Backspace",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                "done" -> Text(
                                    text = "Done",
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0284C7)
                                    )
                                )
                                else -> Text(
                                    text = key,
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


