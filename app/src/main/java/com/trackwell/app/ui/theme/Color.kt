package com.trackwell.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// AURA — Personal Health Intelligence System
// Master Design Palette
// ==========================================

// Master Renal Graft Palette (Derived from Centerpiece Organ)
val RenalCrimson = Color(0xFFBE123C)         // Deep Carmine Cortex
val RenalCrimsonBright = Color(0xFFE11D48)   // Vibrant Arterial Crimson
val RenalCrimsonHighlight = Color(0xFFFB7185)// Vitality Specular Highlight
val RenalCrimsonMist = Color(0xFFFFF1F2)     // Soft Rose Surface Tint
val RenalCrimsonBorder = Color(0xFFFECDD3)   // Rose Border
val RenalCrimsonDark = Color(0xFF881337)     // Deep Shadow Cortex

// Primary 5 AURA Core Colors
val AuraMidnight = Color(0xFF0F172A) // Modern Obsidian Dark Identity
val AuraTeal = Color(0xFFBE123C)     // Primary brand accent now matches Kidney Crimson
val AuraMist = Color(0xFFFFF1F2)     // Soft secondary surfaces
val AuraCanvas = Color(0xFFF8F9FA)   // Main clean porcelain background
val AuraAmber = Color(0xFFF59E0B)    // Ureter Amber secondary accent

// Metric Domain Specific Colors
val MetricIntakeBlue = Color(0xFF0284C7)    // Hydration / Fluid intake
val MetricIntakeBlueBg = Color(0xFFF0F9FF)
val MetricIntakeBlueBorder = Color(0xFFBAE6FD)

val MetricUrineAmber = Color(0xFFE8A34A)    // Diuresis / Urine output
val MetricUrineAmberBg = Color(0xFFFFFBEB)
val MetricUrineAmberBorder = Color(0xFFFDE68A)

val MetricSugarViolet = Color(0xFF8B5CF6)   // Blood glucose / Metabolic
val MetricSugarVioletBg = Color(0xFFF5F3FF)
val MetricSugarVioletBorder = Color(0xFFDDD6FE)

val MetricBpCoral = Color(0xFFF43F5E)       // Cardiovascular / Blood pressure
val MetricBpCoralBg = Color(0xFFFFF1F2)
val MetricBpCoralBorder = Color(0xFFFECDD3)

val MetricMedEmerald = Color(0xFF10B981)    // Immunosuppression / Medications
val MetricMedEmeraldBg = Color(0xFFECFDF5)
val MetricMedEmeraldBorder = Color(0xFFA7F3D0)

val MetricWalkingTeal = Color(0xFF087E78)   // Mobility / Steps
val MetricWalkingTealBg = Color(0xFFE8F2F0)

val MetricWeightSlate = Color(0xFF334155)   // Morning dry weight
val MetricWeightSlateBg = Color(0xFFF1F5F9)

val MetricTempOrange = Color(0xFFF97316)    // Body temperature
val MetricTempOrangeBg = Color(0xFFFFF7ED)

// Fluid Difference Interpretation Tones
val DifferenceOptimal = Color(0xFF10B981)
val DifferenceOptimalBg = Color(0xFFECFDF5)
val DifferenceOptimalBorder = Color(0xFFA7F3D0)

val DifferenceRetention = Color(0xFFE8A34A)
val DifferenceRetentionBg = Color(0xFFFFFBEB)
val DifferenceRetentionBorder = Color(0xFFFDE68A)

val DifferenceDeficit = Color(0xFFF43F5E)
val DifferenceDeficitBg = Color(0xFFFFF1F2)
val DifferenceDeficitBorder = Color(0xFFFECDD3)

// Metabolic Reading Status Colors
val SugarGreen = Color(0xFF10B981)
val SugarGreenBg = Color(0xFFECFDF5)
val SugarGreenBorder = Color(0xFFA7F3D0)

val SugarAmber = Color(0xFFE8A34A)
val SugarAmberBg = Color(0xFFFFFBEB)
val SugarAmberBorder = Color(0xFFFDE68A)

val SugarRed = Color(0xFFF43F5E)
val SugarRedBg = Color(0xFFFFF1F2)
val SugarRedBorder = Color(0xFFFECDD3)

val SugarPendingGray = Color(0xFF64748B)
val SugarPendingBg = Color(0xFFF1F5F9)
val SugarPendingBorder = Color(0xFFE2E8F0)

// Canvas & Surface Hierarchy — Claymorphic 3D (Light & Dark)
val CanvasBackgroundLight = Color(0xFFF0F3F8) // Soft dove canvas for extruded 3D claymorphic depth
val SurfaceCardLight = Color(0xFFFFFFFF)      // Pure puffy white ceramic cards
val SurfaceVariantLight = Color(0xFFE9EEF5)
val BorderSubtleLight = Color(0xFFE2E8F0)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)
val TextTertiaryLight = Color(0xFF94A3B8)

val CanvasBackgroundDark = Color(0xFF12151A)  // Deep obsidian charcoal canvas
val SurfaceCardDark = Color(0xFF1E232B)       // Dark puffy clay cards with soft ambient glow
val SurfaceVariantDark = Color(0xFF262C36)
val BorderSubtleDark = Color(0xFF333B47)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextTertiaryDark = Color(0xFF64748B)

// Backward Compatibility Aliases
val BrandTealPrimary = AuraTeal
val BrandTealOnPrimary = Color(0xFFFFFFFF)
val BrandTealContainer = AuraMist
val BrandTealOnContainer = AuraMidnight

val BrandTealDark = Color(0xFF2DD4BF)
val BrandTealOnDark = Color(0xFF042F2C)
val BrandTealContainerDark = Color(0xFF134E4A)
val BrandTealOnContainerDark = Color(0xFF99F6E4)

val BrandSageSecondary = Color(0xFF4A6369)
val BrandSageOnSecondary = Color(0xFFFFFFFF)
val BrandSageContainer = Color(0xFFE2E8F0)
val BrandSageOnContainer = Color(0xFF1E293B)

val BrandSageDark = Color(0xFF94A3B8)
val BrandSageOnDark = Color(0xFF0F172A)
val BrandSageContainerDark = Color(0xFF334155)

val CoralTertiary = MetricBpCoral
val CoralTertiaryContainer = MetricBpCoralBg

val TealPrimary = AuraTeal
val TealOnPrimary = Color(0xFFFFFFFF)
val TealPrimaryContainer = AuraMist
val TealOnPrimaryContainer = AuraMidnight

val SageSecondary = BrandSageSecondary
val SageOnSecondary = Color(0xFFFFFFFF)
val SageSecondaryContainer = BrandSageContainer
val SageOnSecondaryContainer = BrandSageOnContainer

val BackgroundLight = CanvasBackgroundLight
val SurfaceLight = SurfaceCardLight
val OutlineLight = BorderSubtleLight

val BackgroundDark = CanvasBackgroundDark
val SurfaceDark = SurfaceCardDark

val TealPrimaryDark = BrandTealDark
val TealOnPrimaryDark = BrandTealOnDark
val TealPrimaryContainerDark = BrandTealContainerDark
val TealOnPrimaryContainerDark = BrandTealOnContainerDark

val IntakeAqua = MetricIntakeBlue
val IntakeAquaBg = MetricIntakeBlueBg
val IntakeAquaBorder = MetricIntakeBlueBorder
val IntakeAquaDark = Color(0xFF38BDF8)
val IntakeAquaBgDark = Color(0xFF082F49)

val UrineTopaz = MetricUrineAmber
val UrineTopazBg = MetricUrineAmberBg
val UrineTopazBorder = MetricUrineAmberBorder
val UrineTopazDark = Color(0xFFFBBF24)
val UrineTopazBgDark = Color(0xFF451A03)

val IntakeTeal = MetricIntakeBlue
val IntakeTealBg = MetricIntakeBlueBg
val UrineAmber = MetricUrineAmber
val UrineAmberBg = MetricUrineAmberBg
val BalanceNeutral = AuraTeal

val BalanceOptimal = DifferenceOptimal
val BalanceOptimalBg = DifferenceOptimalBg
val BalanceOptimalBorder = DifferenceOptimalBorder

val BalanceWarning = DifferenceRetention
val BalanceWarningBg = DifferenceRetentionBg
val BalanceWarningBorder = DifferenceRetentionBorder

val BalanceDeficit = DifferenceDeficit
val BalanceDeficitBg = DifferenceDeficitBg
val BalanceDeficitBorder = DifferenceDeficitBorder

