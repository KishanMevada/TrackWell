package com.trackwell.app

import com.trackwell.app.domain.model.DailyFluidSummary
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.usecase.CalculatePeriodAveragesUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatePeriodAveragesTest {

    private lateinit var useCase: CalculatePeriodAveragesUseCase

    @Before
    fun setUp() {
        useCase = CalculatePeriodAveragesUseCase()
    }

    @Test
    fun `test period averages on multiple days`() {
        val day1 = DayOverview(
            date = "2026-11-12",
            morningWeightKg = 70.0,
            fluidSummary = DailyFluidSummary(totalIntakeMl = 2000, totalUrineMl = 1500, balanceMl = 500),
            sugarEntries = mapOf(
                SugarSlot.BEFORE_BREAKFAST to SugarEntry(date = "2026-11-12", slot = SugarSlot.BEFORE_BREAKFAST, sugarMgDl = 100, insulinUnits = 4.0, timestamp = 1L),
                SugarSlot.BEFORE_DINNER to SugarEntry(date = "2026-11-12", slot = SugarSlot.BEFORE_DINNER, sugarMgDl = 120, insulinUnits = 6.0, timestamp = 2L)
            )
        )

        val day2 = DayOverview(
            date = "2026-11-13",
            morningWeightKg = 70.4,
            fluidSummary = DailyFluidSummary(totalIntakeMl = 2200, totalUrineMl = 1700, balanceMl = 500),
            sugarEntries = mapOf(
                SugarSlot.BEFORE_BREAKFAST to SugarEntry(date = "2026-11-13", slot = SugarSlot.BEFORE_BREAKFAST, sugarMgDl = 110, insulinUnits = 4.0, timestamp = 3L),
                SugarSlot.BEFORE_DINNER to SugarEntry(date = "2026-11-13", slot = SugarSlot.BEFORE_DINNER, sugarMgDl = 150, insulinUnits = 6.0, timestamp = 4L)
            )
        )

        val averages = useCase.execute(listOf(day1, day2))

        // Weight: (70.0 + 70.4) / 2 = 70.2
        assertEquals(70.2, averages.averageWeightKg, 0.01)
        // Intake: (2000 + 2200) / 2 = 2100
        assertEquals(2100.0, averages.averageIntakeMl, 0.1)
        // Urine: (1500 + 1700) / 2 = 1600
        assertEquals(1600.0, averages.averageUrineMl, 0.1)
        // Balance: 500
        assertEquals(500.0, averages.averageBalanceMl, 0.1)
        // Sugar: (100 + 120 + 110 + 150) / 4 = 480 / 4 = 120.0
        assertEquals(120.0, averages.averageSugarMgDl, 0.1)
        // Total Insulin: 4 + 6 + 4 + 6 = 20.0
        assertEquals(20.0, averages.totalInsulinUnits, 0.1)
    }
}
