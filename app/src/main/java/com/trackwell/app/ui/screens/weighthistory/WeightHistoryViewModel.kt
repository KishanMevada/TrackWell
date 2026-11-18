package com.trackwell.app.ui.screens.weighthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.home.components.WeightPoint
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

enum class WeightTimeRange(val days: Int, val label: String) {
    SEVEN_DAYS(7, "7D"),
    FOURTEEN_DAYS(14, "14D"),
    THIRTY_DAYS(30, "30D"),
    NINETY_DAYS(90, "90D"),
    ALL_TIME(125, "All Time"),
    CUSTOM(0, "Custom 📅")
}

data class WeightHistoryUiState(
    val selectedRange: WeightTimeRange = WeightTimeRange.SEVEN_DAYS,
    val customStartDate: String = DateTimeUtils.getDaysAgoIso(7),
    val customEndDate: String = DateTimeUtils.getTodayIso(),
    val isLoggedToday: Boolean = false,
    val currentWeightKg: Float? = null,
    val yesterdayWeightKg: Float? = null,
    val yesterdayWeightDiffKg: Float? = null,
    val priorWeightPoints: List<WeightPoint> = emptyList(),
    val latestDeltaKg: Float = 0f,
    val averageWeightKg: Float = 0f,
    val netPeriodChangeKg: Float = 0f,
    val baselineGainKg: Float = 0f,
    val targetWeightKg: Float = 0f,
    val chartPoints: List<WeightPoint> = emptyList(),
    val historyEntries: List<DayOverview> = emptyList(),
    val appSettings: AppSettings = AppSettings(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WeightHistoryViewModel @Inject constructor(
    private val dailyHealthRepository: DailyHealthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(WeightTimeRange.SEVEN_DAYS)
    val selectedRange: StateFlow<WeightTimeRange> = _selectedRange.asStateFlow()

    private val _customStartDate = MutableStateFlow(DateTimeUtils.getDaysAgoIso(7))
    private val _customEndDate = MutableStateFlow(DateTimeUtils.getTodayIso())

    val uiState: StateFlow<WeightHistoryUiState> = combine(
        combine(_selectedRange, _customStartDate, _customEndDate) { range, start, end ->
            if (range == WeightTimeRange.CUSTOM) Pair(start, end)
            else DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
        }.flatMapLatest { (start, end) ->
            dailyHealthRepository.getAllDaysInRange(start, end)
        },
        userPreferences.settingsFlow,
        _selectedRange
    ) { allDays, settings, range ->
        val todayIso = DateTimeUtils.getTodayIso()
        val todayEntry = allDays.firstOrNull { it.date == todayIso }
        val isLoggedToday = todayEntry?.morningWeightKg != null

        val daysWithWeight = allDays.filter { it.morningWeightKg != null }
        val weights = daysWithWeight.map { it.morningWeightKg!!.toFloat() }

        val priorDaysWithWeight = allDays.filter { it.date < todayIso && it.morningWeightKg != null }
        val lastKnownPriorDay = priorDaysWithWeight.maxByOrNull { it.date }
        val yesterdayWeightKg = lastKnownPriorDay?.morningWeightKg?.toFloat()
        val yesterdayWeightDiffKg = lastKnownPriorDay?.weightDiffPreviousDay?.toFloat()

        val currentWeight = if (isLoggedToday) todayEntry?.morningWeightKg?.toFloat() else null
        val latestDay = daysWithWeight.lastOrNull()
        val latestDelta = latestDay?.weightDiffPreviousDay?.toFloat() ?: (yesterdayWeightDiffKg ?: 0f)

        val avgWeight = if (weights.isNotEmpty()) weights.average().toFloat() else 0f
        val netChange = if (weights.size >= 2) weights.last() - weights.first() else 0f
        val baselineGain = if (weights.isNotEmpty()) (weights.last() - weights.first()) else 0f

        val dayFormatter = SimpleDateFormat("EEE", Locale.US)
        val shortDateFormatter = SimpleDateFormat("d MMM", Locale.US)

        val chartPoints = daysWithWeight.map { day ->
            val label = try {
                val parsed = DateTimeUtils.parseIsoDate(day.date)
                if (parsed != null) {
                    if (range == WeightTimeRange.SEVEN_DAYS) dayFormatter.format(parsed)
                    else shortDateFormatter.format(parsed)
                } else day.date
            } catch (e: Exception) {
                day.date
            }
            WeightPoint(label = label, weightKg = day.morningWeightKg!!.toFloat())
        }

        val priorWeightPoints = priorDaysWithWeight.takeLast(6).map { day ->
            val label = try {
                val parsed = DateTimeUtils.parseIsoDate(day.date)
                if (parsed != null) dayFormatter.format(parsed) else day.date
            } catch (e: Exception) {
                day.date
            }
            WeightPoint(label = label, weightKg = day.morningWeightKg!!.toFloat())
        }

        WeightHistoryUiState(
            selectedRange = range,
            isLoggedToday = isLoggedToday,
            currentWeightKg = currentWeight,
            yesterdayWeightKg = yesterdayWeightKg,
            yesterdayWeightDiffKg = yesterdayWeightDiffKg,
            priorWeightPoints = priorWeightPoints,
            latestDeltaKg = latestDelta,
            averageWeightKg = avgWeight,
            netPeriodChangeKg = netChange,
            baselineGainKg = baselineGain,
            targetWeightKg = 55.50f,
            chartPoints = chartPoints,
            historyEntries = daysWithWeight.reversed(),
            appSettings = settings,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeightHistoryUiState()
    )

    fun selectRange(range: WeightTimeRange) {
        _selectedRange.value = range
    }

    fun setCustomRange(startDate: String, endDate: String) {
        _customStartDate.value = startDate
        _customEndDate.value = endDate
        _selectedRange.value = WeightTimeRange.CUSTOM
    }

    fun updateWeight(weightKg: Double) {
        viewModelScope.launch {
            dailyHealthRepository.updateMorningWeight(DateTimeUtils.getTodayIso(), weightKg)
        }
    }

    /** Update weight for a specific day (used by the bottom‑sheet editing UI) */
    fun updateWeightForDay(dateIso: String, weightKg: Double) {
        viewModelScope.launch {
            dailyHealthRepository.updateMorningWeight(dateIso, weightKg)
        }
    }

    /** Delete morning weight for a specific day */
    fun deleteWeightForDay(dateIso: String) {
        viewModelScope.launch {
            dailyHealthRepository.deleteMorningWeight(dateIso)
        }
    }
}
