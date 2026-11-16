package com.trackwell.app

import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.ui.screens.importdata.parser.DetectedLayout
import com.trackwell.app.ui.screens.importdata.parser.OfflineLayoutParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineLayoutParserTest {

    @Test
    fun `test layout detection for fluid grid`() {
        val sampleFluidText = """
            Kidney Transplant Daily Log
            Date: 2026-11-10
            Morning Weight: 68.5 kg
            Intake: 200 ml, 250 ml, 300 ml
            Urine Output: 350 ml, 400 ml
            Total Balance: +0 ml
        """.trimIndent()

        val layout = OfflineLayoutParser.detectLayout(sampleFluidText)
        assertEquals(DetectedLayout.FLUID_GRID, layout)
    }

    @Test
    fun `test layout detection for sugar and insulin table`() {
        val sampleSugarText = """
            Blood Glucose and Insulin Chart
            Date: 2026-11-10
            Before Breakfast: 110 mg/dL, 4 Units
            Before Lunch: 125 mg/dL, 6 Units
            After Lunch: 140 mg/dL
            Before Dinner: 130 mg/dL, 4 Units
        """.trimIndent()

        val layout = OfflineLayoutParser.detectLayout(sampleSugarText)
        assertEquals(DetectedLayout.SUGAR_TABLE, layout)
    }

    @Test
    fun `test parsing fluid and weight from text`() {
        val text = """
            Date: 2026-11-14
            Weight: 72.4 kg
            Intake: 200 ml
            Intake: 150 ml
            Urine: 300 ml
        """.trimIndent()

        val records = OfflineLayoutParser.parseOcrText(text, defaultDate = "2026-11-14")
        assertEquals(1, records.size)

        val record = records[0]
        assertEquals("2026-11-14", record.date)
        assertEquals(72.4, record.morningWeightKg!!, 0.01)

        val intakeList = record.fluidItems.filter { it.type == FluidType.INTAKE }
        val urineList = record.fluidItems.filter { it.type == FluidType.URINE }

        assertEquals(2, intakeList.size)
        assertEquals(1, urineList.size)
        assertEquals(200, intakeList[0].amountMl)
        assertEquals(300, urineList[0].amountMl)
    }

    @Test
    fun `test parsing sugar readings`() {
        val text = """
            2026-11-14
            Fasting Breakfast: 115 mg/dL
            Before Lunch: 130 mg/dL
        """.trimIndent()

        val records = OfflineLayoutParser.parseOcrText(text, defaultDate = "2026-11-14")
        val record = records[0]

        val bfSugar = record.sugarItems[SugarSlot.BEFORE_BREAKFAST]
        assertNotNull(bfSugar)
        assertEquals(115, bfSugar!!.sugarMgDl)

        val lunchSugar = record.sugarItems[SugarSlot.BEFORE_LUNCH]
        assertNotNull(lunchSugar)
        assertEquals(130, lunchSugar!!.sugarMgDl)
    }
}
