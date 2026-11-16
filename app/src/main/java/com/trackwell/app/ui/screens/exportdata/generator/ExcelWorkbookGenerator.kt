package com.trackwell.app.ui.screens.exportdata.generator

import android.content.Context
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.PeriodAverages
import com.trackwell.app.domain.model.SugarSlot
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExcelWorkbookGenerator {

    fun generateCsv(
        context: Context,
        startDate: String,
        endDate: String,
        days: List<DayOverview>
    ): File {
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDir, "TrackWell_Export_${startDate}_to_${endDate}.csv")
        val writer = OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)

        writer.write("Date,Weight(kg),Intake(ml),Urine(ml),Balance(ml),Sugar_Before_Breakfast,Insulin_Breakfast,Sugar_Before_Lunch,Insulin_Lunch,Sugar_After_Lunch,Insulin_After_Lunch,Sugar_Before_Dinner,Insulin_Dinner\n")

        for (day in days) {
            val w = day.morningWeightKg?.toString() ?: ""
            val inMl = day.fluidSummary.totalIntakeMl
            val urMl = day.fluidSummary.totalUrineMl
            val bal = day.fluidSummary.balanceMl

            val bfSugar = day.sugarEntries[SugarSlot.BEFORE_BREAKFAST]?.sugarMgDl?.toString() ?: ""
            val bfIns = day.sugarEntries[SugarSlot.BEFORE_BREAKFAST]?.insulinUnits?.toString() ?: ""

            val lnSugar = day.sugarEntries[SugarSlot.BEFORE_LUNCH]?.sugarMgDl?.toString() ?: ""
            val lnIns = day.sugarEntries[SugarSlot.BEFORE_LUNCH]?.insulinUnits?.toString() ?: ""

            val alSugar = day.sugarEntries[SugarSlot.AFTER_LUNCH]?.sugarMgDl?.toString() ?: ""
            val alIns = day.sugarEntries[SugarSlot.AFTER_LUNCH]?.insulinUnits?.toString() ?: ""

            val dnSugar = day.sugarEntries[SugarSlot.BEFORE_DINNER]?.sugarMgDl?.toString() ?: ""
            val dnIns = day.sugarEntries[SugarSlot.BEFORE_DINNER]?.insulinUnits?.toString() ?: ""

            writer.write("${day.date},$w,$inMl,$urMl,$bal,$bfSugar,$bfIns,$lnSugar,$lnIns,$alSugar,$alIns,$dnSugar,$dnIns\n")
        }

        writer.flush()
        writer.close()
        return file
    }

    fun generateXlsx(
        context: Context,
        startDate: String,
        endDate: String,
        days: List<DayOverview>,
        averages: PeriodAverages
    ): File {
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDir, "TrackWell_Health_Log_${startDate}_to_${endDate}.xlsx")
        val zos = ZipOutputStream(FileOutputStream(file))

        // 1. [Content_Types].xml
        addZipEntry(
            zos,
            "[Content_Types].xml",
            """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                <Default Extension="xml" ContentType="application/xml"/>
                <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                <Override PartName="/xl/worksheets/sheet3.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
            </Types>""".trimIndent()
        )

        // 2. _rels/.rels
        addZipEntry(
            zos,
            "_rels/.rels",
            """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
            </Relationships>""".trimIndent()
        )

        // 3. xl/workbook.xml
        addZipEntry(
            zos,
            "xl/workbook.xml",
            """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                <sheets>
                    <sheet name="Fluid Log" sheetId="1" r:id="rId1"/>
                    <sheet name="Sugar and Insulin" sheetId="2" r:id="rId2"/>
                    <sheet name="Summary" sheetId="3" r:id="rId3"/>
                </sheets>
            </workbook>""".trimIndent()
        )

        // 4. xl/_rels/workbook.xml.rels
        addZipEntry(
            zos,
            "xl/_rels/workbook.xml.rels",
            """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
                <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet3.xml"/>
            </Relationships>""".trimIndent()
        )

        // 5. Sheet 1: Fluid Log
        val sheet1Xml = buildString {
            append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
            append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
            append("""<row r="1">""")
            append("""<c r="A1" t="inlineStr"><is><t>Date</t></is></c>""")
            append("""<c r="B1" t="inlineStr"><is><t>Morning Weight (kg)</t></is></c>""")
            append("""<c r="C1" t="inlineStr"><is><t>Total Intake (ml)</t></is></c>""")
            append("""<c r="D1" t="inlineStr"><is><t>Total Urine (ml)</t></is></c>""")
            append("""<c r="E1" t="inlineStr"><is><t>Fluid Balance (ml)</t></is></c>""")
            append("""</row>""")

            days.forEachIndexed { idx, day ->
                val r = idx + 2
                append("""<row r="$r">""")
                append("""<c r="A$r" t="inlineStr"><is><t>${day.date}</t></is></c>""")
                append("""<c r="B$r"><v>${day.morningWeightKg ?: 0.0}</v></c>""")
                append("""<c r="C$r"><v>${day.fluidSummary.totalIntakeMl}</v></c>""")
                append("""<c r="D$r"><v>${day.fluidSummary.totalUrineMl}</v></c>""")
                append("""<c r="E$r"><v>${day.fluidSummary.balanceMl}</v></c>""")
                append("""</row>""")
            }
            append("""</sheetData></worksheet>""")
        }
        addZipEntry(zos, "xl/worksheets/sheet1.xml", sheet1Xml)

        // 6. Sheet 2: Sugar and Insulin
        val sheet2Xml = buildString {
            append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
            append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
            append("""<row r="1">""")
            append("""<c r="A1" t="inlineStr"><is><t>Date</t></is></c>""")
            append("""<c r="B1" t="inlineStr"><is><t>Slot</t></is></c>""")
            append("""<c r="C1" t="inlineStr"><is><t>Sugar (mg/dL)</t></is></c>""")
            append("""<c r="D1" t="inlineStr"><is><t>Insulin (Units)</t></is></c>""")
            append("""</row>""")

            var rowCounter = 2
            days.forEach { day ->
                day.sugarEntries.forEach { (slot, entry) ->
                    append("""<row r="$rowCounter">""")
                    append("""<c r="A$rowCounter" t="inlineStr"><is><t>${day.date}</t></is></c>""")
                    append("""<c r="B$rowCounter" t="inlineStr"><is><t>${slot.title}</t></is></c>""")
                    append("""<c r="C$rowCounter"><v>${entry.sugarMgDl}</v></c>""")
                    append("""<c r="D$rowCounter"><v>${entry.insulinUnits}</v></c>""")
                    append("""</row>""")
                    rowCounter++
                }
            }
            append("""</sheetData></worksheet>""")
        }
        addZipEntry(zos, "xl/worksheets/sheet2.xml", sheet2Xml)

        // 7. Sheet 3: Summary
        val sheet3Xml = buildString {
            append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
            append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
            append("""<row r="1"><c r="A1" t="inlineStr"><is><t>Metric</t></is></c><c r="B1" t="inlineStr"><is><t>Value</t></is></c></row>""")
            append("""<row r="2"><c r="A2" t="inlineStr"><is><t>Days Evaluated</t></is></c><c r="B2"><v>${days.size}</v></c></row>""")
            append("""<row r="3"><c r="A3" t="inlineStr"><is><t>Average Intake (ml)</t></is></c><c r="B3"><v>${averages.averageIntakeMl}</v></c></row>""")
            append("""<row r="4"><c r="A4" t="inlineStr"><is><t>Average Urine (ml)</t></is></c><c r="B4"><v>${averages.averageUrineMl}</v></c></row>""")
            append("""<row r="5"><c r="A5" t="inlineStr"><is><t>Average Balance (ml)</t></is></c><c r="B5"><v>${averages.averageBalanceMl}</v></c></row>""")
            append("""<row r="6"><c r="A6" t="inlineStr"><is><t>Average Weight (kg)</t></is></c><c r="B6"><v>${averages.averageWeightKg}</v></c></row>""")
            append("""<row r="7"><c r="A7" t="inlineStr"><is><t>Average Blood Sugar (mg/dL)</t></is></c><c r="B7"><v>${averages.averageSugarMgDl}</v></c></row>""")
            append("""</sheetData></worksheet>""")
        }
        addZipEntry(zos, "xl/worksheets/sheet3.xml", sheet3Xml)

        zos.close()
        return file
    }

    private fun addZipEntry(zos: ZipOutputStream, path: String, content: String) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        val bytes = content.toByteArray(StandardCharsets.UTF_8)
        zos.write(bytes, 0, bytes.size)
        zos.closeEntry()
    }
}
