package com.trackwell.app.ui.screens.weighthistory

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.PlausibleRanges
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.theme.InterFontFamily
import java.util.Locale
import kotlin.math.abs

/**
 * 3D Claymorphic "Log & Update Morning Weight" Bottom Sheet matching TrackWell's
 * Behance Claymorphic design system (calm medical-glass dashboard theme).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightEntryBottomSheet(
    selectedDateIso: String = DateTimeUtils.getTodayIso(),
    initialWeightKg: Double? = null,
    targetWeightKg: Double = 55.50,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit,
    onDelete: (() -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val isEditMode = initialWeightKg != null

    // Default starting weight if nothing recorded yet
    val startingWeight = initialWeightKg ?: 55.70
    var weightText by remember(initialWeightKg) {
        mutableStateOf(String.format(Locale.US, "%.2f", startingWeight))
    }

    val parsedWeight = weightText.toDoubleOrNull()
    val isValid = parsedWeight != null && PlausibleRanges.isPlausibleWeight(parsedWeight)

    val dateFormatted = remember(selectedDateIso) {
        try {
            val parsed = DateTimeUtils.parseIsoDate(selectedDateIso)
            if (parsed != null) {
                val isToday = DateTimeUtils.isToday(selectedDateIso)
                val isYesterday = selectedDateIso == DateTimeUtils.getDaysAgoIso(1)
                val prefix = if (isToday) "Today · " else if (isYesterday) "Yesterday · " else ""
                prefix + java.text.SimpleDateFormat("EEE, d MMM", Locale.US).format(parsed)
            } else selectedDateIso
        } catch (e: Exception) {
            selectedDateIso
        }
    }

    // Target comparison calculations
    val deltaFromTarget = if (parsedWeight != null) parsedWeight - targetWeightKg else 0.0
    val isNearTarget = abs(deltaFromTarget) <= 0.30
    val isOverTarget = deltaFromTarget > 0.30

    val (badgeBg, badgeBorder, badgeText, badgeColor) = when {
        parsedWeight == null -> Quad(Color(0xFFF1F5F9), Color(0xFFE2E8F0), "Target: ${String.format(Locale.US, "%.1f", targetWeightKg)} kg", Color(0xFF64748B))
        isNearTarget -> Quad(Color(0xFFDCFCE7), Color(0xFFBBF7D0), "Target Optimal (${String.format(Locale.US, "%.1f", targetWeightKg)} kg)", Color(0xFF16A34A))
        isOverTarget -> Quad(Color(0xFFFEF3C7), Color(0xFFFDE68A), "+${String.format(Locale.US, "%.2f", deltaFromTarget)} kg vs target", Color(0xFFD97706))
        else -> Quad(Color(0xFFEFF6FF), Color(0xFFBFDBFE), "${String.format(Locale.US, "%.2f", deltaFromTarget)} kg vs target", Color(0xFF2563EB))
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
            // 1. HEADER ROW: 3D Scale Icon + Title + 3D Close Button
            // -------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .homeCardShadow(
                                cornerRadius = 16.dp,
                                elevation = 6.dp,
                                spotColor = Color(0x302563EB),
                                ambientColor = Color(0x182563EB)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFEFF6FF))
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFBFDBFE))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_scale),
                            contentDescription = "Weight Scale",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isEditMode) "Update Morning Weight" else "Log Morning Weight",
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
                            text = "$dateFormatted · Dry Weight",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }

                // 3D Elevated Circular Close Button
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
            // 2. 3D CLAYMORPHIC WEIGHT STEPPER CARD
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
                            colors = listOf(Color.White, Color(0xFFE2E8F0)),
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
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Morning Dry Weight",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        )

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeBg)
                                .border(1.dp, badgeBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    color = badgeColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stepper: Minus, Editable Display, Plus
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
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
                                        val cur = weightText.toDoubleOrNull() ?: startingWeight
                                        val next = (cur - 0.10).coerceAtLeast(30.0)
                                        weightText = String.format(Locale.US, "%.2f", next)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Weight",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Central Large Display & Text Field
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BasicTextField(
                                value = weightText,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() || it == '.' }
                                    if (filtered.count { it == '.' } <= 1 && filtered.length <= 6) {
                                        weightText = filtered
                                    }
                                },
                                textStyle = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center,
                                    letterSpacing = (-0.5).sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                cursorBrush = SolidColor(Color(0xFF2563EB)),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (weightText.isEmpty()) {
                                            Text(
                                                text = "0.00",
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

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Kilograms (kg)",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
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
                                        val cur = weightText.toDoubleOrNull() ?: startingWeight
                                        val next = (cur + 0.10).coerceAtMost(250.0)
                                        weightText = String.format(Locale.US, "%.2f", next)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Weight",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quick Offset Chips
                    Text(
                        text = "Quick Adjust",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val offsets = listOf(
                            "-0.5" to -0.5,
                            "-0.2" to -0.2,
                            "-0.1" to -0.1,
                            "Target (${String.format(Locale.US, "%.1f", targetWeightKg)})" to 0.0,
                            "+0.1" to 0.1,
                            "+0.2" to 0.2,
                            "+0.5" to 0.5
                        )

                        offsets.forEach { (label, delta) ->
                            val isTargetChip = delta == 0.0
                            val chipBg = if (isTargetChip) Color(0xFFEFF6FF) else Color.White
                            val chipBorder = if (isTargetChip) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)
                            val chipTextCol = if (isTargetChip) Color(0xFF2563EB) else Color(0xFF475569)

                            Box(
                                modifier = Modifier
                                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x15000000))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(chipBg)
                                    .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            HapticFeedbackHelper.performTickHaptic(context)
                                            if (isTargetChip) {
                                                weightText = String.format(Locale.US, "%.2f", targetWeightKg)
                                            } else {
                                                val cur = weightText.toDoubleOrNull() ?: startingWeight
                                                val next = (cur + delta).coerceIn(30.0, 250.0)
                                                weightText = String.format(Locale.US, "%.2f", next)
                                            }
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = chipTextCol
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // 3. CLINICAL ADVISORY NOTE CARD
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEFF6FF))
                    .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Clinical Protocol: Weigh each morning immediately after first urination and before breakfast. A rapid gain of >1.0 kg over 48h may signal fluid retention.",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 17.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------------------------------------------
            // 4. PRIMARY SAVE / UPDATE BUTTON
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .homeCardShadow(
                        cornerRadius = 22.dp,
                        elevation = if (isValid) 8.dp else 0.dp,
                        spotColor = Color(0x352563EB),
                        ambientColor = Color(0x202563EB)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (isValid) {
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
                            )
                        }
                    )
                    .clickable(
                        enabled = isValid,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (parsedWeight != null && isValid) {
                                HapticFeedbackHelper.performSuccessHaptic(context)
                                onSave(parsedWeight)
                            }
                        }
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (parsedWeight != null && isValid) {
                            if (isEditMode) "Update Weight · ${String.format(Locale.US, "%.2f", parsedWeight)} kg"
                            else "Save Weight · ${String.format(Locale.US, "%.2f", parsedWeight)} kg"
                        } else {
                            "Enter Valid Weight (30-250 kg)"
                        },
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp,
                            color = Color.White
                        )
                    )
                }
            }

            // -------------------------------------------------------------
            // 5. DESTRUCTIVE CLEAR / DELETE ACTION (If editing an existing day)
            // -------------------------------------------------------------
            if (isEditMode && onDelete != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFF1F2))
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(18.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onDelete()
                            }
                        )
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Remove Morning Weight Log",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp,
                                color = Color(0xFFEF4444)
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
