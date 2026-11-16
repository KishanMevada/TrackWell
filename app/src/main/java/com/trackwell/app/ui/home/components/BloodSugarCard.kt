package com.trackwell.app.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.ui.common.HapticFeedbackHelper

data class BloodSugarPoint(
    val timeLabel: String,
    val valueMgDl: Int? = null,
    val slot: SugarSlot? = null
)

/**
 * Reusable full-width Blood Sugar Card supporting:
 * 1. Logged / Partial State: Full primary reading, status pill (In Range / High / Low), target zone,
 *    and an interactive 4-slot daily chart (8 AM, 12 PM, 4 PM, 8 PM). Unlogged slots display
 *    tactile pending target nodes with glowing halos and an Add button action on tap.
 * 2. Unlogged State: "+ Log [Slot] Glucose" tactile hero button, target range context, and 4 daily slots.
 */
@Composable
fun BloodSugarCard(
    isLoggedToday: Boolean,
    currentValueMgDl: Int?,
    status: String?,
    todayPoints: List<BloodSugarPoint>,
    priorPoints: List<BloodSugarPoint>,
    priorAvgMgDl: Int?,
    suggestedSlot: SugarSlot = SugarSlot.suggestCurrentSlot(),
    onViewHistory: () -> Unit,
    onLogClick: () -> Unit,
    onSlotClick: ((SugarSlot) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val context = LocalContext.current

    val slotShortName = when (suggestedSlot) {
        SugarSlot.BEFORE_BREAKFAST -> "Fasting"
        SugarSlot.BEFORE_LUNCH -> "Pre-Lunch"
        SugarSlot.AFTER_LUNCH -> "Post-Lunch"
        SugarSlot.BEFORE_DINNER -> "Pre-Dinner"
    }

    val standardSlots = listOf(
        SugarSlot.BEFORE_BREAKFAST to "8 AM",
        SugarSlot.BEFORE_LUNCH to "12 PM",
        SugarSlot.AFTER_LUNCH to "4 PM",
        SugarSlot.BEFORE_DINNER to "8 PM"
    )

    // Ensure all 4 slots are consistently present across all states
    val fourSlotPoints = remember(todayPoints) {
        standardSlots.map { (slot, defaultLabel) ->
            val existing = todayPoints.firstOrNull { it.slot == slot }
                ?: todayPoints.firstOrNull { it.timeLabel.contains(defaultLabel, ignoreCase = true) }
            existing?.copy(slot = slot) ?: BloodSugarPoint(timeLabel = defaultLabel, valueMgDl = null, slot = slot)
        }
    }

    val loggedCount = fourSlotPoints.count { it.valueMgDl != null && it.valueMgDl > 0 }
    val isAnyLogged = isLoggedToday || loggedCount > 0

    val isHigh = status?.equals("High", ignoreCase = true) == true
    val isLow = status?.equals("Low", ignoreCase = true) == true
    val pillBg = when {
        !isAnyLogged && priorPoints.isEmpty() -> Color(0xFFEFF6FF)
        !isAnyLogged -> Color(0xFFFEF3C7)
        isHigh -> Color(0xFFFEE2E2)
        isLow -> Color(0xFFFEF3C7)
        else -> Color(0xFFDCFCE7)
    }
    val pillTextColor = when {
        !isAnyLogged && priorPoints.isEmpty() -> Color(0xFF2563EB)
        !isAnyLogged -> Color(0xFFD97706)
        isHigh -> Color(0xFFEF4444)
        isLow -> Color(0xFFD97706)
        else -> Color(0xFF16A34A)
    }
    val headerStatusText = when {
        !isAnyLogged && priorPoints.isEmpty() -> "Day 1"
        !isAnyLogged -> "Pending $slotShortName"
        else -> status ?: "In Range"
    }

    Surface(
        onClick = onViewHistory,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .homeCardShadow(cornerRadius = 28.dp, elevation = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFFE2E8F0).copy(alpha = 0.60f)
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .semantics {
                contentDescription = if (isAnyLogged && currentValueMgDl != null) {
                    "Blood Sugar Card: Current reading is $currentValueMgDl milligrams per deciliter, status is $status. Tap to view blood sugar history."
                } else {
                    "Blood Sugar Card: Morning glucose reading is pending. Tap to log or view history."
                }
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFFDF4F4)
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // 1. Top Header Row: Blood Drop Icon + Title + Status Pill + Right Chevron
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_blood_drop),
                                contentDescription = "Blood Drop Icon",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Blood Sugar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.3).sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status Pill (e.g. "Day 1", "Pending Fasting", or "In Range")
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 3.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = pillTextColor.copy(alpha = 0.20f),
                                    ambientColor = pillTextColor.copy(alpha = 0.10f)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .background(pillBg)
                                .padding(horizontal = 13.dp, vertical = 5.5.dp)
                        ) {
                            Text(
                                text = headerStatusText,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = pillTextColor
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Right navigation chevron
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = "View Blood Sugar History",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(width = 8.dp, height = 14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Middle Body: Logged vs Unlogged States
                if (isAnyLogged && (currentValueMgDl != null && currentValueMgDl > 0 || loggedCount > 0)) {
                    // ==========================================
                    // STATE A: AT LEAST ONE READING LOGGED TODAY
                    // ==========================================
                    val displayValue = currentValueMgDl ?: fourSlotPoints.lastOrNull { it.valueMgDl != null && it.valueMgDl > 0 }?.valueMgDl ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = displayValue.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "mg/dL",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Today",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B)
                            )
                        }

                        Text(
                            text = "Target: 70–140 mg/dL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // All 4 dots chart with interactive touch columns
                    FourSlotBloodSugarChart(
                        points = fourSlotPoints,
                        textMeasurer = textMeasurer,
                        onSlotClick = { slot ->
                            (onSlotClick ?: { onLogClick() })(slot)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                    )
                } else {
                    // ==========================================
                    // STATE B: UNLOGGED TODAY (0 of 4 slots logged)
                    // ==========================================
                    Surface(
                        onClick = onLogClick,
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFEE2E2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Glucose Reading",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Log $slotShortName Glucose",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val subtitleText = if (priorAvgMgDl != null && priorPoints.isNotEmpty()) {
                        "Past ${priorPoints.size} days avg: $priorAvgMgDl mg/dL • 4 daily slots"
                    } else {
                        "Clinical target: 70–140 mg/dL • 4 daily slots (Fasting, Meals, Bedtime)"
                    }

                    Text(
                        text = subtitleText,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // All 4 dots chart with interactive touch columns
                    FourSlotBloodSugarChart(
                        points = fourSlotPoints,
                        textMeasurer = textMeasurer,
                        onSlotClick = { slot ->
                            (onSlotClick ?: { onLogClick() })(slot)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                    )
                }
            }
        }
    }
}

/**
 * Convenience overload preserving backward compatibility.
 */
@Composable
fun BloodSugarCard(
    currentValueMgDl: Int,
    status: String,
    points: List<BloodSugarPoint>,
    onViewHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    BloodSugarCard(
        isLoggedToday = currentValueMgDl > 0,
        currentValueMgDl = currentValueMgDl,
        status = status,
        todayPoints = points,
        priorPoints = emptyList(),
        priorAvgMgDl = null,
        onViewHistory = onViewHistory,
        onLogClick = onViewHistory,
        onSlotClick = null,
        modifier = modifier
    )
}

/**
 * Comprehensive 4-Slot Blood Sugar Chart:
 * Always plots all 4 standard slots across the day (8 AM, 12 PM, 4 PM, 8 PM).
 * - Logged slots: Solid red connection line, crisp node, and measured mg/dL value.
 * - Unlogged slots: Soft dashed guideline leading into hollow pending target nodes
 *   with glowing halos, plus-icon styling, and "+ Add" / "(Pending)" indicators.
 * - Interactive: Tapping any slot column (especially empty dots) invokes onSlotClick for that slot!
 */
@Composable
private fun FourSlotBloodSugarChart(
    points: List<BloodSugarPoint>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    onSlotClick: (SugarSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val firstUnloggedIndex = points.indexOfFirst { it.valueMgDl == null || it.valueMgDl <= 0 }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val horizontalPadding = 24.dp.toPx()
            val topPadding = 24.dp.toPx()
            val bottomPadding = 30.dp.toPx()
            val usableW = w - horizontalPadding * 2
            val usableH = h - topPadding - bottomPadding

            val loggedValues = points.mapNotNull { it.valueMgDl }.filter { it > 0 }
            val minVal = minOf(loggedValues.minOrNull() ?: 70, 70)
            val maxVal = maxOf(loggedValues.maxOrNull() ?: 140, 180)
            val scaleRange = (maxVal - minVal).coerceAtLeast(40).toFloat()

            fun mapY(value: Float): Float {
                val norm = 1f - ((value - minVal) / scaleRange).coerceIn(0f, 1f)
                return topPadding + norm * usableH
            }

            // Pale green target range band: 70 - 140 mg/dL
            val targetBandTop = mapY(140f)
            val targetBandBottom = mapY(70f)
            val targetBandHeight = (targetBandBottom - targetBandTop).coerceAtLeast(20.dp.toPx())
            val targetCenterY = (targetBandTop + targetBandBottom) / 2f

            drawRoundRect(
                color = Color(0xFFDCFCE7).copy(alpha = 0.70f),
                topLeft = Offset(x = horizontalPadding - 8.dp.toPx(), y = targetBandTop),
                size = Size(width = usableW + 16.dp.toPx(), height = targetBandHeight),
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )

            // Step between the 4 slots
            val n = points.size.coerceAtLeast(2)
            val stepX = usableW / (n - 1)

            // Calculate coordinates for all 4 slots
            val coords = points.mapIndexed { idx, pt ->
                val x = horizontalPadding + idx * stepX
                val y = if (pt.valueMgDl != null && pt.valueMgDl > 0) {
                    mapY(pt.valueMgDl.toFloat())
                } else {
                    targetCenterY
                }
                Offset(x, y)
            }

            // Draw line segments between consecutive slots
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)

            for (i in 0 until n - 1) {
                val pt1 = points[i]
                val pt2 = points[i + 1]
                val isPt1Logged = pt1.valueMgDl != null && pt1.valueMgDl > 0
                val isPt2Logged = pt2.valueMgDl != null && pt2.valueMgDl > 0

                if (isPt1Logged && isPt2Logged) {
                    // Solid red line between logged points
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = coords[i],
                        end = coords[i + 1],
                        strokeWidth = 2.4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                } else {
                    // Dashed continuation line leading to or between unlogged points
                    val dashColor = if (isPt1Logged) Color(0xFFF87171) else Color(0xFFFCA5A5).copy(alpha = 0.65f)
                    val dashWidth = if (isPt1Logged) 2.dp.toPx() else 1.6.dp.toPx()
                    drawLine(
                        color = dashColor,
                        start = coords[i],
                        end = coords[i + 1],
                        strokeWidth = dashWidth,
                        pathEffect = dashEffect
                    )
                }
            }

            // Draw each of the 4 nodes and labels
            coords.forEachIndexed { idx, pt ->
                val point = points[idx]
                val isLogged = point.valueMgDl != null && point.valueMgDl > 0
                val isNextPending = (idx == firstUnloggedIndex)

                if (isLogged) {
                    // ==========================================
                    // LOGGED NODE: White circle + Red outline + Value above + Time below
                    // ==========================================
                    drawCircle(color = Color.White, radius = 4.5.dp.toPx(), center = pt)
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 4.5.dp.toPx(),
                        center = pt,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Value label above dot
                    val valStr = point.valueMgDl.toString()
                    val valLayout = textMeasurer.measure(
                        text = valStr,
                        style = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    )
                    drawText(
                        textLayoutResult = valLayout,
                        topLeft = Offset(pt.x - valLayout.size.width / 2f, pt.y - valLayout.size.height - 4.dp.toPx())
                    )

                    // Time label below dot
                    val timeStr = point.timeLabel
                    val timeLayout = textMeasurer.measure(
                        text = timeStr,
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, color = Color(0xFF94A3B8))
                    )
                    drawText(
                        textLayoutResult = timeLayout,
                        topLeft = Offset(pt.x - timeLayout.size.width / 2f, h - bottomPadding + 6.dp.toPx())
                    )
                } else {
                    // ==========================================
                    // UNLOGGED / EMPTY NODE (Matching WeightCard Pending Node + Add Action)
                    // ==========================================
                    if (isNextPending) {
                        // Glowing halo ring around the next pending node
                        drawCircle(
                            color = Color(0xFFEF4444).copy(alpha = 0.22f),
                            radius = 13.dp.toPx(),
                            center = pt
                        )
                        // Outer colored ring
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 7.dp.toPx(),
                            center = pt,
                            style = Stroke(width = 2.2.dp.toPx())
                        )
                        // Inner white circle
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        // Mini Add '+' sign inside the node
                        val plusArm = 2.4.dp.toPx()
                        drawLine(
                            color = Color(0xFFEF4444),
                            start = Offset(pt.x - plusArm, pt.y),
                            end = Offset(pt.x + plusArm, pt.y),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color(0xFFEF4444),
                            start = Offset(pt.x, pt.y - plusArm),
                            end = Offset(pt.x, pt.y + plusArm),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Time label below dot
                        val timeStr = point.timeLabel
                        val timeLayout = textMeasurer.measure(
                            text = timeStr,
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        )
                        drawText(
                            textLayoutResult = timeLayout,
                            topLeft = Offset(pt.x - timeLayout.size.width / 2f, h - bottomPadding + 4.dp.toPx())
                        )

                        // "(Pending)" subtitle below time
                        val pendingLayout = textMeasurer.measure(
                            text = "(Pending)",
                            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Normal, color = Color(0xFF64748B))
                        )
                        drawText(
                            textLayoutResult = pendingLayout,
                            topLeft = Offset(pt.x - pendingLayout.size.width / 2f, h - bottomPadding + 4.dp.toPx() + timeLayout.size.height - 1.dp.toPx())
                        )
                    } else {
                        // Future upcoming unlogged slot: subtle hollow node with '+'
                        drawCircle(
                            color = Color(0xFFFCA5A5).copy(alpha = 0.70f),
                            radius = 6.dp.toPx(),
                            center = pt,
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4.2.dp.toPx(),
                            center = pt
                        )
                        // Faint plus arm
                        val plusArm = 2.dp.toPx()
                        drawLine(
                            color = Color(0xFFFCA5A5),
                            start = Offset(pt.x - plusArm, pt.y),
                            end = Offset(pt.x + plusArm, pt.y),
                            strokeWidth = 1.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color(0xFFFCA5A5),
                            start = Offset(pt.x, pt.y - plusArm),
                            end = Offset(pt.x, pt.y + plusArm),
                            strokeWidth = 1.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Time label below dot
                        val timeStr = point.timeLabel
                        val timeLayout = textMeasurer.measure(
                            text = timeStr,
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, color = Color(0xFF94A3B8))
                        )
                        drawText(
                            textLayoutResult = timeLayout,
                            topLeft = Offset(pt.x - timeLayout.size.width / 2f, h - bottomPadding + 6.dp.toPx())
                        )
                    }
                }
            }
        }

        // ==========================================
        // Interactive Touch Targets Overlay: 4 Touch Columns
        // Allows tapping any empty (or logged) dot to log/edit that slot
        // ==========================================
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            points.forEach { pt ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                pt.slot?.let { slot ->
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    onSlotClick(slot)
                                }
                            }
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F4F9)
@Composable
fun BloodSugarCardPreview() {
    val samplePoints = listOf(
        BloodSugarPoint("8 AM", 81, SugarSlot.BEFORE_BREAKFAST),
        BloodSugarPoint("12 PM", 169, SugarSlot.BEFORE_LUNCH),
        BloodSugarPoint("4 PM", null, SugarSlot.AFTER_LUNCH),
        BloodSugarPoint("8 PM", null, SugarSlot.BEFORE_DINNER)
    )

    BloodSugarCard(
        isLoggedToday = true,
        currentValueMgDl = 169,
        status = "High",
        todayPoints = samplePoints,
        priorPoints = emptyList(),
        priorAvgMgDl = null,
        onViewHistory = {},
        onLogClick = {}
    )
}
