package com.trackwell.app.ui.screens.fluidhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

enum class FluidTimeRange(val days: Int, val label: String) {
    SEVEN_DAYS(7, "7D"),
    FOURTEEN_DAYS(14, "14D"),
    THIRTY_DAYS(30, "30D"),
    NINETY_DAYS(90, "90D"),
    ALL_TIME(125, "All Time"),
    CUSTOM(0, "Custom 📅")
}

enum class FluidStreamFilter(val label: String) {
    NET_BALANCE("Net Balance"),
    INTAKE_ONLY("Intake"),
    URINE_ONLY("Urine Output")
}

data class FluidDayPoint(
    val date: String,
    val dayLabel: String,
    val intakeMl: Int,
    val urineMl: Int,
    val balanceMl: Int,
    val isOptimal: Boolean
)

data class FluidHistoryUiState(
    val selectedRange: FluidTimeRange = FluidTimeRange.SEVEN_DAYS,
    val selectedFilter: FluidStreamFilter = FluidStreamFilter.NET_BALANCE,
    val customStartDate: String = DateTimeUtils.getDaysAgoIso(7),
    val customEndDate: String = DateTimeUtils.getTodayIso(),
    val currentNetBalanceMl: Int = 500,
    val currentIntakeMl: Int = 3600,
    val currentUrineMl: Int = 3100,
    val statusLabel: String = "Optimal Balance",
    val averageIntakeMl: Int = 3480,
    val averageUrineMl: Int = 3060,
    val averageNetBalanceMl: Int = 420,
    val intakeTargetMl: Int = 3500,
    val urineTargetMl: Int = 2500,
    val chartPoints: List<FluidDayPoint> = emptyList(),
    val historyDays: List<DayOverview> = emptyList(),
    val appSettings: AppSettings = AppSettings(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FluidHistoryViewModel @Inject constructor(
    private val dailyHealthRepository: DailyHealthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(FluidTimeRange.SEVEN_DAYS)
    val selectedRange: StateFlow<FluidTimeRange> = _selectedRange.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FluidStreamFilter.NET_BALANCE)
    val selectedFilter: StateFlow<FluidStreamFilter> = _selectedFilter.asStateFlow()

    private val _customStartDate = MutableStateFlow(DateTimeUtils.getDaysAgoIso(7))
    private val _customEndDate = MutableStateFlow(DateTimeUtils.getTodayIso())

    val uiState: StateFlow<FluidHistoryUiState> = combine(
        combine(_selectedRange, _customStartDate, _customEndDate) { range, start, end ->
            if (range == FluidTimeRange.CUSTOM) Pair(start, end)
            else DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
        }.flatMapLatest { (start, end) ->
            dailyHealthRepository.getAllDaysInRange(start, end)
        },
        userPreferences.settingsFlow,
        _selectedRange,
        _selectedFilter
    ) { allDays, settings, range, filter ->
        val daysWithFluid = allDays.filter { it.fluidSummary.totalIntakeMl > 0 || it.fluidSummary.totalUrineMl > 0 }

        // Latest day metrics (defaults to today if present)
        val todayIso = DateTimeUtils.getTodayIso()
        val latestDay = allDays.find { it.date == todayIso } ?: daysWithFluid.lastOrNull()

        val currentIntake = latestDay?.fluidSummary?.totalIntakeMl ?: 3600
        val currentUrine = latestDay?.fluidSummary?.totalUrineMl ?: 3100
        val currentBalance = latestDay?.fluidSummary?.balanceMl ?: (currentIntake - currentUrine)

        val isOptimal = currentBalance in -100..700
        val statusLabel = when {
            currentBalance > 700 -> "Fluid Retention"
            currentBalance < -100 -> "Negative Deficit"
            else -> "Optimal Balance"
        }

        val daysWithIntake = daysWithFluid.filter { it.fluidSummary.totalIntakeMl > 0 }
        val daysWithUrine = daysWithFluid.filter { it.fluidSummary.totalUrineMl > 0 }

        val avgIntake = if (daysWithIntake.isNotEmpty()) daysWithIntake.map { it.fluidSummary.totalIntakeMl }.average().toInt() else 3480
        val avgUrine = if (daysWithUrine.isNotEmpty()) daysWithUrine.map { it.fluidSummary.totalUrineMl }.average().toInt() else 3060
        val avgBalance = if (daysWithFluid.isNotEmpty()) daysWithFluid.map { it.fluidSummary.balanceMl }.average().toInt() else 420

        val dayFormatter = SimpleDateFormat("EEE", Locale.US)
        val shortDateFormatter = SimpleDateFormat("d MMM", Locale.US)

        val chartPoints = daysWithFluid.map { day ->
            val label = try {
                val parsed = DateTimeUtils.parseIsoDate(day.date)
                if (parsed != null) {
                    if (range == FluidTimeRange.SEVEN_DAYS) dayFormatter.format(parsed)
                    else shortDateFormatter.format(parsed)
                } else day.date
            } catch (e: Exception) {
                day.date
            }
            FluidDayPoint(
                date = day.date,
                dayLabel = label,
                intakeMl = day.fluidSummary.totalIntakeMl,
                urineMl = day.fluidSummary.totalUrineMl,
                balanceMl = day.fluidSummary.balanceMl,
                isOptimal = day.fluidSummary.balanceMl in -100..700
            )
        }

        FluidHistoryUiState(
            selectedRange = range,
            selectedFilter = filter,
            currentNetBalanceMl = currentBalance,
            currentIntakeMl = currentIntake,
            currentUrineMl = currentUrine,
            statusLabel = statusLabel,
            averageIntakeMl = avgIntake,
            averageUrineMl = avgUrine,
            averageNetBalanceMl = avgBalance,
            intakeTargetMl = settings.intakeTargetMl,
            urineTargetMl = settings.urineTargetMl,
            chartPoints = chartPoints,
            historyDays = daysWithFluid.reversed(),
            appSettings = settings,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FluidHistoryUiState()
    )

    fun selectRange(range: FluidTimeRange) {
        _selectedRange.value = range
    }

    fun selectFilter(filter: FluidStreamFilter) {
        _selectedFilter.value = filter
    }

    fun setCustomRange(startDate: String, endDate: String) {
        _customStartDate.value = startDate
        _customEndDate.value = endDate
        _selectedRange.value = FluidTimeRange.CUSTOM
    }
}
