package com.trackwell.app

import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.usecase.CalculateDailySummaryUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculateDailySummaryTest {

    private lateinit var useCase: CalculateDailySummaryUseCase

    @Before
    fun setUp() {
        useCase = CalculateDailySummaryUseCase()
    }

    @Test
    fun `test empty fluid entries yields zeros`() {
        val summary = useCase.execute(emptyList(), targetMl = 2000)
        assertEquals(0, summary.totalIntakeMl)
        assertEquals(0, summary.totalUrineMl)
        assertEquals(0, summary.balanceMl)
        assertEquals(0.0f, summary.progress, 0.001f)
    }

    @Test
    fun `test fluid intake and urine calculation and balance`() {
        val entries = listOf(
            FluidEntry(id = 1, date = "2026-11-14", timestamp = 1000L, amountMl = 200, type = FluidType.INTAKE),
            FluidEntry(id = 2, date = "2026-11-14", timestamp = 2000L, amountMl = 250, type = FluidType.INTAKE),
            FluidEntry(id = 3, date = "2026-11-14", timestamp = 3000L, amountMl = 300, type = FluidType.URINE),
            FluidEntry(id = 4, date = "2026-11-14", timestamp = 4000L, amountMl = 500, type = FluidType.INTAKE),
            FluidEntry(id = 5, date = "2026-11-14", timestamp = 5000L, amountMl = 400, type = FluidType.URINE)
        )

        val summary = useCase.execute(entries, targetMl = 1900)
        // Intake: 200 + 250 + 500 = 950 ml
        // Urine: 300 + 400 = 700 ml
        // Balance: 950 - 700 = +250 ml
        assertEquals(950, summary.totalIntakeMl)
        assertEquals(700, summary.totalUrineMl)
        assertEquals(250, summary.balanceMl)
        assertEquals(0.5f, summary.progress, 0.001f)
    }

    @Test
    fun `test negative balance when urine exceeds intake`() {
        val entries = listOf(
            FluidEntry(id = 1, date = "2026-11-14", timestamp = 1000L, amountMl = 300, type = FluidType.INTAKE),
            FluidEntry(id = 2, date = "2026-11-14", timestamp = 2000L, amountMl = 600, type = FluidType.URINE)
        )

        val summary = useCase.execute(entries, targetMl = 2000)
        assertEquals(-300, summary.balanceMl)
    }

    @Test
    fun `test weight difference calculation`() {
        val diff1 = useCase.calculateWeightDifference(68.50, 68.25)
        assertEquals(0.25, diff1!!, 0.001)

        val diff2 = useCase.calculateWeightDifference(67.80, 68.40)
        assertEquals(-0.60, diff2!!, 0.001)

        val diffNull = useCase.calculateWeightDifference(null, 68.0)
        assertNull(diffNull)
    }
}
