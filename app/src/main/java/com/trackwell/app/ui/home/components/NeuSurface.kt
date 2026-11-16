package com.trackwell.app.ui.home.components

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates an android.graphics.Path representing a rounded rectangle with independent top and bottom radii.
 */
private fun createRoundRectPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    topRad: Float,
    botRad: Float
): Path {
    val path = Path()
    val radii = floatArrayOf(
        topRad, topRad, // Top-left
        topRad, topRad, // Top-right
        botRad, botRad, // Bottom-right
        botRad, botRad  // Bottom-left
    )
    path.addRoundRect(RectF(left, top, right, bottom), radii, Path.Direction.CW)
    return path
}

/**
 * Raised-L: hero card, stat cards, bottom bar, vial capsules
 * Dark shadow: +6, +8, 20 dp, shadowDark at 45% (#8E9FBA at 45%)
 * Light shadow: -6, -6, 18 dp, #FFFFFF at 100%
 * Face: surface gradient (#F6F8FC to #E5ECF6)
 * Extras: 1 dp inner white stroke at 80% on top and left edges
 */
fun Modifier.neuRaisedL(
    cornerRadius: Dp = 24.dp,
    customTopRadius: Dp? = null,
    customBottomRadius: Dp? = null
): Modifier = this.drawBehind {
    val topRad = (customTopRadius ?: cornerRadius).toPx()
    val botRad = (customBottomRadius ?: cornerRadius).toPx()
    val width = size.width
    val height = size.height

    drawIntoNativeCanvas(this) { nativeCanvas ->
        // 1. Light shadow (-6dp, -6dp, blur 18dp, #FFFFFF at 95%)
        val lightDx = (-6).dp.toPx()
        val lightDy = (-6).dp.toPx()
        val lightBlur = 18.dp.toPx()
        val lightPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(240, 255, 255, 255)
            maskFilter = BlurMaskFilter(lightBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val lightPath = createRoundRectPath(
            lightDx, lightDy, width + lightDx, height + lightDy, topRad, botRad
        )
        nativeCanvas.drawPath(lightPath, lightPaint)

        // 2. Dark shadow (+6dp, +8dp, blur 20dp, #8E9FBA at 45%)
        val darkDx = 6.dp.toPx()
        val darkDy = 8.dp.toPx()
        val darkBlur = 20.dp.toPx()
        val darkCol = android.graphics.Color.argb(115, 142, 159, 186)
        val darkPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = darkCol
            maskFilter = BlurMaskFilter(darkBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val darkPath = createRoundRectPath(
            darkDx, darkDy, width + darkDx, height + darkDy, topRad, botRad
        )
        nativeCanvas.drawPath(darkPath, darkPaint)

        // 3. Face: surface gradient (#F6F8FC to #E5ECF6)
        val facePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            shader = LinearGradient(
                0f, 0f, width, height,
                android.graphics.Color.rgb(246, 248, 252),
                android.graphics.Color.rgb(229, 236, 246),
                Shader.TileMode.CLAMP
            )
        }
        val facePath = createRoundRectPath(0f, 0f, width, height, topRad, botRad)
        nativeCanvas.drawPath(facePath, facePaint)

        // 4. Inner white stroke 1 dp at 80% on top and left edges
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.dp.toPx()
            color = android.graphics.Color.argb(204, 255, 255, 255)
        }
        val strokePath = createRoundRectPath(
            0.5f, 0.5f, width - 0.5f, height - 0.5f, topRad, botRad
        )
        nativeCanvas.drawPath(strokePath, strokePaint)
    }
}

/**
 * Raised-S: date pill
 * Dark shadow: +4, +5, 12 dp, shadowDark at 40%
 * Light shadow: -4, -4, 10 dp, #FFFFFF at 100%
 */
fun Modifier.neuRaisedS(
    cornerRadius: Dp = 21.5.dp
): Modifier = this.drawBehind {
    val rad = cornerRadius.toPx()
    val width = size.width
    val height = size.height

    drawIntoNativeCanvas(this) { nativeCanvas ->
        // 1. Light shadow
        val lightDx = (-4).dp.toPx()
        val lightDy = (-4).dp.toPx()
        val lightBlur = 10.dp.toPx()
        val lightPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(240, 255, 255, 255)
            maskFilter = BlurMaskFilter(lightBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val lightPath = createRoundRectPath(
            lightDx, lightDy, width + lightDx, height + lightDy, rad, rad
        )
        nativeCanvas.drawPath(lightPath, lightPaint)

        // 2. Dark shadow
        val darkDx = 4.dp.toPx()
        val darkDy = 5.dp.toPx()
        val darkBlur = 12.dp.toPx()
        val darkCol = android.graphics.Color.argb(102, 142, 159, 186)
        val darkPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = darkCol
            maskFilter = BlurMaskFilter(darkBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val darkPath = createRoundRectPath(
            darkDx, darkDy, width + darkDx, height + darkDy, rad, rad
        )
        nativeCanvas.drawPath(darkPath, darkPaint)

        // 3. Face
        val facePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            shader = LinearGradient(
                0f, 0f, width, height,
                android.graphics.Color.rgb(246, 248, 252),
                android.graphics.Color.rgb(229, 236, 246),
                Shader.TileMode.CLAMP
            )
        }
        val facePath = createRoundRectPath(0f, 0f, width, height, rad, rad)
        nativeCanvas.drawPath(facePath, facePaint)

        // 4. Inner white stroke 1 dp at 80%
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.dp.toPx()
            color = android.graphics.Color.argb(204, 255, 255, 255)
        }
        val strokePath = createRoundRectPath(
            0.5f, 0.5f, width - 0.5f, height - 0.5f, rad, rad
        )
        nativeCanvas.drawPath(strokePath, strokePaint)
    }
}

/**
 * Chip style: blue, green, peach
 * Dark shadow: 0, +4, 8 dp (darkened 35% at 50%)
 * Light shadow: -2, -2, 4 dp, #FFFFFF at 80%
 */
fun Modifier.neuChip(
    chipColor: Color,
    cornerRadius: Dp = 15.dp
): Modifier = this.drawBehind {
    val rad = cornerRadius.toPx()
    val width = size.width
    val height = size.height

    drawIntoNativeCanvas(this) { nativeCanvas ->
        // Light shadow top-left
        val lightDx = (-2).dp.toPx()
        val lightDy = (-2).dp.toPx()
        val lightBlur = 4.dp.toPx()
        val lightPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(204, 255, 255, 255)
            maskFilter = BlurMaskFilter(lightBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val lightPath = createRoundRectPath(
            lightDx, lightDy, width + lightDx, height + lightDy, rad, rad
        )
        nativeCanvas.drawPath(lightPath, lightPaint)

        // Dark shadow bottom
        val darkDy = 4.dp.toPx()
        val darkBlur = 8.dp.toPx()
        val r = (chipColor.red * 0.65f * 255).toInt()
        val g = (chipColor.green * 0.65f * 255).toInt()
        val b = (chipColor.blue * 0.65f * 255).toInt()
        val darkCol = android.graphics.Color.argb(128, r, g, b)
        val darkPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = darkCol
            maskFilter = BlurMaskFilter(darkBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val darkPath = createRoundRectPath(
            0f, darkDy, width, height + darkDy, rad, rad
        )
        nativeCanvas.drawPath(darkPath, darkPaint)

        // Face
        val facePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = chipColor.toArgb()
        }
        val facePath = createRoundRectPath(0f, 0f, width, height, rad, rad)
        nativeCanvas.drawPath(facePath, facePaint)
    }
}

/**
 * Pressed: active nav pill
 * Inner dark shadow: +4, +4, 8 dp, #B9C6D9 at 70% (top-left inside)
 * Inner light shadow: -4, -4, 8 dp, #FFFFFF at 90% (bottom-right inside)
 * Face: #E3ECF5 to #EAF0F9
 */
fun Modifier.neuPressed(
    cornerRadius: Dp = 26.dp
): Modifier = this.drawWithContent {
    val rad = cornerRadius.toPx()
    val width = size.width
    val height = size.height
    val cr = CornerRadius(rad, rad)

    // 1. Face gradient: #E3ECF5 to #EAF0F9
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE3ECF5), Color(0xFFEAF0F9)),
            start = Offset.Zero,
            end = Offset(width, height)
        ),
        cornerRadius = cr
    )

    // 2. Inner dark shadow on top and left: gradient overlay
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xB3B9C6D9), Color.Transparent),
            start = Offset.Zero,
            end = Offset(8.dp.toPx(), 8.dp.toPx())
        ),
        cornerRadius = cr
    )

    // 3. Inner light highlight on bottom and right: gradient overlay
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color(0xE6FFFFFF)),
            start = Offset(width - 8.dp.toPx(), height - 8.dp.toPx()),
            end = Offset(width, height)
        ),
        cornerRadius = cr
    )

    this@drawWithContent.drawContent()
}

/**
 * Claymorphic Card Shadow matching the home screen's tactile aesthetic:
 * - Upper light glow: -5dp, -5dp, blur 16dp, #FFFFFF at 95%
 * - Lower diffuse dark shadow: +5dp, +9dp, blur 22dp, #8E9FBA at 40% (or tintColor)
 * - Surface fill (default pure white)
 * - Inner specular white highlight stroke 1dp at 80%
 */
fun Modifier.neuClayCard(
    cornerRadius: Dp = 28.dp,
    faceColor: Color = Color.White,
    darkShadowAlpha: Float = 0.38f,
    tintColor: Color? = null
): Modifier = this.drawBehind {
    val rad = cornerRadius.toPx()
    val width = size.width
    val height = size.height

    drawIntoNativeCanvas(this) { nativeCanvas ->
        // 1. Soft top-left light highlight
        val lightDx = (-5).dp.toPx()
        val lightDy = (-5).dp.toPx()
        val lightBlur = 16.dp.toPx()
        val lightPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(240, 255, 255, 255)
            maskFilter = BlurMaskFilter(lightBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val lightPath = createRoundRectPath(
            lightDx, lightDy, width + lightDx, height + lightDy, rad, rad
        )
        nativeCanvas.drawPath(lightPath, lightPaint)

        // 2. Soft bottom-right diffuse dark shadow (home screen neomorphic style)
        val darkDx = 5.dp.toPx()
        val darkDy = 9.dp.toPx()
        val darkBlur = 22.dp.toPx()
        val baseR = tintColor?.let { (it.red * 255).toInt() } ?: 142
        val baseG = tintColor?.let { (it.green * 255).toInt() } ?: 159
        val baseB = tintColor?.let { (it.blue * 255).toInt() } ?: 186
        val alphaInt = (darkShadowAlpha * 255).toInt().coerceIn(0, 255)
        val darkPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(alphaInt, baseR, baseG, baseB)
            maskFilter = BlurMaskFilter(darkBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val darkPath = createRoundRectPath(
            darkDx, darkDy, width + darkDx, height + darkDy, rad, rad
        )
        nativeCanvas.drawPath(darkPath, darkPaint)

        // 3. Face fill
        val facePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = faceColor.toArgb()
        }
        val facePath = createRoundRectPath(0f, 0f, width, height, rad, rad)
        nativeCanvas.drawPath(facePath, facePaint)

        // 4. Inner white specular stroke 1dp on top and left edges
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.dp.toPx()
            color = android.graphics.Color.argb(204, 255, 255, 255)
        }
        val strokePath = createRoundRectPath(
            0.5f, 0.5f, width - 0.5f, height - 0.5f, rad, rad
        )
        nativeCanvas.drawPath(strokePath, strokePaint)
    }
}

/**
 * Medium / Small item claymorphic shadow for badges and list items:
 * - Upper light glow: -3dp, -3dp, blur 10dp, #FFFFFF
 * - Lower dark shadow: +3dp, +6dp, blur 14dp, #8E9FBA at 30%
 * - Face: pure white
 * - Inner 1dp white highlight stroke
 */
fun Modifier.neuClayItem(
    cornerRadius: Dp = 20.dp,
    faceColor: Color = Color.White,
    darkShadowAlpha: Float = 0.30f
): Modifier = this.drawBehind {
    val rad = cornerRadius.toPx()
    val width = size.width
    val height = size.height

    drawIntoNativeCanvas(this) { nativeCanvas ->
        // 1. Light glow
        val lightDx = (-3).dp.toPx()
        val lightDy = (-3).dp.toPx()
        val lightBlur = 10.dp.toPx()
        val lightPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(220, 255, 255, 255)
            maskFilter = BlurMaskFilter(lightBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val lightPath = createRoundRectPath(
            lightDx, lightDy, width + lightDx, height + lightDy, rad, rad
        )
        nativeCanvas.drawPath(lightPath, lightPaint)

        // 2. Dark shadow
        val darkDx = 3.dp.toPx()
        val darkDy = 5.dp.toPx()
        val darkBlur = 14.dp.toPx()
        val alphaInt = (darkShadowAlpha * 255).toInt().coerceIn(0, 255)
        val darkPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = android.graphics.Color.argb(alphaInt, 142, 159, 186)
            maskFilter = BlurMaskFilter(darkBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val darkPath = createRoundRectPath(
            darkDx, darkDy, width + darkDx, height + darkDy, rad, rad
        )
        nativeCanvas.drawPath(darkPath, darkPaint)

        // 3. Face
        val facePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = faceColor.toArgb()
        }
        val facePath = createRoundRectPath(0f, 0f, width, height, rad, rad)
        nativeCanvas.drawPath(facePath, facePaint)

        // 4. Inner white specular stroke
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.dp.toPx()
            color = android.graphics.Color.argb(180, 255, 255, 255)
        }
        val strokePath = createRoundRectPath(
            0.5f, 0.5f, width - 0.5f, height - 0.5f, rad, rad
        )
        nativeCanvas.drawPath(strokePath, strokePaint)
    }
}

/**
 * Standard Home Screen tactile shadow for full-width rounded cards (28dp corners).
 * Uses TargetTokens.shadowDark palette to achieve deep, soft, diffused elevation.
 */
fun Modifier.homeCardShadow(
    cornerRadius: Dp = 28.dp,
    elevation: Dp = 16.dp,
    spotColor: Color = Color(0xFF2B3F58),
    ambientColor: Color = Color(0xFF384F6E)
): Modifier = this.shadow(
    elevation = elevation,
    shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius),
    spotColor = spotColor,
    ambientColor = ambientColor
)

/**
 * Standard tactile shadow for smaller badges, pills, and history list items.
 */
fun Modifier.homeItemShadow(
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 8.dp,
    spotColor: Color = Color(0xCC334B68),
    ambientColor: Color = Color(0x99384F6E)
): Modifier = this.shadow(
    elevation = elevation,
    shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius),
    spotColor = spotColor,
    ambientColor = ambientColor
)

private inline fun drawIntoNativeCanvas(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    block: (android.graphics.Canvas) -> Unit
) {
    drawScope.drawContext.canvas.nativeCanvas.let(block)
}
