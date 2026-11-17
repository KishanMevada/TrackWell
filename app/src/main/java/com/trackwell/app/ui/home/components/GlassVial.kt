package com.trackwell.app.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.ui.theme.InterFontFamily
import com.trackwell.app.ui.theme.LocalDesignScale
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography
import com.trackwell.app.ui.theme.ds
import com.trackwell.app.ui.theme.dsp
import kotlin.math.sin

enum class VialType {
    FLUID_INTAKE,
    URINE_OUTPUT
}

/**
 * Glass Vial capsule: 64 x 221 design dp.
 * Outer capsule: Raised-L, top corners 14 ds, bottom corners 32 ds.
 * Inner glass tube: 50 x 205 ds at (7.ds, 8.ds), top radius 8 ds, bottom radius 25 ds.
 * Rotated labels: VerticalText anchored at block-local y = 292 ds (y = 141 ds inside vial).
 * Add button: 40 x 57 ds at (12.ds, 151.ds) inside vial (block-local y = 302 ds).
 */
@Composable
fun GlassVial(
    type: VialType,
    currentMl: Int,
    goalMl: Int,
    fillLevel: Float, // 0.0 to 1.0
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIntake = type == VialType.FLUID_INTAKE
    val title = if (isIntake) "Fluid Intake" else "Urine Output"
    val buttonSubtitle = if (isIntake) "Water" else "Urine"
    val formattedCurrent = "%,d".format(currentMl)
    val formattedGoal = "%,d".format(goalMl)
    val valueText = "$formattedCurrent ml / $formattedGoal ml"
    val contentDesc = "$title, $formattedCurrent of $formattedGoal millilitres"

    // 4s idle wave animation with 3 ds amplitude
    val waveAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        waveAnim.animateTo(
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = modifier
            .size(width = 64.ds, height = 221.ds)
            .semantics { contentDescription = contentDesc }
            .neuRaisedL(
                customTopRadius = 14.ds,
                customBottomRadius = 32.ds
            )
            .clip(
                RoundedCornerShape(
                    topStart = 14.ds,
                    topEnd = 14.ds,
                    bottomStart = 32.ds,
                    bottomEnd = 32.ds
                )
            )
    ) {
        // 1. Inner Glass Tube (50 x 205 ds) at (7.ds, 8.ds), top radius 8 ds, bottom radius 25 ds
        Box(
            modifier = Modifier
                .offset(x = 7.ds, y = 8.ds)
                .size(width = 50.ds, height = 205.ds)
                .clip(
                    RoundedCornerShape(
                        topStart = 8.ds,
                        topEnd = 8.ds,
                        bottomStart = 25.ds,
                        bottomEnd = 25.ds
                    )
                )
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    // 1. Empty glass background gradient
                    val glassBrush = if (isIntake) {
                        Brush.verticalGradient(TargetTokens.waterGlassColors)
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFFF7F2EB), TargetTokens.urineGlass))
                    }
                    drawRect(brush = glassBrush)

                    // 2. Liquid fill
                    val liquidHeight = h * fillLevel.coerceIn(0.05f, 0.98f)
                    val liquidTopY = h - liquidHeight

                    val waveAmp = 3.dp.toPx()
                    val liquidPath = Path().apply {
                        moveTo(0f, liquidTopY)
                        val wavePhase = waveAnim.value
                        val steps = 20
                        for (i in 1..steps) {
                            val px = (w / steps) * i
                            val py = liquidTopY + (sin(wavePhase + (px / w) * Math.PI * 2) * waveAmp).toFloat()
                            lineTo(px, py)
                        }
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }

                    val liquidBrush = if (isIntake) {
                        Brush.verticalGradient(
                            listOf(
                                TargetTokens.waterLiquidTop,
                                TargetTokens.waterLiquidBody,
                                TargetTokens.waterLiquidBottom
                            ),
                            startY = liquidTopY,
                            endY = h
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                TargetTokens.urineLiquidTop,
                                TargetTokens.urineLiquidBody,
                                TargetTokens.urineLiquidBottom
                            ),
                            startY = liquidTopY,
                            endY = h
                        )
                    }
                    drawPath(path = liquidPath, brush = liquidBrush)

                    // 3. Surface lighter wave band (3 ds band at liquid surface)
                    val surfaceHighlightPath = Path().apply {
                        moveTo(0f, liquidTopY)
                        val wavePhase = waveAnim.value
                        val steps = 20
                        for (i in 1..steps) {
                            val px = (w / steps) * i
                            val py = liquidTopY + (sin(wavePhase + (px / w) * Math.PI * 2) * waveAmp).toFloat()
                            lineTo(px, py)
                        }
                    }
                    drawPath(
                        path = surfaceHighlightPath,
                        color = Color.White.copy(alpha = 0.45f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                    )

                    // 4. Inner vertical white sheen streak (4 ds wide at 35% along the left inner edge)
                    drawRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.0f)
                            )
                        ),
                        topLeft = Offset(1.dp.toPx(), 0f),
                        size = Size(4.dp.toPx(), h)
                    )
                }
        )

        // 2. Rotated Labels with VerticalText and auto-fit
        // Title centered at x = 20.5 ds, Value centered at x = 43.5 ds
        // Anchored at bottom end y = 141 ds inside vial (block-local y = 292 ds)
        // Max available length: 119 ds
        VerticalText(
            text = title,
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TargetTokens.textPrimary
            ),
            centerX = 20.5.ds,
            anchorBottomY = 141.ds,
            maxLength = 119.ds
        )

        VerticalText(
            text = valueText,
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = TargetTokens.textPrimary
            ),
            centerX = 43.5.ds,
            anchorBottomY = 141.ds,
            maxLength = 119.ds
        )

        // 3. Add Button: 40 x 57 ds, centered at x = 12 ds, y = 151 ds inside vial
        Box(
            modifier = Modifier
                .offset(x = 12.ds, y = 151.ds)
                .size(width = 40.ds, height = 57.ds)
                .clip(RoundedCornerShape(20.ds))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddClick
                )
                .drawBehind {
                    val btnBrush = if (isIntake) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF9EE8F5),
                                Color(0xFF6ED4E5),
                                Color(0xFF4CA0C4)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFF7D594),
                                Color(0xFFEBBE6C),
                                Color(0xFFD69A45)
                            )
                        )
                    }
                    drawRoundRect(
                        brush = btnBrush,
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.6f),
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = TargetTypography.addButtonPlus.copy(
                    fontSize = 28.dsp,
                    fontWeight = FontWeight.Bold,
                    color = TargetTokens.textPrimary
                )
            )
        }
    }
}

/**
 * VerticalText: measures text horizontally, draws rotated -90 degrees (bottom to top).
 * Auto-fits if text exceeds maxLength: steps down font size in 0.25sp steps down to 85% of design size.
 * If it still doesn't fit, compacts units (e.g. 12,345 ml -> 12.3 L).
 * Centers horizontally at centerX, anchors bottom of text at anchorBottomY.
 */
@Composable
fun VerticalText(
    text: String,
    style: TextStyle,
    centerX: Dp,
    anchorBottomY: Dp,
    maxLength: Dp,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val designScale = LocalDesignScale.current

    val initialSp = style.fontSize.value * designScale
    val minSp = initialSp * 0.85f
    val maxLengthPx = with(density) { maxLength.toPx() }

    // Auto-fit loop: step down in 0.25sp steps
    var currentSp = initialSp
    var candidateText = text
    var measured = textMeasurer.measure(
        text = candidateText,
        style = style.copy(fontSize = currentSp.sp),
        softWrap = false,
        maxLines = 1
    )

    while (measured.size.width > maxLengthPx && currentSp > minSp) {
        currentSp = (currentSp - 0.25f).coerceAtLeast(minSp)
        measured = textMeasurer.measure(
            text = candidateText,
            style = style.copy(fontSize = currentSp.sp),
            softWrap = false,
            maxLines = 1
        )
    }

    // Fallback to compact unit if still exceeds
    if (measured.size.width > maxLengthPx && candidateText.contains("ml")) {
        candidateText = candidateText.replace(" ml", "").replace(",", "")
        measured = textMeasurer.measure(
            text = candidateText,
            style = style.copy(fontSize = currentSp.sp),
            softWrap = false,
            maxLines = 1
        )
    }

    val effectiveStyle = style.copy(fontSize = currentSp.sp)

    Layout(
        content = {
            Text(
                text = candidateText,
                style = effectiveStyle,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        },
        modifier = modifier
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(
            constraints.copy(
                minWidth = 0,
                maxWidth = Constraints.Infinity,
                minHeight = 0,
                maxHeight = Constraints.Infinity
            )
        )

        val layoutWidth = placeable.height
        val layoutHeight = placeable.width

        val xPx = with(density) { centerX.toPx() } - (layoutWidth / 2f)
        val yPx = with(density) { anchorBottomY.toPx() } - layoutHeight

        layout(layoutWidth, layoutHeight) {
            placeable.placeWithLayer(
                x = (xPx - (placeable.width - placeable.height) / 2f).toInt(),
                y = (yPx + (placeable.width - placeable.height) / 2f).toInt(),
                layerBlock = {
                    rotationZ = -90f
                }
            )
        }
    }
}
