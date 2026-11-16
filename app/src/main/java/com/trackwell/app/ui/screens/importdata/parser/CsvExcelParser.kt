package com.trackwell.app.ui.screens.importdata.parser

import com.trackwell.app.data.model.ParsedDayRecord
import com.trackwell.app.data.model.ParsedFluidItem
import com.trackwell.app.data.model.ParsedSugarItem
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.ui.common.PlausibleRanges

object CsvExcelParser {

    fun parseCsv(csvContent: String): List<ParsedDayRecord> {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.size < 2) return emptyList()

        val delimiter = if (lines[0].contains('\t')) '\t' else ','
        val headers = lines[0].split(delimiter).map { it.trim().lowercase() }

        val dateCol = headers.indexOfFirst { it.contains("date") }
        val weightCol = headers.indexOfFirst { it.contains("weight") || it.contains("wt") }
        val intakeCol = headers.indexOfFirst { it.contains("intake") || it.contains("fluid") }
        val urineCol = headers.indexOfFirst { it.contains("urine") || it.contains("output") }
        val sugarBfCol = headers.indexOfFirst { it.contains("breakfast") || it.contains("fasting") }
        val sugarLunchCol = headers.indexOfFirst { it.contains("lunch") }
        val sugarDinnerCol = headers.indexOfFirst { it.contains("dinner") }

        val records = mutableListOf<ParsedDayRecord>()

        for (i in 1 until lines.size) {
            val tokens = lines[i].split(delimiter).map { it.trim() }
            val dateStr = if (dateCol >= 0 && dateCol < tokens.size) tokens[dateCol] else null
            if (dateStr.isNullOrBlank()) continue

            val record = ParsedDayRecord(date = dateStr)

            // Weight
            if (weightCol >= 0 && weightCol < tokens.size) {
                tokens[weightCol].toDoubleOrNull()?.let {
                    record.morningWeightKg = it
                }
            }

            // Intake
            if (intakeCol >= 0 && intakeCol < tokens.size) {
                tokens[intakeCol].toIntOrNull()?.let {
                    record.fluidItems.add(
                        ParsedFluidItem(
                            amountMl = it,
                            type = FluidType.INTAKE,
                            isOutlier = !PlausibleRanges.isPlausibleFluid(it)
                        )
                    )
                }
            }

            // Urine
            if (urineCol >= 0 && urineCol < tokens.size) {
                tokens[urineCol].toIntOrNull()?.let {
                    record.fluidItems.add(
                        ParsedFluidItem(
                            amountMl = it,
                            type = FluidType.URINE,
                            isOutlier = !PlausibleRanges.isPlausibleFluid(it)
                        )
                    )
                }
            }

            // Sugar Breakfast
            if (sugarBfCol >= 0 && sugarBfCol < tokens.size) {
                tokens[sugarBfCol].toIntOrNull()?.let {
                    record.sugarItems[SugarSlot.BEFORE_BREAKFAST] = ParsedSugarItem(
                        slot = SugarSlot.BEFORE_BREAKFAST,
                        sugarMgDl = it,
                        isOutlier = !PlausibleRanges.isPlausibleSugar(it)
                    )
                }
            }

            // Sugar Lunch
            if (sugarLunchCol >= 0 && sugarLunchCol < tokens.size) {
                tokens[sugarLunchCol].toIntOrNull()?.let {
                    record.sugarItems[SugarSlot.BEFORE_LUNCH] = ParsedSugarItem(
                        slot = SugarSlot.BEFORE_LUNCH,
                        sugarMgDl = it,
                        isOutlier = !PlausibleRanges.isPlausibleSugar(it)
                    )
                }
            }

            // Sugar Dinner
            if (sugarDinnerCol >= 0 && sugarDinnerCol < tokens.size) {
                tokens[sugarDinnerCol].toIntOrNull()?.let {
                    record.sugarItems[SugarSlot.BEFORE_DINNER] = ParsedSugarItem(
                        slot = SugarSlot.BEFORE_DINNER,
                        sugarMgDl = it,
                        isOutlier = !PlausibleRanges.isPlausibleSugar(it)
                    )
                }
            }

            record.hasOutliers = record.fluidItems.any { it.isOutlier } || record.sugarItems.values.any { it.isOutlier }
            records.add(record)
        }

        return records
    }
}
