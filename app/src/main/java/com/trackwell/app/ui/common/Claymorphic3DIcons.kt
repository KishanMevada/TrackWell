package com.trackwell.app.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-Fidelity 3D Tactile Icons for AURA Claymorphic Theme.
 * Rendered using Compose Vector DrawScope with multi-stop radial & linear gradients,
 * specular lighting crests, ambient contact shadows, and depth extrusions.
 */

@Composable
fun Icon3DWaterDrop(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Soft Ambient Contact Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x350284C7), Color(0x100284C7), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.92f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.15f, h * 0.82f),
                size = Size(w * 0.7f, h * 0.18f)
            )

            // 2. Teardrop Geometry
            val dropPath = Path().apply {
                moveTo(w * 0.5f, h * 0.08f)
                cubicTo(w * 0.40f, h * 0.32f, w * 0.12f, h * 0.56f, w * 0.12f, h * 0.72f)
                cubicTo(w * 0.12f, h * 0.90f, w * 0.30f, h * 0.96f, w * 0.50f, h * 0.96f)
                cubicTo(w * 0.70f, h * 0.96f, w * 0.88f, h * 0.90f, w * 0.88f, h * 0.72f)
                cubicTo(w * 0.88f, h * 0.56f, w * 0.60f, h * 0.32f, w * 0.50f, h * 0.08f)
                close()
            }

            // 3. Rich 3D Radial Gradient Body
            drawPath(
                path = dropPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF67E8F9), // Bright Cyan Light
                        Color(0xFF00E5FF), // Vivid Electric Aqua
                        Color(0xFF0284C7), // Deep Sapphire Core
                        Color(0xFF0369A1)  // Dark Shadow Cortex
                    ),
                    center = Offset(w * 0.38f, h * 0.65f),
                    radius = w * 0.60f
                )
            )

            // 4. Specular Curved Meniscus Highlight Gleam
            val highlightPath = Path().apply {
                moveTo(w * 0.32f, h * 0.52f)
                cubicTo(w * 0.22f, h * 0.62f, w * 0.22f, h * 0.76f, w * 0.32f, h * 0.84f)
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 5. Specular Apex Light Pinpoint
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = 1.6.dp.toPx(),
                center = Offset(w * 0.44f, h * 0.32f)
            )
        }
    }
}

@Composable
fun Icon3DUrineDrop(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Soft Ambient Contact Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x35D97706), Color(0x10D97706), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.92f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.15f, h * 0.82f),
                size = Size(w * 0.7f, h * 0.18f)
            )

            // 2. Teardrop Geometry
            val dropPath = Path().apply {
                moveTo(w * 0.5f, h * 0.08f)
                cubicTo(w * 0.40f, h * 0.32f, w * 0.12f, h * 0.56f, w * 0.12f, h * 0.72f)
                cubicTo(w * 0.12f, h * 0.90f, w * 0.30f, h * 0.96f, w * 0.50f, h * 0.96f)
                cubicTo(w * 0.70f, h * 0.96f, w * 0.88f, h * 0.90f, w * 0.88f, h * 0.72f)
                cubicTo(w * 0.88f, h * 0.56f, w * 0.60f, h * 0.32f, w * 0.50f, h * 0.08f)
                close()
            }

            // 3. Rich 3D Amber Gradient Body
            drawPath(
                path = dropPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFEF08A), // Warm Golden Light
                        Color(0xFFFFB300), // Rich Golden Amber
                        Color(0xFFF59E0B), // Diuresis Topaz Core
                        Color(0xFFB45309)  // Deep Cortex
                    ),
                    center = Offset(w * 0.38f, h * 0.65f),
                    radius = w * 0.60f
                )
            )

            // 4. Specular Curved Meniscus Highlight Gleam
            val highlightPath = Path().apply {
                moveTo(w * 0.32f, h * 0.52f)
                cubicTo(w * 0.22f, h * 0.62f, w * 0.22f, h * 0.76f, w * 0.32f, h * 0.84f)
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 5. Specular Apex Light Pinpoint
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = 1.6.dp.toPx(),
                center = Offset(w * 0.44f, h * 0.32f)
            )
        }
    }
}

@Composable
fun Icon3DHome(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // 3D House Roof
            val roofPath = Path().apply {
                moveTo(w * 0.50f, h * 0.12f)
                lineTo(w * 0.88f, h * 0.46f)
                lineTo(w * 0.78f, h * 0.52f)
                lineTo(w * 0.50f, h * 0.28f)
                lineTo(w * 0.22f, h * 0.52f)
                lineTo(w * 0.12f, h * 0.46f)
                close()
            }
            drawPath(path = roofPath, color = primaryColor)

            // 3D House Body with rounded door
            val bodyPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = w * 0.22f,
                        top = h * 0.44f,
                        right = w * 0.78f,
                        bottom = h * 0.90f,
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                )
            }
            drawPath(path = bodyPath, color = primaryColor.copy(alpha = 0.90f))

            // Door Deboss
            drawRoundRect(
                color = if (isDark) Color(0xFF1E232B) else Color.White,
                topLeft = Offset(w * 0.40f, h * 0.62f),
                size = Size(w * 0.20f, h * 0.28f),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }
    }
}

@Composable
fun Icon3DDiary(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // Notepad Base
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(w * 0.20f, h * 0.15f),
                size = Size(w * 0.60f, h * 0.72f),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Inner lines
            val lineBrush = if (isDark) Color(0xFF1E232B) else Color.White
            drawLine(
                color = lineBrush,
                start = Offset(w * 0.32f, h * 0.35f),
                end = Offset(w * 0.68f, h * 0.35f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = lineBrush,
                start = Offset(w * 0.32f, h * 0.50f),
                end = Offset(w * 0.68f, h * 0.50f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = lineBrush,
                start = Offset(w * 0.32f, h * 0.65f),
                end = Offset(w * 0.54f, h * 0.65f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun Icon3DAnalytics(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // 3 Rounded Pillars
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.75f),
                topLeft = Offset(w * 0.18f, h * 0.50f),
                size = Size(w * 0.16f, h * 0.38f),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(w * 0.42f, h * 0.22f),
                size = Size(w * 0.16f, h * 0.66f),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.88f),
                topLeft = Offset(w * 0.66f, h * 0.38f),
                size = Size(w * 0.16f, h * 0.50f),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
        }
    }
}

@Composable
fun Icon3DSettings(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // Outer Cog Rim
            drawCircle(
                color = primaryColor,
                radius = w * 0.36f,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = 4.dp.toPx())
            )

            // Inner Center Cavity
            drawCircle(
                color = if (isDark) Color(0xFF1E232B) else Color.White,
                radius = w * 0.14f,
                center = Offset(w * 0.5f, h * 0.5f)
            )

            // 4 Radial Gear Teeth
            for (i in 0..3) {
                val angle = i * 45f
                val length = w * 0.44f
                val rad = Math.toRadians(angle.toDouble())
                val cos = Math.cos(rad).toFloat()
                val sin = Math.sin(rad).toFloat()
                drawLine(
                    color = primaryColor,
                    start = Offset(w * 0.5f - cos * length, h * 0.5f - sin * length),
                    end = Offset(w * 0.5f + cos * length, h * 0.5f + sin * length),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun Icon3DScale(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Ambient Contact Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x25000000), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.92f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.12f, h * 0.82f),
                size = Size(w * 0.76f, h * 0.16f)
            )

            // 2. 3D Rounded Slate Scale Body
            val scaleBody = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = w * 0.14f,
                        top = h * 0.16f,
                        right = w * 0.86f,
                        bottom = h * 0.86f,
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                )
            }
            drawPath(
                path = scaleBody,
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(Color(0xFF475569), Color(0xFF334155), Color(0xFF1E293B))
                    else listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                )
            )

            // 3. Digital Dial Display Inset at Top
            drawRoundRect(
                color = if (isDark) Color(0xFF0F172A) else Color(0xFF334155),
                topLeft = Offset(w * 0.30f, h * 0.25f),
                size = Size(w * 0.40f, h * 0.18f),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
            // Glowing digital readout bar
            drawRoundRect(
                color = Color(0xFF38BDF8),
                topLeft = Offset(w * 0.38f, h * 0.30f),
                size = Size(w * 0.24f, h * 0.08f),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )

            // 4. Center Standing Dial Divider
            drawLine(
                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                start = Offset(w * 0.5f, h * 0.48f),
                end = Offset(w * 0.5f, h * 0.80f),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun Icon3DBloodDrop(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Ambient Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x35EF4444), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.92f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.15f, h * 0.82f),
                size = Size(w * 0.7f, h * 0.18f)
            )

            // Droplet Geometry
            val dropPath = Path().apply {
                moveTo(w * 0.5f, h * 0.08f)
                cubicTo(w * 0.40f, h * 0.32f, w * 0.12f, h * 0.56f, w * 0.12f, h * 0.72f)
                cubicTo(w * 0.12f, h * 0.90f, w * 0.30f, h * 0.96f, w * 0.50f, h * 0.96f)
                cubicTo(w * 0.70f, h * 0.96f, w * 0.88f, h * 0.90f, w * 0.88f, h * 0.72f)
                cubicTo(w * 0.88f, h * 0.56f, w * 0.60f, h * 0.32f, w * 0.50f, h * 0.08f)
                close()
            }

            // Crimson / Ruby Blood Drop Gradient
            drawPath(
                path = dropPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFECDD3), // Soft Rose Light
                        Color(0xFFFB7185), // Vibrant Coral
                        Color(0xFFEF4444), // Crimson Core
                        Color(0xFF991B1B)  // Deep Ruby Shadow
                    ),
                    center = Offset(w * 0.38f, h * 0.65f),
                    radius = w * 0.60f
                )
            )

            // Specular Gleam
            val highlightPath = Path().apply {
                moveTo(w * 0.32f, h * 0.52f)
                cubicTo(w * 0.22f, h * 0.62f, w * 0.22f, h * 0.76f, w * 0.32f, h * 0.84f)
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Pinpoint Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = 1.6.dp.toPx(),
                center = Offset(w * 0.44f, h * 0.32f)
            )
        }
    }
}

@Composable
fun Icon3DSugarDrop(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Ambient Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x358B5CF6), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.92f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.15f, h * 0.82f),
                size = Size(w * 0.7f, h * 0.18f)
            )

            // Droplet Geometry
            val dropPath = Path().apply {
                moveTo(w * 0.5f, h * 0.08f)
                cubicTo(w * 0.40f, h * 0.32f, w * 0.12f, h * 0.56f, w * 0.12f, h * 0.72f)
                cubicTo(w * 0.12f, h * 0.90f, w * 0.30f, h * 0.96f, w * 0.50f, h * 0.96f)
                cubicTo(w * 0.70f, h * 0.96f, w * 0.88f, h * 0.90f, w * 0.88f, h * 0.72f)
                cubicTo(w * 0.88f, h * 0.56f, w * 0.60f, h * 0.32f, w * 0.50f, h * 0.08f)
                close()
            }

            // Violet/Purple Sugar Gradient
            drawPath(
                path = dropPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFDDD6FE), // Lavender Light
                        Color(0xFFA78BFA), // Vibrant Violet
                        Color(0xFF8B5CF6), // Royal Purple
                        Color(0xFF5B21B6)  // Deep Core
                    ),
                    center = Offset(w * 0.38f, h * 0.65f),
                    radius = w * 0.60f
                )
            )

            // Specular Gleam
            val highlightPath = Path().apply {
                moveTo(w * 0.32f, h * 0.52f)
                cubicTo(w * 0.22f, h * 0.62f, w * 0.22f, h * 0.76f, w * 0.32f, h * 0.84f)
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Pinpoint Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = 1.6.dp.toPx(),
                center = Offset(w * 0.44f, h * 0.32f)
            )
        }
    }
}

@Composable
fun Icon3DMoon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Crescent Moon
            val moonPath = Path().apply {
                moveTo(w * 0.55f, h * 0.15f)
                cubicTo(w * 0.25f, h * 0.22f, w * 0.15f, h * 0.50f, w * 0.28f, h * 0.78f)
                cubicTo(w * 0.42f, h * 0.95f, w * 0.72f, h * 0.92f, w * 0.85f, h * 0.80f)
                cubicTo(w * 0.60f, h * 0.80f, w * 0.40f, h * 0.62f, w * 0.42f, h * 0.38f)
                cubicTo(w * 0.44f, h * 0.24f, w * 0.52f, h * 0.17f, w * 0.55f, h * 0.15f)
                close()
            }

            drawPath(
                path = moonPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF64748B)),
                    center = Offset(w * 0.35f, h * 0.45f),
                    radius = w * 0.55f
                )
            )

            // Specular Crest
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 1.5.dp.toPx(),
                center = Offset(w * 0.32f, h * 0.42f)
            )
        }
    }
}

@Composable
fun Icon3DHeart(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val heartPath = Path().apply {
                moveTo(w * 0.5f, h * 0.82f)
                cubicTo(w * 0.20f, h * 0.62f, w * 0.08f, h * 0.44f, w * 0.08f, h * 0.28f)
                cubicTo(w * 0.08f, h * 0.12f, w * 0.26f, h * 0.08f, w * 0.42f, h * 0.20f)
                lineTo(w * 0.5f, h * 0.28f)
                lineTo(w * 0.58f, h * 0.20f)
                cubicTo(w * 0.74f, h * 0.08f, w * 0.92f, h * 0.12f, w * 0.92f, h * 0.28f)
                cubicTo(w * 0.92f, h * 0.44f, w * 0.80f, h * 0.62f, w * 0.5f, h * 0.82f)
                close()
            }

            drawPath(
                path = heartPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFDA4AF), Color(0xFFF43F5E), Color(0xFFBE123C)),
                    center = Offset(w * 0.35f, h * 0.35f),
                    radius = w * 0.55f
                )
            )

            // Gleam
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 1.8.dp.toPx(),
                center = Offset(w * 0.30f, h * 0.28f)
            )
        }
    }
}

@Composable
fun Icon3DPill(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Pill capsule
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF047857)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                topLeft = Offset(w * 0.18f, h * 0.28f),
                size = Size(w * 0.64f, h * 0.44f),
                cornerRadius = CornerRadius(h * 0.22f)
            )

            // Gleam
            drawLine(
                color = Color.White.copy(alpha = 0.75f),
                start = Offset(w * 0.28f, h * 0.36f),
                end = Offset(w * 0.72f, h * 0.36f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun Icon3DBell(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // Bell outline dome
            val bellPath = Path().apply {
                moveTo(w * 0.50f, h * 0.15f)
                cubicTo(w * 0.32f, h * 0.15f, w * 0.22f, h * 0.40f, w * 0.20f, h * 0.65f)
                lineTo(w * 0.14f, h * 0.75f)
                lineTo(w * 0.86f, h * 0.75f)
                lineTo(w * 0.80f, h * 0.65f)
                cubicTo(w * 0.78f, h * 0.40f, w * 0.68f, h * 0.15f, w * 0.50f, h * 0.15f)
                close()
            }
            drawPath(
                path = bellPath,
                color = primaryColor,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Top loop
            drawCircle(
                color = primaryColor,
                radius = 2.dp.toPx(),
                center = Offset(w * 0.50f, h * 0.12f),
                style = Stroke(width = 1.4.dp.toPx())
            )

            // Bottom clapper
            drawCircle(
                color = primaryColor,
                radius = 2.2.dp.toPx(),
                center = Offset(w * 0.50f, h * 0.84f)
            )
        }
    }
}

@Composable
fun Icon3DStethoscope(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // Medical vitals sheet outline
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(w * 0.22f, h * 0.18f),
                size = Size(w * 0.56f, h * 0.64f),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Pulse wave inside
            val pulsePath = Path().apply {
                moveTo(w * 0.30f, h * 0.50f)
                lineTo(w * 0.42f, h * 0.50f)
                lineTo(w * 0.48f, h * 0.32f)
                lineTo(w * 0.54f, h * 0.68f)
                lineTo(w * 0.60f, h * 0.50f)
                lineTo(w * 0.70f, h * 0.50f)
            }
            drawPath(
                path = pulsePath,
                color = primaryColor,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun Icon3DDocumentOutline(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSelected: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = if (isSelected) {
                if (isDark) Color(0xFFFB7185) else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            // Document body
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(w * 0.22f, h * 0.16f),
                size = Size(w * 0.56f, h * 0.68f),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 1.8.dp.toPx())
            )

            // 3 Horizontal lines
            val lineY = listOf(0.36f, 0.50f, 0.64f)
            lineY.forEach { y ->
                drawLine(
                    color = primaryColor,
                    start = Offset(w * 0.34f, h * y),
                    end = Offset(w * 0.66f, h * y),
                    strokeWidth = 1.6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview(name = "3D Claymorphic Icons - Light", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Preview(name = "3D Claymorphic Icons - Dark", showBackground = true, backgroundColor = 0xFF0F172A, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Claymorphic3DIconsPreview() {
    Surface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon3DWaterDrop(size = 36.dp)
                Icon3DUrineDrop(size = 36.dp)
                Icon3DBloodDrop(size = 36.dp)
                Icon3DSugarDrop(size = 36.dp)
                Icon3DScale(size = 36.dp)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon3DMoon(size = 36.dp)
                Icon3DHeart(size = 36.dp)
                Icon3DPill(size = 36.dp)
                Icon3DHome(size = 32.dp, isSelected = true)
                Icon3DDiary(size = 32.dp, isSelected = true)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon3DAnalytics(size = 32.dp, isSelected = true)
                Icon3DSettings(size = 32.dp, isSelected = true)
                Icon3DBell(size = 32.dp, isSelected = true)
                Icon3DStethoscope(size = 32.dp, isSelected = true)
                Icon3DDocumentOutline(size = 32.dp, isSelected = true)
            }
        }
    }
}

