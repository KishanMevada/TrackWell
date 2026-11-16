package com.trackwell.app.ui.screens.exportdata.generator

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.PeriodAverages
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.model.ThresholdSettings
import com.trackwell.app.domain.util.DateTimeUtils
import java.io.File
import java.io.FileOutputStream

object PdfReportGenerator {

    fun generateDoctorReport(
        context: Context,
        patientName: String,
        startDate: String,
        endDate: String,
        days: List<DayOverview>,
        averages: PeriodAverages,
        thresholds: ThresholdSettings
    ): File {
        val document = PdfDocument()

        val pageWidth = 595 // A4 standard width in points at 72 dpi
        val pageHeight = 842 // A4 standard height

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // 1. Header Banner
        paint.color = Color.parseColor("#006D77") // Teal Primary
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 70f, paint)

        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("TrackWell · Patient Routine Health Report", 30f, 38f, paint)

        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Kidney Transplant Clinical Follow-Up Summary", 30f, 54f, paint)

        // 2. Patient & Date Range Info
        var yPos = 100f
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.isFakeBoldText = true
        val displayName = if (patientName.isNotBlank()) patientName else "Anonymous / Not Specified"
        canvas.drawText("Patient: $displayName", 30f, yPos, paint)

        paint.isFakeBoldText = false
        canvas.drawText("Report Period: $startDate to $endDate (${days.size} days)", 300f, yPos, paint)

        yPos += 24f

        // 3. Clinical Period Summary Table
        paint.color = Color.parseColor("#F4F0EA")
        canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 60f, 8f, 8f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 10f
        val colW = (pageWidth - 60f) / 4f

        canvas.drawText("Avg Daily Intake", 45f, yPos + 22f, paint)
        canvas.drawText("Avg Daily Urine", 45f + colW, yPos + 22f, paint)
        canvas.drawText("Avg Fluid Balance", 45f + colW * 2, yPos + 22f, paint)
        canvas.drawText("Avg Blood Sugar", 45f + colW * 3, yPos + 22f, paint)

        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.isFakeBoldText = true

        canvas.drawText("${averages.averageIntakeMl.toInt()} ml", 45f, yPos + 44f, paint)
        canvas.drawText("${averages.averageUrineMl.toInt()} ml", 45f + colW, yPos + 44f, paint)
        val balSign = if (averages.averageBalanceMl > 0) "+" else ""
        canvas.drawText("$balSign${averages.averageBalanceMl.toInt()} ml", 45f + colW * 2, yPos + 44f, paint)
        canvas.drawText("${averages.averageSugarMgDl.toInt()} mg/dL", 45f + colW * 3, yPos + 44f, paint)

        yPos += 80f

        // 4. Daily Routine Logs Table
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Daily Routine Logs", 30f, yPos, paint)

        yPos += 16f

        // Table Header
        paint.color = Color.parseColor("#E0E3E2")
        canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 22f, paint)

        paint.color = Color.BLACK
        paint.textSize = 9f
        paint.isFakeBoldText = true

        val tCols = floatArrayOf(35f, 105f, 165f, 225f, 285f, 345f, 405f, 465f, 525f)
        canvas.drawText("Date", tCols[0], yPos + 15f, paint)
        canvas.drawText("Weight", tCols[1], yPos + 15f, paint)
        canvas.drawText("Intake", tCols[2], yPos + 15f, paint)
        canvas.drawText("Urine", tCols[3], yPos + 15f, paint)
        canvas.drawText("Balance", tCols[4], yPos + 15f, paint)
        canvas.drawText("B. Fast", tCols[5], yPos + 15f, paint)
        canvas.drawText("Lunch", tCols[6], yPos + 15f, paint)
        canvas.drawText("Aft. L", tCols[7], yPos + 15f, paint)
        canvas.drawText("Dinner", tCols[8], yPos + 15f, paint)

        yPos += 22f
        paint.isFakeBoldText = false

        // Draw rows
        for ((index, day) in days.take(28).withIndex()) {
            if (index % 2 == 1) {
                paint.color = Color.parseColor("#F9FAF9")
                canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 20f, paint)
            }

            paint.color = Color.BLACK
            paint.textSize = 8.5f

            canvas.drawText(DateTimeUtils.formatShortDate(day.date), tCols[0], yPos + 14f, paint)
            canvas.drawText(day.morningWeightKg?.let { "$it kg" } ?: "-", tCols[1], yPos + 14f, paint)
            canvas.drawText("${day.fluidSummary.totalIntakeMl} ml", tCols[2], yPos + 14f, paint)
            canvas.drawText("${day.fluidSummary.totalUrineMl} ml", tCols[3], yPos + 14f, paint)
            val sign = if (day.fluidSummary.balanceMl > 0) "+" else ""
            canvas.drawText("$sign${day.fluidSummary.balanceMl} ml", tCols[4], yPos + 14f, paint)

            val bf = day.sugarEntries[SugarSlot.BEFORE_BREAKFAST]?.let { "${it.sugarMgDl} (${it.insulinUnits}U)" } ?: "-"
            val ln = day.sugarEntries[SugarSlot.BEFORE_LUNCH]?.let { "${it.sugarMgDl} (${it.insulinUnits}U)" } ?: "-"
            val al = day.sugarEntries[SugarSlot.AFTER_LUNCH]?.let { "${it.sugarMgDl} (${it.insulinUnits}U)" } ?: "-"
            val dn = day.sugarEntries[SugarSlot.BEFORE_DINNER]?.let { "${it.sugarMgDl} (${it.insulinUnits}U)" } ?: "-"

            canvas.drawText(bf, tCols[5], yPos + 14f, paint)
            canvas.drawText(ln, tCols[6], yPos + 14f, paint)
            canvas.drawText(al, tCols[7], yPos + 14f, paint)
            canvas.drawText(dn, tCols[8], yPos + 14f, paint)

            yPos += 20f
        }

        // Footer disclaimer
        paint.color = Color.GRAY
        paint.textSize = 7.5f
        canvas.drawText(
            "Medical Disclaimer: TrackWell is a routine logging assistant. Data is reported directly by the patient for review by their nephrologist.",
            30f,
            (pageHeight - 20).toFloat(),
            paint
        )

        document.finishPage(page)

        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val reportFile = File(reportsDir, "TrackWell_Report_${startDate}_to_${endDate}.pdf")
        val out = FileOutputStream(reportFile)
        document.writeTo(out)
        out.close()
        document.close()

        return reportFile
    }
}
