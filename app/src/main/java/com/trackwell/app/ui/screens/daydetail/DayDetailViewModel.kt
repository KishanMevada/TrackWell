package com.trackwell.app.ui.screens.daydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.DayOverview
import com.trackwell.app.domain.model.FluidEntry
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.MedicationItem
import com.trackwell.app.domain.model.OptionalMetrics
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.repository.SugarRepository
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DayDetailUiState(
    val date: String = DateTimeUtils.getTodayIso(),
    val dayOverview: DayOverview = DayOverview(date = DateTimeUtils.getTodayIso()),
    val appSettings: AppSettings = AppSettings()
)

@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dailyHealthRepository: DailyHealthRepository,
    private val fluidRepository: FluidRepository,
    private val sugarRepository: SugarRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val dateParam: String = savedStateHandle["date"] ?: DateTimeUtils.getTodayIso()
    private val _currentDate = MutableStateFlow(dateParam)

    val uiState: StateFlow<DayDetailUiState> = combine(
        _currentDate.flatMapLatest { date ->
            dailyHealthRepository.observeDayOverview(date)
        },
        userPreferences.settingsFlow,
        _currentDate
    ) { overview, settings, date ->
        DayDetailUiState(
            date = date,
            dayOverview = overview,
            appSettings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DayDetailUiState(date = dateParam)
    )

    fun deleteFluidEntry(id: Long) {
        viewModelScope.launch {
            fluidRepository.deleteEntry(id)
        }
    }

    fun updateFluidEntry(entry: FluidEntry) {
        viewModelScope.launch {
            fluidRepository.updateEntry(entry)
        }
    }

    fun deleteSugarEntry(id: Long) {
        viewModelScope.launch {
            sugarRepository.deleteEntry(id)
        }
    }

    fun logSugar(slot: SugarSlot, sugarMgDl: Int, insulinUnits: Double, timestamp: Long, note: String?) {
        viewModelScope.launch {
            sugarRepository.logSugar(_currentDate.value, slot, sugarMgDl, insulinUnits, timestamp, note)
        }
    }

    fun saveVitals(systolic: Int?, diastolic: Int?, pulse: Int?, temp: Double?, note: String?) {
        viewModelScope.launch {
            dailyHealthRepository.saveOptionalMetrics(
                OptionalMetrics(
                    date = _currentDate.value,
                    timestamp = System.currentTimeMillis(),
                    systolicBp = systolic,
                    diastolicBp = diastolic,
                    pulseBpm = pulse,
                    temperatureCelsius = temp,
                    note = note
                )
            )
        }
    }

    fun toggleMedication(id: Long, isTaken: Boolean) {
        viewModelScope.launch {
            dailyHealthRepository.toggleMedication(id, isTaken)
        }
    }

    fun addMedication(name: String, dosage: String, time: String) {
        viewModelScope.launch {
            dailyHealthRepository.addMedication(
                MedicationItem(
                    date = _currentDate.value,
                    name = name,
                    dosage = dosage,
                    scheduledTime = time,
                    isTaken = false
                )
            )
        }
    }
}
