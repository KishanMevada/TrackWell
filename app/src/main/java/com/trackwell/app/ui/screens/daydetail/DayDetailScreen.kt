package com.trackwell.app.ui.screens.daydetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.Icon3DUrineDrop
import com.trackwell.app.ui.common.Icon3DWaterDrop
import com.trackwell.app.ui.home.components.BloodSugarCard
import com.trackwell.app.ui.home.components.BloodSugarPoint
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.home.components.homeItemShadow
import com.trackwell.app.ui.screens.today.components.SugarEntryBottomSheet
import com.trackwell.app.ui.theme.TargetTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    viewModel: DayDetailViewModel,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val overview = uiState.dayOverview
    val context = LocalContext.current
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var showSugarSheet by remember { mutableStateOf(false) }
    var activeSugarSlot by remember { mutableStateOf(SugarSlot.BEFORE_BREAKFAST) }

    // 4 standard blood sugar slots for the day
    val daySugarPoints = remember(overview.sugarEntries) {
        val entries = overview.sugarEntries
        listOf(
            BloodSugarPoint("8 AM", entries[SugarSlot.BEFORE_BREAKFAST]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.BEFORE_BREAKFAST),
            BloodSugarPoint("12 PM", entries[SugarSlot.BEFORE_LUNCH]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.BEFORE_LUNCH),
            BloodSugarPoint("4 PM", entries[SugarSlot.AFTER_LUNCH]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.AFTER_LUNCH),
            BloodSugarPoint("8 PM", entries[SugarSlot.BEFORE_DINNER]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.BEFORE_DINNER)
        )
    }

    val latestSugar = daySugarPoints.lastOrNull { it.valueMgDl != null && it.valueMgDl > 0 }
    val isAnySugarLogged = latestSugar != null
    val sugarStatus = when {
        latestSugar?.valueMgDl == null -> null
        latestSugar.valueMgDl > 140 -> "High"
        latestSugar.valueMgDl < 70 -> "Low"
        else -> "In Range"
    }
    val suggestedSlot = SugarSlot.suggestCurrentSlot()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        // Living vascular background matching Home & Fluid History
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

            // 2. Top Action Bar: Clean Title & Subtitle (Removed "AURA")
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
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

                    Column {
                        Text(
                            text = "Day Breakdown",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.4).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = DateTimeUtils.formatDisplayDate(uiState.date),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 3. Hero Card: Hydro-Pill UI with Detailed Clinical Analysis
            item {
                DayDetailHydroHeroCard(
                    overview = overview,
                    intakeTarget = uiState.appSettings.intakeTargetMl,
                    urineTarget = uiState.appSettings.urineTargetMl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }

            // 4. Section Label: Metabolic Rhythm
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Metabolic Rhythm",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 5. Blood Sugar Interactive Card (Matching Dashboard Graph & Interactive 4-Dot System)
            item {
                BloodSugarCard(
                    isLoggedToday = isAnySugarLogged,
                    currentValueMgDl = latestSugar?.valueMgDl,
                    status = sugarStatus,
                    todayPoints = daySugarPoints,
                    priorPoints = emptyList(),
                    priorAvgMgDl = null,
                    suggestedSlot = suggestedSlot,
                    onViewHistory = {
                        // Keep on screen or perform haptic
                        HapticFeedbackHelper.performTickHaptic(context)
                    },
                    onLogClick = {
                        HapticFeedbackHelper.performClickHaptic(context)
                        activeSugarSlot = suggestedSlot
                        showSugarSheet = true
                    },
                    onSlotClick = { slot ->
                        HapticFeedbackHelper.performClickHaptic(context)
                        activeSugarSlot = slot
                        showSugarSheet = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))
            }

            // 6. Section Header: Dual Stream Intake vs Urine Output
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Logged Streams",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${overview.intakeEntries.size} intake • ${overview.urineEntries.size} urine",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 7. Side-by-Side Dual-Stream Columns: Left = Water Intake Only, Right = Urine Output Only
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column: Water Intake Only
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Water Header Pill
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFE0F2FE),
                            border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon3DWaterDrop(size = 16.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Water",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                                Text(
                                    text = "${overview.fluidSummary.totalIntakeMl} ml",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }

                        if (overview.intakeEntries.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = "No water logs",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            overview.intakeEntries.sortedBy { it.timestamp }.forEach { entry ->
                                FluidLogSplitCard(
                                    entry = entry,
                                    isWater = true,
                                    onDelete = {
                                        HapticFeedbackHelper.performClickHaptic(context)
                                        viewModel.deleteFluidEntry(entry.id)
                                    }
                                )
                            }
                        }
                    }

                    // Right Column: Urine Output Only
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Urine Header Pill
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon3DUrineDrop(size = 16.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Urine",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                                Text(
                                    text = "${overview.fluidSummary.totalUrineMl} ml",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        if (overview.urineEntries.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = "No urine logs",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            overview.urineEntries.sortedBy { it.timestamp }.forEach { entry ->
                                FluidLogSplitCard(
                                    entry = entry,
                                    isWater = false,
                                    onDelete = {
                                        HapticFeedbackHelper.performClickHaptic(context)
                                        viewModel.deleteFluidEntry(entry.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Spacing for Floating Navigation Bar
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // Sugar Entry Bottom Sheet
    if (showSugarSheet) {
        val currentEntry = overview.sugarEntries[activeSugarSlot]
        SugarEntryBottomSheet(
            initialSlot = activeSugarSlot,
            existingEntry = currentEntry,
            thresholds = uiState.appSettings.thresholdSettings,
            onDismiss = { showSugarSheet = false },
            onSave = { slot, reading, dose, timestamp, note ->
                viewModel.logSugar(slot, reading, dose, timestamp, note)
                showSugarSheet = false
            },
            onDelete = { id ->
                viewModel.deleteSugarEntry(id)
                showSugarSheet = false
            }
        )
    }
}

/**
 * Hero Card utilizing the exact Hydro-Pill UI (Image 5) with expanded clinical diuresis analysis.
 */
@Composable
fun DayDetailHydroHeroCard(
    overview: DayOverview,
    intakeTarget: Int,
    urineTarget: Int,
    modifier: Modifier = Modifier
) {
    val intakeMl = overview.fluidSummary.totalIntakeMl
    val urineMl = overview.fluidSummary.totalUrineMl
    val balanceMl = overview.fluidSummary.balanceMl
    val isOptimal = balanceMl in -100..700
    val intakeFraction = (intakeMl.toFloat() / intakeTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
    val urineFraction = (urineMl.toFloat() / urineTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
    val dateFormatted = try {
        val parsed = DateTimeUtils.parseIsoDate(overview.date)
        if (parsed != null) java.text.SimpleDateFormat("EEEE, d MMMM yyyy", java.util.Locale.US).format(parsed)
        else overview.date
    } catch (e: Exception) {
        overview.date
    }

    val (statusLabel, statusColor, statusBg, statusBorder) = when {
        balanceMl < -100 -> Quadruple("Negative Deficit", Color(0xFFEF4444), Color(0xFFFEE2E2), Color(0xFFFECACA))
        balanceMl in -100..700 -> Quadruple("Optimal Balance", Color(0xFF16A34A), Color(0xFFDCFCE7), Color(0xFFBBF7D0))
        else -> Quadruple("Fluid Retention", Color(0xFFD97706), Color(0xFFFEF3C7), Color(0xFFFDE68A))
    }

    Surface(
        modifier = modifier
            .homeCardShadow(cornerRadius = 24.dp, elevation = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        color = Color.White,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: 3D Water Icon + Date + Net Balance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F7FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon3DWaterDrop(size = 22.dp)
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
                        Text(
                            text = "${overview.intakeEntries.size} intake • ${overview.urineEntries.size} urine logs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusBorder)
                ) {
                    Text(
                        text = "${if (balanceMl > 0) "+" else ""}$balanceMl ml",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
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

            Spacer(modifier = Modifier.height(16.dp))

            // Expanded Clinical Analysis Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusBg,
                            border = BorderStroke(1.dp, statusBorder)
                        ) {
                            Text(
                                text = statusLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        val hourlyAvg = (urineMl / 24).coerceAtLeast(0)
                        Text(
                            text = "Diuresis Rate: $hourlyAvg ml/hr",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val insightNarrative = when {
                        isOptimal -> "Your daily diuresis is well-balanced with intake (+0 to +600 ml). Optimal graft perfusion and tubular clearance achieved."
                        balanceMl < 0 -> "Urine output exceeded fluid intake. Ensure adequate hydration throughout the day to protect renal allograft perfusion."
                        else -> "Fluid retention observed (intake significantly exceeded excretion). Check morning weight and monitor blood pressure."
                    }

                    Text(
                        text = insightNarrative,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        }
    }
}

/**
 * Tactile claymorphic card for an individual fluid entry in the dual-stream split column.
 */
@Composable
fun FluidLogSplitCard(
    entry: FluidEntry,
    isWater: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = if (isWater) Color(0xFF0284C7) else Color(0xFFD97706)
    val timeFormatted = DateTimeUtils.formatTime(entry.timestamp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .homeItemShadow(cornerRadius = 16.dp, elevation = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${entry.amountMl} ml",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444).copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = timeFormatted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )

            if (!entry.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.note,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 2
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
