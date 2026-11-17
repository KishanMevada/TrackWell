package com.trackwell.app.ui.screens.fluidhistory

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.Icon3DUrineDrop
import com.trackwell.app.ui.common.Icon3DWaterDrop
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.home.components.homeItemShadow
import com.trackwell.app.ui.theme.TargetTokens
import java.util.Locale
import kotlin.math.abs

@Composable
fun FluidHistoryScreen(
    viewModel: FluidHistoryViewModel,
    onNavigateUp: () -> Unit = {},
    onNavigateToDayDetail: (String) -> Unit,
    showBackButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
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

            // 2. Top App Bar (Title + Optional Back Button + Filter Toggle)
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
                        if (showBackButton) {
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
                                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
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

                            Spacer(modifier = Modifier.width(14.dp))
                        }

                        Column {
                            Text(
                                text = "Fluid Balance History",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.4).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Intake & Urine Output Analytics",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        onClick = { showFilters = !showFilters },
                        shape = RoundedCornerShape(14.dp),
                        color = if (showFilters) Color(0xFFE0F2FE) else Color.White,
                        border = BorderStroke(1.dp, if (showFilters) Color(0xFF7DD3FC) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filter 📅",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showFilters) Color(0xFF0284C7) else Color(0xFF475569)
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
                        FluidTimeRange.entries.filter { it != FluidTimeRange.CUSTOM }.forEach { range ->
                            val isSelected = range == uiState.selectedRange
                            val bg = if (isSelected) Color(0xFFE0F2FE) else Color.White
                            val borderCol = if (isSelected) Color(0xFF7DD3FC) else Color(0xFFE2E8F0)
                            val textCol = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)

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

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 4. Stream Filter Chips (Net Balance, Intake Only, Urine Output Only)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FluidStreamFilter.entries.forEach { filter ->
                            val isSelected = filter == uiState.selectedFilter
                            val bg = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
                            val borderCol = if (isSelected) Color(0xFF93C5FD) else Color(0xFFE2E8F0)
                            val textCol = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(bg)
                                    .border(width = 1.dp, color = borderCol, shape = RoundedCornerShape(20.dp))
                                    .clickable {
                                        HapticFeedbackHelper.performTickHaptic(context)
                                        viewModel.selectFilter(filter)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter.label,
                                    fontSize = 12.sp,
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

            // 5. Hero Card (Net Balance / Intake / Urine Output - Dynamic to Selected Filter)
            item {
                val currentFilter = uiState.selectedFilter
                val heroTitle = when (currentFilter) {
                    FluidStreamFilter.NET_BALANCE -> "Net Renal Balance"
                    FluidStreamFilter.INTAKE_ONLY -> "Daily Fluid Intake"
                    FluidStreamFilter.URINE_ONLY -> "Daily Urine Output"
                }
                val heroValue = when (currentFilter) {
                    FluidStreamFilter.NET_BALANCE -> "${if (uiState.currentNetBalanceMl > 0) "+" else ""}${uiState.currentNetBalanceMl}"
                    FluidStreamFilter.INTAKE_ONLY -> "${uiState.currentIntakeMl}"
                    FluidStreamFilter.URINE_ONLY -> "${uiState.currentUrineMl}"
                }
                val isOptimal = uiState.currentNetBalanceMl in -100..700

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
                                        Color(0xFFF0FDFE)
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
                            // Header Row: 3D Drop Icon + Metric Title/Value + Status Pill
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
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (currentFilter == FluidStreamFilter.URINE_ONLY) Color(0xFFFEF3C7)
                                                else Color(0xFFE0F7FA)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (currentFilter == FluidStreamFilter.URINE_ONLY) {
                                            Icon3DUrineDrop(size = 20.dp)
                                        } else {
                                            Icon3DWaterDrop(size = 20.dp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = heroTitle,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = heroValue,
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A),
                                                letterSpacing = (-0.5).sp
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "ml",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF0F172A),
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Status Pill
                                when (currentFilter) {
                                    FluidStreamFilter.NET_BALANCE -> {
                                        Box(
                                            modifier = Modifier
                                                .shadow(
                                                    elevation = 2.dp,
                                                    shape = RoundedCornerShape(14.dp),
                                                    spotColor = if (isOptimal) Color(0x3316A34A) else Color(0x33D97706),
                                                    ambientColor = if (isOptimal) Color(0x1A16A34A) else Color(0x1AD97706)
                                                )
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isOptimal) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = uiState.statusLabel,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isOptimal) Color(0xFF16A34A) else Color(0xFFD97706),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    FluidStreamFilter.INTAKE_ONLY -> {
                                        val percent = (uiState.currentIntakeMl.toFloat() / uiState.intakeTargetMl.coerceAtLeast(1) * 100).toInt()
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFFE0F2FE))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "$percent% Target",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0284C7),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    FluidStreamFilter.URINE_ONLY -> {
                                        val percent = (uiState.currentUrineMl.toFloat() / uiState.urineTargetMl.coerceAtLeast(1) * 100).toInt()
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFFFEF3C7))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "$percent% Target",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD97706),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.selectedRange.label} Trend • Daily Intake ${uiState.intakeTargetMl} ml • Urine ${uiState.urineTargetMl} ml",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Dynamic Filter Trend Bar Chart
                            FluidTrendCanvas(
                                points = uiState.chartPoints,
                                filter = uiState.selectedFilter,
                                intakeTargetMl = uiState.intakeTargetMl,
                                urineTargetMl = uiState.urineTargetMl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Chart Legend Row Dynamic to Filter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (currentFilter) {
                                    FluidStreamFilter.NET_BALANCE -> {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF0EA5E9))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Net Surplus (+ ml)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                        Spacer(modifier = Modifier.width(18.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF59E0B))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Net Deficit (- ml)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    FluidStreamFilter.INTAKE_ONLY -> {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF0EA5E9))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Fluid Intake",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                        Spacer(modifier = Modifier.width(18.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .height(2.dp)
                                                .background(Color(0xFF0284C7).copy(alpha = 0.5f))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Target (${uiState.intakeTargetMl} ml)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    FluidStreamFilter.URINE_ONLY -> {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF59E0B))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Urine Output",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                        Spacer(modifier = Modifier.width(18.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .height(2.dp)
                                                .background(Color(0xFFD97706).copy(alpha = 0.5f))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Target (${uiState.urineTargetMl} ml)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6. Clinical Summary Badges (Avg Intake, Avg Output, Avg Balance)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBadge(
                        title = "Avg Intake",
                        value = "${uiState.averageIntakeMl} ml",
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "Avg Output",
                        value = "${uiState.averageUrineMl} ml",
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "Avg Balance",
                        value = "${if (uiState.averageNetBalanceMl > 0) "+" else ""}${uiState.averageNetBalanceMl} ml",
                        color = if (uiState.averageNetBalanceMl in -100..700) Color(0xFF16A34A) else Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 7. Daily Fluid Logs Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Daily Fluid Logs",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE2E8F0))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${uiState.historyDays.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Text(
                        text = "Hydro-Pill View",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 8. Daily Cards or Empty State Hint Card
            if (uiState.historyDays.isEmpty()) {
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
                                    .background(Color(0xFFE0F7FA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon3DWaterDrop(size = 30.dp)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No fluid logs recorded yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Log your daily water intake and urine output on the home dashboard to track your renal balance.",
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
                    FluidDayCardHydroPill(
                        day = day,
                        intakeTarget = uiState.intakeTargetMl,
                        urineTarget = uiState.urineTargetMl,
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onNavigateToDayDetail(day.date)
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
    }
}

/**
 * Dynamic trend bar chart reflecting exactly whichever filter is selected:
 * - NET_BALANCE: Shows single centered Net Balance bar (cyan/blue for surplus, amber for deficit) with +val/-val.
 * - INTAKE_ONLY: Shows Fluid Intake bar with intake target dashed line.
 * - URINE_ONLY: Shows Urine Output bar with urine target dashed line.
 */
@Composable
fun FluidTrendCanvas(
    points: List<FluidDayPoint>,
    filter: FluidStreamFilter,
    intakeTargetMl: Int,
    urineTargetMl: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    if (points.isEmpty() || points.all { it.intakeMl == 0 && it.urineMl == 0 }) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val padX = 16.dp.toPx()
                val padBottom = 24.dp.toPx()
                val usableW = w - padX * 2
                val usableH = h - 44.dp.toPx()

                // Faint target line
                val targetY = 24.dp.toPx() + usableH * 0.4f
                val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
                drawLine(
                    color = Color(0xFF0284C7).copy(alpha = 0.15f),
                    start = Offset(padX, targetY),
                    end = Offset(w - padX, targetY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dash
                )

                // 7 Un-obscured Light Placeholder Bars
                val placeholderRatios = listOf(0.35f, 0.50f, 0.40f, 0.65f, 0.45f, 0.70f, 0.55f)
                val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val slotW = usableW / 7f
                val barW = (slotW * 0.40f).coerceIn(8.dp.toPx(), 18.dp.toPx())

                placeholderRatios.forEachIndexed { idx, ratio ->
                    val centerX = padX + idx * slotW + slotW / 2f
                    val barH = ratio * usableH
                    val barLeft = centerX - barW / 2f

                    drawRoundRect(
                        color = Color(0xFF0284C7).copy(alpha = 0.15f),
                        topLeft = Offset(barLeft, h - padBottom - barH),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    val labelLayout = textMeasurer.measure(
                        text = dayLabels[idx],
                        style = TextStyle(fontSize = 10.sp, color = Color(0xFFCBD5E1))
                    )
                    drawText(
                        textLayoutResult = labelLayout,
                        topLeft = Offset(centerX - labelLayout.size.width / 2f, h - padBottom + 4.dp.toPx())
                    )
                }

                // Un-obscured top caption text inside canvas
                val noteLayout = textMeasurer.measure(
                    text = "Preview Trend (No logs recorded yet)",
                    style = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
                )
                drawText(
                    textLayoutResult = noteLayout,
                    topLeft = Offset((w - noteLayout.size.width) / 2f, 4.dp.toPx())
                )
            }
        }
        return
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val horizontalPadding = 16.dp.toPx()
        val topPadding = 20.dp.toPx()
        val bottomPadding = 24.dp.toPx()
        val usableW = w - horizontalPadding * 2
        val usableH = h - topPadding - bottomPadding

        val n = points.size
        val groupSlotW = usableW / n.coerceAtLeast(1)
        val barW = (groupSlotW * 0.45f).coerceIn(8.dp.toPx(), 20.dp.toPx())
        val cornerR = CornerRadius(4.dp.toPx(), 4.dp.toPx())

        when (filter) {
            FluidStreamFilter.NET_BALANCE -> {
                val maxBalance = maxOf(
                    points.maxOfOrNull { abs(it.balanceMl) } ?: 1000,
                    1200
                ).toFloat() * 1.2f

                points.forEachIndexed { idx, pt ->
                    val groupCenterX = horizontalPadding + idx * groupSlotW + groupSlotW / 2f
                    val balanceVal = pt.balanceMl
                    val barHeight = (abs(balanceVal).toFloat() / maxBalance).coerceIn(0f, 1f) * usableH
                    val barLeft = groupCenterX - barW / 2f

                    // Positive / Optimal surplus is cyan/blue, Negative deficit is amber
                    val barColor = if (balanceVal >= 0) {
                        if (balanceVal in 0..700) Color(0xFF0EA5E9) else Color(0xFF0284C7)
                    } else {
                        Color(0xFFF59E0B)
                    }

                    if (abs(balanceVal) > 0) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(barLeft, h - bottomPadding - barHeight),
                            size = Size(barW, barHeight),
                            cornerRadius = cornerR
                        )
                    }

                    // Value label above bar
                    val valStr = "${if (balanceVal > 0) "+" else ""}$balanceVal"
                    val valLayout = textMeasurer.measure(
                        text = valStr,
                        style = TextStyle(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (balanceVal >= 0) Color(0xFF0284C7) else Color(0xFFD97706)
                        )
                    )
                    drawText(
                        textLayoutResult = valLayout,
                        topLeft = Offset(
                            groupCenterX - (valLayout.size.width / 2f),
                            h - bottomPadding - barHeight - valLayout.size.height - 2.dp.toPx()
                        )
                    )

                    // Day label below bar
                    val dayLayout = textMeasurer.measure(
                        text = pt.dayLabel,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Color(0xFF64748B))
                    )
                    drawText(
                        textLayoutResult = dayLayout,
                        topLeft = Offset(groupCenterX - (dayLayout.size.width / 2f), h - bottomPadding + 6.dp.toPx())
                    )
                }
            }

            FluidStreamFilter.INTAKE_ONLY -> {
                val maxIntake = maxOf(
                    points.maxOfOrNull { it.intakeMl } ?: 3500,
                    intakeTargetMl
                ).toFloat() * 1.15f

                // Dotted Target Line
                val targetY = topPadding + (1f - (intakeTargetMl / maxIntake).coerceIn(0f, 1f)) * usableH
                val targetDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
                drawLine(
                    color = Color(0xFF0EA5E9).copy(alpha = 0.45f),
                    start = Offset(horizontalPadding, targetY),
                    end = Offset(w - horizontalPadding, targetY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = targetDash
                )

                points.forEachIndexed { idx, pt ->
                    val groupCenterX = horizontalPadding + idx * groupSlotW + groupSlotW / 2f
                    val barHeight = (pt.intakeMl.toFloat() / maxIntake).coerceIn(0f, 1f) * usableH
                    val barLeft = groupCenterX - barW / 2f

                    if (pt.intakeMl > 0) {
                        drawRoundRect(
                            color = Color(0xFF0EA5E9),
                            topLeft = Offset(barLeft, h - bottomPadding - barHeight),
                            size = Size(barW, barHeight),
                            cornerRadius = cornerR
                        )
                    }

                    // Value label above bar
                    val valStr = if (pt.intakeMl >= 1000) String.format(Locale.US, "%.1fk", pt.intakeMl / 1000f) else "${pt.intakeMl}"
                    val valLayout = textMeasurer.measure(
                        text = valStr,
                        style = TextStyle(fontSize = 8.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                    )
                    drawText(
                        textLayoutResult = valLayout,
                        topLeft = Offset(
                            groupCenterX - (valLayout.size.width / 2f),
                            h - bottomPadding - barHeight - valLayout.size.height - 2.dp.toPx()
                        )
                    )

                    // Day label below bar
                    val dayLayout = textMeasurer.measure(
                        text = pt.dayLabel,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Color(0xFF64748B))
                    )
                    drawText(
                        textLayoutResult = dayLayout,
                        topLeft = Offset(groupCenterX - (dayLayout.size.width / 2f), h - bottomPadding + 6.dp.toPx())
                    )
                }
            }

            FluidStreamFilter.URINE_ONLY -> {
                val maxUrine = maxOf(
                    points.maxOfOrNull { it.urineMl } ?: 2500,
                    urineTargetMl
                ).toFloat() * 1.15f

                // Dotted Target Line
                val targetY = topPadding + (1f - (urineTargetMl / maxUrine).coerceIn(0f, 1f)) * usableH
                val targetDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
                drawLine(
                    color = Color(0xFFD97706).copy(alpha = 0.45f),
                    start = Offset(horizontalPadding, targetY),
                    end = Offset(w - horizontalPadding, targetY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = targetDash
                )

                points.forEachIndexed { idx, pt ->
                    val groupCenterX = horizontalPadding + idx * groupSlotW + groupSlotW / 2f
                    val barHeight = (pt.urineMl.toFloat() / maxUrine).coerceIn(0f, 1f) * usableH
                    val barLeft = groupCenterX - barW / 2f

                    if (pt.urineMl > 0) {
                        drawRoundRect(
                            color = Color(0xFFF59E0B),
                            topLeft = Offset(barLeft, h - bottomPadding - barHeight),
                            size = Size(barW, barHeight),
                            cornerRadius = cornerR
                        )
                    }

                    // Value label above bar
                    val valStr = if (pt.urineMl >= 1000) String.format(Locale.US, "%.1fk", pt.urineMl / 1000f) else "${pt.urineMl}"
                    val valLayout = textMeasurer.measure(
                        text = valStr,
                        style = TextStyle(fontSize = 8.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD97706))
                    )
                    drawText(
                        textLayoutResult = valLayout,
                        topLeft = Offset(
                            groupCenterX - (valLayout.size.width / 2f),
                            h - bottomPadding - barHeight - valLayout.size.height - 2.dp.toPx()
                        )
                    )

                    // Day label below bar
                    val dayLayout = textMeasurer.measure(
                        text = pt.dayLabel,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Color(0xFF64748B))
                    )
                    drawText(
                        textLayoutResult = dayLayout,
                        topLeft = Offset(groupCenterX - (dayLayout.size.width / 2f), h - bottomPadding + 6.dp.toPx())
                    )
                }
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
            .homeItemShadow(cornerRadius = 18.dp, elevation = 4.dp)
            .clip(RoundedCornerShape(18.dp)),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

private fun formatCardDate(isoDate: String): String {
    return try {
        val parsed = DateTimeUtils.parseIsoDate(isoDate)
        if (parsed != null) {
            val isToday = DateTimeUtils.isToday(isoDate)
            val isYesterday = isoDate == DateTimeUtils.getDaysAgoIso(1)
            val prefix = if (isToday) "Today, " else if (isYesterday) "Yesterday, " else ""
            prefix + java.text.SimpleDateFormat("EEE, d MMM", Locale.US).format(parsed)
        } else isoDate
    } catch (e: Exception) {
        isoDate
    }
}

// ==============================================================
// HYDRO-PILL UI (Dual Volumetric Chambers / Two Buckets)
// ==============================================================
@Composable
fun FluidDayCardHydroPill(
    day: DayOverview,
    intakeTarget: Int,
    urineTarget: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val intakeMl = day.fluidSummary.totalIntakeMl
    val urineMl = day.fluidSummary.totalUrineMl
    val balanceMl = day.fluidSummary.balanceMl
    val isOptimal = balanceMl in -100..700
    val intakeFraction = (intakeMl.toFloat() / intakeTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
    val urineFraction = (urineMl.toFloat() / urineTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
    val dateFormatted = formatCardDate(day.date)

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
            // Header Row: Circular Icon + Date + Count + Net Balance Pill + Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0F7FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon3DWaterDrop(size = 20.dp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dateFormatted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${day.intakeEntries.size} intake • ${day.urineEntries.size} urine logs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isOptimal) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, if (isOptimal) Color(0xFFBBF7D0) else Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${if (balanceMl > 0) "+" else ""}$balanceMl ml",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isOptimal) Color(0xFF16A34A) else Color(0xFFD97706),
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Net",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOptimal) Color(0xFF15803D) else Color(0xFFB45309),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = if (isOptimal) Color(0xFF16A34A) else Color(0xFFD97706),
                            modifier = Modifier.size(width = 5.dp, height = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Volumetric Chambers: Intake & Urine (Two Buckets)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Intake Chamber (Water Bucket)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFF0F9FF), Color(0xFFFAFDFF))
                            )
                        )
                        .border(1.dp, Color(0xFFE0F2FE), RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0284C7))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "INTAKE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "${(intakeFraction * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$intakeMl",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = " / $intakeTarget ml",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sunken Tube with Gloss
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(intakeFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                        )
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .background(Color.White.copy(alpha = 0.40f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        val toGoal = (intakeTarget - intakeMl).coerceAtLeast(0)
                        Text(
                            text = if (toGoal > 0) "$toGoal ml to goal" else "Target achieved",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Urine Chamber (Urine Bucket)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFFBEB), Color(0xFFFFFDF5))
                            )
                        )
                        .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD97706))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "OUTPUT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "${(urineFraction * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$urineMl",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = " / $urineTarget ml",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sunken Tube with Gloss
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(urineFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                                        )
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .background(Color.White.copy(alpha = 0.40f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        val hourlyAvg = (urineMl / 24).coerceAtLeast(1)
                        Text(
                            text = "Avg $hourlyAvg ml / hr",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Diurnal Rhythm + Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rhythm:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0284C7)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFBBF24)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0284C7)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD97706)))
                    }
                }

                Text(
                    text = "Details →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0284C7)
                )
            }
        }
    }
}
