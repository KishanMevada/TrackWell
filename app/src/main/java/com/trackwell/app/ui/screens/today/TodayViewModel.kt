package com.trackwell.app.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.repository.SugarRepository
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.home.components.BloodSugarPoint
import com.trackwell.app.ui.home.components.WeightPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class TodayUiState(
    val selectedDate: String = DateTimeUtils.getTodayIso(),
    val dayOverview: DayOverview = DayOverview(date = DateTimeUtils.getTodayIso()),
    val weeklyWeightPoints: List<WeightPoint> = emptyList(),
    val priorWeightPoints: List<WeightPoint> = emptyList(),
    val yesterdayWeightKg: Float? = null,
    val yesterdayWeightDiffKg: Float? = null,
    val priorSugarPoints: List<BloodSugarPoint> = emptyList(),
    val priorSugarAvg: Int? = null,
    val appSettings: AppSettings = AppSettings(),
    val isLoading: Boolean = false
)

@kotlinx.coroutines.ExperimentalCoroutinesApi
@HiltViewModel
class TodayViewModel @Inject constructor(
    private val dailyHealthRepository: DailyHealthRepository,
    private val fluidRepository: FluidRepository,
    private val sugarRepository: SugarRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _currentDate = MutableStateFlow(DateTimeUtils.getTodayIso())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()
    private var isTrackingRealtimeToday = true

    val uiState: StateFlow<TodayUiState> = combine(
        _currentDate.flatMapLatest { date ->
            dailyHealthRepository.observeDayOverview(date)
        },
        _currentDate.flatMapLatest { date ->
            val (startDate, endDate) = getSevenDayRange(date)
            dailyHealthRepository.getAllDaysInRange(startDate, endDate)
        },
        userPreferences.settingsFlow,
        _currentDate
    ) { overview, weeklyDays, settings, date ->
        val weightPoints = weeklyDays
            .filter { it.morningWeightKg != null }
            .map { day ->
                val label = if (day.date == date) {
                    "Today"
                } else {
                    try {
                        val parsed = DateTimeUtils.parseIsoDate(day.date)
                        if (parsed != null) {
                            SimpleDateFormat("EEE", Locale.US).format(parsed)
                        } else day.date
                    } catch (e: Exception) {
                        day.date
                    }
                }
                WeightPoint(
                    label = label,
                    weightKg = day.morningWeightKg!!.toFloat()
                )
            }

        val priorDays = weeklyDays.filter { it.date < date }

        val priorWeightPoints = priorDays
            .filter { it.morningWeightKg != null }
            .map { day ->
                val label = try {
                    val parsed = DateTimeUtils.parseIsoDate(day.date)
                    if (parsed != null) {
                        SimpleDateFormat("EEE", Locale.US).format(parsed)
                    } else day.date
                } catch (e: Exception) {
                    day.date
                }
                WeightPoint(
                    label = label,
                    weightKg = day.morningWeightKg!!.toFloat()
                )
            }

        val lastKnownPriorDay = priorDays.filter { it.morningWeightKg != null }.maxByOrNull { it.date }
        val yesterdayWeightKg = lastKnownPriorDay?.morningWeightKg?.toFloat()
        val yesterdayWeightDiffKg = lastKnownPriorDay?.weightDiffPreviousDay?.toFloat()

        val priorSugarPoints = priorDays.mapNotNull { day ->
            val entry = day.sugarEntries[SugarSlot.BEFORE_BREAKFAST]
                ?: day.sugarEntries.values.firstOrNull { it.sugarMgDl > 0 }
            entry?.let {
                val label = try {
                    val parsed = DateTimeUtils.parseIsoDate(day.date)
                    if (parsed != null) {
                        SimpleDateFormat("EEE", Locale.US).format(parsed)
                    } else day.date
                } catch (e: Exception) {
                    day.date
                }
                BloodSugarPoint(
                    timeLabel = label,
                    valueMgDl = it.sugarMgDl
                )
            }
        }
        val priorSugarAvg = if (priorSugarPoints.isNotEmpty()) {
            priorSugarPoints.mapNotNull { it.valueMgDl }.average().toInt()
        } else null

        TodayUiState(
            selectedDate = date,
            dayOverview = overview,
            weeklyWeightPoints = weightPoints,
            priorWeightPoints = priorWeightPoints,
            yesterdayWeightKg = yesterdayWeightKg,
            yesterdayWeightDiffKg = yesterdayWeightDiffKg,
            priorSugarPoints = priorSugarPoints,
            priorSugarAvg = priorSugarAvg,
            appSettings = settings,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayUiState()
    )

    private fun getSevenDayRange(endDateIso: String): Pair<String, String> {
        val parsed = DateTimeUtils.parseIsoDate(endDateIso) ?: return Pair(
            DateTimeUtils.getDaysAgoIso(6),
            endDateIso
        )
        val cal = Calendar.getInstance()
        cal.time = parsed
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startDateIso = DateTimeUtils.formatDateToIso(cal.time)
        return Pair(startDateIso, endDateIso)
    }

    fun checkDateFreshness(force: Boolean = false) {
        val realToday = DateTimeUtils.getTodayIso()
        if (isTrackingRealtimeToday || force) {
            if (_currentDate.value != realToday) {
                _currentDate.value = realToday
            }
        }
    }

    fun setDate(date: String) {
        _currentDate.value = date
        isTrackingRealtimeToday = DateTimeUtils.isToday(date)
    }

    fun goToPreviousDay() {
        _currentDate.value = DateTimeUtils.getPreviousDayIso(_currentDate.value)
        isTrackingRealtimeToday = DateTimeUtils.isToday(_currentDate.value)
    }

    fun goToNextDay() {
        _currentDate.value = DateTimeUtils.getNextDayIso(_currentDate.value)
        isTrackingRealtimeToday = DateTimeUtils.isToday(_currentDate.value)
    }

    fun jumpToToday() {
        _currentDate.value = DateTimeUtils.getTodayIso()
        isTrackingRealtimeToday = true
    }

    fun logFluid(amountMl: Int, type: FluidType, timestamp: Long, note: String? = null) {
        viewModelScope.launch {
            fluidRepository.logFluid(
                date = _currentDate.value,
                timestamp = timestamp,
                amountMl = amountMl,
                type = type,
                note = note
            )
        }
    }

    fun logSugar(
        slot: SugarSlot,
        sugarMgDl: Int,
        insulinUnits: Double,
        timestamp: Long,
        note: String? = null
    ) {
        viewModelScope.launch {
            sugarRepository.logSugar(
                date = _currentDate.value,
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

    fun updateWeight(weightKg: Double) {
        viewModelScope.launch {
            dailyHealthRepository.updateMorningWeight(_currentDate.value, weightKg)
        }
    }

    fun updateTarget(targetMl: Int) {
        viewModelScope.launch {
            userPreferences.updateFluidTarget(targetMl)
        }
    }

    fun updateIntakeAndUrineTargets(intakeTargetMl: Int, urineTargetMl: Int) {
        viewModelScope.launch {
            userPreferences.updateIntakeAndUrineTargets(intakeTargetMl, urineTargetMl)
        }
    }
}
