package com.trackwell.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/* ---------------------------------------------------------------- colors */
private val BgTop = Color(0xFFE9F0FA)
private val BgMid = Color(0xFFEDF4FD)
private val BgBottom = Color(0xFFEAF2FD)
private val Navy = Color(0xFF041F43)
private val LineCool = Color(0xFF9ED3F2)
private val LineWarm = Color(0xFFF4C99E)
private val WaveBlue = Color(0xFF7CC4F0)
private val WaveCyan = Color(0xFF1ED2F0)

private val EaseOutBack = Easing { x ->
    val c1 = 1.70158f; val c3 = c1 + 1f; val t = x - 1f
    1f + c3 * t * t * t + c1 * t * t
}

private fun seg(t: Float, a: Float, b: Float, e: Easing = FastOutSlowInEasing) =
    e.transform(((t - a) / (b - a)).coerceIn(0f, 1f))

private fun path(block: Path.() -> Unit) = Path().apply(block)

/* ------------------------------------------------- design-space geometry
   Illustration design space: 941 x 1090 units (same proportions as the artwork). */

private val BodyPath = path {
    moveTo(380f, -300f)
    lineTo(392f, 110f)
    cubicTo(380f, 175f, 235f, 190f, 178f, 268f)
    cubicTo(142f, 325f, 144f, 430f, 130f, 560f)
    cubicTo(118f, 700f, 82f, 860f, 58f, 1100f)
    lineTo(884f, 1100f)
    cubicTo(860f, 860f, 824f, 700f, 812f, 560f)
    cubicTo(798f, 430f, 800f, 325f, 764f, 268f)
    cubicTo(708f, 190f, 600f, 178f, 586f, 138f)
    lineTo(592f, 118f)
    cubicTo(612f, 112f, 612f, 96f, 603f, 88f)
    cubicTo(618f, 70f, 612f, 60f, 622f, 40f)
    cubicTo(632f, 22f, 640f, 0f, 646f, -300f)
    close()
}

// drawn on the left, mirrored for the right side
private val BodyInnerLeft = path {
    moveTo(392f, 150f); cubicTo(340f, 185f, 300f, 215f, 265f, 250f)   // collar bone
    moveTo(222f, 330f); cubicTo(196f, 450f, 208f, 620f, 238f, 800f)   // arm/torso contour
}

private val LungLeft = path {
    moveTo(445f, 290f)
    cubicTo(390f, 285f, 340f, 350f, 335f, 430f)
    cubicTo(330f, 490f, 350f, 512f, 395f, 502f)
    cubicTo(440f, 495f, 458f, 440f, 452f, 380f)
    cubicTo(450f, 340f, 452f, 310f, 445f, 290f)
    close()
}
private val LungBronchi = path {
    moveTo(458f, 300f); lineTo(452f, 345f); cubicTo(448f, 375f, 425f, 395f, 400f, 422f)
    moveTo(448f, 360f); cubicTo(425f, 360f, 400f, 372f, 380f, 392f)
    moveTo(425f, 398f); cubicTo(410f, 412f, 400f, 440f, 398f, 468f)
    moveTo(438f, 335f); cubicTo(420f, 330f, 395f, 335f, 375f, 350f)
}

private val LiverPath = path {
    moveTo(305f, 585f)
    cubicTo(330f, 550f, 420f, 545f, 455f, 565f)
    cubicTo(450f, 600f, 430f, 625f, 395f, 635f)
    cubicTo(350f, 645f, 315f, 630f, 305f, 585f)
    close()
}

private val HeartPath = path {
    moveTo(560f, 415f)
    cubicTo(580f, 395f, 615f, 405f, 612f, 440f)
    cubicTo(610f, 480f, 575f, 505f, 552f, 530f)
    cubicTo(528f, 510f, 500f, 480f, 505f, 445f)
    cubicTo(508f, 415f, 540f, 400f, 560f, 415f)
    close()
}
private val HeartVessels = path {
    moveTo(535f, 420f); lineTo(520f, 378f)
    moveTo(578f, 412f); lineTo(590f, 372f)
    moveTo(556f, 408f); lineTo(552f, 366f)
}

private val KidneyLeft = path {
    moveTo(405f, 692f)
    cubicTo(360f, 670f, 322f, 712f, 330f, 765f)
    cubicTo(336f, 815f, 375f, 836f, 402f, 812f)
    cubicTo(385f, 795f, 382f, 770f, 395f, 755f)
    cubicTo(410f, 740f, 418f, 712f, 405f, 692f)
    close()
}
private val UreterLeft = path {
    moveTo(398f, 800f); cubicTo(422f, 812f, 440f, 830f, 436f, 858f)
}

/* constellation lines */
private val OrbitLeft = path {
    moveTo(222f, 452f); cubicTo(120f, 505f, 30f, 590f, 60f, 690f)
    cubicTo(70f, 730f, 100f, 745f, 125f, 745f)
}
private val ScaleToKidney = path {
    moveTo(215f, 735f); cubicTo(250f, 775f, 285f, 785f, 333f, 762f)
}
private val DropToTop = path {
    moveTo(325f, 420f); cubicTo(380f, 402f, 430f, 376f, 480f, 365f)
}
private val TopToHeart = path {
    moveTo(480f, 365f); cubicTo(530f, 358f, 580f, 366f, 612f, 372f)
}
private val OrbitRight = path {
    moveTo(725f, 345f); cubicTo(840f, 330f, 915f, 420f, 882f, 500f)
    cubicTo(862f, 555f, 835f, 575f, 822f, 585f)
}
private val PillToKidney = path {
    moveTo(748f, 662f); cubicTo(710f, 690f, 670f, 722f, 612f, 740f)
}
private val SpinePath = path { moveTo(480f, 365f); lineTo(476f, 760f) }
private val BranchLeft = path {
    moveTo(476f, 628f); cubicTo(430f, 645f, 360f, 640f, 336f, 724f)
}
private val JoinLeft = path {
    moveTo(436f, 858f); cubicTo(440f, 890f, 470f, 905f, 475f, 945f)
}
private val SpineTail = path { moveTo(475f, 945f); lineTo(475f, 1030f) }

private val EcgPath = path {              // frame: 941 x 1672
    moveTo(20f, 1474f)
    cubicTo(80f, 1452f, 170f, 1442f, 255f, 1450f)
    cubicTo(300f, 1455f, 325f, 1462f, 345f, 1450f)
    cubicTo(360f, 1442f, 372f, 1470f, 380f, 1485f)
    cubicTo(392f, 1470f, 405f, 1402f, 418f, 1402f)
    cubicTo(432f, 1402f, 440f, 1475f, 452f, 1478f)
    cubicTo(500f, 1480f, 520f, 1446f, 600f, 1440f)
    cubicTo(680f, 1434f, 740f, 1462f, 815f, 1450f)
}

/* ------------------------------------------------------------- helpers */
private fun DrawScope.mirrored(block: DrawScope.() -> Unit) =
    withTransform({ scale(-1f, 1f, Offset(475f, 0f)) }) { block() }

private fun DrawScope.trimmed(p: Path, progress: Float, color: Color, width: Float = 2.4f) {
    if (progress <= 0f) return
    val pm = PathMeasure().apply { setPath(p, false) }
    val part = Path()
    pm.getSegment(0f, pm.length * progress.coerceIn(0f, 1f), part, true)
    drawPath(part, color, alpha = 0.22f, style = Stroke(width * 3.2f, cap = StrokeCap.Round))
    drawPath(part, color, style = Stroke(width, cap = StrokeCap.Round))
}

private fun DrawScope.node(pos: Offset, color: Color, pulse: Float, a: Float) {
    if (a <= 0f) return
    drawCircle(color, radius = 14f * (0.8f + 0.4f * pulse), center = pos, alpha = 0.25f * a)
    drawCircle(color, radius = 5.5f, center = pos, alpha = a)
    drawCircle(Color.White, radius = 2.2f, center = pos, alpha = 0.9f * a)
}

/* ---------------------------------------------------------------- body */
private fun DrawScope.drawBody(k: Float) {
    val fill = Brush.verticalGradient(
        0f to Color(0xFFB9DAF4).copy(alpha = 0.60f),
        0.55f to Color(0xFFC6E0F6).copy(alpha = 0.40f),
        1f to Color(0xFFD8EAFA).copy(alpha = 0f),
        startY = -20f, endY = 1100f
    )
    drawPath(BodyPath, fill, alpha = k)
    val edge = Brush.verticalGradient(
        0f to Color.White.copy(alpha = 0.85f),
        0.7f to Color.White.copy(alpha = 0.40f),
        1f to Color.Transparent,
        startY = -20f, endY = 1100f
    )
    drawPath(BodyPath, edge, alpha = k, style = Stroke(3f, join = StrokeJoin.Round))
    val inner = Color.White
    drawPath(BodyInnerLeft, inner, alpha = 0.35f * k, style = Stroke(2.5f, cap = StrokeCap.Round))
    mirrored { drawPath(BodyInnerLeft, inner, alpha = 0.35f * k, style = Stroke(2.5f, cap = StrokeCap.Round)) }
    drawLine(
        Brush.verticalGradient(listOf(Color(0xFF8FD3F0), Color.Transparent), startY = 230f, endY = 1000f),
        Offset(475f, 230f), Offset(475f, 1000f), strokeWidth = 2f, alpha = 0.5f * k
    )
}

/* -------------------------------------------------------------- organs */
private fun DrawScope.drawLungs(k: Float) {
    val fill = Brush.verticalGradient(
        listOf(Color(0xFFF2B8B8), Color(0xFFEBA3A3)), startY = 285f, endY = 505f
    )
    val draw: DrawScope.() -> Unit = {
        drawPath(LungLeft, fill, alpha = 0.42f * k)
        drawPath(LungLeft, Color(0xFFE88E8A), alpha = 0.45f * k, style = Stroke(2f))
        drawPath(LungBronchi, Color(0xFFE5807C), alpha = 0.55f * k, style = Stroke(2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
    draw(); mirrored(draw)
}

private fun DrawScope.drawLiver(k: Float) {
    drawPath(LiverPath, Color(0xFFC8A2C8), alpha = 0.50f * k)
    drawPath(LiverPath, Color(0xFFB98FBF), alpha = 0.45f * k, style = Stroke(2f))
}

private fun DrawScope.drawKidneys(k: Float, pulse: Float) {
    val draw: DrawScope.() -> Unit = {
        val c = Offset(372f, 760f)
        drawCircle(
            Brush.radialGradient(listOf(Color(0xFFFF9A96), Color.Transparent), center = c, radius = 110f),
            radius = 110f, center = c, alpha = (0.45f + 0.2f * pulse) * k
        )
        drawPath(UreterLeft, Color(0xFFE9DAC3), alpha = k, style = Stroke(8f, cap = StrokeCap.Round))
        drawPath(UreterLeft, Color(0xFFD2BFA3), alpha = 0.6f * k, style = Stroke(1.5f, cap = StrokeCap.Round))
        // renal vessels
        drawLine(Color(0xFF5E7FC0), Offset(396f, 744f), Offset(434f, 744f), 9f, StrokeCap.Round, alpha = k)
        drawLine(Color(0xFFD9534F), Offset(398f, 762f), Offset(434f, 766f), 9f, StrokeCap.Round, alpha = k)
        drawPath(
            KidneyLeft,
            Brush.radialGradient(
                listOf(Color(0xFFF7B0AA), Color(0xFFE98A87), Color(0xFFD56A6D)),
                center = Offset(365f, 735f), radius = 110f
            ), alpha = k
        )
        drawPath(KidneyLeft, Color.White, alpha = 0.35f * k, style = Stroke(2f))
        // highlight
        drawCircle(Color.White, radius = 9f, center = Offset(352f, 725f), alpha = 0.28f * k)
    }
    draw(); mirrored(draw)
}

private fun DrawScope.drawHeart(k: Float, beat: Float) {
    val c = Offset(556f, 465f)
    drawCircle(
        Brush.radialGradient(listOf(Color(0xFFFF8F8A), Color.Transparent), center = c, radius = 120f),
        radius = 120f, center = c, alpha = (0.38f + 0.15f * beat) * k
    )
    val s = 1f + 0.035f * beat
    withTransform({ scale(s, s, c) }) {
        drawPath(HeartVessels, Color(0xFFE9A199), alpha = 0.9f * k, style = Stroke(11f, cap = StrokeCap.Round))
        drawPath(
            HeartPath,
            Brush.radialGradient(
                listOf(Color(0xFFFAC9BC), Color(0xFFEB8F8B), Color(0xFFDD7272)),
                center = Offset(548f, 450f), radius = 95f
            ), alpha = k
        )
        drawCircle(Color.White, radius = 8f, center = Offset(538f, 440f), alpha = 0.3f * k)
    }
}

/* -------------------------------------------------------------- badges */
private fun DrawScope.drawBadge(
    c: Offset, top: Color, bottom: Color, kind: Int, p: Float, dy: Float
) {
    if (p <= 0f) return
    val a = p.coerceIn(0f, 1f)
    val sc = 0.55f + 0.45f * p
    withTransform({ translate(0f, dy); scale(sc, sc, c) }) {
        drawCircle(
            Brush.radialGradient(listOf(bottom, Color.Transparent), center = c, radius = 92f),
            radius = 92f, center = c, alpha = 0.28f * a
        )
        drawCircle(
            Brush.verticalGradient(listOf(top, bottom), startY = c.y - 57f, endY = c.y + 57f),
            radius = 57f, center = c, alpha = a
        )
        drawCircle(
            Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.40f), Color.Transparent),
                center = Offset(c.x - 18f, c.y - 24f), radius = 46f
            ), radius = 57f, center = c, alpha = a
        )
        drawCircle(Color.White, radius = 56f, center = c, alpha = 0.35f * a, style = Stroke(2f))

        val st = Stroke(5.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val x = c.x; val y = c.y
        when (kind) {
            0 -> { // drop
                val drop = path {
                    moveTo(x, y - 27f)
                    cubicTo(x + 8f, y - 12f, x + 21f, y - 2f, x + 21f, y + 10f)
                    cubicTo(x + 21f, y + 22f, x + 11f, y + 29f, x, y + 29f)
                    cubicTo(x - 11f, y + 29f, x - 21f, y + 22f, x - 21f, y + 10f)
                    cubicTo(x - 21f, y - 2f, x - 8f, y - 12f, x, y - 27f)
                    close()
                }
                drawPath(drop, Color.White, alpha = a, style = st)
                val hi = path { moveTo(x - 10f, y + 9f); cubicTo(x - 10f, y + 16f, x - 6f, y + 20f, x - 1f, y + 20f) }
                drawPath(hi, Color.White, alpha = 0.8f * a, style = Stroke(4f, cap = StrokeCap.Round))
            }
            1 -> { // heart + pulse
                val heart = path {
                    moveTo(x, y + 27f)
                    cubicTo(x - 42f, y + 4f, x - 31f, y - 27f, x - 14f, y - 25f)
                    cubicTo(x - 6f, y - 24f, x - 1f, y - 18f, x, y - 13f)
                    cubicTo(x + 1f, y - 18f, x + 6f, y - 24f, x + 14f, y - 25f)
                    cubicTo(x + 31f, y - 27f, x + 42f, y + 4f, x, y + 27f)
                    close()
                }
                drawPath(heart, Color.White, alpha = a, style = st)
                val pulse = path {
                    moveTo(x - 24f, y + 3f); lineTo(x - 10f, y + 3f); lineTo(x - 4f, y - 9f)
                    lineTo(x + 4f, y + 12f); lineTo(x + 9f, y + 3f); lineTo(x + 24f, y + 3f)
                }
                drawPath(pulse, Color.White, alpha = a, style = Stroke(4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            2 -> { // pill
                withTransform({ rotate(-40f, c) }) {
                    val tl = Offset(x - 29f, y - 15f)
                    val sz = Size(58f, 30f)
                    drawRoundRect(Color.White, tl, sz, CornerRadius(15f), alpha = 0.92f * a)
                    drawLine(bottom, Offset(x, y - 15f), Offset(x, y + 15f), 3f, alpha = 0.45f * a)
                    drawRoundRect(bottom, tl, sz, CornerRadius(15f), alpha = 0.18f * a, style = Stroke(1.5f))
                }
            }
            else -> { // scale
                drawRoundRect(
                    Color.White, Offset(x - 30f, y - 27f), Size(60f, 56f),
                    CornerRadius(13f), alpha = a, style = st
                )
                val arrow = path {
                    moveTo(x, y - 18f); lineTo(x, y - 2f)
                    moveTo(x - 8f, y - 9f); lineTo(x, y - 1f); lineTo(x + 8f, y - 9f)
                }
                drawPath(arrow, Color.White, alpha = a, style = Stroke(4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawLine(Color.White, Offset(x - 14f, y + 14f), Offset(x + 14f, y + 14f), 4f, StrokeCap.Round, alpha = 0.85f * a)
            }
        }
    }
}

/* ---------------------------------------------------------- composable */
@Composable
fun TrackWellAnimatedSplash(onFinished: () -> Unit = {}) {
    val isInspection = LocalInspectionMode.current
    val timeline = remember { Animatable(if (isInspection) 1f else 0f) }
    LaunchedEffect(isInspection) {
        if (!isInspection) {
            timeline.animateTo(1f, tween(3400, easing = LinearEasing))
            delay(500)
            onFinished()
        }
    }

    val loop = rememberInfiniteTransition(label = "loop")
    val phase by loop.animateFloat(
        0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "phase"
    )

    val measurer = rememberTextMeasurer()
    val titleLayout = remember {
        measurer.measure("TrackWell", TextStyle(fontSize = 100.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-2).sp))
    }
    val tagLayout = remember {
        measurer.measure("One rhythm for your health", TextStyle(fontSize = 100.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.sp))
    }

    Canvas(Modifier.fillMaxSize()) {
        val t = if (isInspection) 1f else timeline.value
        val W = size.width
        val H = size.height
        val two = (2f * PI).toFloat()

        // animation values
        val bodyK = seg(t, 0.00f, 0.25f)
        val organK = seg(t, 0.12f, 0.40f)
        val lineP = seg(t, 0.30f, 0.65f)
        val titleK = seg(t, 0.55f, 0.75f)
        val tagK = seg(t, 0.62f, 0.82f)
        val ecgP = seg(t, 0.65f, 1.00f)
        val pulse = 0.5f + 0.5f * sin(two * 2f * phase)
        val beat = max(0f, sin(two * 4f * phase)).let { it * it * it }

        // layout
        val si = min(W / 941f, H * 0.66f / 1090f)
        val ox = (W - 941f * si) / 2f
        val hi = 1090f * si
        val rest = H - hi
        val wRef = min(W, H * (941f / 1672f))
        val u = wRef / 941f

        // background
        drawRect(Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom)))
        drawCircle(
            Brush.radialGradient(
                listOf(Color(0xFFCFE5F8).copy(alpha = 0.85f), Color.Transparent),
                center = Offset(W / 2f, 520f * si), radius = 560f * si
            ), radius = 560f * si, center = Offset(W / 2f, 520f * si), alpha = bodyK
        )

        // illustration
        withTransform({ translate(ox, 0f); scale(si, si, Offset.Zero) }) {
            drawBody(bodyK)
            drawLungs(organK)
            drawLiver(organK)
            drawKidneys(organK, pulse)
            drawHeart(organK, beat)

            // constellation lines
            trimmed(OrbitLeft, lineP, LineCool)
            trimmed(ScaleToKidney, lineP, LineWarm)
            trimmed(DropToTop, lineP, LineCool)
            trimmed(TopToHeart, lineP, LineCool)
            trimmed(OrbitRight, lineP, LineWarm)
            trimmed(PillToKidney, lineP, LineCool)
            trimmed(SpinePath, lineP, LineCool, 2f)
            trimmed(BranchLeft, lineP, LineCool, 2f)
            mirrored { trimmed(BranchLeft, lineP, LineCool, 2f) }
            trimmed(JoinLeft, lineP, LineCool, 2f)
            mirrored { trimmed(JoinLeft, lineP, LineCool, 2f) }
            trimmed(SpineTail, lineP, LineCool, 2f)

            // flowing particle along the spine
            if (lineP > 0.95f) {
                val pm = PathMeasure().apply { setPath(SpinePath, false) }
                node(pm.getPosition(pm.length * phase), Color(0xFF6FD6F5), pulse, 1f)
            }

            // glowing nodes
            val na = seg(t, 0.55f, 0.70f)
            node(Offset(480f, 365f), Color(0xFF8FD8F5), pulse, na)
            node(Offset(476f, 623f), Color(0xFF8FD8F5), pulse, na)
            node(Offset(476f, 710f), Color(0xFF8FD8F5), pulse, na)
            node(Offset(120f, 530f), Color(0xFF7EC8F5), pulse, na)
            node(Offset(827f, 360f), Color(0xFFF2B36B), pulse, na)
            node(Offset(303f, 780f), Color(0xFFF2B36B), pulse, na)
            node(Offset(713f, 687f), Color(0xFF7EC8F5), pulse, na)
            node(Offset(643f, 730f), Color(0xFFF2B36B), pulse, na)

            // badges (staggered pop-in + gentle float)
            fun fl(k: Float) = sin(two * (phase + k)) * 5f
            drawBadge(Offset(271f, 428f), Color(0xFF6FCDCF), Color(0xFF3A9FAE), 0, seg(t, 0.35f, 0.52f, EaseOutBack), fl(0.0f))
            drawBadge(Offset(668f, 368f), Color(0xFFF59A96), Color(0xFFE5736F), 1, seg(t, 0.40f, 0.57f, EaseOutBack), fl(0.25f))
            drawBadge(Offset(793f, 612f), Color(0xFF6FCDCF), Color(0xFF3A9FAE), 2, seg(t, 0.45f, 0.62f, EaseOutBack), fl(0.5f))
            drawBadge(Offset(167f, 712f), Color(0xFFF2C378), Color(0xFFD89A45), 3, seg(t, 0.50f, 0.67f, EaseOutBack), fl(0.75f))
        }

        // title + tagline
        val titleW = 0.60f * wRef
        val tagW = 0.66f * wRef
        val tF = titleW / titleLayout.size.width
        val gF = tagW / tagLayout.size.width
        val titleCy = hi + rest * (70f / 582f)
        val tagCy = hi + rest * (172f / 582f)
        val ecgY = hi + rest * (360f / 582f)

        withTransform({
            translate(
                (W - titleW) / 2f,
                titleCy - titleLayout.size.height * tF / 2f + (1f - titleK) * 24f * u
            )
            scale(tF, tF, Offset.Zero)
        }) { drawText(titleLayout, color = Navy, alpha = titleK) }

        withTransform({
            translate(
                (W - tagW) / 2f,
                tagCy - tagLayout.size.height * gF / 2f + (1f - tagK) * 24f * u
            )
            scale(gF, gF, Offset.Zero)
        }) { drawText(tagLayout, color = Navy, alpha = tagK) }

        // ECG wave with glowing end dot
        withTransform({
            translate((W - 941f * u) / 2f, ecgY - 1450f * u)
            scale(u, u, Offset.Zero)
        }) {
            if (ecgP > 0f) {
                val pm = PathMeasure().apply { setPath(EcgPath, false) }
                val part = Path()
                pm.getSegment(0f, pm.length * ecgP, part, true)
                val brush = Brush.horizontalGradient(
                    0f to WaveBlue.copy(alpha = 0.25f), 0.5f to WaveBlue, 1f to WaveCyan,
                    startX = 20f, endX = 815f
                )
                drawPath(part, brush, alpha = 0.14f, style = Stroke(11f, cap = StrokeCap.Round))
                drawPath(part, brush, style = Stroke(3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                val pos = pm.getPosition(pm.length * ecgP)
                drawCircle(WaveCyan, radius = 20f * (0.8f + 0.4f * pulse), center = pos, alpha = 0.18f)
                drawCircle(WaveCyan, radius = 11f, center = pos, alpha = 0.35f)
                drawCircle(WaveCyan, radius = 6.5f, center = pos)
                drawCircle(Color.White, radius = 2.4f, center = pos, alpha = 0.9f)
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SplashPhone() = TrackWellAnimatedSplash()

@Preview(showSystemUi = true, device = "spec:width=411dp,height=914dp")
@Composable
private fun SplashTall() = TrackWellAnimatedSplash()

@Preview(showSystemUi = true, device = "id:pixel_tablet")
@Composable
private fun SplashTablet() = TrackWellAnimatedSplash()