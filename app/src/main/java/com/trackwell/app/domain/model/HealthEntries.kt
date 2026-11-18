package com.trackwell.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FluidEntry(
    val id: Long = 0,
    val date: String,          // Format: "yyyy-MM-dd"
    val timestamp: Long,       // Epoch millis
    val amountMl: Int,
    val type: FluidType,
    val note: String? = null
)

@Immutable
data class SugarEntry(
    val id: Long = 0,
    val date: String,          // Format: "yyyy-MM-dd"
    val slot: SugarSlot,
    val sugarMgDl: Int,        // e.g. 115
    val insulinUnits: Double,  // e.g. 4.0 or 0.0
    val timestamp: Long,
    val note: String? = null
)
