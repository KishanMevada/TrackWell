package com.trackwell.app.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
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

/**
 * Reusable 3D Kidney Graphic with contact shadow and organic breathing animation.
 * Rendered on open background without card surface or container boundary.
 *
 * Local coordinates inside 210 x 359 ds container:
 * - Contact shadow under kidney: ellipse 72 x 16 ds centered at (114.ds, 189.ds)
 * - 3D Kidney graphic: 112 x 157 ds at (48.ds, 26.ds)
 */
@Composable
fun KidneyGraphic(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(width = 210.ds, height = 359.ds)
    ) {
        // 1. Soft diffuse contact shadow under the kidney lower lobe (soft diffuse mist ~12% alpha, 14 ds blur)
        // Centered at (114.ds, 189.ds), size 72 x 16 ds
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
                            color = android.graphics.Color.argb(30, 125, 52, 59) // Soft mauve/burgundy diffuse shadow
                            maskFilter = android.graphics.BlurMaskFilter(
                                shadowBlur.toPx(),
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                        val cxPx = cx.toPx()
                        val cyPx = cy.toPx()
                        val rxPx = rx.toPx()
                        val ryPx = ry.toPx()
                        nativeCanvas.drawOval(
                            android.graphics.RectF(cxPx - rxPx, cyPx - ryPx, cxPx + rxPx, cyPx + ryPx),
                            shadowPaint
                        )
                    }
                }
        )

        // 2. 3D Kidney Graphic (112 x 157 ds) at local (48.ds, 26.ds)
        // With organic respiration breathing pulse (3.8s sinusoidal cycle)
        val infiniteTransition = rememberInfiniteTransition(label = "KidneyBreathing")
        val breathingScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.022f, // 2.2% subtle volumetric breathing
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "BreathingScale"
        )

        Image(
            painter = painterResource(id = R.drawable.kidney_3d),
            contentDescription = "Kidney Graphic",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(x = 48.ds, y = 26.ds)
                .size(width = 112.ds, height = 157.ds)
                .graphicsLayer {
                    scaleX = breathingScale
                    scaleY = breathingScale
                    transformOrigin = TransformOrigin(0.45f, 0.40f)
                }
        )
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(width = 210.ds, height = 359.ds)
    ) {
        // 1. 'Net renal balance' label at baseline 228 ds -> top offset ~210 ds
        Text(
            text = "Net renal balance",
            style = TargetTypography.netBalanceLabel.copy(
                fontSize = 17.dsp
            ),
            modifier = Modifier
                .offset(y = 210.ds)
                .align(Alignment.TopCenter),
            textAlign = TextAlign.Center
        )

        // 2. Value and unit (+830 ml) at digit top 248 ds, baseline 278 ds
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

        // 3. Status Chip (145 x 31 ds) at (32.ds, 297.ds) inside hero space
        Box(
            modifier = Modifier
                .offset(x = 32.ds, y = 297.ds)
                .size(width = 145.ds, height = 31.ds)
                .neuChip(chipColor = statusColor, cornerRadius = 15.5.ds)
                .clip(RoundedCornerShape(15.5.ds)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusText,
                style = TargetTypography.chipText.copy(
                    fontSize = 17.dsp
                ),
                textAlign = TextAlign.Center
            )
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
            statusColor = statusColor
        )
    }
}
