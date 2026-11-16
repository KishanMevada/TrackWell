package com.trackwell.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.trackwell.app.R

object TargetTokens {
    // Screen background: Linear gradient 160deg, #F5F9FC to #EAF1FB
    val bgColors = listOf(Color(0xFFF5F9FC), Color(0xFFEAF1FB))
    val bg = Brush.linearGradient(
        colors = bgColors,
        start = Offset(0f, 0f),
        end = Offset(1000f, 2200f)
    )

    // Surface gradient: #F6F8FC (top-left) to #E5ECF6 (bottom-right)
    val surfaceColors = listOf(Color(0xFFF6F8FC), Color(0xFFE5ECF6))
    val surface = Brush.linearGradient(
        colors = surfaceColors,
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // Shadows
    val shadowLight = Color(0xFFFFFFFF) // 100%
    val shadowDark = Color(0x738E9FBA)  // #8E9FBA at 45% (0x73 = 115)
    val shadowDark40 = Color(0x668E9FBA) // at 40%

    // Text & Icons
    val textPrimary = Color(0xFF05070D)
    val textSecondary = Color(0xFF4A5060)
    val iconInactive = Color(0xFF7F8696)
    val iconActive = Color(0xFF05070D)
    val chevronSide = Color(0xFF4A5060)
    val chevronDown = Color(0xFF1B2030)
    val divider = Color(0xFFD9DFEA)

    // Chips
    val chipBlue = Color(0xFFCEEBF7)
    val chipGreen = Color(0xFFCEF1D9)
    val chipPeachColors = listOf(Color(0xFFF0CEA8), Color(0xFFE8C6A2))
    val chipPeach = Brush.verticalGradient(chipPeachColors)

    // Liquid: Fluid intake vial
    val waterLiquidTop = Color(0xFF7FD9E8)
    val waterLiquidBody = Color(0xFF67D0E0)
    val waterLiquidBottom = Color(0xFF4FA8CC)
    val waterGlassColors = listOf(Color(0xFFD0E9EE), Color(0xFFE6F3F6))
    val waterGlass = Brush.verticalGradient(waterGlassColors)

    // Liquid: Urine output vial
    val urineLiquidTop = Color(0xFFF0C77E)
    val urineLiquidBody = Color(0xFFE7BA68)
    val urineLiquidBottom = Color(0xFFD9A04F)
    val urineGlass = Color(0xFFEFE8DD)

    // Pipes
    val pipeBlueCore = Color(0xFF8BE7F0)
    val pipeBlueRim = Color(0xFF7FDAE6)
    val pipeBlueHighlight = Color(0xFFD8F6FA)
    val pipeBlueGlow = Color(0xFFBEEAF1)

    val pipeAmberCore = Color(0xFFE9CE97)
    val pipeAmberRim = Color(0xFFD9BA7E)
    val pipeAmberHighlight = Color(0xFFF1D69C)
    val pipeAmberGlow = Color(0xE4D1B0)

    // Kidney colors
    val kidneyHighlight = Color(0xFFD99C9F)
    val kidneyUpper = Color(0xFFAE5A5D)
    val kidneyMid = Color(0xFFA04E52)
    val kidneyLower = Color(0xFF7D343B)
    val kidneyDeepEdge = Color(0xFF782D32)
    val ureterGradient = listOf(Color(0xFFF0C3AE), Color(0xFFDFA58E))
    val vein = Color(0xFF7FA5D6)
    val artery = Color(0xFFC45A62)

    // Pressed active nav pill face
    val pressedFace = Brush.linearGradient(
        colors = listOf(Color(0xFFE3ECF5), Color(0xFFEAF0F9)),
        start = Offset.Zero,
        end = Offset.Infinite
    )
}

// Inter Font Family
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

object TargetTypography {
    // Greeting 'Hi, Kishan!' - Bold, ~34sp, measured ink 176.5 wide, capital H 28 tall
    val greeting = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = (-0.01).sp,
        color = TargetTokens.textPrimary
    )

    // Subtitle "Today's Renal Balance" - Regular, ~17.5sp, measured ink 183.6 wide
    val subtitle = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.5.sp,
        letterSpacing = 0.sp,
        color = TargetTokens.textPrimary
    )

    // 'Today' in date pill - Medium, ~17sp, ink 48 wide
    val datePill = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        letterSpacing = (-0.01).sp,
        color = TargetTokens.textPrimary
    )

    // 'Net renal balance' - Regular, ~17sp, ink 143 wide
    val netBalanceLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.5.sp,
        letterSpacing = (-0.01).sp,
        color = TargetTokens.textPrimary
    )

    // '+830' - Bold, ~44sp, ink 114 wide, digits 31 tall
    val netBalanceValue = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        letterSpacing = (-0.02).sp,
        color = TargetTokens.textPrimary
    )

    // 'ml' - Regular, ~24sp, ink 26 wide, shares baseline with +830
    val netBalanceUnit = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        color = TargetTokens.textPrimary
    )

    // Chip text 'Fluid Retention' - Regular, ~17sp, ink 119 wide
    val chipText = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = (-0.01).sp,
        color = TargetTokens.textPrimary
    )

    // Stat card label 'Morning Dry' / 'Weight' - Regular, ~17sp, line height 22 dp
    val statCardLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        letterSpacing = (-0.01).sp,
        color = TargetTokens.textPrimary
    )

    // Stat card value '55.70' - Bold, ~34sp, ink 90 wide, digits 26 tall
    val statCardValue = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = (-0.02).sp,
        color = TargetTokens.textPrimary
    )

    // Stat card unit 'kg', 'mg/dL' - Regular, ~18sp
    val statCardUnit = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        color = TargetTokens.textPrimary
    )

    // Rotated vial title - Medium, ~11sp with etched glass translucency
    val vialTitle = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = (-0.01).sp,
        color = TargetTokens.textPrimary.copy(alpha = 0.70f)
    )

    // Rotated vial value - Regular, ~10.5sp with etched glass translucency (prevents truncation of 2,850 ml / 3,500 ml)
    val vialValue = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.5.sp,
        letterSpacing = (-0.02).sp,
        color = TargetTokens.textPrimary.copy(alpha = 0.65f)
    )

    // Add button text - Regular, ~11sp, '+' ~18sp
    val addButtonPlus = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = TargetTokens.textPrimary
    )
    val addButtonText = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = TargetTokens.textPrimary
    )
}
