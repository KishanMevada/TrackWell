package com.trackwell.app.ui.screens.bloodsugarhistory

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarReadingStatus
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.model.ThresholdSettings
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.home.components.BloodSugarCard
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.home.components.homeItemShadow
import com.trackwell.app.ui.screens.today.components.EditWeightDialog
import com.trackwell.app.ui.screens.today.components.SugarEntryBottomSheet
import com.trackwell.app.ui.theme.TargetTokens
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BloodSugarHistoryScreen(
    viewModel: BloodSugarHistoryViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToDayDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var showSugarSheet by remember { mutableStateOf(false) }
    var activeSugarSlot by remember { mutableStateOf(SugarSlot.suggestCurrentSlot()) }
    var editingSugarDay by remember { mutableStateOf<DayOverview?>(null) }
    var editingSugarSlot by remember { mutableStateOf<SugarSlot?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        // Living vascular background matching Home & Weight History
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
            // 1. Status Bar Inset Spacer
            item {
                Spacer(modifier = Modifier.height(statusBarInset + 8.dp))
            }

            // 2. Top App Bar (Back Button + Title + Range Badge Pill)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Elevated Circular Claymorphic Back Button
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

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Blood Sugar History",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.4).sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Glucose & Insulin Analytics",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B),
                                maxLines = 1
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
                        color = if (showFilters) Color(0xFFFEE2E2) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (showFilters) Color(0xFFFECACA) else Color(0xFFE2E8F0)),
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
                                color = if (showFilters) Color(0xFFEF4444) else Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            if (showFilters) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 3. Time Range Filter Chips Carousel (7D, 14D, 30D, 90D, All Time)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SugarTimeRange.entries.filter { it != SugarTimeRange.CUSTOM }.forEach { range ->
                            val isSelected = range == uiState.selectedRange
                            val bg = if (isSelected) Color(0xFFFEE2E2) else Color.White
                            val borderCol = if (isSelected) Color(0xFFFCA5A5) else Color(0xFFE2E8F0)
                            val textCol = if (isSelected) Color(0xFFEF4444) else Color(0xFF64748B)

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
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 4. Slot Filter Sub-Chips Carousel (All Slots, Fasting, Pre-Lunch, Post-Lunch, Pre-Dinner)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SugarSlotFilter.entries.forEach { filter ->
                        val isSelected = filter == uiState.selectedSlotFilter
                        val bg = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
                        val borderCol = if (isSelected) Color(0xFF93C5FD) else Color(0xFFE2E8F0)
                        val textCol = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(width = 1.dp, color = borderCol, shape = RoundedCornerShape(16.dp))
                                .clickable {
                                    HapticFeedbackHelper.performTickHaptic(context)
                                    viewModel.selectSlotFilter(filter)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = textCol
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Hero Blood Sugar & Interactive Trend Graph Card (28dp corners, morning-rose gradient)
            item {
                if (!uiState.isLoggedToday) {
                    BloodSugarCard(
                        isLoggedToday = false,
                        currentValueMgDl = null,
                        status = null,
                        todayPoints = emptyList(),
                        priorPoints = uiState.priorSugarPoints,
                        priorAvgMgDl = uiState.priorSugarAvg,
                        suggestedSlot = uiState.suggestedSlot,
                        onViewHistory = {},
                        onLogClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            activeSugarSlot = uiState.suggestedSlot
                            showSugarSheet = true
                        },
                        onSlotClick = { slot ->
                            HapticFeedbackHelper.performClickHaptic(context)
                            activeSugarSlot = slot
                            showSugarSheet = true
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
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color(0xFFE2E8F0).copy(alpha = 0.60f)
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
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
                                .padding(20.dp)
                        ) {
                            // Header Row: Drop Icon + Latest Value + Status Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
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
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Latest Blood Sugar",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B),
                                            maxLines = 1
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = if (uiState.latestReadingMgDl > 0) uiState.latestReadingMgDl.toString() else "--",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A),
                                                letterSpacing = (-0.5).sp
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "mg/dL",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF0F172A),
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Status Pill with diffused glow (e.g. "High" in Red, "In Range" in Green)
                                val isHigh = uiState.latestStatus.equals("High", ignoreCase = true)
                                val isLow = uiState.latestStatus.equals("Low", ignoreCase = true)
                                val pillBg = when {
                                    isHigh -> Color(0xFFFEE2E2)
                                    isLow -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFDCFCE7)
                                }
                                val pillTextCol = when {
                                    isHigh -> Color(0xFFEF4444)
                                    isLow -> Color(0xFFD97706)
                                    else -> Color(0xFF16A34A)
                                }

                                Box(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            spotColor = if (isHigh) Color(0x33EF4444) else Color(0x2216A34A),
                                            ambientColor = if (isHigh) Color(0x1AEF4444) else Color(0x1116A34A)
                                        )
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(pillBg)
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = uiState.latestStatus,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = pillTextCol
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.selectedRange.label} Trend • Target Range 70-140 mg/dL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Full-Width Canvas Sugar Line Chart with Pale Green Target Band
                            SugarTrendCanvas(
                                points = uiState.chartPoints,
                                thresholds = uiState.appSettings.thresholdSettings,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )
                        }
                    }
                }
            }
        }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6. Clinical Summary Badges (Period Avg, In Target %, Highest)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBadge(
                        title = "Period Avg",
                        value = if (uiState.averageSugarMgDl > 0) "${uiState.averageSugarMgDl} mg/dL" else "--",
                        color = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "In Target",
                        value = if (uiState.averageSugarMgDl > 0) "${uiState.inRangePercent}%" else "--",
                        color = if (uiState.inRangePercent >= 70) Color(0xFF16A34A) else Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "Peak Reading",
                        value = if (uiState.highestReadingMgDl > 0) "${uiState.highestReadingMgDl} mg/dL" else "--",
                        color = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 7. Detailed Historical Blood Sugar Logs Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "HISTORICAL READINGS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.historyDays.size} Recorded Days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 8. List of Chronological Sugar Day Entries or Empty Hint Card
            if (uiState.historyDays.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
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
                                    .background(Color(0xFFFEE2E2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_blood_drop),
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No glucose readings recorded yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Log pre and post-meal glucose readings to track your glycemic stability and target compliance.",
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                items(uiState.historyDays, key = { it.date }) { day ->
                    SugarDayEntryItem(
                        day = day,
                        thresholds = uiState.appSettings.thresholdSettings,
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            editingSugarDay = day
                            editingSugarSlot = day.sugarEntries.keys.firstOrNull() ?: SugarSlot.BEFORE_BREAKFAST
                        },
                        onSlotClick = { slot ->
                            HapticFeedbackHelper.performClickHaptic(context)
                            editingSugarDay = day
                            editingSugarSlot = slot
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Bottom Spacing for Floating Navigation Bar
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showSugarSheet) {
            SugarEntryBottomSheet(
                initialSlot = activeSugarSlot,
                existingEntry = null,
                selectedDateIso = DateTimeUtils.getTodayIso(),
                thresholds = uiState.appSettings.thresholdSettings,
                onDismiss = { showSugarSheet = false },
                onSave = { slot, sugarMgDl, insulinUnits, timestamp, note ->
                    viewModel.logSugar(slot, sugarMgDl, insulinUnits, timestamp, note)
                    showSugarSheet = false
                },
                onDelete = {
                    showSugarSheet = false
                }
            )
        }

        editingSugarDay?.let { day ->
            val activeSlot = editingSugarSlot ?: day.sugarEntries.keys.firstOrNull() ?: SugarSlot.BEFORE_BREAKFAST
            SugarEntryBottomSheet(
                initialSlot = activeSlot,
                existingEntry = day.sugarEntries[activeSlot],
                existingEntries = day.sugarEntries,
                selectedDateIso = day.date,
                thresholds = uiState.appSettings.thresholdSettings,
                onDismiss = {
                    editingSugarDay = null
                    editingSugarSlot = null
                },
                onSave = { slot, sugarMgDl, insulinUnits, timestamp, note ->
                    viewModel.logSugarForDate(
                        date = day.date,
                        slot = slot,
                        sugarMgDl = sugarMgDl,
                        insulinUnits = insulinUnits,
                        timestamp = timestamp,
                        note = note
                    )
                    editingSugarDay = null
                    editingSugarSlot = null
                },
                onDelete = { entryId ->
                    viewModel.deleteSugarEntry(entryId)
                    editingSugarDay = null
                    editingSugarSlot = null
                }
            )
        }
    }
}

@Composable
fun SugarTrendCanvas(
    points: List<SugarChartPoint>,
    thresholds: ThresholdSettings,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    if (points.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "No blood sugar records in this range",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp
            )
        }
        return
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        if (points.isEmpty()) {
            val padX = 18.dp.toPx()
            val targetY = h * 0.5f
            val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
            drawLine(
                color = Color(0xFFEF4444).copy(alpha = 0.20f),
                start = Offset(padX, targetY),
                end = Offset(w - padX, targetY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dash
            )
            return@Canvas
        }

        val horizontalPadding = 18.dp.toPx()
        val topPadding = 24.dp.toPx()
        val bottomPadding = 26.dp.toPx()
        val usableW = w - horizontalPadding * 2
        val usableH = h - topPadding - bottomPadding

        val allValues = points.map { it.valueMgDl }
        val minVal = minOf(allValues.minOrNull() ?: 70, 70)
        val maxVal = maxOf(allValues.maxOrNull() ?: 200, 200)
        val scaleRange = (maxVal - minVal).coerceAtLeast(40).toFloat()

        fun mapY(valMgDl: Float): Float {
            val norm = 1f - ((valMgDl - minVal) / scaleRange).coerceIn(0f, 1f)
            return topPadding + norm * usableH
        }

        // A. Pale Green Horizontal Target-Range Band (70 - 140 mg/dL)
        val targetBandTop = mapY(thresholds.targetMax.toFloat())
        val targetBandBottom = mapY(thresholds.lowThreshold.toFloat())
        val targetBandHeight = (targetBandBottom - targetBandTop).coerceAtLeast(18.dp.toPx())

        drawRoundRect(
            color = Color(0xFFDCFCE7).copy(alpha = 0.70f),
            topLeft = Offset(x = horizontalPadding - 8.dp.toPx(), y = targetBandTop),
            size = Size(
                width = usableW + 16.dp.toPx(),
                height = targetBandHeight
            ),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
        )

        val n = points.size
        val stepX = if (n > 1) usableW / (n - 1) else 0f
        val coords = points.mapIndexed { idx, pt ->
            val x = if (n > 1) horizontalPadding + idx * stepX else w / 2f
            val y = mapY(pt.valueMgDl.toFloat())
            Offset(x, y)
        }

        // B. Light Dotted Vertical Drop Lines
        val dropDash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()), 0f)
        coords.forEach { pt ->
            drawLine(
                color = Color(0xFFCBD5E1).copy(alpha = 0.65f),
                start = Offset(pt.x, pt.y + 5.dp.toPx()),
                end = Offset(pt.x, h - bottomPadding + 4.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dropDash
            )
        }

        // C. Soft Red Gradient Fill Under Trend Line
        if (coords.size > 1) {
            val fillPath = Path().apply {
                moveTo(coords.first().x, h - bottomPadding)
                lineTo(coords.first().x, coords.first().y)
                for (i in 1 until coords.size) {
                    lineTo(coords[i].x, coords[i].y)
                }
                lineTo(coords.last().x, h - bottomPadding)
                close()
            }
            val minY = coords.minOf { it.y }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEF4444).copy(alpha = 0.22f),
                        Color(0xFFFCA5A5).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = minY,
                    endY = h - bottomPadding
                )
            )

            // D. Red Trend Line connecting points
            val strokePath = Path().apply {
                moveTo(coords.first().x, coords.first().y)
                for (i in 1 until coords.size) {
                    lineTo(coords[i].x, coords[i].y)
                }
            }
            drawPath(
                path = strokePath,
                color = Color(0xFFEF4444),
                style = Stroke(
                    width = 2.4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // E. Circular Nodes, Numeric Values Above, and Date Labels Below
        val showEvery = if (coords.size > 14) coords.size / 7 else 1

        coords.forEachIndexed { idx, pt ->
            val reading = points[idx].valueMgDl
            val isHighReading = reading > thresholds.targetMax

            // Node circles (White-filled with red outline)
            drawCircle(
                color = Color.White,
                radius = 4.2.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = if (isHighReading) Color(0xFFEF4444) else Color(0xFF16A34A),
                radius = 4.2.dp.toPx(),
                center = pt,
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Value label above point
            if (coords.size <= 10 || idx % showEvery == 0 || idx == coords.lastIndex) {
                val valStr = reading.toString()
                val valLayout = textMeasurer.measure(
                    text = valStr,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isHighReading) Color(0xFFDC2626) else Color(0xFF334155)
                    )
                )
                drawText(
                    textLayoutResult = valLayout,
                    topLeft = Offset(
                        x = pt.x - (valLayout.size.width / 2f),
                        y = pt.y - valLayout.size.height - 4.dp.toPx()
                    )
                )

                // Date/slot label below
                val labelStr = points[idx].xLabel
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

// ==============================================================
// GLYCEMIC WAVEFORM RIBBON (Concept 1 - Micro-Sparkline & Corridor)
// ==============================================================
@Composable
fun SugarDayEntryItem(
    day: DayOverview,
    thresholds: ThresholdSettings,
    onClick: () -> Unit,
    onSlotClick: (SugarSlot) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val entries = day.sugarEntries
    val readings = entries.values.map { it.sugarMgDl }.filter { it > 0 }
    val avgReading = if (readings.isNotEmpty()) readings.average().toInt() else 0
    val maxReading = readings.maxOrNull() ?: 0

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

    val standardSlots = listOf(
        SugarSlot.BEFORE_BREAKFAST to "Fasting",
        SugarSlot.BEFORE_LUNCH to "Pre-Lunch",
        SugarSlot.AFTER_LUNCH to "Post-Lunch",
        SugarSlot.BEFORE_DINNER to "Pre-Dinner"
    )

    val slotDataList = standardSlots.map { (slot, label) ->
        val entry = entries[slot]?.takeIf { it.sugarMgDl > 0 }
        Triple(slot, label, entry)
    }

    val isAvgHigh = avgReading > thresholds.targetMax
    val isAvgLow = avgReading in 1 until thresholds.lowThreshold
    val isAvgOptimal = avgReading in thresholds.lowThreshold..thresholds.targetMax

    val avgBg = when {
        readings.isEmpty() -> Color(0xFFF8FAFC)
        isAvgHigh -> Color(0xFFFEE2E2)
        isAvgLow -> Color(0xFFFEF3C7)
        else -> Color(0xFFDCFCE7)
    }
    val avgTextCol = when {
        readings.isEmpty() -> Color(0xFF94A3B8)
        isAvgHigh -> Color(0xFFEF4444)
        isAvgLow -> Color(0xFFD97706)
        else -> Color(0xFF16A34A)
    }
    val avgBorder = when {
        readings.isEmpty() -> Color(0xFFE2E8F0)
        isAvgHigh -> Color(0xFFFECACA)
        isAvgLow -> Color(0xFFFDE68A)
        else -> Color(0xFFBBF7D0)
    }

    val totalInsulin = entries.values.sumOf { it.insulinUnits }

    Surface(
        onClick = onClick,
        modifier = modifier
            .homeItemShadow(cornerRadius = 24.dp, elevation = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        color = Color.White,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: 3D Blood Droplet Icon + Date & Slot Count + Avg Pill + Chevron
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
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_blood_drop),
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = dateFormatted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${readings.size}/4 meals logged",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B)
                            )
                            if (totalInsulin > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "+${String.format(Locale.US, "%.0f", totalInsulin)}u Ins",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = avgBg,
                        border = BorderStroke(1.dp, avgBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (avgReading > 0) "Avg $avgReading mg/dL" else "No Logs",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = avgTextCol
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = avgTextCol,
                                modifier = Modifier.size(width = 5.dp, height = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Glycemic Waveform Ribbon Micro Canvas with Target Corridor (70-140 mg/dL)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            ) {
                SugarWaveformCanvas(
                    slotDataList = slotDataList,
                    thresholds = thresholds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Meal Slot Pills Row with status colors & readings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                slotDataList.forEach { (slot, label, entry) ->
                    val reading = entry?.sugarMgDl
                    val status = reading?.let { thresholds.evaluate(it) }

                    val (pillBg, borderCol, textCol) = when {
                        reading == null -> Triple(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFF94A3B8))
                        status == SugarReadingStatus.HIGH -> Triple(Color(0xFFFFF1F2), Color(0xFFFECACA), Color(0xFFEF4444))
                        status == SugarReadingStatus.LOW -> Triple(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFD97706))
                        else -> Triple(Color(0xFFF0FDF4), Color(0xFFBBF7D0), Color(0xFF16A34A))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(pillBg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .clickable {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onSlotClick(slot)
                            }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = reading?.toString() ?: "--",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textCol
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Micro-Canvas rendering the Glycemic Waveform Ribbon:
 * - Subtle pastel-green target corridor (70 - 140 mg/dL)
 * - Cubic bezier curve traversing the recorded readings
 * - Elevated circular nodes with glowing halos and discrete value callouts
 */
@Composable
fun SugarWaveformCanvas(
    slotDataList: List<Triple<SugarSlot, String, SugarEntry?>>,
    thresholds: ThresholdSettings,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val padX = 32.dp.toPx()
        val padTop = 14.dp.toPx()
        val padBottom = 16.dp.toPx()
        val usableW = w - padX * 2
        val usableH = h - padTop - padBottom

        val minVal = 60f
        val maxVal = 240f
        val range = maxVal - minVal

        fun mapY(value: Float): Float {
            val norm = 1f - ((value - minVal) / range).coerceIn(0f, 1f)
            return padTop + norm * usableH
        }

        // 1. Draw Target Corridor Band (70 - 140 mg/dL)
        val targetTop = mapY(thresholds.targetMax.toFloat())
        val targetBottom = mapY(thresholds.lowThreshold.toFloat())
        val bandH = (targetBottom - targetTop).coerceAtLeast(10.dp.toPx())

        drawRoundRect(
            color = Color(0xFFDCFCE7).copy(alpha = 0.55f),
            topLeft = Offset(0f, targetTop),
            size = Size(w, bandH),
            cornerRadius = CornerRadius(0f, 0f)
        )

        // Dotted Target Boundary Line (140 mg/dL)
        val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
        drawLine(
            color = Color(0xFF10B981).copy(alpha = 0.40f),
            start = Offset(0f, targetTop),
            end = Offset(w, targetTop),
            strokeWidth = 1.dp.toPx(),
            pathEffect = dash
        )

        val n = slotDataList.size
        val stepX = usableW / (n - 1)

        val pointsWithX = slotDataList.mapIndexed { idx, item ->
            val reading = item.third?.sugarMgDl
            val x = padX + idx * stepX
            val y = reading?.let { mapY(it.toFloat()) }
            Triple(x, y, reading)
        }

        val validPoints = pointsWithX.filter { it.second != null }

        // 2. Draw Smooth Bezier Waveform Path
        if (validPoints.size >= 2) {
            val path = Path()
            path.moveTo(validPoints.first().first, validPoints.first().second!!)

            for (i in 0 until validPoints.size - 1) {
                val p0 = validPoints[i]
                val p1 = validPoints[i + 1]
                val midX = (p0.first + p1.first) / 2f
                path.cubicTo(midX, p0.second!!, midX, p1.second!!, p1.first, p1.second!!)
            }

            drawPath(
                path = path,
                color = Color(0xFFEF4444).copy(alpha = 0.85f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        } else if (validPoints.size == 1) {
            // Single point: subtle horizontal indicator
            val pt = validPoints.first()
            drawLine(
                color = Color(0xFFEF4444).copy(alpha = 0.5f),
                start = Offset(pt.first - 20.dp.toPx(), pt.second!!),
                end = Offset(pt.first + 20.dp.toPx(), pt.second!!),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // 3. Draw Nodes with halos
        pointsWithX.forEach { (x, y, reading) ->
            if (y != null && reading != null) {
                val isHigh = reading > thresholds.targetMax
                val isLow = reading < thresholds.lowThreshold
                val nodeColor = when {
                    isHigh -> Color(0xFFEF4444)
                    isLow -> Color(0xFFD97706)
                    else -> Color(0xFF10B981)
                }

                // Outer Halo
                drawCircle(
                    color = nodeColor.copy(alpha = 0.22f),
                    radius = 8.dp.toPx(),
                    center = Offset(x, y)
                )
                // White Ring Border
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                // Core Center Dot
                drawCircle(
                    color = nodeColor,
                    radius = 3.5.dp.toPx(),
                    center = Offset(x, y)
                )
            } else {
                // Empty slot dashed circle placeholder
                val defY = mapY(105f)
                drawCircle(
                    color = Color(0xFFCBD5E1),
                    radius = 3.dp.toPx(),
                    center = Offset(x, defY)
                )
            }
        }
    }
}
