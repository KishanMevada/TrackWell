package com.trackwell.app.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import kotlin.math.sin

// ===================================================================
// GREAT OPTION 14: Dual-Liquid Wave TW Monogram Vault
// ===================================================================

@Preview(name = "Great Option 14: Dual Liquid TW", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenGreatOption14() {
    val transition = rememberInfiniteTransition(label = "dualWave")
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val bubbleY by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        // Living vascular background matching Home & Settings
        com.trackwell.app.ui.home.components.VascularBackground(modifier = Modifier.fillMaxSize())
        RenalBodyFlowBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Extruded 3D Claymorphic Card with Dual Liquid Wave Fill
            Surface(
                shape = RoundedCornerShape(38.dp),
                color = Color.White,
                shadowElevation = 22.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF1F5F9)),
                modifier = Modifier
                    .size(160.dp)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(38.dp), spotColor = Color(0x300284C7))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Dual-Tone Liquid Wave Fill (Left: Aqua Water, Right: Topaz Output)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val liquidTopY = h * 0.42f
                        val waveAmp = 5.dp.toPx()

                        // Aqua Intake Wave (Left)
                        val aquaPath = Path().apply {
                            moveTo(0f, liquidTopY)
                            val steps = 20
                            for (i in 0..steps) {
                                val px = (w / steps) * i
                                val py = liquidTopY + (sin(wavePhase + (px / w) * Math.PI * 2) * waveAmp).toFloat()
                                lineTo(px, py)
                            }
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }

                        drawPath(
                            path = aquaPath,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0284C7).copy(alpha = 0.35f),
                                    Color(0xFF38BDF8).copy(alpha = 0.25f),
                                    Color(0xFFF59E0B).copy(alpha = 0.30f)
                                )
                            )
                        )

                        // Floating Hydro Micro-Bubbles
                        drawCircle(
                            color = Color(0xFF38BDF8).copy(alpha = 0.6f),
                            radius = 3.dp.toPx(),
                            center = Offset(w * 0.35f, h * bubbleY)
                        )
                        drawCircle(
                            color = Color(0xFFF59E0B).copy(alpha = 0.6f),
                            radius = 2.5.dp.toPx(),
                            center = Offset(w * 0.68f, h * ((bubbleY + 0.3f) % 1.0f))
                        )
                    }

                    // Bold TW Monogram Overlay with Dual Gradient Accent
                    Text(
                        text = "TW",
                        fontSize = 62.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        letterSpacing = (-2).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Main Brand Title
            Text(
                text = "TrackWell",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "24H FLUID BALANCE & RENAL SENTINEL",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7),
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Optimal Balance Active Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Optimal 24h Organ Balance Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                }
            }
        }

        // Bottom Security & Standard Compliance Badge
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("100% Offline-First • FHIR R4 Compliant", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
        }
    }
}

// ===================================================================
// MULTI-ORGAN OPTION 14: Lungs, Heart, Bilateral Kidneys, Bladder
// ===================================================================

@Composable
private fun MultiOrganAnatomicalBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Male Torso Outline
        val torsoPath = Path().apply {
            moveTo(w * 0.50f, h * 0.08f)
            cubicTo(w * 0.60f, h * 0.09f, w * 0.76f, h * 0.14f, w * 0.90f, h * 0.22f)
            cubicTo(w * 0.94f, h * 0.32f, w * 0.88f, h * 0.42f, w * 0.82f, h * 0.48f)
            cubicTo(w * 0.76f, h * 0.58f, w * 0.72f, h * 0.68f, w * 0.74f, h * 0.82f)
            lineTo(w * 0.26f, h * 0.82f)
            cubicTo(w * 0.28f, h * 0.68f, w * 0.24f, h * 0.58f, w * 0.18f, h * 0.48f)
            cubicTo(w * 0.12f, h * 0.42f, w * 0.06f, h * 0.32f, w * 0.10f, h * 0.22f)
            cubicTo(w * 0.24f, h * 0.14f, w * 0.40f, h * 0.09f, w * 0.50f, h * 0.08f)
        }

        drawPath(
            path = torsoPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFE0F2FE).copy(alpha = 0.40f), Color(0xFFBAE6FD).copy(alpha = 0.20f), Color.Transparent)
            )
        )
        drawPath(path = torsoPath, color = Color(0xFF0284C7).copy(alpha = 0.25f), style = Stroke(width = 2.dp.toPx()))

        // 2. Lungs (Bilateral Periwinkle Lobes)
        val leftLung = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(w * 0.22f, h * 0.24f, w * 0.40f, h * 0.38f))
        }
        val rightLung = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(w * 0.60f, h * 0.24f, w * 0.78f, h * 0.38f))
        }
        drawPath(leftLung, color = Color(0xFF818CF8).copy(alpha = 0.25f))
        drawPath(rightLung, color = Color(0xFF818CF8).copy(alpha = 0.25f))

        // 3. Heart Aura (Mediastinum)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFB7185).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.48f, h * 0.30f),
                radius = 110f
            ),
            radius = 110f,
            center = Offset(w * 0.48f, h * 0.30f)
        )

        // 4. Bilateral Kidneys (Left & Right Kidney outlines at lower waist)
        val leftKidney = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(w * 0.24f, h * 0.52f, w * 0.36f, h * 0.62f))
        }
        val rightKidney = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(w * 0.64f, h * 0.52f, w * 0.76f, h * 0.62f))
        }
        drawPath(leftKidney, color = Color(0xFFF43F5E).copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx()))
        drawPath(rightKidney, color = Color(0xFFF43F5E).copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx()))

        // 5. Bladder Fluid Reservoir (Pelvic Zone)
        val bladder = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(w * 0.40f, h * 0.70f, w * 0.60f, h * 0.78f))
        }
        drawPath(bladder, color = Color(0xFF38BDF8).copy(alpha = 0.25f))
        drawPath(bladder, color = Color(0xFF0284C7).copy(alpha = 0.35f), style = Stroke(width = 1.5.dp.toPx()))
    }
}

@Preview(name = "Great Option 14: Multi-Organ Vault", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenMultiOrganOption14() {
    val transition = rememberInfiniteTransition(label = "dualWave")
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val bubbleY by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        // Living vascular background
        com.trackwell.app.ui.home.components.VascularBackground(modifier = Modifier.fillMaxSize())

        // Multi-Organ Anatomical Backdrop (Lungs, Heart, Kidneys, Bladder)
        MultiOrganAnatomicalBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Extruded 3D Claymorphic Card with Dual Liquid Wave Fill
            Surface(
                shape = RoundedCornerShape(38.dp),
                color = Color.White,
                shadowElevation = 22.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF1F5F9)),
                modifier = Modifier
                    .size(160.dp)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(38.dp), spotColor = Color(0x300284C7))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val liquidTopY = h * 0.42f
                        val waveAmp = 5.dp.toPx()

                        val aquaPath = Path().apply {
                            moveTo(0f, liquidTopY)
                            val steps = 20
                            for (i in 0..steps) {
                                val px = (w / steps) * i
                                val py = liquidTopY + (sin(wavePhase + (px / w) * Math.PI * 2) * waveAmp).toFloat()
                                lineTo(px, py)
                            }
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }

                        drawPath(
                            path = aquaPath,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0284C7).copy(alpha = 0.35f),
                                    Color(0xFF38BDF8).copy(alpha = 0.25f),
                                    Color(0xFFF59E0B).copy(alpha = 0.30f)
                                )
                            )
                        )

                        drawCircle(
                            color = Color(0xFF38BDF8).copy(alpha = 0.6f),
                            radius = 3.dp.toPx(),
                            center = Offset(w * 0.35f, h * bubbleY)
                        )
                        drawCircle(
                            color = Color(0xFFF59E0B).copy(alpha = 0.6f),
                            radius = 2.5.dp.toPx(),
                            center = Offset(w * 0.68f, h * ((bubbleY + 0.3f) % 1.0f))
                        )
                    }

                    Text(
                        text = "TW",
                        fontSize = 62.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        letterSpacing = (-2).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TrackWell",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "LUNGS • HEART • KIDNEYS • BLADDER • SUGAR",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7),
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Multi-Organ Vital Systems Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("100% Offline-First • FHIR R4 Compliant", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
        }
    }
}
