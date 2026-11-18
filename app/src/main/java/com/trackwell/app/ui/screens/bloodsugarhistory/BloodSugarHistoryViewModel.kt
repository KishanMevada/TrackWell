package com.trackwell.app.ui.screens.bloodsugarhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.SugarEntry
import com.trackwell.app.domain.model.SugarReadingStatus
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.repository.SugarRepository
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.home.components.BloodSugarPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

enum class SugarTimeRange(val days: Int, val label: String) {
    SEVEN_DAYS(7, "7D"),
    FOURTEEN_DAYS(14, "14D"),
    THIRTY_DAYS(30, "30D"),
    NINETY_DAYS(90, "90D"),
    ALL_TIME(125, "All Time"),
    CUSTOM(0, "Custom 📅")
}

enum class SugarSlotFilter(val label: String, val slot: SugarSlot?) {
    ALL("All Slots", null),
    FASTING("Fasting", SugarSlot.BEFORE_BREAKFAST),
    PRE_LUNCH("Pre-Lunch", SugarSlot.BEFORE_LUNCH),
    POST_LUNCH("Post-Lunch", SugarSlot.AFTER_LUNCH),
    PRE_DINNER("Pre-Dinner", SugarSlot.BEFORE_DINNER)
}

data class SugarChartPoint(
    val xLabel: String,
    val valueMgDl: Int,
    val slot: SugarSlot?,
    val date: String,
    val status: SugarReadingStatus
)

data class BloodSugarHistoryUiState(
    val selectedRange: SugarTimeRange = SugarTimeRange.SEVEN_DAYS,
    val selectedSlotFilter: SugarSlotFilter = SugarSlotFilter.ALL,
    val customStartDate: String = DateTimeUtils.getDaysAgoIso(7),
    val customEndDate: String = DateTimeUtils.getTodayIso(),
    val isLoggedToday: Boolean = false,
    val priorSugarPoints: List<BloodSugarPoint> = emptyList(),
    val priorSugarAvg: Int? = null,
    val suggestedSlot: SugarSlot = SugarSlot.suggestCurrentSlot(),
    val latestReadingMgDl: Int = 0,
    val latestStatus: String = "--",
    val latestSlotTitle: String = "Glucose",
    val averageSugarMgDl: Int = 0,
    val inRangePercent: Int = 0,
    val highestReadingMgDl: Int = 0,
    val lowestReadingMgDl: Int = 0,
    val totalInsulinUnits: Double = 0.0,
    val chartPoints: List<SugarChartPoint> = emptyList(),
    val historyDays: List<DayOverview> = emptyList(),
    val appSettings: AppSettings = AppSettings(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BloodSugarHistoryViewModel @Inject constructor(
    private val dailyHealthRepository: DailyHealthRepository,
    private val sugarRepository: SugarRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(SugarTimeRange.SEVEN_DAYS)
    val selectedRange: StateFlow<SugarTimeRange> = _selectedRange.asStateFlow()

    private val _selectedSlotFilter = MutableStateFlow(SugarSlotFilter.ALL)
    val selectedSlotFilter: StateFlow<SugarSlotFilter> = _selectedSlotFilter.asStateFlow()

    private val _customStartDate = MutableStateFlow(DateTimeUtils.getDaysAgoIso(7))
    private val _customEndDate = MutableStateFlow(DateTimeUtils.getTodayIso())

    val uiState: StateFlow<BloodSugarHistoryUiState> = combine(
        combine(_selectedRange, _customStartDate, _customEndDate) { range, start, end ->
            if (range == SugarTimeRange.CUSTOM) Pair(start, end)
            else DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
        }.flatMapLatest { (start, end) ->
            dailyHealthRepository.getAllDaysInRange(start, end)
        },
        userPreferences.settingsFlow,
        _selectedRange,
        _selectedSlotFilter
    ) { allDays, settings, range, slotFilter ->
        val todayIso = DateTimeUtils.getTodayIso()
        val todayEntry = allDays.firstOrNull { it.date == todayIso }
        val isLoggedToday = todayEntry?.sugarEntries?.values?.any { it.sugarMgDl > 0 } == true

        val priorDaysWithSugar = allDays.filter { it.date < todayIso && it.sugarEntries.isNotEmpty() }
        val dayFormatter = SimpleDateFormat("EEE", Locale.US)
        val shortDateFormatter = SimpleDateFormat("d MMM", Locale.US)

        val priorSugarPoints = priorDaysWithSugar.takeLast(6).mapNotNull { day ->
            val entry = day.sugarEntries[SugarSlot.BEFORE_BREAKFAST]
                ?: day.sugarEntries.values.firstOrNull { it.sugarMgDl > 0 }
            entry?.let {
                val label = try {
                    val parsed = DateTimeUtils.parseIsoDate(day.date)
                    if (parsed != null) dayFormatter.format(parsed) else day.date
                } catch (e: Exception) {
                    day.date
                }
                BloodSugarPoint(timeLabel = label, valueMgDl = it.sugarMgDl)
            }
        }
        val priorSugarAvg = if (priorSugarPoints.isNotEmpty()) {
            priorSugarPoints.mapNotNull { it.valueMgDl }.average().toInt()
        } else null
        val suggestedSlot = SugarSlot.suggestCurrentSlot()

        val daysWithSugar = allDays.filter { it.sugarEntries.isNotEmpty() }

        // Gather all matching readings across the period in chronological order
        val chronologicalReadings = mutableListOf<Pair<String, SugarEntry>>()
        for (day in daysWithSugar) {
            val orderedSlots = listOf(
                SugarSlot.BEFORE_BREAKFAST,
                SugarSlot.BEFORE_LUNCH,
                SugarSlot.AFTER_LUNCH,
                SugarSlot.BEFORE_DINNER
            )
            for (slot in orderedSlots) {
                day.sugarEntries[slot]?.let { entry ->
                    if (slotFilter.slot == null || slotFilter.slot == slot) {
                        chronologicalReadings.add(Pair(day.date, entry))
                    }
                }
            }
        }

        val allValues = chronologicalReadings.map { it.second.sugarMgDl }
        val latestReadingEntry = chronologicalReadings.lastOrNull()?.second
        val latestVal = latestReadingEntry?.sugarMgDl ?: 0
        val latestSlotTitle = latestReadingEntry?.slot?.title ?: "Glucose"

        val thresholds = settings.thresholdSettings
        val latestStatusEnum = if (allValues.isNotEmpty()) thresholds.evaluate(latestVal) else SugarReadingStatus.NOT_RECORDED
        val latestStatusStr = when (latestStatusEnum) {
            SugarReadingStatus.HIGH -> "High"
            SugarReadingStatus.LOW -> "Low"
            SugarReadingStatus.IN_RANGE -> "In Range"
            SugarReadingStatus.NOT_RECORDED -> "--"
        }

        val avgSugar = if (allValues.isNotEmpty()) allValues.average().toInt() else 0
        val inRangeCount = allValues.count { thresholds.evaluate(it) == SugarReadingStatus.IN_RANGE }
        val inRangePct = if (allValues.isNotEmpty()) ((inRangeCount.toFloat() / allValues.size) * 100).toInt() else 0
        val maxVal = allValues.maxOrNull() ?: 0
        val minVal = allValues.minOrNull() ?: 0
        val totalInsulin = chronologicalReadings.sumOf { it.second.insulinUnits }

        // Build chart points
        var previousDate = ""
        val chartPoints = if (slotFilter == SugarSlotFilter.ALL && range.days <= 14) {
            // For 7D/14D with All Slots, display Day name on first reading of each day, then slot letter
            chronologicalReadings.map { (date, entry) ->
                val isNewDay = date != previousDate
                previousDate = date
                val dayStr = try {
                    val parsed = DateTimeUtils.parseIsoDate(date)
                    if (parsed != null) dayFormatter.format(parsed) else date
                } catch (e: Exception) {
                    date
                }
                val slotShort = when (entry.slot) {
                    SugarSlot.BEFORE_BREAKFAST -> "B"
                    SugarSlot.BEFORE_LUNCH -> "L"
                    SugarSlot.AFTER_LUNCH -> "PL"
                    SugarSlot.BEFORE_DINNER -> "D"
                }
                val label = if (isNewDay) dayStr else slotShort
                SugarChartPoint(
                    xLabel = label,
                    valueMgDl = entry.sugarMgDl,
                    slot = entry.slot,
                    date = date,
                    status = thresholds.evaluate(entry.sugarMgDl)
                )
            }
        } else {
            // For specific slot filter or longer time horizons, one point per day
            daysWithSugar.mapNotNull { day ->
                val reading = if (slotFilter.slot != null) {
                    day.sugarEntries[slotFilter.slot]?.sugarMgDl
                } else {
                    // Daily average if viewing All Slots over 30D+
                    val vals = day.sugarEntries.values.map { it.sugarMgDl }
                    if (vals.isNotEmpty()) vals.average().toInt() else null
                }

                if (reading != null) {
                    val label = try {
                        val parsed = DateTimeUtils.parseIsoDate(day.date)
                        if (parsed != null) {
                            if (range == SugarTimeRange.SEVEN_DAYS) dayFormatter.format(parsed)
                            else shortDateFormatter.format(parsed)
                        } else day.date
                    } catch (e: Exception) {
                        day.date
                    }
                    SugarChartPoint(
                        xLabel = label,
                        valueMgDl = reading,
                        slot = slotFilter.slot,
                        date = day.date,
                        status = thresholds.evaluate(reading)
                    )
                } else null
            }
        }

        BloodSugarHistoryUiState(
            selectedRange = range,
            selectedSlotFilter = slotFilter,
            isLoggedToday = isLoggedToday,
            priorSugarPoints = priorSugarPoints,
            priorSugarAvg = priorSugarAvg,
            suggestedSlot = suggestedSlot,
            latestReadingMgDl = latestVal,
            latestStatus = latestStatusStr,
            latestSlotTitle = latestSlotTitle,
            averageSugarMgDl = avgSugar,
            inRangePercent = inRangePct,
            highestReadingMgDl = maxVal,
            lowestReadingMgDl = minVal,
            totalInsulinUnits = totalInsulin,
            chartPoints = chartPoints,
            historyDays = daysWithSugar.reversed(),
            appSettings = settings,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BloodSugarHistoryUiState()
    )

    fun selectRange(range: SugarTimeRange) {
        _selectedRange.value = range
    }

    fun selectSlotFilter(filter: SugarSlotFilter) {
        _selectedSlotFilter.value = filter
    }

    fun setCustomRange(startDate: String, endDate: String) {
        _customStartDate.value = startDate
        _customEndDate.value = endDate
        _selectedRange.value = SugarTimeRange.CUSTOM
    }

    fun logSugar(
        slot: SugarSlot,
        sugarMgDl: Int,
        insulinUnits: Double,
        timestamp: Long,
        note: String? = null
    ) {
        logSugarForDate(
            date = DateTimeUtils.getTodayIso(),
            slot = slot,
            sugarMgDl = sugarMgDl,
            insulinUnits = insulinUnits,
            timestamp = timestamp,
            note = note
        )
    }

    fun logSugarForDate(
        date: String,
        slot: SugarSlot,
        sugarMgDl: Int,
        insulinUnits: Double,
        timestamp: Long,
        note: String? = null
    ) {
        viewModelScope.launch {
            sugarRepository.logSugar(
                date = date,
                slot = slot,
                sugarMgDl = sugarMgDl,
                insulinUnits = insulinUnits,
                timestamp = timestamp,
                note = note
            )
        }
    }

    fun deleteSugarEntry(id: Long) {
        viewModelScope.launch {
            sugarRepository.deleteEntry(id)
        }
    }

    fun updateDay(day: DayOverview) {
        viewModelScope.launch {
            day.sugarEntries.forEach { (slot, entry) ->
                sugarRepository.logSugar(
                    date = day.date,
                    slot = slot,
                    sugarMgDl = entry.sugarMgDl,
                    insulinUnits = entry.insulinUnits,
                    timestamp = entry.timestamp,
                    note = entry.note
                )
            }
        }
    }
}
