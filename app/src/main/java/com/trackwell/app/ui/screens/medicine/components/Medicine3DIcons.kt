package com.trackwell.app.ui.screens.medicine.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Photorealistic 3D Claymorphic Two-Tone Capsule (Cyan & White) matching design mockup.
 */
@Composable
fun Icon3DCapsule(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Rotate 40 degrees diagonally
            rotate(degrees = 42f, pivot = Offset(w * 0.5f, h * 0.5f)) {
                // 1. Soft Ambient Contact Shadow
                drawRoundRect(
                    color = Color(0x250284C7),
                    topLeft = Offset(w * 0.22f, h * 0.16f),
                    size = Size(w * 0.58f, h * 0.72f),
                    cornerRadius = CornerRadius(w * 0.29f)
                )

                // 2. Full Capsule Base Outline (Rounded Rectangle)
                val capsuleW = w * 0.50f
                val capsuleH = h * 0.76f
                val capsuleLeft = w * 0.25f
                val capsuleTop = h * 0.12f
                val cornerR = capsuleW * 0.5f

                // White bottom half
                val whiteBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0)),
                    start = Offset(capsuleLeft, capsuleTop + capsuleH * 0.5f),
                    end = Offset(capsuleLeft + capsuleW, capsuleTop + capsuleH)
                )

                // Cyan top half
                val cyanBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0369A1)),
                    start = Offset(capsuleLeft, capsuleTop),
                    end = Offset(capsuleLeft + capsuleW, capsuleTop + capsuleH * 0.5f)
                )

                // Clip & Draw White Bottom Half
                drawRoundRect(
                    brush = whiteBrush,
                    topLeft = Offset(capsuleLeft, capsuleTop),
                    size = Size(capsuleW, capsuleH),
                    cornerRadius = CornerRadius(cornerR, cornerR)
                )

                // Draw Cyan Top Half over top section
                val topPath = Path().apply {
                    moveTo(capsuleLeft, capsuleTop + cornerR)
                    cubicTo(capsuleLeft, capsuleTop, capsuleLeft + cornerR, capsuleTop, capsuleLeft + cornerR, capsuleTop)
                    cubicTo(capsuleLeft + capsuleW, capsuleTop, capsuleLeft + capsuleW, capsuleTop + cornerR, capsuleLeft + capsuleW, capsuleTop + cornerR)
                    lineTo(capsuleLeft + capsuleW, capsuleTop + capsuleH * 0.5f)
                    lineTo(capsuleLeft, capsuleTop + capsuleH * 0.5f)
                    close()
                }
                drawPath(path = topPath, brush = cyanBrush)

                // Divider seam
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(capsuleLeft, capsuleTop + capsuleH * 0.5f),
                    end = Offset(capsuleLeft + capsuleW, capsuleTop + capsuleH * 0.5f),
                    strokeWidth = 1.2.dp.toPx()
                )

                // Specular Light Highlight along side
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(capsuleLeft + capsuleW * 0.28f, capsuleTop + capsuleH * 0.18f),
                    end = Offset(capsuleLeft + capsuleW * 0.28f, capsuleTop + capsuleH * 0.82f),
                    strokeWidth = 2.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Photorealistic 3D Claymorphic Round Tablet Pill with central score line.
 */
@Composable
fun Icon3DTablet(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w * 0.5f, h * 0.5f)
            val radius = w * 0.38f

            // 1. Soft Ambient Contact Shadow
            drawCircle(
                color = Color(0x2064748B),
                radius = radius * 1.05f,
                center = Offset(center.x + 1.dp.toPx(), center.y + 2.dp.toPx())
            )

            // 2. Tablet Ceramic Face
            val tabletBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFE2E8F0)),
                start = Offset(w * 0.2f, h * 0.2f),
                end = Offset(w * 0.8f, h * 0.8f)
            )
            drawCircle(brush = tabletBrush, radius = radius, center = center)

            // 3. Diagonal debossed score line
            rotate(degrees = 38f, pivot = center) {
                // Shadow groove
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(center.x, center.y - radius * 0.72f),
                    end = Offset(center.x, center.y + radius * 0.72f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Light groove edge
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(center.x + 1.dp.toPx(), center.y - radius * 0.70f),
                    end = Offset(center.x + 1.dp.toPx(), center.y + radius * 0.70f),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 4. Subtle top-left rim highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.8f), Color.Transparent),
                    center = Offset(w * 0.35f, h * 0.35f),
                    radius = radius * 0.6f
                ),
                radius = radius,
                center = center
            )
        }
    }
}
