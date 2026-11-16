package com.trackwell.app.ui.screens.medicine.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.domain.medicine.model.MedicineDoseUnit
import com.trackwell.app.domain.medicine.model.MedicineSchedule
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.home.components.homeCardShadow
import com.trackwell.app.ui.theme.InterFontFamily

/**
 * Custom tactile bottom sheet for medicine schedule actions (Edit, Pause/Resume, Delete)
 * matching the TrackWell dashboard and history UI theme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineOptionsBottomSheet(
    schedule: MedicineSchedule,
    onEdit: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = Color(0xFFF8FBFF),
        scrimColor = Color.Black.copy(alpha = 0.40f),
        windowInsets = WindowInsets(0, 0, 0, 0),
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
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            // Header with Medicine Pill preview and Elevated Circular Close 'X'
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE0F7FA).copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (schedule.doseUnit == MedicineDoseUnit.TABLET) {
                            Icon3DTablet(size = 36.dp)
                        } else {
                            Icon3DCapsule(size = 36.dp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = schedule.name,
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.3).sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${schedule.formattedDose} • ${schedule.formattedRepeat}",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }

                Surface(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = Color(0x304A6282),
                            ambientColor = Color(0x1864748B)
                        ),
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Options Cards - Styled exactly like Medicine screen cards
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 1. Edit Schedule
                MedicineOptionItem(
                    icon = Icons.Default.Edit,
                    iconTint = Color(0xFF0284C7),
                    iconBg = Color(0xFFE0F2FE),
                    title = "Edit schedule",
                    subtitle = "Change name, dose, or reminder times",
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context)
                        onDismiss()
                        onEdit()
                    }
                )

                // 2. Pause / Resume Reminders
                if (schedule.isEnabled) {
                    MedicineOptionItem(
                        icon = Icons.Default.Pause,
                        iconTint = Color(0xFF64748B),
                        iconBg = Color(0xFFF1F5F9),
                        title = "Pause reminders",
                        subtitle = "Temporarily stop scheduled reminder alarms",
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onDismiss()
                            onToggleEnabled(false)
                        }
                    )
                } else {
                    MedicineOptionItem(
                        icon = Icons.Default.PlayArrow,
                        iconTint = Color(0xFF059669),
                        iconBg = Color(0xFFDCFCE7),
                        title = "Resume reminders",
                        subtitle = "Restart scheduled reminder alarms",
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            onDismiss()
                            onToggleEnabled(true)
                        }
                    )
                }

                // 3. Delete Reminder
                MedicineOptionItem(
                    icon = Icons.Default.Delete,
                    iconTint = Color(0xFFDC2626),
                    iconBg = Color(0xFFFEE2E2),
                    title = "Delete reminder",
                    subtitle = "Permanently remove this medicine schedule",
                    isDestructive = true,
                    onClick = {
                        HapticFeedbackHelper.performClickHaptic(context)
                        onDismiss()
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun MedicineOptionItem(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val borderBrush = if (isDestructive) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFECACA),
                Color(0xFFFCA5A5).copy(alpha = 0.50f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFE2E8F0).copy(alpha = 0.65f)
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        )
    }

    val backgroundBrush = if (isDestructive) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFFFF1F2)
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF0F6FE)
            ),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .homeCardShadow(
                cornerRadius = 22.dp,
                elevation = 8.dp,
                spotColor = if (isDestructive) Color(0x25EF4444) else Color(0x28384F6E),
                ambientColor = if (isDestructive) Color(0x15EF4444) else Color(0x184A6282)
            )
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(22.dp)
            )
            .background(backgroundBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container matching medicine cards
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDestructive) Color(0xFFDC2626) else Color(0xFF0F172A),
                        letterSpacing = (-0.2).sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = if (isDestructive) Color(0xFFEF4444).copy(alpha = 0.85f) else Color(0xFF64748B)
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = if (isDestructive) Color(0xFFF87171) else Color(0xFF94A3B8),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

