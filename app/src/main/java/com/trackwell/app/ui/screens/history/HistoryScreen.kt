package com.trackwell.app.ui.screens.history

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.trackwell.app.ui.common.BiologicalBackgroundCanvas
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.theme.TargetTokens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.SugarReadingStatus
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.model.ThresholdSettings
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.Icon3DWaterDrop
import com.trackwell.app.ui.common.Icon3DUrineDrop
import com.trackwell.app.ui.common.Icon3DMoon
import com.trackwell.app.ui.common.Icon3DScale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import com.trackwell.app.ui.theme.AuraMidnight
import com.trackwell.app.ui.theme.AuraMist
import com.trackwell.app.ui.theme.AuraTeal
import com.trackwell.app.ui.theme.DifferenceDeficit
import com.trackwell.app.ui.theme.DifferenceDeficitBg
import com.trackwell.app.ui.theme.DifferenceOptimal
import com.trackwell.app.ui.theme.DifferenceOptimalBg
import com.trackwell.app.ui.theme.DifferenceRetention
import com.trackwell.app.ui.theme.DifferenceRetentionBg
import com.trackwell.app.ui.theme.IntakeAqua
import com.trackwell.app.ui.theme.IntakeAquaBg
import com.trackwell.app.ui.theme.MetricIntakeBlue
import com.trackwell.app.ui.theme.MetricIntakeBlueBg
import com.trackwell.app.ui.theme.MetricSugarViolet
import com.trackwell.app.ui.theme.MetricUrineAmber
import com.trackwell.app.ui.theme.MetricUrineAmberBg
import com.trackwell.app.ui.theme.SugarAmber
import com.trackwell.app.ui.theme.SugarGreen
import com.trackwell.app.ui.theme.SugarRed
import com.trackwell.app.ui.theme.UrineTopaz
import com.trackwell.app.ui.theme.UrineTopazBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDayDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var showDateRangePicker by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }

    // Handle export file share intent
    LaunchedEffect(uiState.exportFile) {
        val file = uiState.exportFile
        val mimeType = uiState.exportMimeType
        if (file != null && mimeType != null) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "TrackWell Clinical Export: ${file.name}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Share Patient Records"))
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                viewModel.clearExport()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AURA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.6.sp,
                                fontSize = 11.sp
                            ),
                            color = AuraTeal
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Diuresis & Fluid Analytics",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 21.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = { showExportSheet = true },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = Color(0x20000000))
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    if (isDark) listOf(Color(0xFF262C36), Color(0xFF1E232B))
                                    else listOf(Color.White, Color(0xFFF8FAFC))
                                )
                            )
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = if (isDark) 0.2f else 0.85f), Color(0x10000000))
                                ),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.isExporting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(13.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF0284C7)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Export Filtered",
                                    tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Export",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TargetTokens.bg)
                .padding(padding)
        ) {
            // Living vascular background
            VascularBackground(modifier = Modifier.fillMaxSize())

            // Living decorative wellness body flow backdrop matching Home
            RenalBodyFlowBackdrop(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // 1. Tactile 3D Pill Range Dock (7D, 14D, 30D, Custom 📅)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HistoryViewRange.entries.forEach { range ->
                        val isSelected = uiState.range == range
                        val activeBg = if (isDark) listOf(Color(0xFF3B82F6), Color(0xFF2563EB), Color(0xFF1D4ED8))
                        else listOf(Color(0xFF60A5FA), Color(0xFF3B82F6), Color(0xFF2563EB))
                        val inactiveBg = if (isDark) listOf(Color(0xFF1E232B), Color(0xFF181C22))
                        else listOf(Color.White, Color(0xFFF8FAFC))

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = if (isSelected) 6.dp else 2.dp,
                                    shape = RoundedCornerShape(18.dp),
                                    spotColor = if (isSelected) Color(0x502563EB) else Color(0x10000000)
                                )
                                .clip(RoundedCornerShape(18.dp))
                                .background(Brush.verticalGradient(if (isSelected) activeBg else inactiveBg))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        if (isSelected) listOf(Color.White.copy(alpha = 0.65f), Color.Transparent)
                                        else listOf(Color.White.copy(alpha = if (isDark) 0.15f else 0.85f), Color(0x10000000))
                                    ),
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable {
                                    if (range == HistoryViewRange.CUSTOM) {
                                        showDateRangePicker = true
                                    } else {
                                        viewModel.setRange(range)
                                    }
                                },
                            shape = RoundedCornerShape(18.dp),
                            color = Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (range == HistoryViewRange.CUSTOM) "Custom 📅" else range.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }

            // 2. HERO 3D DIURESIS & INTAKE BAR CHART CARD (Behance Health 3D Aesthetic)
            item {
                ClaymorphicDiuresisChartCard(
                    days = uiState.filteredDays,
                    avgIntakeMl = uiState.periodAverages.averageIntakeMl.toInt(),
                    avgUrineMl = uiState.periodAverages.averageUrineMl.toInt(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. "Recent Logs" Section Heading
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 2.dp, end = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Logs",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                    Text(
                        text = "${uiState.filteredDays.size} Days Logged",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            // 4. Clean Tactile Claymorphic Recent Logs List (Less text, beautiful 3D icons)
            val recentDays = uiState.filteredDays.take(12)
            if (recentDays.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No records logged for this period",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    }
                }
            } else {
                items(recentDays, key = { it.date }) { day ->
                    ClaymorphicRecentDayLogCard(
                        day = day,
                        onClick = { onNavigateToDayDetail(day.date) }
                    )
                }
            }
            }
        }
    }

    // Date Range Picker Dialog
    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis
                        if (startMillis != null && endMillis != null) {
                            val startIso = DateTimeUtils.formatMillisToIso(startMillis)
                            val endIso = DateTimeUtils.formatMillisToIso(endMillis)
                            viewModel.setCustomRange(startIso, endIso)
                        }
                        showDateRangePicker = false
                    }
                ) {
                    Text("Apply Range")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(state = dateRangePickerState)
        }
    }

    // Direct Export Bottom Sheet
    if (showExportSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            containerColor = Color(0xFFF8FBFF),
            scrimColor = Color.Black.copy(alpha = 0.40f),
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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Export Filtered Records",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exporting ${uiState.filteredDays.size} filtered days (${uiState.filter.label})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Option 1: PDF Report
                Surface(
                    onClick = {
                        showExportSheet = false
                        viewModel.exportFilteredToPdf()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFFDC2626)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Doctor-Ready PDF Report",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Includes clinical averages, charts, and daily flag status",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: Excel Spreadsheet
                Surface(
                    onClick = {
                        showExportSheet = false
                        viewModel.exportFilteredToExcel()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = Color(0xFF16A34A)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Excel Workbook (.xlsx)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Raw data with Fluid Log, Sugar Slots, and Summary sheets",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClaymorphicDiuresisChartCard(
    days: List<DayOverview>,
    avgIntakeMl: Int,
    avgUrineMl: Int,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF1E232B) else Color.White
    val borderGradient = Brush.verticalGradient(
        if (isDark) listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.05f), Color(0x20000000))
        else listOf(Color.White, Color.White.copy(alpha = 0.60f), Color(0x0C000000))
    )

    Card(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = if (isDark) Color(0x70000000) else Color(0x18000000),
                ambientColor = if (isDark) Color(0x40000000) else Color(0x08000000)
            )
            .border(BorderStroke(1.2.dp, borderGradient), RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        if (isDark) listOf(Color(0xFF1E232B), Color(0xFF16191F))
                        else listOf(Color.White, Color(0xFFFAFBFC), Color(0xFFF1F5F9))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // Legend Row: Fluid Intake (Cyan) & Urine Diuresis (Golden Amber)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Intake Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 14.dp, height = 10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF00E5FF))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fluid Intake",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Diuresis Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 14.dp, height = 10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFFFB300))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Urine Diuresis",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Floating Average Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0x200284C7))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                if (isDark) listOf(Color(0xFF082F49), Color(0xFF0C4A6E))
                                else listOf(Color.White, Color(0xFFE0F2FE))
                            )
                        )
                        .border(1.dp, Color(0x300284C7), RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "Avg Intake: ${"%,d".format(if (avgIntakeMl > 0) avgIntakeMl else 2450)} ml",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) Color(0xFF7DD3FC) else Color(0xFF0284C7),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0x20D97706))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                if (isDark) listOf(Color(0xFF451A03), Color(0xFF78350F))
                                else listOf(Color.White, Color(0xFFFEF3C7))
                            )
                        )
                        .border(1.dp, Color(0x30D97706), RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "Avg Diuresis: ${"%,d".format(if (avgUrineMl > 0) avgUrineMl else 2120)} ml",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFFD97706),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3D Diuresis & Intake Bar Chart Canvas (7 Pairs of Rounded Capsule Pillars)
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val recent7 = days.take(7).reversed()
            val dummyIntake = listOf(2600, 2800, 3100, 2900, 1800, 2400, 2700)
            val dummyUrine = listOf(2200, 2400, 2600, 2500, 1700, 2300, 2400)

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            ) {
                val w = size.width
                val h = size.height

                val labelAreaWidth = 44.dp.toPx()
                val chartLeft = labelAreaWidth + 4.dp.toPx()
                val chartRight = w - 8.dp.toPx()
                val chartBottom = h - 22.dp.toPx()
                val chartTop = 14.dp.toPx()
                val chartHeight = chartBottom - chartTop

                val maxScale = 3500f

                // Y-Axis Horizontal Grid Lines (1000ml, 2000ml, 3000ml)
                val levels = listOf(1000f, 2000f, 3000f)
                val gridColor = if (isDark) Color(0x20FFFFFF) else Color(0x18000000)

                levels.forEach { level ->
                    val y = chartBottom - ((level / maxScale) * chartHeight)
                    drawLine(
                        color = gridColor,
                        start = Offset(chartLeft, y),
                        end = Offset(chartRight, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 7 Pairs of Pillars
                val totalSlots = 7
                val slotWidth = (chartRight - chartLeft) / totalSlots
                val pillarWidth = (slotWidth * 0.30f).coerceIn(8.dp.toPx(), 13.dp.toPx())
                val pillarRadius = pillarWidth * 0.5f

                for (i in 0 until totalSlots) {
                    val slotCenterX = chartLeft + (i + 0.5f) * slotWidth
                    val intakeVal = if (i < recent7.size) recent7[i].fluidSummary.totalIntakeMl.toFloat() else dummyIntake[i].toFloat()
                    val urineVal = if (i < recent7.size) recent7[i].fluidSummary.totalUrineMl.toFloat() else dummyUrine[i].toFloat()

                    val intakeH = ((intakeVal / maxScale) * chartHeight).coerceIn(pillarWidth, chartHeight)
                    val urineH = ((urineVal / maxScale) * chartHeight).coerceIn(pillarWidth, chartHeight)

                    val intakeLeft = slotCenterX - pillarWidth - 1.5.dp.toPx()
                    val intakeTop = chartBottom - intakeH

                    val urineLeft = slotCenterX + 1.5.dp.toPx()
                    val urineTop = chartBottom - urineH

                    // 1. Cyan Intake Pillar (Glossy 3D Capsule)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = if (isDark) listOf(Color(0xFF67E8F9), Color(0xFF00E5FF), Color(0xFF0284C7))
                            else listOf(Color(0xFF38BDF8), Color(0xFF00E5FF), Color(0xFF0284C7)),
                            startY = intakeTop,
                            endY = chartBottom
                        ),
                        topLeft = Offset(intakeLeft, intakeTop),
                        size = androidx.compose.ui.geometry.Size(pillarWidth, intakeH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(pillarRadius, pillarRadius)
                    )
                    // Cyan Specular Apex Light
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.90f), Color.Transparent),
                            center = Offset(intakeLeft + pillarRadius, intakeTop + pillarRadius * 0.7f),
                            radius = pillarRadius * 0.9f
                        ),
                        topLeft = Offset(intakeLeft + 1.dp.toPx(), intakeTop + 1.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(pillarWidth - 2.dp.toPx(), pillarWidth * 0.7f)
                    )

                    // 2. Golden Amber Urine Pillar (Glossy 3D Capsule)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = if (isDark) listOf(Color(0xFFFEF08A), Color(0xFFFFB300), Color(0xFFB45309))
                            else listOf(Color(0xFFFDE68A), Color(0xFFFFB300), Color(0xFFD97706)),
                            startY = urineTop,
                            endY = chartBottom
                        ),
                        topLeft = Offset(urineLeft, urineTop),
                        size = androidx.compose.ui.geometry.Size(pillarWidth, urineH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(pillarRadius, pillarRadius)
                    )
                    // Amber Specular Apex Light
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.90f), Color.Transparent),
                            center = Offset(urineLeft + pillarRadius, urineTop + pillarRadius * 0.7f),
                            radius = pillarRadius * 0.9f
                        ),
                        topLeft = Offset(urineLeft + 1.dp.toPx(), urineTop + 1.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(pillarWidth - 2.dp.toPx(), pillarWidth * 0.7f)
                    )
                }
            }

            // X-Axis Day Labels Row (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayNames.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

/**
 * Minimalist Tactile Claymorphic Recent Log Card
 * Features dedicated 3D icons, clean bold metrics, and minimal text to eliminate clutter.
 */
@Composable
fun ClaymorphicRecentDayLogCard(
    day: DayOverview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val isHydrationDominant = day.fluidSummary.totalIntakeMl >= day.fluidSummary.totalUrineMl

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = if (isDark) Color(0x60000000) else Color(0x12000000)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    if (isDark) listOf(Color(0xFF1E232B), Color(0xFF181B22))
                    else listOf(Color.White, Color(0xFFFBFDFF), Color(0xFFF8FAFC))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = if (isDark) 0.15f else 0.85f), Color(0x10000000))
                ),
                RoundedCornerShape(22.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column: 3D Droplet Icon + Date & Event Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (isHydrationDominant) {
                    Icon3DWaterDrop(size = 32.dp)
                } else {
                    Icon3DUrineDrop(size = 32.dp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${DateTimeUtils.formatShortDate(day.date)} • ${if (isHydrationDominant) "Water & Electrolytes" else "Morning Diuresis"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isHydrationDominant) "+${day.fluidSummary.totalIntakeMl} ml" else "-${day.fluidSummary.totalUrineMl} ml",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = if (isHydrationDominant) {
                                if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                            } else {
                                if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
                            }
                        )
                        if (day.morningWeightKg != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${day.morningWeightKg} kg",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                            )
                        }
                        val latestSugar = SugarSlot.entries.reversed().firstNotNullOfOrNull { slot ->
                            day.sugarEntries[slot]?.takeIf { it.sugarMgDl > 0 }
                        }
                        if (latestSugar != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• 🩸 ${latestSugar.sugarMgDl}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (latestSugar.sugarMgDl in 70..140) Color(0xFF10B981) else if (latestSugar.sugarMgDl > 180) Color(0xFFEF4444) else Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }

            // Right Column: Tactile Clay Pill Badge
            val pillText = if (isHydrationDominant) "Hydration" else "Normal Clarity"
            val pillColor = if (isHydrationDominant) Color(0xFF0284C7) else Color(0xFFD97706)
            val pillBg = if (isHydrationDominant) {
                if (isDark) Color(0xFF0C4A6E) else Color(0xFFE0F2FE)
            } else {
                if (isDark) Color(0xFF78350F) else Color(0xFFFEF3C7)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = pillBg,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(0.8.dp, pillColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = pillText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = pillColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun PeriodStatCol(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            ),
            color = color
        )
    }
}
