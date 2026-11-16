package com.trackwell.app.ui.screens.importdata.parser

import com.trackwell.app.data.model.ParsedDayRecord
import com.trackwell.app.data.model.ParsedFluidItem
import com.trackwell.app.data.model.ParsedSugarItem
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.common.PlausibleRanges
import java.util.regex.Pattern

enum class DetectedLayout {
    FLUID_GRID,
    SUGAR_TABLE,
    UNIFIED_HEALTH
}

object OfflineLayoutParser {

    private val DATE_PATTERN = Pattern.compile(
        "(\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2})|(\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{2,4})|([A-Za-z]{3,9}\\s+\\d{1,2}(?:st|nd|rd|th)?(?:,?\\s+\\d{4})?)"
    )

    fun detectLayout(rawText: String): DetectedLayout {
        val lower = rawText.lowercase()
        val fluidKeywords = listOf("intake", "urine", "fluid", "balance", "output", "water", "weight", "kg")
        val sugarKeywords = listOf("sugar", "glucose", "insulin", "breakfast", "lunch", "dinner", "mg/dl", "units")

        val fluidCount = fluidKeywords.count { lower.contains(it) }
        val sugarCount = sugarKeywords.count { lower.contains(it) }

        return when {
            fluidCount >= 2 && sugarCount >= 2 -> DetectedLayout.UNIFIED_HEALTH
            fluidCount > sugarCount -> DetectedLayout.FLUID_GRID
            sugarCount > fluidCount -> DetectedLayout.SUGAR_TABLE
            else -> DetectedLayout.FLUID_GRID
        }
    }

    fun parseOcrText(rawText: String, defaultDate: String = DateTimeUtils.getTodayIso()): List<ParsedDayRecord> {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val recordsMap = mutableMapOf<String, ParsedDayRecord>()

        var activeDate = defaultDate

        for (line in lines) {
            val dateMatcher = DATE_PATTERN.matcher(line)
            if (dateMatcher.find()) {
                val matchedDate = normalizeDateString(dateMatcher.group())
                if (matchedDate != null) {
                    activeDate = matchedDate
                }
            }

            val record = recordsMap.getOrPut(activeDate) {
                ParsedDayRecord(date = activeDate)
            }

            val lower = line.lowercase()

            // 1. Morning Weight
            if (lower.contains("wt") || lower.contains("weight") || lower.contains("kg")) {
                val numbers = extractNumbers(line)
                val weightVal = numbers.firstOrNull { it in 30.0..250.0 }
                if (weightVal != null) {
                    record.morningWeightKg = weightVal
                }
            }

            // 2. Intake / Urine
            if (lower.contains("intake") || lower.contains("in:") || lower.contains("water") || lower.contains("drink")) {
                val numbers = extractNumbers(line).map { it.toInt() }
                numbers.filter { it in 10..3000 }.forEach { amt ->
                    val isOutlier = !PlausibleRanges.isPlausibleFluid(amt)
                    record.fluidItems.add(
                        ParsedFluidItem(amountMl = amt, type = FluidType.INTAKE, isOutlier = isOutlier)
                    )
                }
            } else if (lower.contains("urine") || lower.contains("out:") || lower.contains("void")) {
                val numbers = extractNumbers(line).map { it.toInt() }
                numbers.filter { it in 10..3000 }.forEach { amt ->
                    val isOutlier = !PlausibleRanges.isPlausibleFluid(amt)
                    record.fluidItems.add(
                        ParsedFluidItem(amountMl = amt, type = FluidType.URINE, isOutlier = isOutlier)
                    )
                }
            }

            // 3. Sugar / Insulin Slots
            val slot = when {
                lower.contains("breakfast") || lower.contains("bf") || lower.contains("fasting") -> SugarSlot.BEFORE_BREAKFAST
                lower.contains("after lunch") || lower.contains("post lunch") -> SugarSlot.AFTER_LUNCH
                lower.contains("lunch") -> SugarSlot.BEFORE_LUNCH
                lower.contains("dinner") || lower.contains("supper") -> SugarSlot.BEFORE_DINNER
                else -> null
            }

            if (slot != null || lower.contains("sugar") || lower.contains("glu") || lower.contains("insulin")) {
                val targetSlot = slot ?: SugarSlot.BEFORE_BREAKFAST
                val nums = extractNumbers(line)
                val sugarVal = nums.firstOrNull { it in 30.0..500.0 }?.toInt()
                val insulinVal = nums.firstOrNull { it in 0.5..60.0 && it != sugarVal?.toDouble() } ?: 0.0

                if (sugarVal != null) {
                    val isOutlier = !PlausibleRanges.isPlausibleSugar(sugarVal)
                    record.sugarItems[targetSlot] = ParsedSugarItem(
                        slot = targetSlot,
                        sugarMgDl = sugarVal,
                        insulinUnits = insulinVal,
                        isOutlier = isOutlier
                    )
                }
            }
        }

        // Validate outliers
        recordsMap.values.forEach { rec ->
            rec.hasOutliers = rec.fluidItems.any { it.isOutlier } || rec.sugarItems.values.any { it.isOutlier }
        }

        return recordsMap.values.toList()
    }

    private fun extractNumbers(text: String): List<Double> {
        val pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)")
        val matcher = pattern.matcher(text)
        val list = mutableListOf<Double>()
        while (matcher.find()) {
            matcher.group().toDoubleOrNull()?.let { list.add(it) }
        }
        return list
    }

    private fun normalizeDateString(rawDate: String): String? {
        val clean = rawDate.replace('/', '-').replace('.', '-')
        val parts = clean.split('-')
        return when {
            parts.size == 3 && parts[0].length == 4 -> {
                val y = parts[0]
                val m = parts[1].padStart(2, '0')
                val d = parts[2].padStart(2, '0')
                "$y-$m-$d"
            }
            parts.size == 3 && parts[2].length == 4 -> {
                val y = parts[2]
                val m = parts[0].padStart(2, '0')
                val d = parts[1].padStart(2, '0')
                "$y-$m-$d"
            }
            else -> null
        }
    }
}
