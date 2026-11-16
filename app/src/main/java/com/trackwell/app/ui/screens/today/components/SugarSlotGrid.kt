package com.trackwell.app.ui.screens.today.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarReadingStatus
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.model.ThresholdSettings
import com.trackwell.app.ui.common.TrackWellCard
import com.trackwell.app.ui.theme.MetricSugarViolet
import com.trackwell.app.ui.theme.MetricSugarVioletBg
import com.trackwell.app.ui.theme.SugarAmber
import com.trackwell.app.ui.theme.SugarAmberBg
import com.trackwell.app.ui.theme.SugarAmberBorder
import com.trackwell.app.ui.theme.SugarGreen
import com.trackwell.app.ui.theme.SugarGreenBg
import com.trackwell.app.ui.theme.SugarGreenBorder
import com.trackwell.app.ui.theme.SugarPendingBg
import com.trackwell.app.ui.theme.SugarPendingBorder
import com.trackwell.app.ui.theme.SugarPendingGray
import com.trackwell.app.ui.theme.SugarRed
import com.trackwell.app.ui.theme.SugarRedBg
import com.trackwell.app.ui.theme.SugarRedBorder

@Composable
fun SugarSlotGrid(
    sugarEntries: Map<SugarSlot, SugarEntry>,
    thresholds: ThresholdSettings,
    onSlotClick: (SugarSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    TrackWellCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MetricSugarVioletBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = MetricSugarViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Metabolic Rhythm",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "4-Slot Blood Glucose & Insulin",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Recorded count badge
                val recordedCount = sugarEntries.values.count { it.sugarMgDl != null }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "$recordedCount of 4 logged",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Grid of Metabolic Slot Tiles
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SugarSlotItem(
                        slot = SugarSlot.BEFORE_BREAKFAST,
                        timeLabel = "7:30 AM",
                        entry = sugarEntries[SugarSlot.BEFORE_BREAKFAST],
                        thresholds = thresholds,
                        onClick = { onSlotClick(SugarSlot.BEFORE_BREAKFAST) },
                        modifier = Modifier.weight(1f)
                    )
                    SugarSlotItem(
                        slot = SugarSlot.BEFORE_LUNCH,
                        timeLabel = "1:00 PM",
                        entry = sugarEntries[SugarSlot.BEFORE_LUNCH],
                        thresholds = thresholds,
                        onClick = { onSlotClick(SugarSlot.BEFORE_LUNCH) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SugarSlotItem(
                        slot = SugarSlot.AFTER_LUNCH,
                        timeLabel = "4:30 PM",
                        entry = sugarEntries[SugarSlot.AFTER_LUNCH],
                        thresholds = thresholds,
                        onClick = { onSlotClick(SugarSlot.AFTER_LUNCH) },
                        modifier = Modifier.weight(1f)
                    )
                    SugarSlotItem(
                        slot = SugarSlot.BEFORE_DINNER,
                        timeLabel = "8:00 PM",
                        entry = sugarEntries[SugarSlot.BEFORE_DINNER],
                        thresholds = thresholds,
                        onClick = { onSlotClick(SugarSlot.BEFORE_DINNER) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SugarSlotItem(
    slot: SugarSlot,
    timeLabel: String,
    entry: SugarEntry?,
    thresholds: ThresholdSettings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sugarValue = entry?.sugarMgDl
    val status = sugarValue?.let { thresholds.evaluate(it) } ?: SugarReadingStatus.NOT_RECORDED

    val (badgeBg, badgeBorder, badgeText, statusColor) = when (status) {
        SugarReadingStatus.IN_RANGE -> Quadruple(SugarGreenBg, SugarGreenBorder, "In Range", SugarGreen)
        SugarReadingStatus.HIGH -> Quadruple(SugarRedBg, SugarRedBorder, "High", SugarRed)
        SugarReadingStatus.LOW -> Quadruple(SugarAmberBg, SugarAmberBorder, "Low", SugarAmber)
        SugarReadingStatus.NOT_RECORDED -> Quadruple(SugarPendingBg, SugarPendingBorder, "Pending", SugarPendingGray)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Row 1: Slot title & time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slot.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Reading Value & Status Chip
            if (sugarValue != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$sugarValue",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = statusColor
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "mg/dL",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg,
                        border = BorderStroke(0.8.dp, badgeBorder)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Record",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 3: Insulin Dose / Status
            val insulinUnits = entry?.insulinUnits
            if (insulinUnits != null && insulinUnits > 0) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "💉 ${String.format("%.1f", insulinUnits)} U dose",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else if (sugarValue != null) {
                Text(
                    text = "No insulin logged",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = "Tap to enter",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
