package com.trackwell.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.ui.common.Icon3DHeart
import com.trackwell.app.ui.common.Icon3DPill
import com.trackwell.app.ui.common.Icon3DScale
import com.trackwell.app.ui.common.Icon3DSugarDrop
import com.trackwell.app.ui.common.Icon3DWaterDrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.neuRaisedL
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        VascularBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(statusBarInset + 16.dp))

            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboardingStep"
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> {
                        WelcomeStepContent(
                            onStartSetup = { viewModel.selectStep(OnboardingStep.CHOOSE_MODULES) },
                            onStartPreviewMode = { viewModel.startPreviewMode(onOnboardingComplete) }
                        )
                    }

                    OnboardingStep.CHOOSE_MODULES -> {
                        ChooseModulesStepContent(
                            selectedModules = uiState.selectedModules,
                            onToggleModule = { viewModel.toggleModule(it) },
                            onNext = { viewModel.selectStep(OnboardingStep.BASIC_SETUP) }
                        )
                    }

                    OnboardingStep.BASIC_SETUP -> {
                        BasicSetupStepContent(
                            fluidTargetMl = uiState.fluidTargetMl,
                            onFluidTargetChange = { viewModel.setFluidTarget(it) },
                            onNext = { viewModel.selectStep(OnboardingStep.FIRST_ACTION) },
                            onSkip = { viewModel.completeSetup(onOnboardingComplete) }
                        )
                    }

                    OnboardingStep.FIRST_ACTION -> {
                        FirstActionStepContent(
                            onLogDrink = { amount -> viewModel.logFirstDrink(amount, onOnboardingComplete) },
                            onSkip = { viewModel.completeSetup(onOnboardingComplete) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun WelcomeStepContent(
    onStartSetup: () -> Unit,
    onStartPreviewMode: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Neumorphic Hero Cluster Card
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

        Spacer(modifier = Modifier.height(36.dp))

        // Primary Neumorphic Button
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
                .clickable { onStartSetup() },
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
    }
}

@Composable
private fun ChooseModulesStepContent(
    selectedModules: Set<String>,
    onToggleModule: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
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

        ModuleChoiceCard(
            title = "Fluid & Urine Output",
            subtitle = "Realtime 24h organ balance",
            badgeText = "Hydration",
            badgeBg = TargetTokens.chipBlue,
            icon = { Icon3DWaterDrop(size = 32.dp) },
            isSelected = selectedModules.contains("FLUID"),
            onToggle = { onToggleModule("FLUID") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModuleChoiceCard(
            title = "Morning Weight",
            subtitle = "Scale trends & baseline change",
            badgeText = "Weight",
            badgeBg = TargetTokens.chipGreen,
            icon = { Icon3DScale(size = 32.dp) },
            isSelected = selectedModules.contains("WEIGHT"),
            onToggle = { onToggleModule("WEIGHT") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModuleChoiceCard(
            title = "Blood Sugar & Insulin",
            subtitle = "Glucose slots & Target Corridor",
            badgeText = "Glycemic",
            badgeBg = Color(0xFFF3E8FF),
            icon = { Icon3DSugarDrop(size = 32.dp) },
            isSelected = selectedModules.contains("SUGAR"),
            onToggle = { onToggleModule("SUGAR") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModuleChoiceCard(
            title = "Medicines & Reminders",
            subtitle = "Daily doses & schedule alerts",
            badgeText = "Pharmacy",
            badgeBg = Color(0xFFDCFCE7),
            icon = { Icon3DPill(size = 32.dp) },
            isSelected = selectedModules.contains("MEDS"),
            onToggle = { onToggleModule("MEDS") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModuleChoiceCard(
            title = "Vitals (BP, Temp, Pulse)",
            subtitle = "Blood pressure & heart metrics",
            badgeText = "Cardiovascular",
            badgeBg = Color(0xFFFEE2E2),
            icon = { Icon3DHeart(size = 32.dp) },
            isSelected = selectedModules.contains("VITALS"),
            onToggle = { onToggleModule("VITALS") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .neuRaisedL(cornerRadius = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0284C7))
                .clickable { onNext() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continue (${selectedModules.size} selected)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ModuleChoiceCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeBg: Color,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    onToggle: () -> Unit
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
            .clickable { onToggle() }
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

@Composable
private fun BasicSetupStepContent(
    fluidTargetMl: Int,
    onFluidTargetChange: (Int) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Set your daily target",
            style = TargetTypography.greeting.copy(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold),
            color = TargetTokens.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Personalize your hydration goal. You can adjust targets anytime.",
            style = TargetTypography.subtitle.copy(fontSize = 13.sp),
            color = TargetTokens.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaisedL(cornerRadius = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(TargetTokens.surface)
                .border(1.dp, TargetTokens.divider, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daily Fluid Intake Target",
                    style = TargetTypography.statCardLabel.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = TargetTokens.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$fluidTargetMl mL",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0284C7)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(2000, 2500, 3000, 3500).forEach { amount ->
                        val isSel = amount == fluidTargetMl
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .neuRaisedL(cornerRadius = 14.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(TargetTokens.surface)
                                .border(1.dp, if (isSel) Color(0xFF0284C7) else TargetTokens.divider, RoundedCornerShape(14.dp))
                                .clickable { onFluidTargetChange(amount) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${amount / 1000}.${(amount % 1000) / 100}L",
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSel) Color(0xFF0284C7) else TargetTokens.textPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .neuRaisedL(cornerRadius = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0284C7))
                .clickable { onNext() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Next: First Action",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onSkip) {
            Text(
                text = "Skip for now",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TargetTokens.textSecondary
            )
        }
    }
}

@Composable
private fun FirstActionStepContent(
    onLogDrink: (Int) -> Unit,
    onSkip: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Start today's flow",
            style = TargetTypography.greeting.copy(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold),
            color = TargetTokens.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Log your first water intake to activate your live 3D Organ Vials.",
            style = TargetTypography.subtitle.copy(fontSize = 13.sp),
            color = TargetTokens.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(150, 200, 250, 500).forEach { ml ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .neuRaisedL(cornerRadius = 18.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(TargetTokens.surface)
                        .border(1.dp, TargetTokens.chipBlue, RoundedCornerShape(18.dp))
                        .clickable { onLogDrink(ml) }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon3DWaterDrop(size = 24.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+$ml",
                            fontSize = 15.sp,
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

        Spacer(modifier = Modifier.height(36.dp))

        TextButton(onClick = onSkip) {
            Text(
                text = "Go to Dashboard",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7)
            )
        }
    }
}
