package com.trackwell.app.ui.common

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.ui.theme.SugarAmber
import com.trackwell.app.ui.theme.SugarAmberBg
import com.trackwell.app.ui.theme.SugarRed
import com.trackwell.app.ui.theme.SugarRedBg

object HapticFeedbackHelper {
    fun performClickHaptic(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(25)
        }
    }

    fun performSuccessHaptic(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(45)
        }
    }

    fun performOrganPulseHaptic(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Heartbeat rhythmic doublet: lub-dub [pause, pulse1, gap, pulse2]
            val timings = longArrayOf(0, 30, 80, 50)
            val amplitudes = intArrayOf(0, 160, 0, 240)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 80, 50), -1)
        }
    }

    fun performTickHaptic(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }
}

/**
 * Subtle Background Vascular Nephron & Fluid Circulation Animation
 * Renders faint biological capillary arcades, glomerulus loops, and slow drifting particles
 * at 8-10% opacity in the background layer so it NEVER obscures or blocks foreground data.
 */
@Composable
fun BiologicalBackgroundCanvas(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bioFlow")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flowPhase"
    )
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsePhase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baseAlpha = if (isDark) 0.09f else 0.07f

        val primaryVesselColor = if (isDark) Color(0xFF00E5FF) else Color(0xFF0284C7)
        val secondaryVesselColor = if (isDark) Color(0xFFFFB300) else Color(0xFFE8A34A)
        val arterialColor = if (isDark) Color(0xFFFF3366) else Color(0xFFE11D48)
        val nephronLineColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

        // Left-side Anatomical Nephron Architecture (Bowman's Capsule & Tubules)
        val leftNephron = Path().apply {
            // Glomerular capsule dome
            moveTo(w * 0.14f, h * 0.08f)
            cubicTo(w * 0.02f, h * 0.12f, w * 0.02f, h * 0.28f, w * 0.16f, h * 0.32f)
            // Proximal convoluted tubule loops
            cubicTo(w * 0.26f, h * 0.35f, w * 0.04f, h * 0.44f, w * 0.12f, h * 0.54f)
            // Loop of Henle descending & ascending hairpin
            cubicTo(w * 0.18f, h * 0.62f, w * 0.02f, h * 0.74f, w * 0.08f, h * 0.88f)
            cubicTo(w * 0.12f, h * 0.95f, w * 0.20f, h * 0.92f, w * 0.16f, h * 0.82f)
            cubicTo(w * 0.12f, h * 0.70f, w * 0.22f, h * 0.60f, w * 0.14f, h * 0.48f)
        }
        drawPath(
            path = leftNephron,
            color = nephronLineColor.copy(alpha = baseAlpha * 1.1f),
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Right-side Collecting Duct System & Renal Calyx linework
        val rightDuct = Path().apply {
            moveTo(w * 0.86f, h * 0.06f)
            cubicTo(w * 0.96f, h * 0.14f, w * 0.82f, h * 0.26f, w * 0.92f, h * 0.38f)
            cubicTo(w * 1.02f, h * 0.50f, w * 0.84f, h * 0.64f, w * 0.90f, h * 0.76f)
            cubicTo(w * 0.96f, h * 0.88f, w * 0.82f, h * 0.94f, w * 0.88f, h * 0.98f)
            // Tributary side branches
            moveTo(w * 0.80f, h * 0.22f)
            cubicTo(w * 0.86f, h * 0.24f, w * 0.90f, h * 0.28f, w * 0.92f, h * 0.32f)
            moveTo(w * 0.78f, h * 0.58f)
            cubicTo(w * 0.84f, h * 0.60f, w * 0.88f, h * 0.64f, w * 0.86f, h * 0.70f)
        }
        drawPath(
            path = rightDuct,
            color = secondaryVesselColor.copy(alpha = baseAlpha * 1.1f),
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Left Inflow Circulation Stream
        val leftArcade = Path().apply {
            moveTo(w * 0.08f, h * 0.12f)
            cubicTo(w * 0.18f, h * 0.24f, w * 0.06f, h * 0.38f, w * 0.14f, h * 0.52f)
            cubicTo(w * 0.22f, h * 0.66f, w * 0.06f, h * 0.80f, w * 0.12f, h * 0.94f)
        }
        drawPath(
            path = leftArcade,
            color = primaryVesselColor.copy(alpha = baseAlpha * pulsePhase),
            style = Stroke(width = 2.0.dp.toPx(), cap = StrokeCap.Round)
        )

        // Drifting biological micro-particles along left & right margins
        val p1Y = (flowPhase * h) % h
        val p2Y = ((flowPhase + 0.35f) * h) % h
        val p3Y = ((flowPhase + 0.70f) * h) % h
        drawCircle(
            color = primaryVesselColor.copy(alpha = baseAlpha * 1.6f),
            radius = 2.5.dp.toPx(),
            center = Offset(w * 0.08f, p1Y)
        )
        drawCircle(
            color = secondaryVesselColor.copy(alpha = baseAlpha * 1.6f),
            radius = 2.5.dp.toPx(),
            center = Offset(w * 0.92f, p2Y)
        )
        drawCircle(
            color = arterialColor.copy(alpha = baseAlpha * 1.2f),
            radius = 2.0.dp.toPx(),
            center = Offset(w * 0.50f, p3Y)
        )
    }
}

/**
 * AURA Tactile Claymorphic 3D Card
 * Puffy extruded geometry with double diffused shadows, specular bevel highlight,
 * and tactile spring compression on touch.
 */
@Composable
fun TrackWellCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(26.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = null,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.965f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "clayScale"
    )
    val cardElevation by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 2.dp else elevation,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "clayElevation"
    )

    val bevelBorder = borderColor?.let { BorderStroke(1.dp, it) } ?: BorderStroke(
        1.2.dp,
        Brush.verticalGradient(
            if (isDark) listOf(
                Color.White.copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.06f),
                Color(0x20000000)
            ) else listOf(
                Color.White,
                Color.White.copy(alpha = 0.45f),
                Color(0x0C000000)
            )
        )
    )

    val surfaceGradient = if (isDark) {
        listOf(containerColor, containerColor, Color(0xFF161A20))
    } else {
        listOf(containerColor, containerColor, Color(0xFFF8FAFC))
    }

    Card(
        modifier = modifier
            .scale(cardScale)
            .shadow(
                elevation = cardElevation,
                shape = shape,
                spotColor = if (isDark) Color(0x60000000) else Color(0x18000000),
                ambientColor = if (isDark) Color(0x40000000) else Color(0x08000000)
            )
            .border(bevelBorder, shape)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = interactionSource, indication = null) { onClick() }
                } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(surfaceGradient))
        ) {
            // Top specular gloss line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.75f),
                                Color.Transparent
                            )
                        )
                    )
            )
            content()
        }
    }
}

@Composable
fun AuraCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(26.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = null,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    TrackWellCard(
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        borderColor = borderColor,
        elevation = elevation,
        onClick = onClick,
        content = content
    )
}

/**
 * Editorial Section Header with consistent typography and optional action chip.
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.clickable { onActionClick() }
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Discreet, Elegant Medical Disclaimer Pill / Banner.
 * Respects screen real estate while fulfilling all medical device regulatory safety disclaimers.
 */
@Composable
fun MedicalDisclaimerBanner(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clinical Logging Tool • Not Medical Advice",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (expanded) "Hide" else "Learn more",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = stringResource(id = R.string.medical_disclaimer),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReadingAlertBanner(
    message: String,
    isCritical: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isCritical) SugarRedBg else SugarAmberBg
    val contentColor = if (isCritical) SugarRed else SugarAmber

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = contentColor
            )
        }
    }
}

object PlausibleRanges {
    const val MIN_WEIGHT_KG = 30.0
    const val MAX_WEIGHT_KG = 250.0

    const val MIN_FLUID_ML = 10
    const val MAX_FLUID_ML = 3000

    const val MIN_SUGAR_MGDL = 20
    const val MAX_SUGAR_MGDL = 600

    const val MIN_INSULIN_UNITS = 0.0
    const val MAX_INSULIN_UNITS = 120.0

    fun isPlausibleWeight(weight: Double): Boolean = weight in MIN_WEIGHT_KG..MAX_WEIGHT_KG
    fun isPlausibleFluid(amount: Int): Boolean = amount in MIN_FLUID_ML..MAX_FLUID_ML
    fun isPlausibleSugar(sugar: Int): Boolean = sugar in MIN_SUGAR_MGDL..MAX_SUGAR_MGDL
    fun isPlausibleInsulin(units: Double): Boolean = units in MIN_INSULIN_UNITS..MAX_INSULIN_UNITS
}

@Preview(name = "Components - Light", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Preview(name = "Components - Dark", showBackground = true, backgroundColor = 0xFF0F172A, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ComponentsPreview() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionHeader(
                    title = "Today's Summary",
                    subtitle = "Vitals and fluid intake",
                    actionText = "View All",
                    onActionClick = {}
                )

                TrackWellCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TrackWell Tactile 3D Card",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Puffy extruded claymorphic surface with specular highlight.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                ReadingAlertBanner(
                    message = "Warning: Blood sugar level is elevated (210 mg/dL)",
                    isCritical = false
                )

                ReadingAlertBanner(
                    message = "Critical: Hypoglycemia detected! Take fast-acting carbs.",
                    isCritical = true
                )

                MedicalDisclaimerBanner()
            }
        }
    }
}

@Preview(name = "Biological Background Canvas", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun BiologicalBackgroundCanvasPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            BiologicalBackgroundCanvas(modifier = Modifier.matchParentSize(), isDark = true)
        }
    }
}

/**
 * Claymorphic Skeleton Shimmer modifier placeholder extension for zero pop-in loading transitions.
 */
fun Modifier.claymorphicShimmer(
    visible: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp)
): Modifier = if (!visible) this else this
    .clip(shape)
    .background(
        Brush.linearGradient(
            listOf(
                Color(0xFFF1F5F9),
                Color(0xFFE2E8F0),
                Color(0xFFF1F5F9)
            )
        )
    )
