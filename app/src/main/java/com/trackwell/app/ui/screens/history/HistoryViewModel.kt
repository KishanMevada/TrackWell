package com.trackwell.app.ui.screens.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.PeriodAverages
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.screens.exportdata.generator.ExcelWorkbookGenerator
import com.trackwell.app.ui.screens.exportdata.generator.PdfReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

enum class HistoryViewRange(val days: Int, val label: String) {
    WEEK(7, "7D"),
    TWO_WEEKS(14, "14D"),
    MONTH(30, "30D"),
    NINETY_DAYS(90, "90D"),
    CUSTOM(0, "Custom 📅")
}

enum class HistoryFilter(val label: String) {
    ALL("All"),
    DEFICIT("⚠️ Deficit"),
    RETENTION("💧 Retention"),
    HIGH_SUGAR("🩸 High Sugar"),
    LOW_SUGAR("📉 Low Sugar"),
    WEIGHT_DELTA("⚖️ Weight ±0.5kg")
}

data class HistoryUiState(
    val range: HistoryViewRange = HistoryViewRange.WEEK,
    val customStartDate: String = DateTimeUtils.getDaysAgoIso(7),
    val customEndDate: String = DateTimeUtils.getTodayIso(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val allDays: List<DayOverview> = emptyList(),
    val filteredDays: List<DayOverview> = emptyList(),
    val periodAverages: PeriodAverages = PeriodAverages(),
    val appSettings: AppSettings = AppSettings(),
    val exportFile: File? = null,
    val exportMimeType: String? = null,
    val isExporting: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyHealthRepository: DailyHealthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(HistoryViewRange.WEEK)
    val selectedRange: StateFlow<HistoryViewRange> = _selectedRange.asStateFlow()

    private val _customStartDate = MutableStateFlow(DateTimeUtils.getDaysAgoIso(7))
    private val _customEndDate = MutableStateFlow(DateTimeUtils.getTodayIso())

    private val _selectedFilter = MutableStateFlow(HistoryFilter.ALL)
    val selectedFilter: StateFlow<HistoryFilter> = _selectedFilter.asStateFlow()

    private val _exportFile = MutableStateFlow<File?>(null)
    private val _exportMimeType = MutableStateFlow<String?>(null)
    private val _isExporting = MutableStateFlow(false)

    val uiState: StateFlow<HistoryUiState> = combine(
        combine(_selectedRange, _customStartDate, _customEndDate) { range, start, end ->
            if (range == HistoryViewRange.CUSTOM) Pair(start, end)
            else DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
        }.flatMapLatest { (start, end) ->
            dailyHealthRepository.getAllDaysInRange(start, end)
        },
        combine(_selectedRange, _customStartDate, _customEndDate) { range, start, end ->
            if (range == HistoryViewRange.CUSTOM) Pair(start, end)
            else DateTimeUtils.getStartAndEndDateForDaysBack(range.days)
        }.flatMapLatest { (start, end) ->
            dailyHealthRepository.getPeriodAverages(start, end)
        },
        userPreferences.settingsFlow,
        _selectedRange,
        _selectedFilter
    ) { allDays, averages, settings, range, filter ->
        val reversed = allDays.reversed()
        val filtered = when (filter) {
            HistoryFilter.ALL -> reversed
            HistoryFilter.DEFICIT -> reversed.filter { it.fluidSummary.balanceMl < -100 }
            HistoryFilter.RETENTION -> reversed.filter { it.fluidSummary.balanceMl > 600 }
            HistoryFilter.HIGH_SUGAR -> reversed.filter { day ->
                day.sugarEntries.values.any { it.sugarMgDl > settings.thresholdSettings.targetMax }
            }
            HistoryFilter.LOW_SUGAR -> reversed.filter { day ->
                day.sugarEntries.values.any { it.sugarMgDl < settings.thresholdSettings.lowThreshold }
            }
            HistoryFilter.WEIGHT_DELTA -> reversed.filter { day ->
                day.weightDiffPreviousDay != null && abs(day.weightDiffPreviousDay) >= 0.5
            }
        }

        HistoryUiState(
            range = range,
            customStartDate = _customStartDate.value,
            customEndDate = _customEndDate.value,
            filter = filter,
            allDays = reversed,
            filteredDays = filtered,
            periodAverages = averages,
            appSettings = settings,
            exportFile = _exportFile.value,
            exportMimeType = _exportMimeType.value,
            isExporting = _isExporting.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun setRange(range: HistoryViewRange) {
        _selectedRange.value = range
    }

    fun setCustomRange(startDate: String, endDate: String) {
        _customStartDate.value = startDate
        _customEndDate.value = endDate
        _selectedRange.value = HistoryViewRange.CUSTOM
    }

    fun setFilter(filter: HistoryFilter) {
        _selectedFilter.value = filter
    }

    fun exportFilteredToPdf() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val state = uiState.value
                val file = PdfReportGenerator.generateDoctorReport(
                    context = context,
                    patientName = state.appSettings.patientName,
                    startDate = state.filteredDays.lastOrNull()?.date ?: state.customStartDate,
                    endDate = state.filteredDays.firstOrNull()?.date ?: state.customEndDate,
                    days = state.filteredDays.reversed(),
                    averages = state.periodAverages,
                    thresholds = state.appSettings.thresholdSettings
                )
                _exportFile.value = file
                _exportMimeType.value = "application/pdf"
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportFilteredToExcel() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val state = uiState.value
                val file = ExcelWorkbookGenerator.generateXlsx(
                    context = context,
                    startDate = state.filteredDays.lastOrNull()?.date ?: state.customStartDate,
                    endDate = state.filteredDays.firstOrNull()?.date ?: state.customEndDate,
                    days = state.filteredDays.reversed(),
                    averages = state.periodAverages
                )
                _exportFile.value = file
                _exportMimeType.value = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun clearExport() {
        _exportFile.value = null
        _exportMimeType.value = null
    }
}
