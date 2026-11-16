package com.trackwell.app.ui.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

data class VascularVessel(
    val pathBuilder: (width: Float, height: Float) -> Path,
    val color: Color,
    val strokeWidthDp: Float,
    val particleCount: Int,
    val speedMultiplier: Float
)

/**
 * Living procedural human vascular background.
 * Draws subtle, organic capillary and venous branches along the margins
 * with microscopic glowing blood cell pulses traveling through them.
 * Opacity is kept very low (<= 8%) to guarantee zero interference with
 * claymorphic surfaces, shadows, or text contrast.
 */
@Composable
fun VascularBackground(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "BloodFlowTransition")
    val flowProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FlowProgress"
    )

    // Arterial and venous paths framing the screen margins without crossing center text
    val vessels = remember {
        listOf(
            // Left Arterial Branch (oxygenated rose crimson)
            VascularVessel(
                pathBuilder = { w, h ->
                    Path().apply {
                        moveTo(w * 0.04f, h * 0.95f)
                        cubicTo(w * 0.08f, h * 0.70f, w * 0.02f, h * 0.45f, w * 0.12f, h * 0.20f)
                        cubicTo(w * 0.15f, h * 0.12f, w * 0.10f, h * 0.05f, w * 0.18f, 0f)
                    }
                },
                color = Color(0x14E89A9E), // ~8% alpha rose
                strokeWidthDp = 2.2f,
                particleCount = 5,
                speedMultiplier = 1.0f
            ),
            // Left Secondary Capillary Fork
            VascularVessel(
                pathBuilder = { w, h ->
                    Path().apply {
                        moveTo(w * 0.06f, h * 0.65f)
                        cubicTo(w * 0.16f, h * 0.55f, w * 0.14f, h * 0.40f, w * 0.22f, h * 0.32f)
                    }
                },
                color = Color(0x0FE89A9E), // ~6% alpha
                strokeWidthDp = 1.4f,
                particleCount = 3,
                speedMultiplier = 0.8f
            ),
            // Right Venous Branch (deoxygenated soft sky blue)
            VascularVessel(
                pathBuilder = { w, h ->
                    Path().apply {
                        moveTo(w * 0.88f, 0f)
                        cubicTo(w * 0.94f, h * 0.25f, w * 0.86f, h * 0.50f, w * 0.95f, h * 0.75f)
                        cubicTo(w * 0.98f, h * 0.85f, w * 0.92f, h * 0.92f, w * 0.96f, h)
                    }
                },
                color = Color(0x1496C2E6), // ~8% alpha sky blue
                strokeWidthDp = 2.2f,
                particleCount = 5,
                speedMultiplier = 1.1f
            ),
            // Right Secondary Venule Fork
            VascularVessel(
                pathBuilder = { w, h ->
                    Path().apply {
                        moveTo(w * 0.89f, h * 0.38f)
                        cubicTo(w * 0.80f, h * 0.48f, w * 0.82f, h * 0.60f, w * 0.76f, h * 0.68f)
                    }
                },
                color = Color(0x0F96C2E6), // ~6% alpha
                strokeWidthDp = 1.4f,
                particleCount = 3,
                speedMultiplier = 0.85f
            )
        )
    }

    val pathMeasure = remember { PathMeasure() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        vessels.forEach { vessel ->
            val path = vessel.pathBuilder(w, h)
            val strokePx = vessel.strokeWidthDp.dp.toPx()

            // 1. Draw smooth capillary pathway
            drawPath(
                path = path,
                color = vessel.color,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // 2. Animate blood cell micro-droplets flowing along the path
            pathMeasure.setPath(path, false)
            val pathLength = pathMeasure.length
            if (pathLength > 0f) {
                for (i in 0 until vessel.particleCount) {
                    val phaseOffset = (i.toFloat() / vessel.particleCount)
                    val currentProgress = ((flowProgress * vessel.speedMultiplier) + phaseOffset) % 1f
                    val distance = currentProgress * pathLength
                    val position = pathMeasure.getPosition(distance)

                    // Draw soft translucent blood cell particle
                    drawCircle(
                        color = vessel.color.copy(alpha = 0.15f),
                        radius = strokePx * 1.6f,
                        center = position
                    )
                }
            }
        }
    }
}
