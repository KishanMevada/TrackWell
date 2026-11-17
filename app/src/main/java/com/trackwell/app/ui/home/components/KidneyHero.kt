package com.trackwell.app.ui.home.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.trackwell.app.R
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography
import com.trackwell.app.ui.theme.ds
import com.trackwell.app.ui.theme.dsp
import kotlin.math.PI
import kotlin.math.sin

// Smooth sine easing for organic motion
private val SineInOut: Easing = Easing { t ->
    ((-sin(t * PI + PI) + 1) / 2).toFloat()
}

/**
 * Reusable 3D Kidney Graphic with multi-layered organic animation:
 *
 * Layer 1 — Breathing scale pulse (3.8s, 2.2% scale, sinusoidal)
 * Layer 2 — Gentle vertical float (4.6s, ±3.5 dp, sine eased)
 * Layer 3 — Subtle rotation wobble (6.0s, ±1.8°, sine eased)
 * Layer 4 — Glowing halo ring (5.2s alpha pulse, 3 concentric rings)
 * Layer 5 — Contact shadow breathes with kidney scale
 *
 * Local coordinates inside 210 × 359 ds container.
 */
@Composable
fun KidneyGraphic(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "KidneyAnimations")

    // --- Layer 1: Visible organic breathing scale (2.2s pulse, 10% volumetric range) ---
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingScale"
    )

    // --- Layer 2: Visible vertical floating bob (2.8s, ±7 dp float) ---
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -7.0f,
        targetValue = 7.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatOffsetY"
    )

    // --- Layer 3: Organic rotational sway (3.4s, ±4.5°) ---
    val rotationWobble by infiniteTransition.animateFloat(
        initialValue = -4.5f,
        targetValue = 4.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RotationWobble"
    )

    // --- Layer 4: Halo glow alpha pulse (2.2s, synchronized with breathing) ---
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloAlpha"
    )

    // --- Layer 5: Halo ring expanding radius (synchronized with breath) ---
    val haloRadius by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloRadius"
    )

    // Shadow scale breathes with kidney scale and float
    val shadowScaleX = 0.85f + (breathingScale - 0.95f) * 1.2f
    val shadowAlpha = (22 + ((breathingScale - 0.95f) / 0.11f * 18).toInt()).coerceIn(16, 45)

    Box(
        modifier = modifier.size(width = 210.ds, height = 359.ds)
    ) {
        // ── Layer 5: Animated contact shadow ──────────────────────────────────────
        val shadowBlur = 14.ds
        val cx = 114.ds
        val cy = 189.ds
        val rx = 36.ds
        val ry = 8.ds

        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawContext.canvas.nativeCanvas.let { nativeCanvas ->
                        val shadowPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            style = android.graphics.Paint.Style.FILL
                            color = android.graphics.Color.argb(shadowAlpha, 125, 52, 59)
                            maskFilter = android.graphics.BlurMaskFilter(
                                shadowBlur.toPx(),
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                        val cxPx = cx.toPx()
                        val cyPx = cy.toPx()
                        val rxPx = rx.toPx() * shadowScaleX
                        val ryPx = ry.toPx()
                        nativeCanvas.drawOval(
                            android.graphics.RectF(cxPx - rxPx, cyPx - ryPx, cxPx + rxPx, cyPx + ryPx),
                            shadowPaint
                        )
                    }
                }
        )

        // ── Unified Floating & Wobbling Kidney Unit ──────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = 48.ds, y = 26.ds)
                .size(width = 112.ds, height = 157.ds)
                .graphicsLayer {
                    translationY = floatOffset * density
                    rotationZ = rotationWobble
                    transformOrigin = TransformOrigin(0.45f, 0.40f)
                }
        ) {
            // Layer 4: Glowing halo rings behind kidney
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val centerX = size.width * 0.45f
                val centerY = size.height * 0.40f
                val baseRadius = (size.width.coerceAtMost(size.height) * 0.42f) * haloRadius

                // Draw 3 concentric softly glowing rings (outermost to innermost)
                listOf(
                    Triple(baseRadius * 1.55f, haloAlpha * 0.35f, 2.8f),
                    Triple(baseRadius * 1.25f, haloAlpha * 0.60f, 2.0f),
                    Triple(baseRadius * 1.00f, haloAlpha * 0.90f, 1.4f)
                ).forEach { (radius, alpha, strokeDp) ->
                    drawCircle(
                        color = Color(0xFFE89A9E).copy(alpha = alpha),
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = strokeDp * density)
                    )
                }

                // Inner warm radial glow (soft filled circle)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE89A9E).copy(alpha = haloAlpha * 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(centerX, centerY),
                        radius = baseRadius * 1.1f
                    ),
                    radius = baseRadius * 1.1f,
                    center = Offset(centerX, centerY)
                )
            }

            // Layers 1–3: Kidney image with breathing scale
            Image(
                painter = painterResource(id = R.drawable.kidney_3d),
                contentDescription = "Kidney Graphic",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = breathingScale
                        scaleY = breathingScale
                        transformOrigin = TransformOrigin(0.45f, 0.40f)
                    }
            )
        }
    }
}

/**
 * Reusable Renal Balance Summary composable.
 * Renders the net-balance label, signed balance value with unit, and status chip
 * directly on the open background without a card container.
 */
@Composable
fun RenalBalanceSummary(
    balanceMl: Int,
    statusText: String,
    statusColor: Color = TargetTokens.chipPeachColors[0],
    onStatusClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(width = 210.ds, height = 359.ds)
    ) {
        // 1. 'Net renal balance' label - lighter text color
        Text(
            text = "Net renal balance",
            style = TargetTypography.netBalanceLabel.copy(
                fontSize = 17.dsp,
                color = TargetTokens.textSecondary
            ),
            modifier = Modifier
                .offset(y = 210.ds)
                .align(Alignment.TopCenter),
            textAlign = TextAlign.Center
        )

        // 2. Value and unit (+830 ml)
        val sign = if (balanceMl >= 0) "+" else ""
        val formattedBal = "%,d".format(balanceMl)
        Row(
            modifier = Modifier
                .offset(y = 236.ds)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$sign$formattedBal",
                style = TargetTypography.netBalanceValue.copy(
                    fontSize = 44.dsp
                )
            )
            Spacer(modifier = Modifier.width(4.ds))
            Text(
                text = "ml",
                style = TargetTypography.netBalanceUnit.copy(
                    fontSize = 24.dsp
                ),
                modifier = Modifier.padding(bottom = 4.ds)
            )
        }

        // 3. Status Chip with DatePicker-styled pill & colored status shadow (clickable to Day Detail)
        Box(
            modifier = Modifier
                .offset(x = 32.ds, y = 297.ds)
                .size(width = 145.ds, height = 31.ds)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(15.5.ds),
                    spotColor = statusColor.copy(alpha = 0.5f),
                    ambientColor = statusColor.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(15.5.ds))
                .background(Color.White)
                .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(15.5.ds))
                .then(
                    if (onStatusClick != null) {
                        Modifier.clickable { onStatusClick() }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.ds)
            ) {
                Text(
                    text = statusText,
                    style = TargetTypography.chipText.copy(
                        fontSize = 14.dsp,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.ds))
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = TargetTokens.chevronSide,
                    modifier = Modifier.size(width = 5.ds, height = 10.ds)
                )
            }
        }
    }
}

/**
 * Cardless Kidney Hero (210 x 359 design dp).
 * Renders on open mist-blue background without the previous raised white card.
 *
 * Composes [KidneyGraphic] and [RenalBalanceSummary] in a unified card-free layout.
 */
@Composable
fun KidneyHero(
    balanceMl: Int,
    statusText: String,
    statusColor: Color = TargetTokens.chipPeachColors[0],
    onStatusClick: (() -> Unit)? = null,
    renderKidneyGraphic: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(width = 210.ds, height = 359.ds)
    ) {
        if (renderKidneyGraphic) {
            KidneyGraphic()
        }
        RenalBalanceSummary(
            balanceMl = balanceMl,
            statusText = statusText,
            statusColor = statusColor,
            onStatusClick = onStatusClick
        )
    }
}
