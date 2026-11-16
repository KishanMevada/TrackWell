package com.trackwell.app.data.model

import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot

enum class DuplicateResolution {
    SKIP,
    OVERWRITE,
    MERGE
}

data class ParsedFluidItem(
    val amountMl: Int,
    val type: FluidType,
    val timestamp: Long? = null,
    val isOutlier: Boolean = false,
    val confidence: Float = 1.0f
)

data class ParsedSugarItem(
    val slot: SugarSlot,
    val sugarMgDl: Int,
    val insulinUnits: Double = 0.0,
    val isOutlier: Boolean = false,
    val confidence: Float = 1.0f
)

data class ParsedDayRecord(
    val date: String,                   // "yyyy-MM-dd"
    var morningWeightKg: Double? = null,
    val fluidItems: MutableList<ParsedFluidItem> = mutableListOf(),
    val sugarItems: MutableMap<SugarSlot, ParsedSugarItem> = mutableMapOf(),
    var isDuplicate: Boolean = false,
    var duplicateResolution: DuplicateResolution = DuplicateResolution.MERGE,
    var hasOutliers: Boolean = false,
    var overallConfidence: Float = 1.0f
)
