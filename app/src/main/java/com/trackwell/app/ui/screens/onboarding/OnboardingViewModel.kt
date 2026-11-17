package com.trackwell.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackwell.app.data.local.database.DatabaseSeeder
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OnboardingStep {
    WELCOME,
    CHOOSE_MODULES,
    BASIC_SETUP,
    FIRST_ACTION
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val selectedModules: Set<String> = setOf("FLUID", "WEIGHT", "SUGAR", "MEDS", "VITALS"),
    val fluidTargetMl: Int = 3000,
    val isPreviewMode: Boolean = false,
    val isCompleted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val fluidRepository: FluidRepository,
    private val databaseSeeder: DatabaseSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectStep(step: OnboardingStep) {
        _uiState.value = _uiState.value.copy(step = step)
    }

    fun toggleModule(moduleKey: String) {
        val current = _uiState.value.selectedModules.toMutableSet()
        if (current.contains(moduleKey)) {
            if (current.size > 1) {
                current.remove(moduleKey)
            }
        } else {
            current.add(moduleKey)
        }
        _uiState.value = _uiState.value.copy(selectedModules = current)
    }

    fun setFluidTarget(targetMl: Int) {
        _uiState.value = _uiState.value.copy(fluidTargetMl = targetMl)
    }

    fun startPreviewMode(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPreviewMode = true)
            databaseSeeder.forceReseed()
            userPreferences.completeInitialSetup(
                selectedModulesCsv = "FLUID,WEIGHT,SUGAR,MEDS,VITALS",
                isPreview = true
            )
            _uiState.value = _uiState.value.copy(isCompleted = true)
            onComplete()
        }
    }

    fun logFirstDrink(amountMl: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            val today = DateTimeUtils.getTodayIso()
            val now = System.currentTimeMillis()
            fluidRepository.logFluid(
                date = today,
                timestamp = now,
                amountMl = amountMl,
                type = FluidType.INTAKE,
                note = "First hydration log"
            )
            userPreferences.markFirstActionLogged()
            completeSetup(onComplete)
        }
    }

    fun completeSetup(onComplete: () -> Unit) {
        viewModelScope.launch {
            val modulesCsv = _uiState.value.selectedModules.joinToString(",")
            userPreferences.updateFluidTarget(_uiState.value.fluidTargetMl)
            userPreferences.completeInitialSetup(
                selectedModulesCsv = modulesCsv,
                isPreview = false
            )
            _uiState.value = _uiState.value.copy(isCompleted = true)
            onComplete()
        }
    }
}
