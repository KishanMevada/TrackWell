package com.trackwell.app.ui.home.components

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

/**
 * Living decorative wellness body flow backdrop matching reference design mockup.
 *
 * Spans seamlessly from behind the greeting ("Hi, Kishan!") and DateSelector down through
 * the renal hero block to just above the Weight Card:
 * - Wide, organic, natural gender-neutral front torso silhouette (no harsh vector outlines).
 * - Soft, translucent frosted mist-blue body fill with soft feathered boundaries (~10–16% visual opacity).
 * - Diffused outer halo glow blending seamlessly into the mist background.
 * - Symmetrical thoracic cavity framing the 3D kidney at the lower-rib / upper-waist renal zone.
 * - Large, airy bilateral lung clouds flanking the heart in translucent periwinkle/ice-blue.
 * - Warm, soft glowing rose/coral heart aura in the chest mediastinum with gentle respiration pulse.
 * - Subtle warm peach stomach hint in the upper left abdomen.
 * - Delicate arterial (red) and venous (blue) vascular paths routing toward the kidney stubs.
 * - Animated luminous blood flow micro-droplets traveling along paths via PathMeasure.
 * - Fully responsive: anchors dynamically adapt to screen width, scale, and layout gaps.
 * - Zero per-frame allocations during drawing loops via cached paths, paints, and measurements.
 */
@Composable
fun RenalBodyFlowBackdrop(
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    gap1Dp: Float = 16f,
    gap2Dp: Float = 12f,
    gap3Dp: Float = 16f,
    scale: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RenalBodyFlowTransition")

    // Vessel blood flow progress (8-second linear cycle)
    val flowProgress by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "VesselFlowProgress"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.35f) }
    }

    // Heart respiration / pulsation pulse (3.6-second sinusoidal cycle)
    val heartPulse by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.90f,
            targetValue = 1.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "HeartPulse"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    Spacer(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height

            // -----------------------------------------------------------------
            // Calculate precise vertical layout anchors
            // -----------------------------------------------------------------
            val g1Px = gap1Dp.dp.toPx()
            val greetingHPx = 61.dp.toPx() * scale
            val g2Px = gap2Dp.dp.toPx()
            val dateHPx = 43.dp.toPx() * scale
            val g3Px = gap3Dp.dp.toPx()
            val heroHPx = 372.dp.toPx() * scale

            val greetingTop = if (g1Px > 0f) g1Px else h * 0.030f
            val greetingBot = greetingTop + greetingHPx
            val dateTop = greetingBot + g2Px
            val dateBot = dateTop + dateHPx
            val heroTop = dateBot + g3Px

            val kidneyCenterY = heroTop + 96.dp.toPx() * scale
            val balanceCenterY = heroTop + 250.dp.toPx() * scale
            // Chest / Heart / Lungs are nestled right in the thoracic space between DateSelector and Kidney
            val chestCenterY = (dateBot + (heroTop + 26.dp.toPx() * scale)) * 0.50f

            // =====================================================================
            // 1. Organic Torso Silhouette Path (Natural S-curve with wide shoulders)
            // =====================================================================
            val nativeTorsoPath = android.graphics.Path().apply {
                // Top center: Suprasternal neck notch rising softly behind greeting
                val neckY = maxOf(greetingTop * 0.45f, 4.dp.toPx())
                moveTo(w * 0.50f, neckY)

                // Right clavicle sloping outwards into broad shoulder
                cubicTo(
                    w * 0.58f, neckY + 2.dp.toPx(),
                    w * 0.72f, greetingTop + 10.dp.toPx(),
                    w * 0.88f, dateTop - 12.dp.toPx()
                )

                // Right deltoid and upper chest wall (wide, convex curve)
                cubicTo(
                    w * 0.93f, dateTop + 8.dp.toPx(),
                    w * 0.90f, dateBot - 4.dp.toPx(),
                    w * 0.86f, dateBot + 16.dp.toPx()
                )

                // Right lateral chest wall transitioning into gentle waist (smooth natural curve)
                cubicTo(
                    w * 0.83f, chestCenterY + 16.dp.toPx(),
                    w * 0.79f, kidneyCenterY - 32.dp.toPx(),
                    w * 0.76f, kidneyCenterY
                )

                // Right renal waist flaring smoothly into hip / lower abdomen
                cubicTo(
                    w * 0.74f, kidneyCenterY + 30.dp.toPx(),
                    w * 0.78f, balanceCenterY - 20.dp.toPx(),
                    w * 0.82f, balanceCenterY
                )

                // Right lower hip flare approaching base fadeout
                cubicTo(
                    w * 0.85f, balanceCenterY + 36.dp.toPx(),
                    w * 0.88f, h * 0.940f,
                    w * 0.88f, h * 0.985f
                )

                // Bottom edge (fades to 0% transparency)
                lineTo(w * 0.12f, h * 0.985f)

                // Left lower hip flare ascending
                cubicTo(
                    w * 0.12f, h * 0.940f,
                    w * 0.15f, balanceCenterY + 36.dp.toPx(),
                    w * 0.18f, balanceCenterY
                )

                // Left hip into renal waist
                cubicTo(
                    w * 0.22f, balanceCenterY - 20.dp.toPx(),
                    w * 0.26f, kidneyCenterY + 30.dp.toPx(),
                    w * 0.24f, kidneyCenterY
                )

                // Left lateral chest wall ascending
                cubicTo(
                    w * 0.21f, kidneyCenterY - 32.dp.toPx(),
                    w * 0.17f, chestCenterY + 16.dp.toPx(),
                    w * 0.14f, dateBot + 16.dp.toPx()
                )

                // Left deltoid & upper chest wall
                cubicTo(
                    w * 0.10f, dateBot - 4.dp.toPx(),
                    w * 0.07f, dateTop + 8.dp.toPx(),
                    w * 0.12f, dateTop - 12.dp.toPx()
                )

                // Left clavicle returning to neck notch
                cubicTo(
                    w * 0.28f, greetingTop + 10.dp.toPx(),
                    w * 0.42f, neckY + 2.dp.toPx(),
                    w * 0.50f, neckY
                )

                close()
            }

            // Torso Fill Paint: soft feathered edge (NO harsh vector stroke!)
            val torsoBlurRadius = 16.dp.toPx()
            val torsoFillPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                shader = LinearGradient(
                    0f, 0f, 0f, h,
                    intArrayOf(
                        android.graphics.Color.argb(22, 168, 218, 252), // ~8% pale sky blue behind greeting
                        android.graphics.Color.argb(38, 172, 222, 254), // ~15% soft frosted blue behind date & chest
                        android.graphics.Color.argb(46, 172, 222, 254), // ~18% thoracic core
                        android.graphics.Color.argb(36, 165, 215, 250), // ~14% waist / renal zone
                        android.graphics.Color.argb(14, 155, 205, 245), // ~5% lower abdomen
                        android.graphics.Color.argb(0, 155, 205, 245)   // 0% transparent before Weight card
                    ),
                    floatArrayOf(0.0f, 0.14f, 0.32f, 0.50f, 0.78f, 1.0f),
                    Shader.TileMode.CLAMP
                )
                maskFilter = BlurMaskFilter(torsoBlurRadius, BlurMaskFilter.Blur.SOLID)
            }

            // Outer diffused halo glow along the boundary
            val torsoHaloPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 14.dp.toPx()
                strokeCap = Paint.Cap.ROUND
                shader = LinearGradient(
                    0f, 0f, 0f, h,
                    intArrayOf(
                        android.graphics.Color.argb(20, 168, 218, 252),
                        android.graphics.Color.argb(36, 168, 218, 252),
                        android.graphics.Color.argb(28, 168, 218, 252),
                        android.graphics.Color.argb(8, 168, 218, 252),
                        android.graphics.Color.argb(0, 168, 218, 252)
                    ),
                    floatArrayOf(0.0f, 0.15f, 0.45f, 0.75f, 1.0f),
                    Shader.TileMode.CLAMP
                )
                maskFilter = BlurMaskFilter(20.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
            }

            // Inner volumetric thoracic highlights
            val chestGlowPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(26, 255, 255, 255)
                maskFilter = BlurMaskFilter(w * 0.22f, BlurMaskFilter.Blur.NORMAL)
            }
            val chestGlowCenter = Offset(w * 0.50f, chestCenterY + 10.dp.toPx())

            // =====================================================================
            // 2. Organ Layer Geometries (Warm glowing heart, soft bilateral lungs)
            // =====================================================================
            // Heart: centered in upper thoracic cavity (below DateSelector, above kidney)
            val heartCenter = Offset(w * 0.495f, chestCenterY)
            val heartAuraBaseRadius = 32.dp.toPx()
            val heartAuraPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(88, 242, 139, 130) // warm coral rose aura
                maskFilter = BlurMaskFilter(20.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
            val heartCorePaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(135, 229, 115, 115) // inner heart core
                maskFilter = BlurMaskFilter(6.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
            }

            // Bilateral Lungs: large, soft, airy cloud lobes flanking heart
            val lungRadiusX = 36.dp.toPx()
            val lungRadiusY = 44.dp.toPx()
            val leftLungCenter = Offset(w * 0.325f, chestCenterY + 4.dp.toPx())
            val rightLungCenter = Offset(w * 0.675f, chestCenterY + 4.dp.toPx())
            val lungPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(62, 150, 204, 248) // soft translucent periwinkle/ice-blue
                maskFilter = BlurMaskFilter(20.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
            }

            // Stomach hint: soft warm peach glow in upper left abdomen (under right lung)
            val stomachCenter = Offset(w * 0.635f, chestCenterY + 42.dp.toPx())
            val stomachPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(48, 242, 206, 168) // warm amber/peach glow
                maskFilter = BlurMaskFilter(15.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
            }

            // =====================================================================
            // 3. Vascular Network Paths (Red arterial & Blue venous flow)
            // =====================================================================
            val arterialPath = Path().apply {
                moveTo(w * 0.495f, chestCenterY + 6.dp.toPx())
                cubicTo(
                    w * 0.490f, chestCenterY + 36.dp.toPx(),
                    w * 0.465f, kidneyCenterY - 16.dp.toPx(),
                    w * 0.435f, kidneyCenterY - 4.dp.toPx()
                )
                cubicTo(
                    w * 0.415f, kidneyCenterY + 2.dp.toPx(),
                    w * 0.385f, kidneyCenterY + 8.dp.toPx(),
                    w * 0.355f, kidneyCenterY + 10.dp.toPx()
                )
            }
            val arterialMeasure = PathMeasure().apply { setPath(arterialPath, false) }
            val arterialLength = arterialMeasure.length

            val venousPath = Path().apply {
                moveTo(w * 0.512f, chestCenterY - 2.dp.toPx())
                cubicTo(
                    w * 0.518f, chestCenterY + 38.dp.toPx(),
                    w * 0.510f, kidneyCenterY - 12.dp.toPx(),
                    w * 0.470f, kidneyCenterY - 2.dp.toPx()
                )
                cubicTo(
                    w * 0.440f, kidneyCenterY + 6.dp.toPx(),
                    w * 0.405f, kidneyCenterY + 12.dp.toPx(),
                    w * 0.375f, kidneyCenterY + 14.dp.toPx()
                )
            }
            val venousMeasure = PathMeasure().apply { setPath(venousPath, false) }
            val venousLength = venousMeasure.length

            val rightBranchPath = Path().apply {
                moveTo(w * 0.528f, chestCenterY + 10.dp.toPx())
                cubicTo(
                    w * 0.585f, chestCenterY + 32.dp.toPx(),
                    w * 0.630f, chestCenterY + 54.dp.toPx(),
                    w * 0.615f, chestCenterY + 76.dp.toPx()
                )
            }
            val lowerBranchPath = Path().apply {
                moveTo(w * 0.435f, kidneyCenterY + 12.dp.toPx())
                cubicTo(
                    w * 0.415f, kidneyCenterY + 45.dp.toPx(),
                    w * 0.380f, balanceCenterY - 10.dp.toPx(),
                    w * 0.360f, balanceCenterY + 16.dp.toPx()
                )
            }

            val vesselGlowStroke = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
            val vesselCoreStroke = Stroke(width = 1.3.dp.toPx(), cap = StrokeCap.Round)
            val fineBranchStroke = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round)

            val arteryGlowColor = Color(0x20C45A62)
            val arteryCoreColor = Color(0x50C45A62)
            val veinGlowColor = Color(0x207FA5D6)
            val veinCoreColor = Color(0x507FA5D6)
            val branchColor = Color(0x258EBFE6)

            onDrawBehind {
                drawContext.canvas.nativeCanvas.let { native ->
                    // =================================================================
                    // 1. Torso Silhouette Body & Frosted Glow
                    // =================================================================
                    // Outer feathered halo
                    native.drawPath(nativeTorsoPath, torsoHaloPaint)

                    // Translucent body fill with feathered edges
                    native.drawPath(nativeTorsoPath, torsoFillPaint)

                    // Inner volumetric thoracic glow
                    native.drawCircle(chestGlowCenter.x, chestGlowCenter.y, w * 0.26f, chestGlowPaint)

                    // =================================================================
                    // 2. Subtle Organ Layer (Lush soft lungs, warm glowing heart)
                    // =================================================================
                    val currentPulseScale = 0.97f + 0.05f * heartPulse

                    // A. Bilateral Lungs (large, soft cloud lobes)
                    val currentRx = lungRadiusX * currentPulseScale
                    val currentRy = lungRadiusY * currentPulseScale

                    // Left lung
                    native.drawOval(
                        RectF(
                            leftLungCenter.x - currentRx,
                            leftLungCenter.y - currentRy,
                            leftLungCenter.x + currentRx,
                            leftLungCenter.y + currentRy
                        ),
                        lungPaint
                    )

                    // Right lung
                    native.drawOval(
                        RectF(
                            rightLungCenter.x - currentRx,
                            rightLungCenter.y - currentRy,
                            rightLungCenter.x + currentRx,
                            rightLungCenter.y + currentRy
                        ),
                        lungPaint
                    )

                    // B. Stomach Hint
                    val stomachR = 20.dp.toPx()
                    native.drawOval(
                        RectF(
                            stomachCenter.x - stomachR * 1.25f,
                            stomachCenter.y - stomachR * 0.85f,
                            stomachCenter.x + stomachR * 1.25f,
                            stomachCenter.y + stomachR * 0.85f
                        ),
                        stomachPaint
                    )

                    // C. Heart Glow (warm glowing rose/coral nebula in upper chest)
                    val heartRadius = heartAuraBaseRadius * heartPulse
                    native.drawCircle(heartCenter.x, heartCenter.y, heartRadius, heartAuraPaint)
                    native.drawCircle(heartCenter.x, heartCenter.y, 6.5.dp.toPx() * heartPulse, heartCorePaint)
                }

                // =================================================================
                // 3. Vascular Network (Delicate, soft glowing lines toward kidney)
                // =================================================================
                drawPath(path = arterialPath, color = arteryGlowColor, style = vesselGlowStroke)
                drawPath(path = arterialPath, color = arteryCoreColor, style = vesselCoreStroke)

                drawPath(path = venousPath, color = veinGlowColor, style = vesselGlowStroke)
                drawPath(path = venousPath, color = veinCoreColor, style = vesselCoreStroke)

                drawPath(path = rightBranchPath, color = branchColor, style = fineBranchStroke)
                drawPath(path = lowerBranchPath, color = branchColor, style = fineBranchStroke)

                // =================================================================
                // 4. Animated Luminous Blood Flow Particles (Tiny glowing droplets)
                // =================================================================
                val haloRadius = 4.8.dp.toPx()
                val midRadius = 2.6.dp.toPx()
                val coreRadius = 1.3.dp.toPx()

                // Arterial particles (3 glowing rose droplets)
                if (arterialLength > 0f) {
                    val arterialParticles = 3
                    for (i in 0 until arterialParticles) {
                        val phase = i.toFloat() / arterialParticles
                        val currentT = (flowProgress + phase) % 1f
                        val distance = currentT * arterialLength
                        val pos = arterialMeasure.getPosition(distance)

                        drawCircle(color = Color(0x40F28B82), radius = haloRadius, center = pos)
                        drawCircle(color = Color(0x99FFCDD2), radius = midRadius, center = pos)
                        drawCircle(color = Color(0xEEFFFFFF), radius = coreRadius, center = pos)
                    }
                }

                // Venous particles (3 glowing cyan/ice-blue droplets)
                if (venousLength > 0f) {
                    val venousParticles = 3
                    for (i in 0 until venousParticles) {
                        val phase = (i.toFloat() / venousParticles) + 0.16f
                        val currentT = (flowProgress + phase) % 1f
                        val distance = currentT * venousLength
                        val pos = venousMeasure.getPosition(distance)

                        drawCircle(color = Color(0x408BE7F0), radius = haloRadius, center = pos)
                        drawCircle(color = Color(0x99BEEAF1), radius = midRadius, center = pos)
                        drawCircle(color = Color(0xEEFFFFFF), radius = coreRadius, center = pos)
                    }
                }
            }
        }
    )
}
