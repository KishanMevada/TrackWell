package com.trackwell.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.ui.home.components.DateSelector
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.neuRaisedL
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography

@Preview(name = "Mockup 1: Homescreen Welcome", showBackground = true, widthDp = 380, heightDp = 720)
@Composable
fun MockupWelcomeScreenPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TargetTokens.bg)
        ) {
            VascularBackground(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Hero Cluster Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaisedL(cornerRadius = 28.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(TargetTokens.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 3D Floating Icon Cluster
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(TargetTokens.chipBlue.copy(alpha = 0.45f))
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-10).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon3DWaterDrop(size = 52.dp)
                                Icon3DHeart(size = 60.dp)
                                Icon3DPill(size = 52.dp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "TrackWell",
                            style = TargetTypography.greeting.copy(fontSize = 32.sp, fontWeight = FontWeight.Black),
                            color = TargetTokens.textPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Your Health, Clearly Connected",
                            style = TargetTypography.subtitle.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                            color = Color(0xFF0284C7)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Track daily fluid balance, blood sugar, morning weight, and medicines in one tactile space.",
                            style = TargetTypography.subtitle.copy(fontSize = 13.sp),
                            color = TargetTokens.textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                    }
                }

                // Actions Area
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Neumorphic Primary CTA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .neuRaisedL(cornerRadius = 18.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                                )
                            )
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Get Started",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Neumorphic Secondary CTA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .neuRaisedL(cornerRadius = 18.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(TargetTokens.surface)
                            .border(1.dp, TargetTokens.divider, RoundedCornerShape(18.dp))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Try Demo Mode (Sample 17-Wk Data)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TargetTokens.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Mockup 2: Homescreen Module Selector", showBackground = true, widthDp = 380, heightDp = 720)
@Composable
fun MockupModuleSelectorScreenPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TargetTokens.bg)
        ) {
            VascularBackground(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Customize Your Space",
                        style = TargetTypography.greeting.copy(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold),
                        color = TargetTokens.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Select what you want to track today. Add or change anytime.",
                        style = TargetTypography.subtitle.copy(fontSize = 13.sp),
                        color = TargetTokens.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3D Neumorphic Selector Cards
                    MockupSelectorTile(
                        title = "Fluid & Urine Output",
                        subtitle = "Realtime 24h organ balance",
                        badgeText = "Hydration",
                        badgeBg = TargetTokens.chipBlue,
                        icon = { Icon3DWaterDrop(size = 32.dp) },
                        isSelected = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MockupSelectorTile(
                        title = "Morning Weight",
                        subtitle = "Scale trends & baseline change",
                        badgeText = "Weight",
                        badgeBg = TargetTokens.chipGreen,
                        icon = { Icon3DScale(size = 32.dp) },
                        isSelected = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MockupSelectorTile(
                        title = "Blood Sugar & Insulin",
                        subtitle = "Glucose slots & Target Corridor",
                        badgeText = "Glycemic",
                        badgeBg = Color(0xFFF3E8FF),
                        icon = { Icon3DSugarDrop(size = 32.dp) },
                        isSelected = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MockupSelectorTile(
                        title = "Medicines & Reminders",
                        subtitle = "Daily doses & schedule alerts",
                        badgeText = "Pharmacy",
                        badgeBg = Color(0xFFDCFCE7),
                        icon = { Icon3DPill(size = 32.dp) },
                        isSelected = false
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MockupSelectorTile(
                        title = "Vitals (BP, Temp, Pulse)",
                        subtitle = "Blood pressure & heart metrics",
                        badgeText = "Cardiovascular",
                        badgeBg = Color(0xFFFEE2E2),
                        icon = { Icon3DHeart(size = 32.dp) },
                        isSelected = false
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .neuRaisedL(cornerRadius = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0284C7))
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open My Personalized Space",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MockupSelectorTile(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeBg: Color,
    icon: @Composable () -> Unit,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaisedL(cornerRadius = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(TargetTokens.surface)
            .border(
                1.2.dp,
                if (isSelected) Color(0xFF0284C7) else TargetTokens.divider,
                RoundedCornerShape(20.dp)
            )
            .clickable {}
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = TargetTypography.statCardLabel.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            color = TargetTokens.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeBg
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TargetTokens.textPrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = TargetTokens.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0))
                    .border(1.dp, if (isSelected) Color(0xFF0284C7) else TargetTokens.divider, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Mockup 3: Homescreen Starter Day 1", showBackground = true, widthDp = 380, heightDp = 720)
@Composable
fun MockupStarterDashboardPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TargetTokens.bg)
        ) {
            VascularBackground(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Greeting Header (Homescreen exact typography)
                Text(
                    text = "Hi, Kishan!",
                    style = TargetTypography.greeting.copy(fontSize = 30.sp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Today's Renal Balance",
                    style = TargetTypography.subtitle.copy(fontSize = 16.sp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Date Selector Pill
                DateSelector(
                    dateLabel = "Today",
                    isToday = true,
                    canGoForward = false,
                    onPreviousDay = {},
                    onNextDay = {},
                    onDatePickerClick = {},
                    onResetToToday = {}
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Homescreen Starter Quick-Dock Card (Neumorphic)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaisedL(cornerRadius = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(TargetTokens.surface)
                        .border(1.dp, TargetTokens.divider, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon3DWaterDrop(size = 28.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Today's Balance",
                                style = TargetTypography.statCardLabel.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                color = TargetTokens.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Log your first water intake to activate your live 3D Organ Vials",
                            style = TargetTypography.subtitle.copy(fontSize = 12.sp),
                            color = TargetTokens.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(150, 200, 250, 500).forEach { amount ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .neuRaisedL(cornerRadius = 14.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(TargetTokens.surface)
                                        .border(1.dp, TargetTokens.chipBlue, RoundedCornerShape(14.dp))
                                        .clickable {}
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "+$amount",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0284C7)
                                        )
                                        Text(
                                            text = "mL",
                                            fontSize = 10.sp,
                                            color = TargetTokens.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Setup Progress Card (Neumorphic)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            Text(
                                text = "Make TrackWell Work For You",
                                style = TargetTypography.statCardLabel.copy(fontSize = 13.5.sp, fontWeight = FontWeight.Bold),
                                color = TargetTokens.textPrimary
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TargetTokens.chipBlue
                            ) {
                                Text(
                                    text = "1 of 3 Done",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Customize tracking modules",
                                fontSize = 12.sp,
                                color = TargetTokens.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, TargetTokens.divider, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add optional care profile & name",
                                fontSize = 12.sp,
                                color = TargetTokens.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, TargetTokens.divider, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set up medication reminder alerts",
                                fontSize = 12.sp,
                                color = TargetTokens.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
