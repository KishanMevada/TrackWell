package com.trackwell.app.domain.usecase

import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.PeriodAverages
import javax.inject.Inject

class CalculatePeriodAveragesUseCase @Inject constructor() {

    fun execute(days: List<DayOverview>): PeriodAverages {
        if (days.isEmpty()) return PeriodAverages()

        val dayCount = days.size
        val daysWithWeight = days.filter { it.morningWeightKg != null }
        val daysWithIntake = days.filter { it.fluidSummary.totalIntakeMl > 0 }
        val daysWithUrine = days.filter { it.fluidSummary.totalUrineMl > 0 }
        val daysWithAnyFluid = days.filter { it.fluidSummary.totalIntakeMl > 0 || it.fluidSummary.totalUrineMl > 0 }

        val avgWeight = if (daysWithWeight.isNotEmpty()) {
            val sum = daysWithWeight.sumOf { it.morningWeightKg ?: 0.0 }
            Math.round((sum / daysWithWeight.size) * 100.0) / 100.0
        } else 0.0

        val avgIntake = if (daysWithAnyFluid.isNotEmpty()) {
            val sum = daysWithAnyFluid.sumOf { it.fluidSummary.totalIntakeMl }
            Math.round((sum.toDouble() / daysWithAnyFluid.size) * 10.0) / 10.0
        } else 0.0

        val avgUrine = if (daysWithAnyFluid.isNotEmpty()) {
            val sum = daysWithAnyFluid.sumOf { it.fluidSummary.totalUrineMl }
            Math.round((sum.toDouble() / daysWithAnyFluid.size) * 10.0) / 10.0
        } else 0.0

        val avgBalance = if (daysWithAnyFluid.isNotEmpty()) {
            val sum = daysWithAnyFluid.sumOf { it.fluidSummary.balanceMl }
            Math.round((sum.toDouble() / daysWithAnyFluid.size) * 10.0) / 10.0
        } else 0.0

        val minBalance = if (daysWithAnyFluid.isNotEmpty()) {
            daysWithAnyFluid.minOf { it.fluidSummary.balanceMl }
        } else 0

        val maxBalance = if (daysWithAnyFluid.isNotEmpty()) {
            daysWithAnyFluid.maxOf { it.fluidSummary.balanceMl }
        } else 0

        var totalSugar = 0
        var sugarCount = 0
        var totalInsulin = 0.0

        for (day in days) {
            for (sugarEntry in day.sugarEntries.values) {
                totalSugar += sugarEntry.sugarMgDl
                sugarCount++
                totalInsulin += sugarEntry.insulinUnits
            }
        }

        val avgSugar = if (sugarCount > 0) {
            Math.round((totalSugar.toDouble() / sugarCount) * 10.0) / 10.0
        } else 0.0

        return PeriodAverages(
            dayCount = dayCount,
            daysWithData = daysWithAnyFluid.size.coerceAtLeast(daysWithWeight.size),
            averageWeightKg = avgWeight,
            averageIntakeMl = avgIntake,
            averageUrineMl = avgUrine,
            averageBalanceMl = avgBalance,
            minBalanceMl = minBalance,
            maxBalanceMl = maxBalance,
            averageSugarMgDl = avgSugar,
            totalInsulinUnits = Math.round(totalInsulin * 10.0) / 10.0
        )
    }
}
