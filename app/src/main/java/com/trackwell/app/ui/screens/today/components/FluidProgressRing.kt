package com.trackwell.app.ui.screens.today.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.domain.model.DailyFluidSummary
import com.trackwell.app.ui.theme.AuraMidnight
import com.trackwell.app.ui.theme.AuraTeal
import com.trackwell.app.ui.theme.DifferenceDeficit
import com.trackwell.app.ui.theme.DifferenceDeficitBg
import com.trackwell.app.ui.theme.DifferenceDeficitBorder
import com.trackwell.app.ui.theme.DifferenceOptimal
import com.trackwell.app.ui.theme.DifferenceOptimalBg
import com.trackwell.app.ui.theme.DifferenceOptimalBorder
import com.trackwell.app.ui.theme.DifferenceRetention
import com.trackwell.app.ui.theme.DifferenceRetentionBg
import com.trackwell.app.ui.theme.DifferenceRetentionBorder
import com.trackwell.app.ui.theme.MetricIntakeBlue
import com.trackwell.app.ui.theme.MetricIntakeBlueBg
import com.trackwell.app.ui.theme.MetricUrineAmber
import com.trackwell.app.ui.theme.MetricUrineAmberBg

@Composable
fun FluidProgressRing(
    summary: DailyFluidSummary,
    intakeTargetMl: Int = 3500,
    urineTargetMl: Int = 3200,
    onEditTarget: () -> Unit,
    onQuickAddIntake: () -> Unit = {},
    onQuickAddUrine: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val animatedIntakeProgress = remember { Animatable(0f) }
    val animatedUrineProgress = remember { Animatable(0f) }

    val effectiveIntakeTarget = if (intakeTargetMl > 0) intakeTargetMl else (if (summary.targetMl > 0) summary.targetMl else 3500)
    val effectiveUrineTarget = if (urineTargetMl > 0) urineTargetMl else 3200

    LaunchedEffect(summary.totalIntakeMl, effectiveIntakeTarget) {
        val target = (summary.totalIntakeMl.toFloat() / effectiveIntakeTarget).coerceIn(0f, 1.5f)
        animatedIntakeProgress.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(summary.totalUrineMl, effectiveUrineTarget) {
        val target = (summary.totalUrineMl.toFloat() / effectiveUrineTarget).coerceIn(0f, 1.5f)
        animatedUrineProgress.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with target and edit button
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
                            .background(MetricIntakeBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = MetricIntakeBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AURA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = AuraTeal
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "• FLUID DYNAMICS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Intake & Diuresis Output Balance",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onEditTarget() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Targets: In ${effectiveIntakeTarget} / Out ${effectiveUrineTarget} ml",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Targets",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Dual Circular Progress Rings: Separate Targets for Intake & Urine Output
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val intakePercent = (summary.totalIntakeMl * 100 / effectiveIntakeTarget)
                val urinePercent = (summary.totalUrineMl * 100 / effectiveUrineTarget)

                // Intake Progress Ring
                FluidCircularGauge(
                    title = "INTAKE",
                    currentAmount = summary.totalIntakeMl,
                    targetAmount = effectiveIntakeTarget,
                    animatedProgress = animatedIntakeProgress.value,
                    percent = intakePercent,
                    badgeSuffix = "of target",
                    color = MetricIntakeBlue,
                    trackColor = MetricIntakeBlueBg,
                    icon = Icons.Default.WaterDrop,
                    onTap = onQuickAddIntake
                )

                // Urine Progress Ring
                FluidCircularGauge(
                    title = "URINE",
                    currentAmount = summary.totalUrineMl,
                    targetAmount = effectiveUrineTarget,
                    animatedProgress = animatedUrineProgress.value,
                    percent = urinePercent,
                    badgeSuffix = "of target",
                    color = MetricUrineAmber,
                    trackColor = MetricUrineAmberBg,
                    icon = Icons.Default.Opacity,
                    onTap = onQuickAddUrine
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. High-Impact Highlighted Recorded Fluid Difference Hero Card
            val balance = summary.balanceMl
            val isPositive = balance > 0
            val isZero = balance == 0
            val isNegative = balance < 0
            val sign = if (isPositive) "+" else ""

            val balanceThemeColor = when {
                isNegative -> DifferenceDeficit // Alert red: deficit / dehydration risk
                balance in 0..600 -> DifferenceOptimal // Healthy positive balance for post-transplant
                else -> DifferenceRetention // Moderate fluid retention (>600ml)
            }

            val balanceBgColor = when {
                isNegative -> DifferenceDeficitBg
                balance in 0..600 -> DifferenceOptimalBg
                else -> DifferenceRetentionBg
            }

            val balanceBorderColor = when {
                isNegative -> DifferenceDeficitBorder
                balance in 0..600 -> DifferenceOptimalBorder
                else -> DifferenceRetentionBorder
            }

            val balanceStatusChip = when {
                isNegative -> "Deficit Output ($balance ml)"
                isZero -> "Neutral Balance (0 ml)"
                balance in 1..600 -> "Optimal Retention (+$balance ml)"
                else -> "High Fluid Retention (+$balance ml)"
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = balanceBgColor,
                border = BorderStroke(1.5.dp, balanceBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header of Recorded Fluid Difference Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(balanceThemeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Balance,
                                    contentDescription = "Recorded Fluid Difference",
                                    tint = balanceThemeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "RECORDED FLUID DIFFERENCE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = balanceThemeColor
                                )
                                Text(
                                    text = "Intake minus urine output",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = balanceThemeColor.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Status pill badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = balanceThemeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = balanceStatusChip,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = balanceThemeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prominent Balance Value & Quick Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$sign$balance ml",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = balanceThemeColor
                            )
                            Text(
                                text = if (isPositive) "Intake exceeds urine output" else if (isNegative) "Urine output exceeds intake" else "Intake equals urine output",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Quick Log Shortcuts (+Water, +Urine)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MetricIntakeBlueBg,
                                modifier = Modifier.clickable { onQuickAddIntake() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Log Water",
                                        tint = MetricIntakeBlue,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Water",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MetricIntakeBlue
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MetricUrineAmberBg,
                                modifier = Modifier.clickable { onQuickAddUrine() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Log Urine",
                                        tint = MetricUrineAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Urine",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MetricUrineAmber
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calculation Breakdown Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MetricIntakeBlue)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Intake: ${summary.totalIntakeMl} ml",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "−",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MetricUrineAmber)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Urine: ${summary.totalUrineMl} ml",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "=",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "$sign$balance ml",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = balanceThemeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Visual Proportional Distribution Gauge
                    val totalVolume = (summary.totalIntakeMl + summary.totalUrineMl).coerceAtLeast(1)
                    val intakeRatio = (summary.totalIntakeMl.toFloat() / totalVolume).coerceIn(0.05f, 0.95f)
                    val urineRatio = 1f - intakeRatio
                    val intakePercentInt = kotlin.math.round(intakeRatio * 100).toInt()
                    val urinePercentInt = 100 - intakePercentInt

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(intakeRatio)
                                    .background(MetricIntakeBlue)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(urineRatio)
                                    .background(MetricUrineAmber)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Intake $intakePercentInt%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MetricIntakeBlue
                            )
                            Text(
                                text = if (balance in 0..600) "Normal Insensible Loss Target" else if (balance > 600) "Fluid Retention Warning" else "Deficit Output",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Urine $urinePercentInt%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MetricUrineAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FluidCircularGauge(
    title: String,
    currentAmount: Int,
    targetAmount: Int,
    animatedProgress: Float,
    percent: Int,
    badgeSuffix: String,
    color: Color,
    trackColor: Color,
    icon: ImageVector,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onTap() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(142.dp)
        ) {
            val strokeWidth = 11.dp

            Canvas(modifier = Modifier.size(134.dp)) {
                val strokePx = strokeWidth.toPx()
                // Track
                drawCircle(
                    color = trackColor,
                    style = Stroke(width = strokePx)
                )

                // Arc
                val sweep = (animatedProgress.coerceIn(0f, 1f)) * 360f
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = color
                    )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$currentAmount",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "ml",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Text(
                    text = if (targetAmount > 0) "of $targetAmount ml" else "Target unset",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier.padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = trackColor
                ) {
                    Text(
                        text = "$percent% $badgeSuffix",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tap to add $title",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
