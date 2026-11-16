package com.trackwell.app.ui.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trackwell.app.R
import com.trackwell.app.ui.theme.ds

/**
 * 3D Glass Pipes connecting the vials to the kidney with kinetic fluid flow animations.
 * Left pipe (cyan): 155 x 119 design dp, located at block-local (0.ds, 58.ds).
 * Right pipe (amber): 155 x 119 design dp, located at block-local (217.ds, 58.ds).
 */
@Composable
fun LeftGlassPipe(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "CyanFluidTransition")
    val pulseProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CyanFluidProgress"
    )

    Box(
        modifier = modifier.size(width = 155.ds, height = 119.ds)
    ) {
        // Base 3D Glass Pipe Artwork
        Image(
            painter = painterResource(id = R.drawable.pipe_blue_left),
            contentDescription = "Left Cyan Pipe",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(width = 155.ds, height = 119.ds)
        )

        // Kinetic fluid pulses flowing from left vial (bottom) to kidney (top-right)
        val pathMeasure = remember { PathMeasure() }
        val startXDp = 32.ds
        val startYDp = 95.ds
        val cornerXDp = 32.ds
        val cornerYDp = 32.ds
        val endXDp = 131.ds
        val endYDp = 31.ds
        val arcRadiusDp = 40.ds

        Canvas(modifier = Modifier.size(width = 155.ds, height = 119.ds)) {
            val tubePath = Path().apply {
                val startX = startXDp.toPx()
                val startY = startYDp.toPx()
                val cornerX = cornerXDp.toPx()
                val cornerY = cornerYDp.toPx()
                val endX = endXDp.toPx()
                val endY = endYDp.toPx()
                val arcRadius = arcRadiusDp.toPx()

                moveTo(startX, startY)
                lineTo(cornerX, cornerY + arcRadius)
                quadraticBezierTo(cornerX, cornerY, cornerX + arcRadius, cornerY)
                lineTo(endX, endY)
            }

            pathMeasure.setPath(tubePath, false)
            val pathLen = pathMeasure.length
            if (pathLen > 0f) {
                val particleCount = 2
                val glowRadius = 12.dp.toPx()

                for (i in 0 until particleCount) {
                    val offsetPhase = i.toFloat() / particleCount
                    val currentProgress = (pulseProgress + offsetPhase) % 1f
                    val distance = currentProgress * pathLen
                    val pos = pathMeasure.getPosition(distance)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xD9FFFFFF),
                                Color(0x807FDAE6),
                                Color(0x0052ACCB)
                            ),
                            center = pos,
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = pos
                    )
                }
            }
        }
    }
}

@Composable
fun RightGlassPipe(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "AmberFluidTransition")
    val pulseProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AmberFluidProgress"
    )

    Box(
        modifier = modifier.size(width = 155.ds, height = 119.ds)
    ) {
        // Base 3D Glass Pipe Artwork
        Image(
            painter = painterResource(id = R.drawable.pipe_amber_right),
            contentDescription = "Right Amber Pipe",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(width = 155.ds, height = 119.ds)
        )

        // Kinetic fluid pulses flowing from kidney (top-left) to right vial (bottom)
        val pathMeasure = remember { PathMeasure() }
        val startXDp = 26.ds
        val startYDp = 31.ds
        val cornerXDp = 123.ds
        val cornerYDp = 31.ds
        val endXDp = 123.ds
        val endYDp = 95.ds
        val arcRadiusDp = 40.ds

        Canvas(modifier = Modifier.size(width = 155.ds, height = 119.ds)) {
            val tubePath = Path().apply {
                val startX = startXDp.toPx()
                val startY = startYDp.toPx()
                val cornerX = cornerXDp.toPx()
                val cornerY = cornerYDp.toPx()
                val endX = endXDp.toPx()
                val endY = endYDp.toPx()
                val arcRadius = arcRadiusDp.toPx()

                moveTo(startX, startY)
                lineTo(cornerX - arcRadius, cornerY)
                quadraticBezierTo(cornerX, cornerY, cornerX, cornerY + arcRadius)
                lineTo(endX, endY)
            }

            pathMeasure.setPath(tubePath, false)
            val pathLen = pathMeasure.length
            if (pathLen > 0f) {
                val particleCount = 2
                val glowRadius = 12.dp.toPx()

                for (i in 0 until particleCount) {
                    val offsetPhase = i.toFloat() / particleCount
                    val currentProgress = (pulseProgress + offsetPhase) % 1f
                    val distance = currentProgress * pathLen
                    val pos = pathMeasure.getPosition(distance)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xD9FFFFFF),
                                Color(0x80D9BA7E),
                                Color(0x00C99B4A)
                            ),
                            center = pos,
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = pos
                    )
                }
            }
        }
    }
}
