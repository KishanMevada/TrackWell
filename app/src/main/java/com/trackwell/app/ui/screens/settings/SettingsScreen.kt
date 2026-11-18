package com.trackwell.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.domain.model.ThresholdSettings
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.theme.InterFontFamily
import com.trackwell.app.ui.theme.TargetTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToImport: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToCareProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var showThresholdDialog by remember { mutableStateOf(false) }
    var showGeminiDialog by remember { mutableStateOf(false) }
    var showReseedDialog by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showAdvancedAccordion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.seedMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        // Living vascular background matching Home, Medicines, and History
        VascularBackground(modifier = Modifier.fillMaxSize())

        // Living decorative wellness body flow backdrop matching Home
        RenalBodyFlowBackdrop(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status bar spacer - exact match with Medication
                item {
                    Spacer(modifier = Modifier.height(statusBarInset + 8.dp))
                }

                // 1. Actionbar Header: Exact match with Medication (MedicineScreen)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Settings",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.4).sp,
                                fontFamily = InterFontFamily
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Preferences & Targets",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B),
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }

                // 2. Profile Card (3D Claymorphic)
                item {
                    ClaymorphicProfileCard(
                        userName = if (settings.patientName.isNotBlank()) settings.patientName else "User Profile",
                        subtitle = if (settings.healthContext.isNotBlank()) settings.healthContext else "Tap to edit profile & care details",
                        onEditProfile = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onNavigateToCareProfile()
                        }
                    )
                }

                // 3. Section: Targets & Goals
                item {
                    ClaymorphicSectionHeader(title = "Targets & Goals")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaymorphicCardGroup {
                        val t = settings.thresholdSettings
                        ClaymorphicSettingsRow(
                            title = "Fluid Balance Targets",
                            value = "${settings.intakeTargetMl} ml in / ${settings.urineTargetMl} ml out",
                            icon = Icons.Default.WaterDrop,
                            iconBg = Color(0xFFE0F2FE),
                            iconTint = Color(0xFF0284C7),
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                showTargetDialog = true
                            }
                        )
                        ClaymorphicDivider()
                        ClaymorphicSettingsRow(
                            title = "Blood Sugar Safe Target",
                            value = "${t.targetMin}–${t.targetMax} mg/dL",
                            icon = Icons.Default.Bloodtype,
                            iconBg = Color(0xFFFEE2E2),
                            iconTint = Color(0xFFEF4444),
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                showThresholdDialog = true
                            }
                        )
                        ClaymorphicDivider()
                        ClaymorphicSettingsRow(
                            title = "Quick Log Presets",
                            value = "${settings.quickPresetsMl} ml",
                            icon = Icons.Default.LocalDrink,
                            iconBg = Color(0xFFFEF3C7),
                            iconTint = Color(0xFFD97706),
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                showPresetsDialog = true
                            }
                        )
                    }
                }

                // 3. Section: Security & Data Hub
                item {
                    ClaymorphicSectionHeader(title = "Security & Data Hub")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaymorphicCardGroup {
                        ClaymorphicSwitchRow(
                            title = "Biometric Lock",
                            subtitle = "Require fingerprint or face ID to open TrackWell",
                            icon = Icons.Default.Fingerprint,
                            iconBg = Color(0xFFDCFCE7),
                            iconTint = Color(0xFF16A34A),
                            checked = settings.isBiometricEnabled,
                            onCheckedChange = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                viewModel.updateBiometricLock(it)
                            }
                        )
                        ClaymorphicDivider()
                        ClaymorphicSettingsRow(
                            title = "Export Reports, FHIR R4 & Cloud Sync",
                            value = "PDF / Excel / Drive / FHIR",
                            icon = Icons.Default.Description,
                            iconBg = Color(0xFFECFDF5),
                            iconTint = Color(0xFF059669),
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onNavigateToExport()
                            }
                        )
                        ClaymorphicDivider()
                        ClaymorphicSettingsRow(
                            title = "Import Routine Data",
                            value = "OCR Scan, CSV, Excel",
                            icon = Icons.Default.FileUpload,
                            iconBg = Color(0xFFEEF2FF),
                            iconTint = Color(0xFF4F46E5),
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                onNavigateToImport()
                            }
                        )
                    }
                }

                // 4. Section: Advanced & Diagnostics Hub
                item {
                    ClaymorphicSectionHeader(title = "Advanced & Diagnostics")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaymorphicCardGroup {
                        ClaymorphicSettingsRow(
                            title = if (showAdvancedAccordion) "Hide Advanced Settings" else "Show Advanced & AI Settings",
                            value = if (showAdvancedAccordion) "Tap to collapse" else "Gemini AI, Developer Tools",
                            icon = Icons.Default.AutoAwesome,
                            iconBg = Color(0xFFF3E8FF),
                            iconTint = Color(0xFF9333EA),
                            onClick = {
                                HapticFeedbackHelper.performClickHaptic(context)
                                showAdvancedAccordion = !showAdvancedAccordion
                            }
                        )

                        if (showAdvancedAccordion) {
                            ClaymorphicDivider()
                            ClaymorphicSettingsRow(
                                title = "Gemini Vision AI Key",
                                value = if (settings.geminiApiKey.isNotBlank()) "Configured" else "Optional",
                                icon = Icons.Default.AutoAwesome,
                                iconBg = Color(0xFFF3E8FF),
                                iconTint = Color(0xFF9333EA),
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    showGeminiDialog = true
                                }
                            )
                            ClaymorphicDivider()
                            ClaymorphicSettingsRow(
                                title = "Reload Clinical Dataset",
                                value = "Developer reseed utility",
                                icon = Icons.Default.Refresh,
                                iconBg = Color(0xFFF1F5F9),
                                iconTint = Color(0xFF475569),
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    showReseedDialog = true
                                }
                            )
                        }
                    }
                }

                // Bottom padding to clear bottom navigation bar
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // ==========================================
    // 3D Redesigned Dialogs
    // ==========================================

    if (showTargetDialog) {
        ClaymorphicEditTargetDialog(
            initialIntakeTarget = settings.intakeTargetMl,
            initialUrineTarget = settings.urineTargetMl,
            onDismiss = { showTargetDialog = false },
            onConfirm = { intake, urine ->
                viewModel.updateIntakeAndUrineTargets(intake, urine)
                showTargetDialog = false
            }
        )
    }

    if (showThresholdDialog) {
        ClaymorphicEditThresholdsDialog(
            current = settings.thresholdSettings,
            onDismiss = { showThresholdDialog = false },
            onConfirm = { low, min, max, crit ->
                viewModel.updateThresholds(low, min, max, crit)
                showThresholdDialog = false
            }
        )
    }

    if (showPresetsDialog) {
        ClaymorphicEditPresetsDialog(
            currentPresets = settings.quickPresetsMl,
            onDismiss = { showPresetsDialog = false },
            onConfirm = { presets ->
                viewModel.updateQuickPresets(presets)
                showPresetsDialog = false
            }
        )
    }

    if (showGeminiDialog) {
        ClaymorphicEditGeminiKeyDialog(
            currentKey = settings.geminiApiKey,
            onDismiss = { showGeminiDialog = false },
            onConfirm = { key ->
                viewModel.updateGeminiApiKey(key)
                showGeminiDialog = false
            }
        )
    }

    if (showReseedDialog) {
        ClaymorphicConfirmReseedDialog(
            onDismiss = { showReseedDialog = false },
            onConfirm = {
                showReseedDialog = false
                viewModel.reloadClinicalDataset()
            }
        )
    }
}

// ==========================================
// 4-Hub Redesign Preview
// ==========================================

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SettingsScreenRedesignPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Settings & Care Preferences",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            // Hero Profile Card
            ClaymorphicProfileCard(
                userName = "Payal Mevada (Renal Care)",
                subtitle = "Transplant: Oct 2024 • Nephrologist: Dr. Sharma",
                onEditProfile = {}
            )

            // Clinical Targets Hub
            ClaymorphicSectionHeader(title = "Clinical Targets & Goals")
            ClaymorphicCardGroup {
                ClaymorphicSettingsRow(
                    title = "Fluid Balance Targets",
                    value = "1500 ml in / 1200 ml out",
                    icon = Icons.Default.WaterDrop,
                    iconBg = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0284C7),
                    onClick = {}
                )
                ClaymorphicDivider()
                ClaymorphicSettingsRow(
                    title = "Blood Sugar Safe Zone",
                    value = "80–130 mg/dL",
                    icon = Icons.Default.Bloodtype,
                    iconBg = Color(0xFFFEE2E2),
                    iconTint = Color(0xFFEF4444),
                    onClick = {}
                )
            }

            // Security & Data Hub
            ClaymorphicSectionHeader(title = "Security & Data Hub")
            ClaymorphicCardGroup {
                ClaymorphicSwitchRow(
                    title = "Biometric Lock",
                    subtitle = "Fingerprint / Face ID Protection",
                    icon = Icons.Default.Fingerprint,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF16A34A),
                    checked = true,
                    onCheckedChange = {}
                )
                ClaymorphicDivider()
                ClaymorphicSettingsRow(
                    title = "Export Reports & Cloud Sync",
                    value = "PDF / Excel / Drive / FHIR R4",
                    icon = Icons.Default.Description,
                    iconBg = Color(0xFFECFDF5),
                    iconTint = Color(0xFF059669),
                    onClick = {}
                )
            }
        }
    }
}

// ==========================================
// 3D Claymorphic Settings Components
// ==========================================

@Composable
fun ClaymorphicProfileCard(
    userName: String,
    subtitle: String,
    onEditProfile: () -> Unit
) {
    Surface(
        onClick = onEditProfile,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .homeCardShadow(cornerRadius = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3D Avatar Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Color(0x300284C7))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))
                        )
                    )
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (userName.isNotBlank()) userName.take(2).uppercase() else "ME",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color(0xFF0369A1)
                    )
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color(0xFF334155),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Edit Profile",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun ClaymorphicSectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF0284C7))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF475569),
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun ClaymorphicCardGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .homeCardShadow(cornerRadius = 22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun ClaymorphicSettingsRow(
    title: String,
    value: String = "",
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = iconTint.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF334155),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotBlank()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun ClaymorphicSwitchRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = iconTint.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF334155)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0284C7),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ClaymorphicDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = 16.dp),
        color = Color(0xFFF1F5F9),
        thickness = 1.dp
    )
}

// ==========================================
// 3D Dialog Implementations
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicEditTargetDialog(
    initialIntakeTarget: Int,
    initialUrineTarget: Int,
    onDismiss: () -> Unit,
    onConfirm: (intakeTarget: Int, urineTarget: Int) -> Unit
) {
    var intakeText by remember { mutableStateOf(initialIntakeTarget.toString()) }
    var urineText by remember { mutableStateOf(initialUrineTarget.toString()) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Clinical Fluid Targets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prescribed daily targets for intake flushing and diuresis output.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = intakeText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) intakeText = it },
                    label = { Text("Daily Intake Target (ml)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = urineText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) urineText = it },
                    label = { Text("Daily Urine Target (ml)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val intake = intakeText.toIntOrNull() ?: initialIntakeTarget
                            val urine = urineText.toIntOrNull() ?: initialUrineTarget
                            onConfirm(intake, urine)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x400284C7)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicEditThresholdsDialog(
    current: ThresholdSettings,
    onDismiss: () -> Unit,
    onConfirm: (low: Int, min: Int, max: Int, crit: Int) -> Unit
) {
    var lowText by remember { mutableStateOf(current.lowThreshold.toString()) }
    var minText by remember { mutableStateOf(current.targetMin.toString()) }
    var maxText by remember { mutableStateOf(current.targetMax.toString()) }
    var critText by remember { mutableStateOf(current.criticalHigh.toString()) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Blood Sugar Thresholds",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Calibrate individual safe targets with your doctor.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lowText,
                        onValueChange = { lowText = it },
                        label = { Text("Low (<)", fontSize = 12.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minText,
                        onValueChange = { minText = it },
                        label = { Text("Target Min", fontSize = 12.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = maxText,
                        onValueChange = { maxText = it },
                        label = { Text("Target Max", fontSize = 12.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = critText,
                        onValueChange = { critText = it },
                        label = { Text("Critical (>)", fontSize = 12.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val low = lowText.toIntOrNull() ?: current.lowThreshold
                            val min = minText.toIntOrNull() ?: current.targetMin
                            val max = maxText.toIntOrNull() ?: current.targetMax
                            val crit = critText.toIntOrNull() ?: current.criticalHigh
                            onConfirm(low, min, max, crit)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x400284C7)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicEditPresetsDialog(
    currentPresets: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentPresets) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Quick Log Presets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Comma-separated amounts in ml to show as 1-tap buttons.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Preset Chips (ml)") },
                    placeholder = { Text("150, 200, 250, 500") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onConfirm(text) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x400284C7)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicEditTransplantProfileDialog(
    transplantDate: String,
    bloodGroup: String,
    nephrologistContact: String,
    emergencyNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (txDate: String, bloodGroup: String, nephrologist: String, notes: String) -> Unit
) {
    var date by remember { mutableStateOf(transplantDate) }
    var bg by remember { mutableStateOf(bloodGroup) }
    var nephrologist by remember { mutableStateOf(nephrologistContact) }
    var notes by remember { mutableStateOf(emergencyNotes) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Emergency Medical ID",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crucial clinical info for first responders and doctors.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Transplant Date (YYYY-MM-DD)") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bg,
                    onValueChange = { bg = it },
                    label = { Text("Blood Group (e.g. B+, O+)") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nephrologist,
                    onValueChange = { nephrologist = it },
                    label = { Text("Nephrologist Emergency Contact") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Emergency Instructions") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onConfirm(date, bg, nephrologist, notes) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x400284C7)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicEditPatientNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Patient Name",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name or Nickname") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onConfirm(name) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x400284C7)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicEditGeminiKeyDialog(
    currentKey: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var key by remember { mutableStateOf(currentKey) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E8FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF9333EA),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Gemini Vision AI Key",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Provides AI-enhanced OCR parsing for handwritten log sheets and medical forms.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("AIzaSy...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9333EA),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onConfirm(key) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x409333EA)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
                    ) {
                        Text("Save Key", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaymorphicConfirmReseedDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Reload Clinical Dataset?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This will repopulate TrackWell with Kishan's complete 17-week clinical records (July 19 – Nov 14, 2026), including 121 daily logs, 3,960 fluid measurements, 431 blood sugar readings, and vitals.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x40D97706)),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                    ) {
                        Text("Reload", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
