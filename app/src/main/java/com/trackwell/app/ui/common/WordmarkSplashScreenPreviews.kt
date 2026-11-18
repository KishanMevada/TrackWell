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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===================================================================
// OPTION 9: Kinetic Pulse Wordmark (3D Sculpted Fluid W)
// ===================================================================

@Preview(name = "Option 9: Kinetic Pulse Wordmark", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption9KineticPulseWordmark() {
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.44f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF0284C7).copy(alpha = 0.25f), Color.Transparent),
                    center = center,
                    radius = 320f
                ),
                radius = 320f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Fluid Drop Crest
            Icon3DWaterDrop(size = 64.dp)

            Spacer(modifier = Modifier.height(28.dp))

            // Kinetic Wordmark Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Track",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Well",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8),
                    letterSpacing = (-1).sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Divider Line
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF38BDF8))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CLINICAL RENAL & METABOLIC VAULT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 2.sp,
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
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("100% Offline-First • FHIR R4 Compliant", fontSize = 11.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Medium)
        }
    }
}

// ===================================================================
// OPTION 10: TW Monogram Emblem (Clean 3D Clay Monogram)
// ===================================================================

@Preview(name = "Option 10: TW Monogram Emblem", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption10MonogramTW() {
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
            // 3D Clay Monogram Badge
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = Color.White,
                shadowElevation = 18.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.size(130.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "TW",
                        fontSize = 48.sp,
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
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.8).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Precision Health & Renal Companion",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
        }
    }
}

// ===================================================================
// OPTION 11: Frosted Glass Shield Wordmark (Glassmorphic Lux)
// ===================================================================

@Preview(name = "Option 11: Frosted Glass Wordmark", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption11FrostedGlassWordmark() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF030712), Color(0xFF0F172A), Color(0xFF030712))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Frosted Glass Badge with Wordmark inside
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.07f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.20f)),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0xFF0284C7))
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon3DWaterDrop(size = 48.dp)
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "TRACKWELL",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "OFFLINE-FIRST EMR & HEALTH VAULT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ===================================================================
// OPTION 12: Neon Horizon Wave (OLED High-Tech Glow)
// ===================================================================

@Preview(name = "Option 12: Neon Horizon Wave", showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun SplashScreenOption12NeonHorizonWave() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617)),
        contentAlignment = Alignment.Center
    ) {
        // Neon horizon wave line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.45f)
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0xFF38BDF8), Color(0xFF34D399), Color.Transparent)
                ),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 3f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon3DHeart(size = 64.dp)

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "TRACKWELL",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "120Hz CLINICAL INTELLIGENCE ENGINE",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF34D399),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
