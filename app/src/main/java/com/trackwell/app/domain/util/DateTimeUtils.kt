package com.trackwell.app.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private const val ISO_DATE_PATTERN = "yyyy-MM-dd"
    private const val DISPLAY_DATE_PATTERN = "EEEE, MMMM d, yyyy"
    private const val SHORT_DATE_PATTERN = "MMM d, yyyy"
    private const val TIME_PATTERN = "h:mm a"

    fun getTodayIso(): String {
        return formatDateToIso(Date())
    }

    fun formatDateToIso(date: Date): String {
        val sdf = SimpleDateFormat(ISO_DATE_PATTERN, Locale.US)
        return sdf.format(date)
    }

    fun parseIsoDate(isoDate: String): Date? {
        return try {
            SimpleDateFormat(ISO_DATE_PATTERN, Locale.US).parse(isoDate)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDisplayDate(isoDate: String): String {
        val parsed = parseIsoDate(isoDate) ?: return isoDate
        val today = getTodayIso()
        val yesterday = getDaysAgoIso(1)

        val prefix = when (isoDate) {
            today -> "Today, "
            yesterday -> "Yesterday, "
            else -> ""
        }
        val formatted = SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.getDefault()).format(parsed)
        return prefix + formatted
    }

    fun formatShortDate(isoDate: String): String {
        val parsed = parseIsoDate(isoDate) ?: return isoDate
        return SimpleDateFormat(SHORT_DATE_PATTERN, Locale.getDefault()).format(parsed)
    }

    fun formatTime(timestamp: Long): String {
        return SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(Date(timestamp))
    }

    fun getDaysAgoIso(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return formatDateToIso(cal.time)
    }

    fun getDaysAheadIso(daysAhead: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysAhead)
        return formatDateToIso(cal.time)
    }

    fun getStartAndEndDateForDaysBack(daysBack: Int): Pair<String, String> {
        val endDate = getTodayIso()
        val startDate = getDaysAgoIso(daysBack - 1)
        return Pair(startDate, endDate)
    }

    fun isDateInRange(date: String, startDate: String, endDate: String): Boolean {
        return date in startDate..endDate
    }

    fun getPreviousDayIso(currentIso: String): String {
        val parsed = parseIsoDate(currentIso) ?: return getDaysAgoIso(1)
        val cal = Calendar.getInstance()
        cal.time = parsed
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return formatDateToIso(cal.time)
    }

    fun getNextDayIso(currentIso: String): String {
        val parsed = parseIsoDate(currentIso) ?: return getDaysAheadIso(1)
        val cal = Calendar.getInstance()
        cal.time = parsed
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return formatDateToIso(cal.time)
    }

    fun isToday(isoDate: String): Boolean {
        return isoDate == getTodayIso()
    }

    fun isFuture(isoDate: String): Boolean {
        return isoDate > getTodayIso()
    }

    fun parseIsoToMillis(isoDate: String): Long {
        val sdf = SimpleDateFormat(ISO_DATE_PATTERN, Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
        return sdf.parse(isoDate)?.time ?: System.currentTimeMillis()
    }

    fun formatMillisToIso(millis: Long): String {
        val sdf = SimpleDateFormat(ISO_DATE_PATTERN, Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date(millis))
    }

    fun calculateWeeksPostTx(transplantDateIso: String, currentDateIso: String = getTodayIso()): Int {
        val txDate = parseIsoDate(transplantDateIso) ?: return 17
        val currDate = parseIsoDate(currentDateIso) ?: Date()
        val diffMillis = currDate.time - txDate.time
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return (days / 7).coerceAtLeast(1)
    }

    fun getDaysListBetween(startDate: String, endDate: String): List<String> {
        val dates = mutableListOf<String>()
        val start = parseIsoDate(startDate) ?: return emptyList()
        val end = parseIsoDate(endDate) ?: return emptyList()

        val cal = Calendar.getInstance()
        cal.time = start
        while (!cal.time.after(end)) {
            dates.add(formatDateToIso(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }
}
