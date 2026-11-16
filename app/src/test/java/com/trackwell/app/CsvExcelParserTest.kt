package com.trackwell.app

import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.ui.screens.importdata.parser.CsvExcelParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CsvExcelParserTest {

    @Test
    fun `test csv parser auto mapping`() {
        val csv = """
            Date,Weight,Intake,Urine,Breakfast_Sugar,Lunch_Sugar,Dinner_Sugar
            2026-11-10,69.5,2100,1800,105,120,135
            2026-11-11,69.8,2000,1950,110,125,130
        """.trimIndent()

        val records = CsvExcelParser.parseCsv(csv)
        assertEquals(2, records.size)

        val first = records[0]
        assertEquals("2026-11-10", first.date)
        assertEquals(69.5, first.morningWeightKg!!, 0.01)

        val inItem = first.fluidItems.firstOrNull { it.type == FluidType.INTAKE }
        assertNotNull(inItem)
        assertEquals(2100, inItem!!.amountMl)

        val outItem = first.fluidItems.firstOrNull { it.type == FluidType.URINE }
        assertNotNull(outItem)
        assertEquals(1800, outItem!!.amountMl)

        val bfSugar = first.sugarItems[SugarSlot.BEFORE_BREAKFAST]
        assertNotNull(bfSugar)
        assertEquals(105, bfSugar!!.sugarMgDl)
    }
}
