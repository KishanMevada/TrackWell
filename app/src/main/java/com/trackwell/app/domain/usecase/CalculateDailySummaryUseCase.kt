package com.trackwell.app.domain.usecase

import com.trackwell.app.domain.model.DailyFluidSummary
import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.FluidType
import javax.inject.Inject

class CalculateDailySummaryUseCase @Inject constructor() {

    fun execute(entries: List<FluidEntry>, targetMl: Int): DailyFluidSummary {
        var totalIntake = 0
        var totalUrine = 0

        for (entry in entries) {
            when (entry.type) {
                FluidType.INTAKE -> totalIntake += entry.amountMl
                FluidType.URINE -> totalUrine += entry.amountMl
            }
        }

        val balance = totalIntake - totalUrine
        val progress = if (targetMl > 0) {
            (totalIntake.toFloat() / targetMl.toFloat()).coerceAtLeast(0f)
        } else {
            0f
        }

        return DailyFluidSummary(
            totalIntakeMl = totalIntake,
            totalUrineMl = totalUrine,
            balanceMl = balance,
            targetMl = targetMl,
            progress = progress
        )
    }

    fun calculateWeightDifference(currentWeightKg: Double?, previousWeightKg: Double?): Double? {
        if (currentWeightKg == null || previousWeightKg == null) return null
        val diff = currentWeightKg - previousWeightKg
        return Math.round(diff * 100.0) / 100.0
    }
}
