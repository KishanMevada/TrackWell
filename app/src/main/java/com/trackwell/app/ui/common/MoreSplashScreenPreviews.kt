package com.trackwell.app.ui.common

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop

// ===================================================================
// OPTION 5: Glimmer Holographic Glass (Android XR / Glassmorphism)
// ===================================================================

@Preview(name = "Option 5: Glimmer Glass", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption5GlimmerGlass() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B192C), Color(0xFF1E3E62), Color(0xFF000000))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Holographic aura ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF38BDF8).copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = 350f
                ),
                radius = 350f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Frosted Glass Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .size(150.dp)
                    .shadow(elevation = 30.dp, shape = RoundedCornerShape(32.dp), spotColor = Color(0xFF38BDF8))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon3DHeart(size = 72.dp)
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
                text = "CLINICAL COMPANION FOR RENAL CARE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7DD3FC),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("FHIR R4 Hospital Interoperability", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

// ===================================================================
// OPTION 6: Renal Flow Arc (Minimalist Medical Luxury)
// ===================================================================

@Preview(name = "Option 6: Renal Flow Arc", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption6RenalFlowArc() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        // Continuous Renal Flow Arc Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.44f)
            val arcSize = 260f

            // Intake Arc (Aqua)
            drawArc(
                brush = Brush.sweepGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))),
                startAngle = 140f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(center.x - arcSize, center.y - arcSize),
                size = Size(arcSize * 2, arcSize * 2),
                style = Stroke(width = 10f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // Output Arc (Topaz Gold)
            drawArc(
                brush = Brush.sweepGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706))),
                startAngle = 320f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(center.x - arcSize, center.y - arcSize),
                size = Size(arcSize * 2, arcSize * 2),
                style = Stroke(width = 10f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(elevation = 12.dp, shape = CircleShape, spotColor = Color(0x300284C7))
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon3DWaterDrop(size = 54.dp)
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TrackWell",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "PRECISION HYDRATION & GLYCEMIC CARE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7),
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ===================================================================
// OPTION 7: Cyber Shield Vault (High-Tech Security)
// ===================================================================

@Preview(name = "Option 7: Cyber Shield Vault", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption7CyberShield() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Dual Orbit Ring around Shield
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawCircle(color = Color(0xFF0284C7).copy(alpha = 0.3f), radius = 90.dp.toPx(), style = Stroke(width = 3f))
                    drawCircle(color = Color(0xFF10B981).copy(alpha = 0.4f), radius = 70.dp.toPx(), style = Stroke(width = 2f))
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(52.dp))
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TRACKWELL",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "AES-256 CLOUD VAULT • ZERO DATA LOSS",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981),
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ===================================================================
// OPTION 8: 3D Male Full-Body Outlined Organ Cluster
// ===================================================================

@Composable
private fun MaleFullBodyOutlinedCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val strokeWidthPx = 1.8.dp.toPx()
        val outlineColor = Color(0xFF0284C7).copy(alpha = 0.35f)
        val capillaryColor = Color(0xFF38BDF8).copy(alpha = 0.25f)

        // 1. Head & Neck Outline
        drawCircle(
            color = outlineColor,
            radius = w * 0.07f,
            center = Offset(w * 0.50f, h * 0.10f),
            style = Stroke(width = strokeWidthPx)
        )

        // 2. Full Male Body Vector Outline (Shoulders, Arms, Torso, Legs)
        val fullBodyPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.45f, h * 0.16f) // Neck left
            lineTo(w * 0.28f, h * 0.20f) // Left shoulder
            lineTo(w * 0.20f, h * 0.42f) // Left arm
            lineTo(w * 0.24f, h * 0.44f) // Left hand
            lineTo(w * 0.32f, h * 0.34f) // Left inner arm
            lineTo(w * 0.34f, h * 0.50f) // Left waist
            lineTo(w * 0.36f, h * 0.68f) // Left hip
            lineTo(w * 0.41f, h * 0.90f) // Left leg
            lineTo(w * 0.47f, h * 0.90f) // Left foot inner
            lineTo(w * 0.50f, h * 0.55f) // Crotch center
            lineTo(w * 0.53f, h * 0.90f) // Right foot inner
            lineTo(w * 0.59f, h * 0.90f) // Right leg
            lineTo(w * 0.64f, h * 0.68f) // Right hip
            lineTo(w * 0.66f, h * 0.50f) // Right waist
            lineTo(w * 0.68f, h * 0.34f) // Right inner arm
            lineTo(w * 0.76f, h * 0.44f) // Right hand
            lineTo(w * 0.80f, h * 0.42f) // Right arm
            lineTo(w * 0.72f, h * 0.20f) // Right shoulder
            lineTo(w * 0.55f, h * 0.16f) // Neck right
        }

        drawPath(
            path = fullBodyPath,
            color = outlineColor,
            style = Stroke(width = strokeWidthPx)
        )

        // 3. Chest Pec & Renal Vascular Capillary Outline Lines
        val chestPec = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.32f, h * 0.26f)
            cubicTo(w * 0.40f, h * 0.31f, w * 0.40f, h * 0.31f, w * 0.50f, h * 0.29f)
            cubicTo(w * 0.60f, h * 0.31f, w * 0.60f, h * 0.31f, w * 0.68f, h * 0.26f)
        }
        drawPath(chestPec, color = capillaryColor, style = Stroke(width = 1.2.dp.toPx()))
    }
}

// ===================================================================
// OPTION 8: 3D Male Living Body Flow Organ Cluster
// ===================================================================

@Composable
private fun MaleLivingBodyFlowBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Broad-Shouldered Male Athletic V-Taper Torso Path
        val maleTorsoPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.50f, h * 0.08f) // Suprasternal neck notch
            cubicTo(w * 0.60f, h * 0.09f, w * 0.76f, h * 0.14f, w * 0.90f, h * 0.22f) // Broad right shoulder
            cubicTo(w * 0.94f, h * 0.32f, w * 0.88f, h * 0.42f, w * 0.82f, h * 0.48f) // Wide chest wall
            cubicTo(w * 0.76f, h * 0.58f, w * 0.72f, h * 0.68f, w * 0.74f, h * 0.80f) // Tapered athletic waist & hip
            lineTo(w * 0.26f, h * 0.80f) // Hip base
            cubicTo(w * 0.28f, h * 0.68f, w * 0.24f, h * 0.58f, w * 0.18f, h * 0.48f) // Tapered left waist
            cubicTo(w * 0.12f, h * 0.42f, w * 0.06f, h * 0.32f, w * 0.10f, h * 0.22f) // Left chest wall
            cubicTo(w * 0.24f, h * 0.14f, w * 0.40f, h * 0.09f, w * 0.50f, h * 0.08f) // Broad left shoulder
        }

        // Soft translucent body fill matching TrackWell theme
        drawPath(
            path = maleTorsoPath,
            brush = Brush.verticalGradient(
                listOf(
                    Color(0xFFE0F2FE).copy(alpha = 0.50f),
                    Color(0xFFBAE6FD).copy(alpha = 0.30f),
                    Color.Transparent
                )
            )
        )

        // Soft body outline
        drawPath(
            path = maleTorsoPath,
            color = Color(0xFF0284C7).copy(alpha = 0.30f),
            style = Stroke(width = 2.dp.toPx())
        )

        // 2. Chest Pec Arches & Vascular Flow Channels
        val pecLeft = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.22f, h * 0.28f)
            cubicTo(w * 0.36f, h * 0.36f, w * 0.36f, h * 0.36f, w * 0.48f, h * 0.34f)
        }
        val pecRight = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.78f, h * 0.28f)
            cubicTo(w * 0.64f, h * 0.36f, w * 0.64f, h * 0.36f, w * 0.52f, h * 0.34f)
        }
        drawPath(pecLeft, color = Color(0xFF38BDF8).copy(alpha = 0.40f), style = Stroke(width = 1.8.dp.toPx()))
        drawPath(pecRight, color = Color(0xFF38BDF8).copy(alpha = 0.40f), style = Stroke(width = 1.8.dp.toPx()))

        // 3. Heart Aura (Mediastinum Rose Glow)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFB7185).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.48f, h * 0.32f),
                radius = 120f
            ),
            radius = 120f,
            center = Offset(w * 0.48f, h * 0.32f)
        )
    }
}

@Preview(name = "Option 8: TrackWell Male Living Body Flow", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption8ClayOrganCluster() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        // Living vascular background matching Home, Medicines, History, Settings
        com.trackwell.app.ui.home.components.VascularBackground(modifier = Modifier.fillMaxSize())

        // Male Living Anatomical Body Flow Backdrop
        MaleLivingBodyFlowBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // 3D Floating Organ Cluster (Kidney Hero + Water + Heart + Urine)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 3D Kidney Hero Center Capsule
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White,
                    shadowElevation = 18.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.size(108.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        com.trackwell.app.ui.home.components.KidneyGraphic(
                            modifier = Modifier.size(76.dp)
                        )
                    }
                }

                // Surrounding Organ Badges (Water, Heart, Urine)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 10.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) { Icon3DWaterDrop(size = 34.dp) }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 10.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) { Icon3DHeart(size = 34.dp) }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 10.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) { Icon3DUrineDrop(size = 34.dp) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "TrackWell",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Post-Transplant & Renal Care Companion",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7),
                textAlign = TextAlign.Center
            )
        }

        // Bottom Security Badge
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
