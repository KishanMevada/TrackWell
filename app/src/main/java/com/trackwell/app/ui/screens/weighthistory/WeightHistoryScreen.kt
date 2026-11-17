package com.trackwell.app.ui.screens.weighthistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.home.components.homeItemShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.WeightCard
import com.trackwell.app.ui.home.components.WeightPoint
import com.trackwell.app.ui.theme.TargetTokens
import java.util.Locale
import kotlin.math.abs

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryScreen(
    viewModel: WeightHistoryViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToDayDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var showWeightSheet by remember { mutableStateOf(false) }
    var editWeightDay by remember { mutableStateOf<DayOverview?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        // Living vascular background matching Home
        VascularBackground(modifier = Modifier.fillMaxSize())

        // Living decorative wellness body flow backdrop matching Home
        RenalBodyFlowBackdrop(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Inset Spacer
            item {
                Spacer(modifier = Modifier.height(statusBarInset + 8.dp))
            }

            // 2. Custom Top App Bar (Back button, Title, Subtitle, Filter action)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Soft Circular Claymorphic Back Button
                        Surface(
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onNavigateUp()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0x404A6282),
                                    ambientColor = Color(0x2864748B)
                                ),
                            shape = CircleShape,
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_left),
                                    contentDescription = "Back",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Weight History",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.4).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Body Dry Weight Analytics",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Filter Toggle Pill
                    Surface(
                        onClick = {
                            HapticFeedbackHelper.performTickHaptic(context)
                            showFilters = !showFilters
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (showFilters) Color(0xFFEFF6FF) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (showFilters) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Filter 📅",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (showFilters) Color(0xFF2563EB) else Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            if (showFilters) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 3. Time Range Filter Chips Carousel
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WeightTimeRange.entries.filter { it != WeightTimeRange.CUSTOM }.forEach { range ->
                            val isSelected = range == uiState.selectedRange
                            val bg = if (isSelected) Color(0xFFEFF6FF) else Color.White
                            val borderCol = if (isSelected) Color(0xFF93C5FD) else Color(0xFFE2E8F0)
                            val textCol = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(bg)
                                    .border(width = 1.dp, color = borderCol, shape = RoundedCornerShape(20.dp))
                                    .clickable {
                                        HapticFeedbackHelper.performTickHaptic(context)
                                        viewModel.selectRange(range)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = range.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textCol
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Hero Weight & Interactive Trend Graph Card (28dp corners)
            item {
                if (!uiState.isLoggedToday) {
                    WeightCard(
                        isLoggedToday = false,
                        currentWeightKg = null,
                        weeklyChangeKg = uiState.yesterdayWeightDiffKg,
                        points = emptyList(),
                        priorPoints = uiState.priorWeightPoints,
                        yesterdayWeightKg = uiState.yesterdayWeightKg,
                        yesterdayWeightDiffKg = uiState.yesterdayWeightDiffKg,
                        onViewHistory = {},
                        onLogClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            showWeightSheet = true
                        }
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .homeCardShadow(cornerRadius = 28.dp, elevation = 14.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        color = Color.White,
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Card Header Row: Icon + Current Value + Delta Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    Column {
                                        Text(
                                            text = "Current Dry Weight",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = String.format(Locale.US, "%.2f", uiState.currentWeightKg ?: 55.70f),
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
                                    }
                                }

                                // Directional Change Pill (e.g. ↓ 0.15 kg)
                                val isLoss = uiState.latestDeltaKg < 0
                                val arrow = if (isLoss) "↓" else if (uiState.latestDeltaKg > 0) "↑" else ""
                                val deltaCol = if (isLoss) Color(0xFF2563EB) else Color(0xFFD97706)
                                val deltaBg = if (isLoss) Color(0xFFEFF6FF) else Color(0xFFFEF3C7)
                                val deltaBorder = if (isLoss) Color(0xFF93C5FD) else Color(0xFFFDE68A)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(deltaBg)
                                        .border(width = 1.dp, color = deltaBorder, shape = RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$arrow ${String.format(Locale.US, "%.2f", abs(uiState.latestDeltaKg))} kg",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = deltaCol
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.selectedRange.label} Trend • Target Range Normal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Full-Width Canvas Weight Line Chart
                            WeightTrendCanvas(
                                points = uiState.chartPoints,
                                targetWeightKg = uiState.targetWeightKg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Clinical Summary Badges (Period Avg, Net Change, Baseline Gain)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBadge(
                        title = "Period Avg",
                        value = "${String.format(Locale.US, "%.2f", uiState.averageWeightKg)} kg",
                        color = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "Net Change",
                        value = "${if (uiState.netPeriodChangeKg > 0) "+" else ""}${String.format(Locale.US, "%.2f", uiState.netPeriodChangeKg)} kg",
                        color = if (abs(uiState.netPeriodChangeKg) <= 0.5f) Color(0xFF16A34A) else Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "Post-Tx Gain",
                        value = "+${String.format(Locale.US, "%.2f", uiState.baselineGainKg)} kg",
                        color = Color(0xFF0F766E),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 6. Detailed Weight Logs Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "HISTORICAL ENTRIES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.historyEntries.size} Recorded Days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 7. List of Chronological Weight Entries or Empty Hint Card
            if (uiState.historyEntries.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .homeCardShadow(cornerRadius = 22.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Scale,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No weight logs recorded yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap '+ Set Initial Dry Weight' above or log morning weight daily to track body dry weight trends.",
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                items(uiState.historyEntries, key = { it.date }) { day ->
                    WeightEntryItem(
                        day = day,
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            editWeightDay = day
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }

            // Bottom Spacing for Floating Navigation Bar
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        editWeightDay?.let { day ->
            WeightEntryBottomSheet(
                selectedDateIso = day.date,
                initialWeightKg = day.morningWeightKg?.toDouble(),
                targetWeightKg = uiState.targetWeightKg.toDouble(),
                onDismiss = { editWeightDay = null },
                onSave = { weightKg ->
                    viewModel.updateWeightForDay(day.date, weightKg)
                    editWeightDay = null
                },
                onDelete = {
                    viewModel.deleteWeightForDay(day.date)
                    editWeightDay = null
                }
            )
        }

        if (showWeightSheet) {
            WeightEntryBottomSheet(
                selectedDateIso = DateTimeUtils.getTodayIso(),
                initialWeightKg = uiState.yesterdayWeightKg?.toDouble(),
                targetWeightKg = uiState.targetWeightKg.toDouble(),
                onDismiss = { showWeightSheet = false },
                onSave = { weightKg ->
                    viewModel.updateWeight(weightKg)
                    showWeightSheet = false
                }
            )
        }
    }
}

@Composable
fun WeightTrendCanvas(
    points: List<WeightPoint>,
    targetWeightKg: Float,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    if (points.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "No weight records in this range",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp
            )
        }
        return
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val horizontalPadding = 18.dp.toPx()
        val topPadding = 24.dp.toPx()
        val bottomPadding = 26.dp.toPx()
        val usableW = w - horizontalPadding * 2
        val usableH = h - topPadding - bottomPadding

        val allValues = points.map { it.weightKg } + targetWeightKg
        val minW = allValues.minOrNull() ?: 55.0f
        val maxW = allValues.maxOrNull() ?: 56.5f
        val span = (maxW - minW).coerceAtLeast(0.3f)
        val chartMin = minW - span * 0.20f
        val chartMax = maxW + span * 0.20f
        val scaleRange = chartMax - chartMin

        fun mapY(valKg: Float): Float {
            val norm = 1f - ((valKg - chartMin) / scaleRange).coerceIn(0f, 1f)
            return topPadding + norm * usableH
        }

        val n = points.size
        val stepX = if (n > 1) usableW / (n - 1) else 0f
        val coords = points.mapIndexed { idx, pt ->
            val x = if (n > 1) horizontalPadding + idx * stepX else w / 2f
            val y = mapY(pt.weightKg)
            Offset(x, y)
        }

        // A. Subtle Green Target Line (e.g. 55.50 kg)
        val targetY = mapY(targetWeightKg)
        val targetDash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()), 0f)
        drawLine(
            color = Color(0xFF10B981).copy(alpha = 0.55f),
            start = Offset(horizontalPadding - 6.dp.toPx(), targetY),
            end = Offset(w - horizontalPadding + 6.dp.toPx(), targetY),
            strokeWidth = 1.2.dp.toPx(),
            pathEffect = targetDash
        )

        // B. Light Dotted Vertical Drop Lines
        val dropDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
        coords.forEach { pt ->
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(pt.x, pt.y + 4.dp.toPx()),
                end = Offset(pt.x, h - bottomPadding + 4.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dropDash
            )
        }

        // C. Translucent Blue-to-Transparent Gradient Fill
        if (coords.size > 1) {
            val fillPath = Path().apply {
                moveTo(coords.first().x, h - bottomPadding)
                lineTo(coords.first().x, coords.first().y)
                for (i in 0 until coords.size - 1) {
                    val p0 = coords[i]
                    val p1 = coords[i + 1]
                    val cX = (p0.x + p1.x) / 2f
                    cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
                }
                lineTo(coords.last().x, h - bottomPadding)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF60A5FA).copy(alpha = 0.25f),
                        Color(0xFF93C5FD).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = topPadding,
                    endY = h - bottomPadding
                )
            )

            // D. Smooth Spline Stroke
            val strokePath = Path().apply {
                moveTo(coords.first().x, coords.first().y)
                for (i in 0 until coords.size - 1) {
                    val p0 = coords[i]
                    val p1 = coords[i + 1]
                    val cX = (p0.x + p1.x) / 2f
                    cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
                }
            }
            drawPath(
                path = strokePath,
                color = Color(0xFF2563EB),
                style = Stroke(
                    width = 2.6.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // E. Circular Nodes, Numeric Values Above, and Date Labels Below
        val showEvery = if (coords.size > 14) coords.size / 7 else 1

        coords.forEachIndexed { idx, pt ->
            // Node circles
            drawCircle(
                color = Color.White,
                radius = 4.2.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = 4.2.dp.toPx(),
                center = pt,
                style = Stroke(width = 2.dp.toPx())
            )

            // Number above point (show on selected cadence if large dataset)
            if (coords.size <= 10 || idx % showEvery == 0 || idx == coords.lastIndex) {
                val valueStr = String.format(Locale.US, "%.2f", points[idx].weightKg)
                val textLayout = textMeasurer.measure(
                    text = valueStr,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x = pt.x - (textLayout.size.width / 2f),
                        y = pt.y - textLayout.size.height - 4.dp.toPx()
                    )
                )

                // Date label below
                val labelStr = points[idx].label
                val labelLayout = textMeasurer.measure(
                    text = labelStr,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8)
                    )
                )
                drawText(
                    textLayoutResult = labelLayout,
                    topLeft = Offset(
                        x = pt.x - (labelLayout.size.width / 2f),
                        y = h - labelLayout.size.height
                    )
                )
            }
        }
    }
}

@Composable
fun StatBadge(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .homeItemShadow(cornerRadius = 18.dp, elevation = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp)),
        color = Color.White,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun WeightEntryItem(
    day: DayOverview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weight = day.morningWeightKg ?: return
    val diff = day.weightDiffPreviousDay ?: 0.0
    val isLoss = diff < 0
    val isGain = diff > 0
    val arrow = if (isLoss) "↓" else if (isGain) "↑" else ""
    val pillCol = if (isLoss) Color(0xFF2563EB) else if (isGain) Color(0xFFEF4444) else Color(0xFF16A34A)
    val pillBg = if (isLoss) Color(0xFFEFF6FF) else if (isGain) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)

    val dateFormatted = try {
        val parsed = DateTimeUtils.parseIsoDate(day.date)
        if (parsed != null) {
            val isToday = DateTimeUtils.isToday(day.date)
            val isYesterday = day.date == DateTimeUtils.getDaysAgoIso(1)
            val prefix = if (isToday) "Today, " else if (isYesterday) "Yesterday, " else ""
            prefix + java.text.SimpleDateFormat("EEE, d MMM", Locale.US).format(parsed)
        } else day.date
    } catch (e: Exception) {
        day.date
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .homeItemShadow(cornerRadius = 20.dp, elevation = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFF8FAFC), RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scale),
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = dateFormatted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Morning fasting weigh-in",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", weight)} kg",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(pillBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (diff != 0.0) "$arrow ${String.format(Locale.US, "%.2f", abs(diff))} kg" else "0.00 kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = pillCol
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "View Day Details",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(width = 6.dp, height = 12.dp)
                )
            }
        }
    }
}
