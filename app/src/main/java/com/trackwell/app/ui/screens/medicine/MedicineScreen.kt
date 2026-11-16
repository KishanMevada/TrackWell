package com.trackwell.app.ui.screens.medicine

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackwell.app.R
import com.trackwell.app.domain.medicine.model.MedicineDoseUnit
import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.screens.medicine.components.DeleteMedicineDialog
import com.trackwell.app.ui.screens.medicine.components.Icon3DCapsule
import com.trackwell.app.ui.screens.medicine.components.Icon3DTablet
import com.trackwell.app.ui.screens.medicine.components.MedicineEditorBottomSheet
import com.trackwell.app.ui.screens.medicine.components.MedicineOptionsBottomSheet
import com.trackwell.app.ui.screens.medicine.components.MedicineSettingsBottomSheet
import com.trackwell.app.ui.theme.InterFontFamily
import com.trackwell.app.ui.theme.TargetTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var showEditorSheet by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<MedicineSchedule?>(null) }
    var optionsSchedule by remember { mutableStateOf<MedicineSchedule?>(null) }
    var deletingSchedule by remember { mutableStateOf<MedicineSchedule?>(null) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Permission launcher for Android 13+ notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.refreshPermissions(hasNotificationPermission = isGranted)
    }

    LaunchedEffect(Unit) {
        val hasNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        viewModel.refreshPermissions(hasNotificationPermission = hasNotif)
        viewModel.refreshNextReminder()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        // Living vascular background matching Home & Weight/Blood Sugar History
        VascularBackground(modifier = Modifier.fillMaxSize())

        // Living decorative wellness body flow backdrop matching Home
        RenalBodyFlowBackdrop(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
        )

        // Peeking Renal Hero Illustration in top-right background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .alpha(0.28f),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .offset(x = 35.dp, y = (-20).dp)
                    .size(190.dp)
            ) {
                // Amber pipe stub
                Image(
                    painter = painterResource(id = R.drawable.pipe_amber_right),
                    contentDescription = null,
                    modifier = Modifier
                        .size(105.dp)
                        .offset(x = 65.dp, y = 30.dp)
                )
                // Cyan pipe stub
                Image(
                    painter = painterResource(id = R.drawable.pipe_blue_left),
                    contentDescription = null,
                    modifier = Modifier
                        .size(105.dp)
                        .offset(x = (-30).dp, y = 30.dp)
                )
                // 3D Kidney Graphic
                Image(
                    painter = painterResource(id = R.drawable.kidney_3d),
                    contentDescription = null,
                    modifier = Modifier
                        .size(125.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // Main Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 120.dp)
        ) {
            // Status bar spacer
            Spacer(modifier = Modifier.height(statusBarInset + 8.dp))

            // Actionbar: Matches Weight History / Blood Sugar History Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Elevated Circular Claymorphic Back Button
                    Surface(
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onNavigateBack()
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Medicines",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.4).sp,
                            fontFamily = InterFontFamily
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Reminder Schedule",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B),
                            fontFamily = InterFontFamily
                        )
                    }
                }

                // Elevated Circular Claymorphic Add Button
                Surface(
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context)
                        editingSchedule = null
                        showEditorSheet = true
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
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add medicine",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Today" - Next Reminder Card (Tactile Dashboard Glass-Clay Card)
            if (uiState.nextReminder != null) {
                val next = uiState.nextReminder!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .homeCardShadow(cornerRadius = 26.dp, elevation = 14.dp)
                        .clip(RoundedCornerShape(26.dp))
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
                            shape = RoundedCornerShape(26.dp)
                        )
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
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Today",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.3).sp
                            )
                        )
                        Text(
                            text = "Next reminder",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Next reminder inner item
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, Color(0xFFEDF2F7))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 3D Icon container
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFE0F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon3DCapsule(size = 38.dp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = next.medicineName,
                                        style = TextStyle(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF0F172A)
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = next.doseFormatted,
                                        style = TextStyle(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    )
                                }

                                // Amber scheduled time badge
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = Color(0xFFB45309),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = next.scheduledTime,
                                            style = TextStyle(
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF92400E)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // "Your medicines" Section Header
            Text(
                text = "Your medicines",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0F172A),
                    letterSpacing = (-0.3).sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Medicines List or Empty State
            if (uiState.schedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .homeCardShadow(cornerRadius = 26.dp, elevation = 12.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.95f), Color(0xFFE2E8F0).copy(alpha = 0.60f))
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F6FE))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon3DCapsule(size = 50.dp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No medicines scheduled",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF0F172A)
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add your daily medications and reminder times to stay on track with your wellness routine.",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.5.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 19.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    uiState.schedules.forEach { schedule ->
                        MedicineCardItem(
                            schedule = schedule,
                            onOptionsClick = {
                                optionsSchedule = schedule
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "+ Add medicine" Full-Width CTA Button
            Button(
                onClick = {
                    HapticFeedbackHelper.performClickHaptic(context)
                    editingSchedule = null
                    showEditorSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = Color(0x3000B4D8),
                        spotColor = Color(0x5000B4D8)
                    ),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B4D8)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add medicine",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Strict reminders are on" Banner Card (Matches dashboard and history subcards)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        HapticFeedbackHelper.performClickHaptic(context)
                        showSettingsSheet = true
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isStrictRemindersOn) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (uiState.isStrictRemindersOn) "Strict reminders are on" else "Strict reminders need attention",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp,
                            color = Color(0xFF1E40AF)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Settings",
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }

    // Medicine Schedule Options Bottom Sheet (Replaces old generic DropdownMenu)
    if (optionsSchedule != null) {
        val selected = optionsSchedule!!
        MedicineOptionsBottomSheet(
            schedule = selected,
            onEdit = {
                optionsSchedule = null
                editingSchedule = selected
                showEditorSheet = true
            },
            onToggleEnabled = { isEnabled ->
                optionsSchedule = null
                viewModel.toggleScheduleEnabled(selected.id, isEnabled)
            },
            onDelete = {
                optionsSchedule = null
                deletingSchedule = selected
            },
            onDismiss = {
                optionsSchedule = null
            }
        )
    }

    // Add / Edit Medicine Bottom Sheet
    if (showEditorSheet) {
        MedicineEditorBottomSheet(
            existingSchedule = editingSchedule,
            onSave = { savedSchedule ->
                viewModel.saveSchedule(savedSchedule)
                showEditorSheet = false
            },
            onDismiss = { showEditorSheet = false }
        )
    }

    // Delete Confirmation Dialog
    if (deletingSchedule != null) {
        DeleteMedicineDialog(
            medicineName = deletingSchedule!!.name,
            onConfirm = {
                viewModel.deleteSchedule(deletingSchedule!!.id)
                deletingSchedule = null
            },
            onDismiss = { deletingSchedule = null }
        )
    }

    // Strict Reminders Settings Bottom Sheet
    if (showSettingsSheet) {
        MedicineSettingsBottomSheet(
            hasNotificationPermission = uiState.hasNotificationPermission,
            hasExactAlarmPermission = uiState.hasExactAlarmPermission,
            isLockScreenPrivacyEnabled = uiState.isLockScreenPrivacyEnabled,
            onRequestNotificationPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onToggleLockScreenPrivacy = { enabled ->
                viewModel.setLockScreenPrivacy(enabled)
            },
            onDismiss = { showSettingsSheet = false }
        )
    }
}

/**
 * Individual medicine item card styled with the TrackWell dashboard card aesthetic.
 */
@Composable
private fun MedicineCardItem(
    schedule: MedicineSchedule,
    onOptionsClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .homeCardShadow(cornerRadius = 24.dp, elevation = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFFE2E8F0).copy(alpha = 0.60f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF0F6FE)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 3D Medicine Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0F7FA).copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (schedule.doseUnit == MedicineDoseUnit.TABLET) {
                    Icon3DTablet(size = 38.dp)
                } else {
                    Icon3DCapsule(size = 38.dp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = schedule.name,
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.2).sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Three dots overflow action button -> opens MedicineOptionsBottomSheet
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onOptionsClick()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Dose text
                Text(
                    text = schedule.formattedDose,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.5.sp,
                        color = Color(0xFF64748B)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Chips Row (Time and Frequency)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time chip
                    val displayTime = schedule.reminderTimes.firstOrNull()?.let { timeStr ->
                        try {
                            val parts = timeStr.split(":")
                            val h = parts[0].toInt()
                            val m = parts[1].toInt()
                            val ampm = if (h >= 12) "PM" else "AM"
                            val displayH = if (h % 12 == 0) 12 else h % 12
                            String.format("%d:%02d %s", displayH, m, ampm)
                        } catch (e: Exception) {
                            timeStr
                        }
                    } ?: "8:00 AM"

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = displayTime,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E40AF)
                                )
                            )
                        }
                    }

                    // Repeat chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = schedule.formattedRepeat,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reminder active status chip
                if (schedule.isEnabled) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE0F2FE),
                        border = BorderStroke(0.5.dp, Color(0xFFBAE6FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(12.5.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reminder active",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF0284C7)
                                )
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(12.5.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reminder paused",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
