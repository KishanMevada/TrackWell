package com.trackwell.app

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DatabaseSeederTest {

    @Test
    fun testSeedDataJsonIntegrity() {
        val seedFile = File("src/main/assets/seed_data.json")
        assertTrue("seed_data.json must exist in assets directory", seedFile.exists())

        val jsonString = seedFile.readText()
        val root = JsonParser.parseString(jsonString).asJsonObject

        assertEquals("Kishan", root.get("patientName").asString)
        assertEquals(3500, root.get("fluidTargetMl").asInt)

        // 1. Verify Daily Logs
        val dailyLogs = root.getAsJsonArray("dailyLogs")
        assertTrue("Must have at least 120 daily logs", dailyLogs.size() >= 120)

        // Find Day 1 (2026-07-19)
        var day1Found = false
        var nov14Found = false
        var nov14Weight: Double? = null

        for (i in 0 until dailyLogs.size()) {
            val obj = dailyLogs.get(i).asJsonObject
            val dt = obj.get("date").asString
            if (dt == "2026-07-19") {
                day1Found = true
                assertEquals(50.0, obj.get("morningWeight").asDouble, 0.01)
            }
            if (dt == "2026-11-14") {
                nov14Found = true
                nov14Weight = obj.get("morningWeight").asDouble
            }
        }

        assertTrue("2026-07-19 daily log must exist", day1Found)
        assertTrue("2026-11-14 daily log must exist", nov14Found)
        assertEquals("Nov 14 weight must be 55.85 kg", 55.85, nov14Weight ?: 0.0, 0.01)

        // 2. Verify Fluid Entries
        val fluidEntries = root.getAsJsonArray("fluidEntries")
        assertTrue("Must have at least 3,500 fluid entries", fluidEntries.size() >= 3500)

        var nov14IntakeSum = 0
        var nov14UrineSum = 0

        for (i in 0 until fluidEntries.size()) {
            val obj = fluidEntries.get(i).asJsonObject
            if (obj.get("date").asString == "2026-11-14") {
                val amt = obj.get("amountMl").asInt
                val type = obj.get("type").asString
                if (type == "INTAKE") {
                    nov14IntakeSum += amt
                } else if (type == "URINE") {
                    nov14UrineSum += amt
                }
            }
        }

        assertEquals("Nov 14 total intake must equal 3450 mL", 3450, nov14IntakeSum)
        assertEquals("Nov 14 total urine must equal 3790 mL", 3790, nov14UrineSum)
        val nov14Balance = nov14IntakeSum - nov14UrineSum
        assertEquals("Nov 14 balance must be -340 mL", -340, nov14Balance)

        // 3. Verify Sugar Entries
        val sugarEntries = root.getAsJsonArray("sugarEntries")
        assertTrue("Must have at least 400 sugar readings", sugarEntries.size() >= 400)

        var day1BbfSugar: Int? = null
        var nov14BbfSugar: Int? = null

        for (i in 0 until sugarEntries.size()) {
            val obj = sugarEntries.get(i).asJsonObject
            val dt = obj.get("date").asString
            val slot = obj.get("slot").asString
            if (dt == "2026-07-19" && slot == "BEFORE_BREAKFAST") {
                day1BbfSugar = obj.get("sugarMgDl").asInt
            }
            if (dt == "2026-11-14" && slot == "BEFORE_BREAKFAST") {
                nov14BbfSugar = obj.get("sugarMgDl").asInt
            }
        }

        assertEquals("Day 1 Before Breakfast sugar must be 70 mg/dL", 70, day1BbfSugar)
        assertEquals("Nov 14 Before Breakfast sugar must be 91 mg/dL", 91, nov14BbfSugar)

        // 4. Verify Medications
        val meds = root.getAsJsonArray("medications")
        assertTrue("Must have medications preloaded for today", meds.size() >= 4)
    }
}
