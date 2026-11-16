package com.trackwell.app.ui.home.components

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import java.util.Locale
import kotlin.math.abs

data class WeightPoint(
    val label: String,
    val weightKg: Float
)

/**
 * Reusable full-width Morning Dry Weight Card supporting:
 * 1. Logged State: Full weight metric, delta pill, and 7-day spline curve.
 * 2. Unlogged State (Concept 1: Tactile Action Hero):
 *    - Condition 1 (First-Time User / Zero Data): "+ Set Initial Dry Weight" CTA,
 *      target baseline guideline, and glowing Day 1 Starting Baseline node.
 *    - Condition 2 (Partial Data / < 6 Days): "+ Log Today's Weight" CTA,
 *      dynamic "Past N days" badge, yesterday subtitle, and Catmull-Rom curve
 *      continuing via dotted guideline to "Today (Pending)" target node.
 */
@Composable
fun WeightCard(
    isLoggedToday: Boolean,
    currentWeightKg: Float?,
    weeklyChangeKg: Float?,
    points: List<WeightPoint>,
    priorPoints: List<WeightPoint>,
    yesterdayWeightKg: Float?,
    yesterdayWeightDiffKg: Float?,
    onViewHistory: () -> Unit,
    onLogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

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
                contentDescription = if (isLoggedToday && currentWeightKg != null) {
                    "Weight Card: Current weight is ${
                        String.format(Locale.US, "%.2f", currentWeightKg)
                    } kilograms. Tap to view weight history."
                } else {
                    "Weight Card: Morning weight is not logged yet. Tap to log weight or view history."
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
                            Color(0xFFF0F6FE)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // 1. Top Header Row: Scale Icon + "Weight" Title + Right Chevron
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
                                .background(Color(0xFFE8F1FC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_scale),
                                contentDescription = "Weight Scale Icon",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Weight",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.3).sp
                        )
                    }

                    // Right navigation chevron
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = "View Weight History",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(width = 8.dp, height = 14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Middle Body: Logged vs Unlogged States
                if (isLoggedToday && currentWeightKg != null) {
                    // ==========================================
                    // STATE A: TODAY IS LOGGED
                    // ==========================================
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
                                    text = String.format(Locale.US, "%.2f", currentWeightKg),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "kg",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            val trendLabel = when {
                                points.size >= 6 -> "7-day trend"
                                points.size > 1 -> "Past ${points.size} days"
                                else -> "Current weight"
                            }
                            Text(
                                text = trendLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Outlined Blue Delta Pill (e.g. "↓ 0.15 kg")
                        val change = weeklyChangeKg ?: 0f
                        val isLoss = change < 0
                        val arrow = if (isLoss) "↓" else if (change > 0) "↑" else ""
                        val deltaText = if (arrow.isNotEmpty()) {
                            "$arrow ${String.format(Locale.US, "%.2f", abs(change))} kg"
                        } else {
                            "0.00 kg"
                        }

                        Box(
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFEFF6FF))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF93C5FD),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = deltaText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logged 7-day spline curve
                    if (points.isNotEmpty()) {
                        LoggedWeightChart(
                            points = points,
                            textMeasurer = textMeasurer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp)
                        )
                    }
                } else if (priorPoints.isNotEmpty()) {
                    // ==========================================
                    // STATE B: UNLOGGED TODAY — PARTIAL DATA (< 6 DAYS / PAST N DAYS)
                    // ==========================================
                    // Tactile Action Hero Button
                    Surface(
                        onClick = onLogClick,
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
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
                                contentDescription = "Add Weight",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Log Today's Weight",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle with Yesterday Context
                    val diffString = if (yesterdayWeightDiffKg != null && yesterdayWeightDiffKg != 0f) {
                        val arrow = if (yesterdayWeightDiffKg < 0f) "↓" else "↑"
                        " ($arrow ${String.format(Locale.US, "%.2f", abs(yesterdayWeightDiffKg))} kg)"
                    } else ""

                    val yesterdayText = if (yesterdayWeightKg != null) {
                        "Yesterday: ${String.format(Locale.US, "%.2f", yesterdayWeightKg)} kg$diffString • Morning weigh-in pending"
                    } else {
                        "Morning weigh-in pending for today"
                    }

                    Text(
                        text = yesterdayText,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic History Badge + Chart
                    val dynamicBadgeText = when {
                        priorPoints.size == 1 -> "Yesterday's reading"
                        priorPoints.size in 2..5 -> "Past ${priorPoints.size} days"
                        else -> "Past 6 days"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = dynamicBadgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Partial Data Chart with Dotted Continuation to Today (Pending) Node
                    PartialWeightChart(
                        priorPoints = priorPoints,
                        textMeasurer = textMeasurer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                } else {
                    // ==========================================
                    // STATE C: UNLOGGED TODAY — FIRST-TIME USER (ZERO DATA)
                    // ==========================================
                    // Tactile Action Hero Button: Initial Dry Weight
                    Surface(
                        onClick = onLogClick,
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
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
                                contentDescription = "Add Weight Baseline",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Set Initial Dry Weight",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Establish your post-transplant dry weight baseline",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // First-Time Zero Data Canvas with Baseline Guide & Day 1 Target Node
                    FirstTimeWeightChart(
                        textMeasurer = textMeasurer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "7-day trend curve will build here as you log daily",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
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
fun WeightCard(
    currentWeightKg: Float,
    weeklyChangeKg: Float,
    points: List<WeightPoint>,
    onViewHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    WeightCard(
        isLoggedToday = currentWeightKg > 0f,
        currentWeightKg = currentWeightKg,
        weeklyChangeKg = weeklyChangeKg,
        points = points,
        priorPoints = points.dropLast(1),
        yesterdayWeightKg = points.dropLast(1).lastOrNull()?.weightKg,
        yesterdayWeightDiffKg = weeklyChangeKg,
        onViewHistory = onViewHistory,
        onLogClick = onViewHistory,
        modifier = modifier
    )
}

/**
 * Chart for Logged State: Full smooth Catmull-Rom spline curve with gradient fill and dotted drops.
 */
@Composable
private fun LoggedWeightChart(
    points: List<WeightPoint>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val horizontalPadding = 14.dp.toPx()
        val topPadding = 22.dp.toPx()
        val bottomPadding = 10.dp.toPx()
        val usableW = w - horizontalPadding * 2
        val usableH = h - topPadding - bottomPadding

        val minW = points.minOf { it.weightKg }
        val maxW = points.maxOf { it.weightKg }
        val rangeSpan = (maxW - minW).coerceAtLeast(0.2f)
        val chartMin = minW - rangeSpan * 0.25f
        val chartMax = maxW + rangeSpan * 0.25f
        val chartScale = chartMax - chartMin

        val n = points.size
        val stepX = if (n > 1) usableW / (n - 1) else 0f

        val coords = points.mapIndexed { idx, pt ->
            val x = horizontalPadding + idx * stepX
            val normalizedY = 1f - ((pt.weightKg - chartMin) / chartScale).coerceIn(0f, 1f)
            val y = topPadding + normalizedY * usableH
            Offset(x, y)
        }

        val smoothPath = Path().apply {
            if (coords.isNotEmpty()) {
                moveTo(coords.first().x, coords.first().y)
                if (coords.size == 2) {
                    lineTo(coords[1].x, coords[1].y)
                } else if (coords.size > 2) {
                    for (i in 0 until coords.size - 1) {
                        val p0 = if (i > 0) coords[i - 1] else coords[i]
                        val p1 = coords[i]
                        val p2 = coords[i + 1]
                        val p3 = if (i + 2 < coords.size) coords[i + 2] else coords[i + 1]

                        val cp1x = p1.x + (p2.x - p0.x) / 5.5f
                        val cp1y = p1.y + (p2.y - p0.y) / 5.5f
                        val cp2x = p2.x - (p3.x - p1.x) / 5.5f
                        val cp2y = p2.y - (p3.y - p1.y) / 5.5f

                        cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
                    }
                }
            }
        }

        // Dotted vertical grid lines
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()), 0f)
        coords.forEach { pt ->
            drawLine(
                color = Color(0xFFCBD5E1).copy(alpha = 0.70f),
                start = Offset(pt.x, pt.y + 5.dp.toPx()),
                end = Offset(pt.x, h - 2.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )
        }

        // Soft gradient fill under line
        val fillPath = Path().apply {
            addPath(smoothPath)
            lineTo(coords.last().x, h)
            lineTo(coords.first().x, h)
            close()
        }
        val minY = coords.minOf { it.y }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF3B82F6).copy(alpha = 0.38f),
                    Color(0xFF60A5FA).copy(alpha = 0.18f),
                    Color(0xFF93C5FD).copy(alpha = 0.04f),
                    Color.Transparent
                ),
                startY = minY - 2.dp.toPx(),
                endY = h
            )
        )

        // Smooth blue trend curve
        drawPath(
            path = smoothPath,
            color = Color(0xFF2563EB),
            style = Stroke(
                width = 2.4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Data points & numeric values
        coords.forEachIndexed { idx, pt ->
            drawCircle(
                color = Color.White,
                radius = 4.2.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = 4.2.dp.toPx(),
                center = pt,
                style = Stroke(width = 1.8.dp.toPx())
            )

            val valueStr = String.format(Locale.US, "%.2f", points[idx].weightKg)
            val textLayout = textMeasurer.measure(
                text = valueStr,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B)
                )
            )
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    x = pt.x - (textLayout.size.width / 2f),
                    y = pt.y - textLayout.size.height - 4.dp.toPx()
                )
            )
        }
    }
}

/**
 * Chart for Condition 2 (Partial Data / Past N Days):
 * Plots historical recorded days on left, smooth curve & fill, then dotted line to Today (Pending) node.
 */
@Composable
private fun PartialWeightChart(
    priorPoints: List<WeightPoint>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val horizontalPadding = 18.dp.toPx()
        val topPadding = 24.dp.toPx()
        val bottomPadding = 26.dp.toPx()
        val usableW = w - horizontalPadding * 2
        val usableH = h - topPadding - bottomPadding

        val minW = priorPoints.minOf { it.weightKg }
        val maxW = priorPoints.maxOf { it.weightKg }
        val rangeSpan = (maxW - minW).coerceAtLeast(0.2f)
        val chartMin = minW - rangeSpan * 0.30f
        val chartMax = maxW + rangeSpan * 0.30f
        val chartScale = chartMax - chartMin

        // Total columns = N prior points + 1 Today (Pending) node
        val totalSlots = priorPoints.size
        val stepX = usableW / totalSlots

        // Calculate coords for prior points
        val coords = priorPoints.mapIndexed { idx, pt ->
            val x = horizontalPadding + idx * stepX
            val normalizedY = 1f - ((pt.weightKg - chartMin) / chartScale).coerceIn(0f, 1f)
            val y = topPadding + normalizedY * usableH
            Offset(x, y)
        }

        // Today node position on far right
        val todayX = horizontalPadding + totalSlots * stepX
        val todayY = coords.last().y

        // 1. Dotted vertical drop lines under prior points
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()), 0f)
        coords.forEach { pt ->
            drawLine(
                color = Color(0xFFCBD5E1).copy(alpha = 0.60f),
                start = Offset(pt.x, pt.y + 4.dp.toPx()),
                end = Offset(pt.x, h - bottomPadding + 2.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )
        }

        // 2. Smooth Catmull-Rom curve through prior points
        val smoothPath = Path().apply {
            if (coords.isNotEmpty()) {
                moveTo(coords.first().x, coords.first().y)
                if (coords.size == 2) {
                    lineTo(coords[1].x, coords[1].y)
                } else if (coords.size > 2) {
                    for (i in 0 until coords.size - 1) {
                        val p0 = if (i > 0) coords[i - 1] else coords[i]
                        val p1 = coords[i]
                        val p2 = coords[i + 1]
                        val p3 = if (i + 2 < coords.size) coords[i + 2] else coords[i + 1]

                        val cp1x = p1.x + (p2.x - p0.x) / 5.5f
                        val cp1y = p1.y + (p2.y - p0.y) / 5.5f
                        val cp2x = p2.x - (p3.x - p1.x) / 5.5f
                        val cp2y = p2.y - (p3.y - p1.y) / 5.5f

                        cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
                    }
                }
            }
        }

        // Gradient fill under prior curve
        val fillPath = Path().apply {
            addPath(smoothPath)
            lineTo(coords.last().x, h - bottomPadding + 2.dp.toPx())
            lineTo(coords.first().x, h - bottomPadding + 2.dp.toPx())
            close()
        }
        val minY = coords.minOf { it.y }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF3B82F6).copy(alpha = 0.35f),
                    Color(0xFF60A5FA).copy(alpha = 0.16f),
                    Color(0xFF93C5FD).copy(alpha = 0.04f),
                    Color.Transparent
                ),
                startY = minY - 2.dp.toPx(),
                endY = h - bottomPadding + 2.dp.toPx()
            )
        )

        // Draw solid blue curve
        drawPath(
            path = smoothPath,
            color = Color(0xFF2563EB),
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 3. Dotted continuation guideline to Today (Pending) node
        val continuationDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
        drawLine(
            color = Color(0xFF60A5FA),
            start = coords.last(),
            end = Offset(todayX, todayY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = continuationDash
        )

        // 4. Draw circles and labels for prior points
        coords.forEachIndexed { idx, pt ->
            drawCircle(
                color = Color.White,
                radius = 4.2.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = 4.2.dp.toPx(),
                center = pt,
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Value above point
            val valStr = String.format(Locale.US, "%.2f", priorPoints[idx].weightKg)
            val valLayout = textMeasurer.measure(
                text = valStr,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            )
            drawText(
                textLayoutResult = valLayout,
                topLeft = Offset(pt.x - valLayout.size.width / 2f, pt.y - valLayout.size.height - 4.dp.toPx())
            )

            // Day label below point
            val dayStr = priorPoints[idx].label
            val dayLayout = textMeasurer.measure(
                text = dayStr,
                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, color = Color(0xFF64748B))
            )
            drawText(
                textLayoutResult = dayLayout,
                topLeft = Offset(pt.x - dayLayout.size.width / 2f, h - bottomPadding + 6.dp.toPx())
            )
        }

        // 5. Today (Pending) Target Node
        // Glowing halo
        drawCircle(
            color = Color(0xFF3B82F6).copy(alpha = 0.22f),
            radius = 12.dp.toPx(),
            center = Offset(todayX, todayY)
        )
        // Outer ring
        drawCircle(
            color = Color(0xFF2563EB),
            radius = 6.5.dp.toPx(),
            center = Offset(todayX, todayY),
            style = Stroke(width = 2.2.dp.toPx())
        )
        // White center
        drawCircle(
            color = Color.White,
            radius = 4.5.dp.toPx(),
            center = Offset(todayX, todayY)
        )

        // Two-line "Today / (Pending)" label
        val todayLayout = textMeasurer.measure(
            text = "Today",
            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
        )
        drawText(
            textLayoutResult = todayLayout,
            topLeft = Offset(todayX - todayLayout.size.width / 2f, h - bottomPadding + 4.dp.toPx())
        )
        val pendingLayout = textMeasurer.measure(
            text = "(Pending)",
            style = TextStyle(fontSize = 9.5.sp, fontWeight = FontWeight.Normal, color = Color(0xFF64748B))
        )
        drawText(
            textLayoutResult = pendingLayout,
            topLeft = Offset(todayX - pendingLayout.size.width / 2f, h - bottomPadding + 4.dp.toPx() + todayLayout.size.height - 1.dp.toPx())
        )
    }
}

/**
 * Chart for Condition 1 (First-Time User / Zero Data):
 * Shows horizontal dashed baseline guideline with target dry weight, leading to Day 1 Starting Baseline node.
 */
@Composable
private fun FirstTimeWeightChart(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val horizontalPadding = 18.dp.toPx()
        val usableW = w - horizontalPadding * 2
        val midY = h * 0.44f

        val targetX = horizontalPadding + usableW - 20.dp.toPx()

        // 1. Dashed baseline guideline across
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(horizontalPadding + 60.dp.toPx(), midY),
            end = Offset(targetX, midY),
            strokeWidth = 1.6.dp.toPx(),
            pathEffect = dashEffect
        )

        // 2. "~55.0 kg" baseline text on left
        val baselineLayout = textMeasurer.measure(
            text = "~55.0 kg",
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
        )
        drawText(
            textLayoutResult = baselineLayout,
            topLeft = Offset(horizontalPadding, midY - baselineLayout.size.height / 2f)
        )

        // 3. Glowing circular target node on right
        // Pulsing halo
        drawCircle(
            color = Color(0xFF3B82F6).copy(alpha = 0.22f),
            radius = 14.dp.toPx(),
            center = Offset(targetX, midY)
        )
        // Strong blue ring
        drawCircle(
            color = Color(0xFF2563EB),
            radius = 7.5.dp.toPx(),
            center = Offset(targetX, midY),
            style = Stroke(width = 2.4.dp.toPx())
        )
        // White center
        drawCircle(
            color = Color.White,
            radius = 5.dp.toPx(),
            center = Offset(targetX, midY)
        )

        // "Starting Baseline" text above node
        val startLabel = textMeasurer.measure(
            text = "Starting\nBaseline",
            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
        )
        drawText(
            textLayoutResult = startLabel,
            topLeft = Offset(targetX - startLabel.size.width / 2f, midY - startLabel.size.height - 10.dp.toPx())
        )

        // "Day 1 (Today)" text below node
        val day1Label = textMeasurer.measure(
            text = "Day 1 (Today)",
            style = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
        )
        drawText(
            textLayoutResult = day1Label,
            topLeft = Offset(targetX - day1Label.size.width / 2f, midY + 12.dp.toPx())
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F4F9)
@Composable
fun WeightCardPreview() {
    val samplePoints = listOf(
        WeightPoint("Sun", 55.92f),
        WeightPoint("Mon", 55.85f),
        WeightPoint("Tue", 55.84f),
        WeightPoint("Wed", 55.76f),
        WeightPoint("Thu", 55.82f),
        WeightPoint("Fri", 55.75f),
        WeightPoint("Sat", 55.70f)
    )

    WeightCard(
        isLoggedToday = true,
        currentWeightKg = 55.70f,
        weeklyChangeKg = -0.15f,
        points = samplePoints,
        priorPoints = samplePoints.dropLast(1),
        yesterdayWeightKg = 55.75f,
        yesterdayWeightDiffKg = -0.07f,
        onViewHistory = {},
        onLogClick = {}
    )
}
