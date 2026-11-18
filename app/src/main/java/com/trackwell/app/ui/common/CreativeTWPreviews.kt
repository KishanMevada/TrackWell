package com.trackwell.app.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
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
import kotlin.math.sin

// ===================================================================
// OPTION 13: TW Vascular Heartbeat Monogram (Animated ECG Flow in TW)
// ===================================================================

@Preview(name = "Option 13: TW Vascular Heartbeat", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption13VascularTW() {
    val transition = rememberInfiniteTransition(label = "ecgFlow")
    val pulseProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated 3D TW Vascular Monogram Box
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                modifier = Modifier
                    .size(150.dp)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(36.dp), spotColor = Color(0xFF38BDF8))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Animated ECG pulse passing through TW
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        val ecgPath = Path().apply {
                            moveTo(w * 0.15f, h * 0.50f)
                            lineTo(w * 0.35f, h * 0.50f)
                            lineTo(w * 0.42f, h * 0.30f)
                            lineTo(w * 0.50f, h * 0.70f)
                            lineTo(w * 0.58f, h * 0.40f)
                            lineTo(w * 0.65f, h * 0.50f)
                            lineTo(w * 0.85f, h * 0.50f)
                        }

                        drawPath(
                            path = ecgPath,
                            color = Color(0xFFFB7185).copy(alpha = 0.4f),
                            style = Stroke(width = 4.dp.toPx())
                        )

                        // Animated Luminous Heartbeat Pulse Dot
                        val dotX = w * 0.15f + (w * 0.70f * pulseProgress)
                        drawCircle(
                            color = Color(0xFFFB7185),
                            radius = 6.dp.toPx(),
                            center = Offset(dotX, h * 0.50f)
                        )
                    }

                    Text(
                        text = "TW",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-2).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TrackWell",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "VITAL VASCULAR & CARDIOPULMONARY COMPANION",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFB7185),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFB7185), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("100% Offline-First • FHIR R4 Compliant", fontSize = 11.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Medium)
        }
    }
}

// ===================================================================
// OPTION 14: TW Renal Fluid Level Monogram (Animated Liquid in TW)
// ===================================================================

@Preview(name = "Option 14: TW Liquid Wave Monogram", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption14RenalFluidTW() {
    val transition = rememberInfiniteTransition(label = "fluidWave")
    val waveAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // 3D Monogram Card with Animated Liquid Fill
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = Color.White,
                shadowElevation = 18.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.size(150.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Animated Liquid Sine Wave Layer inside Monogram Card
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val liquidTopY = h * 0.45f
                        val waveAmp = 4.dp.toPx()

                        val liquidPath = Path().apply {
                            moveTo(0f, liquidTopY)
                            val steps = 20
                            for (i in 1..steps) {
                                val px = (w / steps) * i
                                val py = liquidTopY + (sin(waveAnim + (px / w) * Math.PI * 2) * waveAmp).toFloat()
                                lineTo(px, py)
                            }
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }

                        drawPath(
                            path = liquidPath,
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFE0F2FE).copy(alpha = 0.8f), Color(0xFF0284C7).copy(alpha = 0.3f))
                            )
                        )
                    }

                    Text(
                        text = "TW",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0284C7),
                        letterSpacing = (-2).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TrackWell",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.8).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "24H FLUID BALANCE & URINE SENTINEL",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ===================================================================
// OPTION 15: Anatomical TW Body Silhouette Monogram
// ===================================================================

@Preview(name = "Option 15: Anatomical Body TW", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption15AnatomicalTW() {
    val transition = rememberInfiniteTransition(label = "chestRespiration")
    val respirationScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "respirationScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF0284C7), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // TW Monogram styled into an Anatomical Body Heart Crest
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF7DD3FC).copy(alpha = 0.5f)),
                modifier = Modifier
                    .size(140.dp)
                    .shadow(elevation = 28.dp, shape = CircleShape, spotColor = Color(0xFF38BDF8))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon3DHeart(size = (68 * respirationScale).dp)

                    Text(
                        text = "TW",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TrackWell",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "HOLISTIC BODY HEALTH & CARE SENTINEL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7DD3FC),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ===================================================================
// OPTION 16: TrackWell Glycemic Waveform Ribbon
// ===================================================================

@Preview(name = "Option 16: Glycemic Wave Wordmark", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption16GlycemicWaveWordmark() {
    val transition = rememberInfiniteTransition(label = "glycemicWave")
    val dotOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon3DSugarDrop(size = 64.dp)

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "TrackWell",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.8).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Glycemic Safe Zone Waveform Line
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(40.dp)
            ) {
                val w = size.width
                val h = size.height

                val wavePath = Path().apply {
                    moveTo(0f, h * 0.50f)
                    lineTo(w * 0.25f, h * 0.50f)
                    lineTo(w * 0.40f, h * 0.20f)
                    lineTo(w * 0.55f, h * 0.80f)
                    lineTo(w * 0.70f, h * 0.50f)
                    lineTo(w, h * 0.50f)
                }

                drawPath(
                    path = wavePath,
                    color = Color(0xFFF3E8FF).copy(alpha = 0.5f),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Glowing Glycemic Dot
                val x = w * dotOffset
                drawCircle(
                    color = Color(0xFF9333EA),
                    radius = 6.dp.toPx(),
                    center = Offset(x, h * 0.50f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "GLYCEMIC CORRIDOR & INSULIN SENTINEL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC084FC),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
