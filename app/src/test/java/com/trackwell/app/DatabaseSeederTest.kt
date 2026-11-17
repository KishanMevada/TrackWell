package com.trackwell.app

import com.google.gson.JsonParser
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

        assertNotNull("Root JSON object should not be null", root)
        assertTrue("Must contain dailyLogs array", root.has("dailyLogs"))
        assertTrue("Must contain fluidEntries array", root.has("fluidEntries"))
        assertTrue("Must contain sugarEntries array", root.has("sugarEntries"))
        assertTrue("Must contain medications array", root.has("medications"))
    }
}
