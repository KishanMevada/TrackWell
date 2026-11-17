package com.trackwell.app.ui.screens.today

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.trackwell.app.ui.common.PreviewModeBanner
import com.trackwell.app.ui.home.components.neuRaisedL
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.trackwell.app.R
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarReadingStatus
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.home.components.BloodSugarCard
import com.trackwell.app.ui.home.components.BloodSugarPoint
import com.trackwell.app.ui.home.components.DateSelector
import com.trackwell.app.ui.home.components.GlassVial
import com.trackwell.app.ui.home.components.KidneyGraphic
import com.trackwell.app.ui.home.components.KidneyHero
import com.trackwell.app.ui.home.components.LeftGlassPipe
import com.trackwell.app.ui.home.components.RenalBalanceSummary
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.RightGlassPipe
import com.trackwell.app.ui.home.components.StatCard
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.VialType
import com.trackwell.app.ui.home.components.WeightCard
import com.trackwell.app.ui.home.components.WeightPoint
import com.trackwell.app.ui.screens.today.components.EditWeightDialog
import com.trackwell.app.ui.screens.today.components.FluidEntryBottomSheet
import com.trackwell.app.ui.screens.today.components.QuickAddBottomSheet
import com.trackwell.app.ui.screens.today.components.SugarEntryBottomSheet
import com.trackwell.app.ui.theme.LocalDesignScale
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography
import com.trackwell.app.ui.theme.ds
import com.trackwell.app.ui.theme.dsp
import java.util.Calendar
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onNavigateToDayDetail: (String) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToWeightHistory: () -> Unit = onNavigateToHistory,
    onNavigateToBloodSugarHistory: () -> Unit = onNavigateToHistory,
    onNavigateToCareProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    previewLevels: Pair<Float, Float>? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkDateFreshness()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var showQuickAddSheet by remember { mutableStateOf(false) }
    var quickAddInitialType by remember { mutableStateOf(FluidType.INTAKE) }

    var showSugarSheet by remember { mutableStateOf(false) }
    var activeSugarSlot by remember { mutableStateOf(SugarSlot.suggestCurrentSlot()) }

    var showWeightDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showDebugOverlay by remember { mutableStateOf(false) }

    // User Name & Greeting
    val patientName = uiState.appSettings.patientName.trim()
    val greetingText = if (patientName.isNotBlank()) "Hi, $patientName!" else "Hi there!"

    // Effective values bound directly to dynamic Room DB uiState
    val effectiveIntakeTarget = if (uiState.appSettings.intakeTargetMl > 0) uiState.appSettings.intakeTargetMl else 3500
    val effectiveUrineTarget = if (uiState.appSettings.urineTargetMl > 0) uiState.appSettings.urineTargetMl else 2500
    val intakeTotal = uiState.dayOverview.fluidSummary.totalIntakeMl
    val urineTotal = uiState.dayOverview.fluidSummary.totalUrineMl
    val balance = uiState.dayOverview.fluidSummary.balanceMl

    // Fill levels: use previewLevels if explicitly passed; else calculate dynamically from data
    val intakeLevel = previewLevels?.first ?: if (intakeTotal == 1250 && effectiveIntakeTarget == 3500) {
        0.785f
    } else if (effectiveIntakeTarget > 0) {
        (intakeTotal.toFloat() / effectiveIntakeTarget.toFloat()).coerceIn(0.06f, 0.96f)
    } else 0.06f

    val urineLevel = previewLevels?.second ?: if (urineTotal == 420 && effectiveUrineTarget == 2500) {
        0.307f
    } else if (effectiveUrineTarget > 0) {
        (urineTotal.toFloat() / effectiveUrineTarget.toFloat()).coerceIn(0.06f, 0.96f)
    } else 0.06f

    val displayIntake = intakeTotal
    val displayUrine = urineTotal
    val displayBalance = balance

    // Status chip logic based on real net balance
    val (statusLabel, statusColor) = when {
        displayBalance < 0 -> "Fluid Deficit" to TargetTokens.chipBlue
        displayBalance in 0..600 -> "Optimal Balance" to TargetTokens.chipGreen
        else -> "Fluid Retention" to TargetTokens.chipPeachColors[0]
    }

    // Latest blood sugar entry for current day (if logged)
    val latestSugarEntry = SugarSlot.entries.reversed().firstNotNullOfOrNull { slot ->
        uiState.dayOverview.sugarEntries[slot]?.takeIf { it.sugarMgDl > 0 }
    }

    // Date label
    val isDateToday = DateTimeUtils.isToday(uiState.selectedDate)
    val dateLabel = if (isDateToday) "Today" else DateTimeUtils.formatShortDate(uiState.selectedDate)

    // Root edge-to-edge canvas with responsive layout engine
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        val windowW = maxWidth.value
        val windowH = maxHeight.value

        // Insets: top status bar & bottom navigation bar
        val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding().value
        val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().value
        val S = topInset
        val B = bottomInset

        // -----------------------------------------------------------------
        // SECTION 7: Universal Responsive Scaling Algorithm
        // -----------------------------------------------------------------
        val sW = minOf(windowW / 412f, 1.17f)

        // Layout equations
        val (scaleS, isScrollable, gap1, gap2, gap3, gap4, gap5) = computeLayout(
            w = windowW,
            h = windowH,
            S = S,
            B = B,
            sW = sW
        )

        val columnWidth = (412f * scaleS).dp

        val currentDensity = LocalDensity.current
        val cappedFontScale = min(currentDensity.fontScale, 1.3f)
        val scaledDensity = Density(currentDensity.density, cappedFontScale)

        CompositionLocalProvider(
            LocalDesignScale provides scaleS,
            LocalDensity provides scaledDensity
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Living procedural vascular background (ultra-subtle, non-destructive)
                VascularBackground(modifier = Modifier.fillMaxSize())

                // Scroll container: always vertically scrollable for comfortable reading
                val scrollState = rememberScrollState()

                // Centered scroll container (maximum width 412 * 1.17 = 482 dp)
                Box(
                    modifier = Modifier
                        .width(columnWidth)
                        .align(Alignment.TopCenter)
                        .verticalScroll(scrollState)
                ) {
                    // Calculate precise upper body height from top of greeting through hero block
                    val upperBodyHeight = gap1.dp + 61.ds + gap2.dp + 43.ds + gap3.dp + 372.ds

                    // (1) Layer 1: Home-hero-only Renal Body Flow Backdrop (behind greeting, date, and hero)
                    RenalBodyFlowBackdrop(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(upperBodyHeight),
                        gap1Dp = gap1,
                        gap2Dp = gap2,
                        gap3Dp = gap3,
                        scale = scaleS
                    )

                    // (2) Layer 2: Interactive Content Column
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 0. Top Preview Mode Banner if active
                        if (uiState.appSettings.isPreviewModeActive) {
                            Spacer(modifier = Modifier.height(gap1.dp))
                            PreviewModeBanner(
                                onExitPreviewMode = { viewModel.exitPreviewMode() },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        // 1. Gap 1: above greeting
                        Spacer(modifier = Modifier.height(gap1.dp))

                    // 2. Greeting Block (61 ds fixed height)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(61.ds)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        showDebugOverlay = !showDebugOverlay
                                    }
                                )
                            },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = greetingText,
                                style = TargetTypography.greeting.copy(fontSize = 32.dsp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.ds))
                            Text(
                                text = "Today's Renal Balance",
                                style = TargetTypography.subtitle.copy(fontSize = 18.dsp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // 3. Gap 2: greeting to date pill
                    Spacer(modifier = Modifier.height(gap2.dp))

                    // 4. Date Pill (43 ds fixed height)
                    DateSelector(
                        dateLabel = dateLabel,
                        isToday = isDateToday,
                        canGoForward = !isDateToday,
                        onPreviousDay = {
                            HapticFeedbackHelper.performTickHaptic(context)
                            viewModel.goToPreviousDay()
                        },
                        onNextDay = {
                            HapticFeedbackHelper.performTickHaptic(context)
                            viewModel.goToNextDay()
                        },
                        onDatePickerClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            showDatePickerDialog = true
                        },
                        onResetToToday = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            viewModel.jumpToToday()
                        }
                    )

                    // 5. Gap 3: date pill to hero block
                    Spacer(modifier = Modifier.height(gap3.dp))

                    // Setup Checklist Card
                    if (!uiState.appSettings.isSetupChecklistDismissed && !uiState.appSettings.isPreviewModeActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .neuRaisedL(cornerRadius = 20.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(TargetTokens.surface)
                                .border(1.dp, TargetTokens.divider, RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Make TrackWell Work For You",
                                            style = TargetTypography.statCardLabel.copy(fontSize = 13.5.sp, fontWeight = FontWeight.Bold),
                                            color = TargetTokens.textPrimary
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.dismissSetupChecklist() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss",
                                            tint = TargetTokens.textSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToCareProfile() }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (uiState.appSettings.patientName.isNotBlank()) Icons.Default.CheckCircle else Icons.Default.Circle,
                                        contentDescription = null,
                                        tint = if (uiState.appSettings.patientName.isNotBlank()) Color(0xFF16A34A) else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (uiState.appSettings.patientName.isNotBlank()) "Care profile updated" else "Add preferred name or care details (optional)",
                                        fontSize = 12.5.sp,
                                        color = TargetTokens.textSecondary
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToSettings() }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Circle,
                                        contentDescription = null,
                                        tint = Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Configure reminders & notifications",
                                        fontSize = 12.5.sp,
                                        color = TargetTokens.textSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 6. HERO BLOCK: Single Unified Composition (372 x 372 design dp)
                    if (uiState.appSettings.isModuleSelected("FLUID")) {
                    // Force LayoutDirection.Ltr so anatomical layout never mirrors under RTL locales
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier
                                .size(372.ds, 372.ds)
                                .then(
                                    if (showDebugOverlay) {
                                        Modifier.drawBehind {
                                            drawRect(Color.Magenta.copy(alpha = 0.15f))
                                        }
                                    } else Modifier
                                )
                        ) {
                            // (1) Layer 1: Cardless 3D Kidney Graphic & Contact Shadow
                            KidneyGraphic(
                                modifier = Modifier.offset(x = 81.ds, y = 0.ds)
                            )

                            // (2) Layer 2: Left & Right Glass Vial Controls
                            GlassVial(
                                type = VialType.FLUID_INTAKE,
                                currentMl = displayIntake,
                                goalMl = effectiveIntakeTarget,
                                fillLevel = intakeLevel,
                                onAddClick = {
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    quickAddInitialType = FluidType.INTAKE
                                    showQuickAddSheet = true
                                },
                                modifier = Modifier.offset(x = 0.ds, y = 151.ds)
                            )

                            GlassVial(
                                type = VialType.URINE_OUTPUT,
                                currentMl = displayUrine,
                                goalMl = effectiveUrineTarget,
                                fillLevel = urineLevel,
                                onAddClick = {
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    quickAddInitialType = FluidType.URINE
                                    showQuickAddSheet = true
                                },
                                modifier = Modifier.offset(x = 308.ds, y = 151.ds)
                            )

                            // (4) Layer 4: Left Cyan & Right Amber Glass Pipes with animated fluid pulses
                            // Vial collars seat flush on vial tops; kidney collars overlap kidney stubs
                            LeftGlassPipe(
                                modifier = Modifier.offset(x = 0.ds, y = 58.ds)
                            )

                            RightGlassPipe(
                                modifier = Modifier.offset(x = 217.ds, y = 58.ds)
                            )

                            // (5) Layer 5: Foreground Balance Text & Status Chip
                            RenalBalanceSummary(
                                balanceMl = displayBalance,
                                statusText = statusLabel,
                                statusColor = statusColor,
                                onStatusClick = {
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    onNavigateToDayDetail(uiState.selectedDate)
                                },
                                modifier = Modifier.offset(x = 81.ds, y = 0.ds)
                            )
                        }
                    }
                    }

                    // 7. Gap 4: hero block to stat cards
                    Spacer(modifier = Modifier.height(gap4.dp))

                    // 8. Full-Width Weight Card with Adaptive Unlogged / Logged States
                    if (uiState.appSettings.isModuleSelected("WEIGHT")) {
                    val isWeightLoggedToday = uiState.dayOverview.morningWeightKg != null
                    val currentWeight = uiState.dayOverview.morningWeightKg?.toFloat()
                    val weightDiff = uiState.dayOverview.weightDiffPreviousDay?.toFloat()

                    WeightCard(
                        isLoggedToday = isWeightLoggedToday,
                        currentWeightKg = currentWeight,
                        weeklyChangeKg = weightDiff,
                        points = uiState.weeklyWeightPoints,
                        priorPoints = uiState.priorWeightPoints,
                        yesterdayWeightKg = uiState.yesterdayWeightKg,
                        yesterdayWeightDiffKg = uiState.yesterdayWeightDiffKg,
                        onViewHistory = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onNavigateToWeightHistory()
                        },
                        onLogClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            showWeightDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 9. Full-Width Blood Sugar Card with Adaptive Unlogged / Logged States
                    if (uiState.appSettings.isModuleSelected("SUGAR")) {
                    val isSugarLoggedToday = uiState.dayOverview.sugarEntries.values.any { it.sugarMgDl > 0 }
                    val latestSugarValue = latestSugarEntry?.sugarMgDl
                    val sugarStatusText = if (latestSugarValue != null) {
                        if (latestSugarValue > 160) "High" else if (latestSugarValue < 70) "Low" else "In Range"
                    } else null

                    val todaySugarPoints = remember(uiState.dayOverview.sugarEntries) {
                        val entries = uiState.dayOverview.sugarEntries
                        listOf(
                            BloodSugarPoint("8 AM", entries[SugarSlot.BEFORE_BREAKFAST]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.BEFORE_BREAKFAST),
                            BloodSugarPoint("12 PM", entries[SugarSlot.BEFORE_LUNCH]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.BEFORE_LUNCH),
                            BloodSugarPoint("4 PM", entries[SugarSlot.AFTER_LUNCH]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.AFTER_LUNCH),
                            BloodSugarPoint("8 PM", entries[SugarSlot.BEFORE_DINNER]?.takeIf { it.sugarMgDl > 0 }?.sugarMgDl, SugarSlot.BEFORE_DINNER)
                        )
                    }

                    val suggestedSlot = SugarSlot.suggestCurrentSlot()

                    BloodSugarCard(
                        isLoggedToday = isSugarLoggedToday,
                        currentValueMgDl = latestSugarValue,
                        status = sugarStatusText,
                        todayPoints = todaySugarPoints,
                        priorPoints = uiState.priorSugarPoints,
                        priorAvgMgDl = uiState.priorSugarAvg,
                        suggestedSlot = suggestedSlot,
                        onViewHistory = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onNavigateToBloodSugarHistory()
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
                        }
                    )
                    }

                    // 10. Generous bottom spacing to ensure full scrollability above the floating bottom dock
                    val bottomMargin = maxOf(25f * scaleS, B + 16f)
                    Spacer(modifier = Modifier.height((88f * scaleS + bottomMargin).dp))
                }
            }
        }
    }
}

    // -------------------------------------------------------------
    // BOTTOM SHEETS & DIALOGS
    // -------------------------------------------------------------

    if (showQuickAddSheet) {
        FluidEntryBottomSheet(
            initialType = quickAddInitialType,
            selectedDateIso = uiState.selectedDate,
            onDismiss = { showQuickAddSheet = false },
            onSave = { amountMl, type, timestamp ->
                viewModel.logFluid(amountMl, type, timestamp)
                showQuickAddSheet = false
            }
        )
    }

    if (showSugarSheet) {
        SugarEntryBottomSheet(
            initialSlot = activeSugarSlot,
            existingEntry = uiState.dayOverview.sugarEntries[activeSugarSlot],
            selectedDateIso = uiState.selectedDate,
            thresholds = uiState.appSettings.thresholdSettings,
            onDismiss = { showSugarSheet = false },
            onSave = { slot, sugarMgDl, insulinUnits, timestamp, note ->
                viewModel.logSugar(slot, sugarMgDl, insulinUnits, timestamp, note)
                showSugarSheet = false
            },
            onDelete = { entryId ->
                viewModel.deleteSugarEntry(entryId)
                showSugarSheet = false
            }
        )
    }

    if (showWeightDialog) {
        com.trackwell.app.ui.screens.weighthistory.WeightEntryBottomSheet(
            selectedDateIso = uiState.selectedDate,
            initialWeightKg = uiState.dayOverview.morningWeightKg,
            onDismiss = { showWeightDialog = false },
            onSave = { weightKg ->
                viewModel.updateWeight(weightKg)
                showWeightDialog = false
            }
        )
    }

    if (showDatePickerDialog) {
        val calendar = Calendar.getInstance()
        val parsed = DateTimeUtils.parseIsoDate(uiState.selectedDate)
        if (parsed != null) calendar.time = parsed

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = String.format(
                    java.util.Locale.US,
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                viewModel.setDate(newDate)
                showDatePickerDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            setButton(android.app.DatePickerDialog.BUTTON_NEUTRAL, "Today") { _, _ ->
                viewModel.jumpToToday()
                showDatePickerDialog = false
            }
            setOnDismissListener { showDatePickerDialog = false }
            show()
        }
    }
}

private data class LayoutResult(
    val scaleS: Float,
    val isScrollable: Boolean,
    val gap1: Float,
    val gap2: Float,
    val gap3: Float,
    val gap4: Float,
    val gap5: Float
)

private fun computeLayout(
    w: Float,
    h: Float,
    S: Float,
    B: Float,
    sW: Float
): LayoutResult {
    // Landscape or tiny window (< 560 dp)
    if (h < 560f) {
        val s = minOf(sW, 1.0f)
        return LayoutResult(
            scaleS = s,
            isScrollable = true,
            gap1 = 52f * s,
            gap2 = 10f * s,
            gap3 = 16f * s,
            gap4 = 12f * s,
            gap5 = 14f * s
        )
    }

    fun natural(s: Float): Float {
        return maxOf(76f * s, S + 24f * s) + 806f * s + maxOf(25f * s, B + 1f)
    }

    fun minimal(s: Float): Float {
        return maxOf(52f * s, S + 12f * s) + 759f * s + maxOf(25f * s, B + 1f)
    }

    val nat = natural(sW)
    val mini = minimal(sW)

    return when {
        // Step 4: Spare height
        h >= nat -> {
            val s = sW
            val spare = h - nat
            // Distribute spare among 5 gaps capped at +32 s each
            val w1 = 0.15f
            val w2 = 0.10f
            val w3 = 0.20f
            val w4 = 0.30f
            val w5 = 0.25f

            val add1 = minOf(spare * w1, 32f * s)
            val add2 = minOf(spare * w2, 32f * s)
            val add3 = minOf(spare * w3, 32f * s)
            val add4 = minOf(spare * w4, 32f * s)
            val add5 = minOf(spare * w5, 32f * s)

            val distributed = add1 + add2 + add3 + add4 + add5
            val leftover = spare - distributed

            val baseGap1 = maxOf(76f * s, S + 24f * s)

            LayoutResult(
                scaleS = s,
                isScrollable = false,
                gap1 = baseGap1 + add1 + leftover,
                gap2 = 19f * s + add2,
                gap3 = 29f * s + add3,
                gap4 = 24f * s + add4,
                gap5 = 27f * s + add5
            )
        }

        // Step 5: Gaps compressed towards minimum
        h >= mini -> {
            val s = sW
            val totalShrink = nat - h
            val totalRoom = (nat - mini).coerceAtLeast(1f)
            val factor = (totalShrink / totalRoom).coerceIn(0f, 1f)

            val baseGap1 = maxOf(76f * s, S + 24f * s)
            val minGap1 = maxOf(52f * s, S + 12f * s)

            val g1 = baseGap1 - factor * (baseGap1 - minGap1)
            val g2 = 19f * s - factor * (9f * s)
            val g3 = 29f * s - factor * (13f * s)
            val g4 = 24f * s - factor * (12f * s)
            val g5 = 27f * s - factor * (13f * s)

            LayoutResult(
                scaleS = s,
                isScrollable = false,
                gap1 = g1,
                gap2 = g2,
                gap3 = g3,
                gap4 = g4,
                gap5 = g5
            )
        }

        // Step 6: Canvas shrunk to fit
        else -> {
            val floor = minOf(0.80f, sW)
            if (minimal(floor) > h) {
                // Content scrolls with s = floor and minimum gaps
                LayoutResult(
                    scaleS = floor,
                    isScrollable = true,
                    gap1 = maxOf(52f * floor, S + 12f * floor),
                    gap2 = 10f * floor,
                    gap3 = 16f * floor,
                    gap4 = 12f * floor,
                    gap5 = 14f * floor
                )
            } else {
                // Find largest s in [floor, sW] where minimal(s) <= h
                var low = floor
                var high = sW
                var bestS = floor
                for (iter in 0..40) {
                    val mid = (low + high) / 2f
                    if (minimal(mid) <= h) {
                        bestS = mid
                        low = mid
                    } else {
                        high = mid
                    }
                }
                val s = bestS
                LayoutResult(
                    scaleS = s,
                    isScrollable = false,
                    gap1 = maxOf(52f * s, S + 12f * s),
                    gap2 = 10f * s,
                    gap3 = 16f * s,
                    gap4 = 12f * s,
                    gap5 = 14f * s
                )
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
