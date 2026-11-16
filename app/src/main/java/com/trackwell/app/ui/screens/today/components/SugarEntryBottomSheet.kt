package com.trackwell.app.ui.screens.today.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarReadingStatus
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.model.ThresholdSettings
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.Icon3DBloodDrop
import com.trackwell.app.ui.common.PlausibleRanges
import com.trackwell.app.ui.common.ReadingAlertBanner
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.theme.InterFontFamily
import java.util.Locale

/**
 * 3D Claymorphic "Log Sugar & Insulin" Bottom Sheet matching the TrackWell
 * dashboard, renal cards, and medicine option sheet design language.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarEntryBottomSheet(
    initialSlot: SugarSlot = SugarSlot.suggestCurrentSlot(),
    existingEntry: SugarEntry? = null,
    existingEntries: Map<SugarSlot, SugarEntry>? = null,
    selectedDateIso: String = DateTimeUtils.getTodayIso(),
    thresholds: ThresholdSettings,
    onDismiss: () -> Unit,
    onSave: (slot: SugarSlot, sugarMgDl: Int, insulinUnits: Double, timestamp: Long, note: String?) -> Unit,
    onDelete: ((Long) -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    var selectedSlot by remember(initialSlot) { mutableStateOf(existingEntry?.slot ?: initialSlot) }

    val currentSlotEntry = remember(selectedSlot, existingEntry, existingEntries) {
        existingEntries?.get(selectedSlot) ?: (if (selectedSlot == (existingEntry?.slot ?: initialSlot)) existingEntry else null)
    }

    var sugarInput by remember(selectedSlot, existingEntry, existingEntries) {
        mutableStateOf(currentSlotEntry?.sugarMgDl?.toString() ?: "")
    }
    var insulinUnits by remember(selectedSlot, existingEntry, existingEntries) {
        mutableDoubleStateOf(currentSlotEntry?.insulinUnits ?: 0.0)
    }
    var note by remember(selectedSlot, existingEntry, existingEntries) {
        mutableStateOf(currentSlotEntry?.note ?: "")
    }

    val sugarValue = sugarInput.toIntOrNull()
    val status = sugarValue?.let { thresholds.evaluate(it) } ?: SugarReadingStatus.NOT_RECORDED

    val (badgeBg, badgeText, statusColor) = when (status) {
        SugarReadingStatus.IN_RANGE -> Triple(Color(0xFFDCFCE7), "In Range (70–140)", Color(0xFF16A34A))
        SugarReadingStatus.HIGH -> Triple(Color(0xFFFEE2E2), "High Reading", Color(0xFFDC2626))
        SugarReadingStatus.LOW -> Triple(Color(0xFFFEF3C7), "Low Reading", Color(0xFFD97706))
        SugarReadingStatus.NOT_RECORDED -> Triple(Color(0xFFF1F5F9), "Target: 70–140 mg/dL", Color(0xFF64748B))
    }

    val isEditMode = currentSlotEntry != null
    val isFormValid = sugarValue != null && sugarValue > 0 && PlausibleRanges.isPlausibleSugar(sugarValue)

    val slotShortName = when (selectedSlot) {
        SugarSlot.BEFORE_BREAKFAST -> "Fasting"
        SugarSlot.BEFORE_LUNCH -> "Pre-Lunch"
        SugarSlot.AFTER_LUNCH -> "Post-Lunch"
        SugarSlot.BEFORE_DINNER -> "Pre-Dinner"
    }

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
                .padding(horizontal = 20.dp)
                .imePadding()
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            // -------------------------------------------------------------
            // 1. HEADER ROW: 3D Blood Drop Icon + Title + 3D Close Button
            // -------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Elevated 3D Blood Drop Container
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .homeCardShadow(
                                cornerRadius = 16.dp,
                                elevation = 6.dp,
                                spotColor = Color(0x30EF4444),
                                ambientColor = Color(0x18EF4444)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFFFF1F2))
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFFECACA))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon3DBloodDrop(size = 30.dp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isEditMode) "Edit Sugar & Insulin" else "Log Sugar & Insulin",
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
                            text = "${DateTimeUtils.formatDisplayDate(selectedDateIso)} · $slotShortName",
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

            // -------------------------------------------------------------
            // 2. 3D TACTILE TIME SLOT SELECTOR (Segmented Control)
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEEF2F6))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE2E8F0).copy(alpha = 0.80f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    val slots = listOf(
                        SugarSlot.BEFORE_BREAKFAST to "B. Fast",
                        SugarSlot.BEFORE_LUNCH to "B. Lunch",
                        SugarSlot.AFTER_LUNCH to "A. Lunch",
                        SugarSlot.BEFORE_DINNER to "B. Dinner"
                    )

                    slots.forEach { (slot, label) ->
                        val isSelected = (selectedSlot == slot)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (isSelected) {
                                        Modifier
                                            .shadow(
                                                elevation = 3.dp,
                                                shape = RoundedCornerShape(14.dp),
                                                spotColor = Color(0x25EF4444)
                                            )
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(Color.White, Color(0xFFFFF1F2))
                                                )
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFFECACA),
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                    } else {
                                        Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = {
                                                    HapticFeedbackHelper.performClickHaptic(context)
                                                    selectedSlot = slot
                                                }
                                            )
                                    }
                                )
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color(0xFFDC2626) else Color(0xFF64748B)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // 3. 3D TACTILE BLOOD GLUCOSE CARD
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .homeCardShadow(
                        cornerRadius = 24.dp,
                        elevation = 8.dp,
                        spotColor = Color(0x20384F6E),
                        ambientColor = Color(0x104A6282)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color(0xFFE2E8F0).copy(alpha = 0.70f)
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFFF8FAFC)),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Blood Glucose",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        )

                        // Status evaluation chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    color = statusColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Value Stepper Row (Minus / Display / Plus)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3D Circular Minus Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(
                                    elevation = 3.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0x20384F6E)
                                )
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        HapticFeedbackHelper.performClickHaptic(context)
                                        val cur = sugarInput.toIntOrNull() ?: 120
                                        val next = (cur - 1).coerceAtLeast(20)
                                        sugarInput = next.toString()
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Sugar",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Large Numeric Value Display & Direct Input
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BasicTextField(
                                value = sugarInput,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }.take(3)
                                    sugarInput = filtered
                                },
                                textStyle = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center,
                                    letterSpacing = (-0.5).sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                cursorBrush = SolidColor(Color(0xFFEF4444)),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (sugarInput.isEmpty()) {
                                            Text(
                                                text = "0",
                                                style = TextStyle(
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 36.sp,
                                                    color = Color(0xFFCBD5E1),
                                                    textAlign = TextAlign.Center,
                                                    letterSpacing = (-0.5).sp
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )

                            Text(
                                text = "mg/dL",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                        }

                        // 3D Circular Plus Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(
                                    elevation = 3.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0x20384F6E)
                                )
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        HapticFeedbackHelper.performClickHaptic(context)
                                        val cur = sugarInput.toIntOrNull() ?: 120
                                        val next = (cur + 1).coerceAtMost(600)
                                        sugarInput = next.toString()
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Sugar",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Presets Row: 80 to 250 by 20 difference, horizontally scrollable
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(80, 100, 120, 140, 160, 180, 200, 220, 240, 250)
                        presets.forEach { presetVal ->
                            val isPresetActive = (sugarValue == presetVal)
                            Box(
                                modifier = Modifier
                                    .shadow(
                                        elevation = if (isPresetActive) 3.dp else 1.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        spotColor = if (isPresetActive) Color(0x30EF4444) else Color(0x10384F6E)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isPresetActive) {
                                            Brush.linearGradient(listOf(Color.White, Color(0xFFFFF1F2)))
                                        } else {
                                            Brush.linearGradient(listOf(Color.White, Color(0xFFF8FAFC)))
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isPresetActive) Color(0xFFFECACA) else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            HapticFeedbackHelper.performClickHaptic(context)
                                            sugarInput = presetVal.toString()
                                        }
                                    )
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = presetVal.toString(),
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isPresetActive) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.5.sp,
                                        color = if (isPresetActive) Color(0xFFDC2626) else Color(0xFF475569)
                                    )
                                )
                            }
                        }
                    }

                    // Warning Alert Banner if clinical range boundary breached
                    AnimatedVisibility(
                        visible = sugarValue != null && !PlausibleRanges.isPlausibleSugar(sugarValue),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            ReadingAlertBanner(
                                message = "Unusual glucose reading. Please verify before saving.",
                                isCritical = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // 4. 3D TACTILE INSULIN DOSE CARD
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .homeCardShadow(
                        cornerRadius = 24.dp,
                        elevation = 8.dp,
                        spotColor = Color(0x20384F6E),
                        ambientColor = Color(0x104A6282)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color(0xFFE2E8F0).copy(alpha = 0.70f)
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFFF8FAFC)),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Insulin Dose",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        )

                        Text(
                            text = "Rapid / Basal Units",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stepper Row (Minus / Dose Display / Plus)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3D Circular Minus Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(
                                    elevation = 3.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0x20384F6E)
                                )
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        HapticFeedbackHelper.performClickHaptic(context)
                                        if (insulinUnits >= 0.5) {
                                            insulinUnits = Math.round((insulinUnits - 0.5) * 10.0) / 10.0
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Dose",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Large Units Display
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (insulinUnits > 0) String.format(Locale.US, "%.1f", insulinUnits) else "0",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Text(
                                text = "Units",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                        }

                        // 3D Circular Plus Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(
                                    elevation = 3.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0x20384F6E)
                                )
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        HapticFeedbackHelper.performClickHaptic(context)
                                        if (insulinUnits < 100) {
                                            insulinUnits = Math.round((insulinUnits + 0.5) * 10.0) / 10.0
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Dose",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Dose Presets: 0, 2, 4, 6, 10, 14 U
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val dosePresets = listOf(0.0, 2.0, 4.0, 6.0, 10.0, 14.0)
                        dosePresets.forEach { presetDose ->
                            val isPresetActive = (insulinUnits == presetDose)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .shadow(
                                        elevation = if (isPresetActive) 3.dp else 1.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        spotColor = if (isPresetActive) Color(0x300284C7) else Color(0x10384F6E)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isPresetActive) {
                                            Brush.linearGradient(listOf(Color.White, Color(0xFFF0F9FF)))
                                        } else {
                                            Brush.linearGradient(listOf(Color.White, Color(0xFFF8FAFC)))
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isPresetActive) Color(0xFFBAE6FD) else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            HapticFeedbackHelper.performClickHaptic(context)
                                            insulinUnits = presetDose
                                        }
                                    )
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (presetDose == 0.0) "0 U" else "${presetDose.toInt()} U",
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isPresetActive) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (isPresetActive) Color(0xFF0284C7) else Color(0xFF475569)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // 5. 3D TACTILE NOTE INPUT CARD
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .homeCardShadow(
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        spotColor = Color(0x18384F6E)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFFE2E8F0).copy(alpha = 0.65f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = {
                            Text(
                                text = "Note (e.g. snack, pre-walk, fasting)",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        maxLines = 2
                    )
                }
            }

            // -------------------------------------------------------------
            // 6. 3D DESTRUCTIVE DELETE ACTION (If Editing Existing Record)
            // -------------------------------------------------------------
            if (isEditMode && onDelete != null && currentSlotEntry != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .homeCardShadow(
                            cornerRadius = 20.dp,
                            elevation = 6.dp,
                            spotColor = Color(0x25EF4444),
                            ambientColor = Color(0x15EF4444)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White, Color(0xFFFECACA))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.White, Color(0xFFFFF1F2))
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onDelete(currentSlotEntry.id)
                            }
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Reading",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Delete this reading",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFDC2626)
                                )
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = "Permanently removes this slot entry",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444).copy(alpha = 0.80f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------------------------------------------
            // 7. PRIMARY 3D SAVE CTA BUTTON
            // -------------------------------------------------------------
            val buttonBrush = if (isFormValid) {
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .then(
                        if (isFormValid) {
                            Modifier.shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color(0x40EF4444)
                            )
                        } else Modifier
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(buttonBrush)
                    .clickable(
                        enabled = isFormValid,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (sugarValue != null && PlausibleRanges.isPlausibleSugar(sugarValue)) {
                                HapticFeedbackHelper.performSuccessHaptic(context)
                                onSave(
                                    selectedSlot,
                                    sugarValue,
                                    insulinUnits,
                                    existingEntry?.timestamp ?: System.currentTimeMillis(),
                                    note.ifBlank { null }
                                )
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Update Sugar Reading" else "Save Sugar Reading",
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
