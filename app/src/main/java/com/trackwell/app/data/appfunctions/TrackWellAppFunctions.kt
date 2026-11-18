package com.trackwell.app.data.appfunctions

import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.repository.SugarRepository
import com.trackwell.app.domain.util.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class AppFunctionResult(
    val isSuccess: Boolean,
    val feedbackMessage: String
)

@Singleton
class TrackWellAppFunctions @Inject constructor(
    private val fluidRepository: FluidRepository,
    private val sugarRepository: SugarRepository
) {

    /**
     * AppFunction Voice Action: Hands-free Fluid Intake / Urine Output logging.
     * Invoked via Google Assistant voice commands or System AppFunctions shortcuts.
     */
    suspend fun executeLogFluidAction(amountMl: Int, type: String = "INTAKE"): AppFunctionResult = withContext(Dispatchers.IO) {
        try {
            val today = DateTimeUtils.getTodayIso()
            val now = System.currentTimeMillis()
            val fluidType = if (type.uppercase() == "URINE") FluidType.URINE else FluidType.INTAKE

            fluidRepository.logFluid(
                date = today,
                timestamp = now,
                amountMl = amountMl,
                type = fluidType,
                note = "Voice Assistant Log"
            )

            val label = if (fluidType == FluidType.URINE) "urine output" else "fluid intake"
            AppFunctionResult(
                isSuccess = true,
                feedbackMessage = "Logged $amountMl mL of $label successfully."
            )
        } catch (e: Exception) {
            AppFunctionResult(
                isSuccess = false,
                feedbackMessage = "Could not log fluid: ${e.localizedMessage}"
            )
        }
    }

    /**
     * AppFunction Voice Action: Hands-free Blood Sugar logging.
     * Invoked via Google Assistant voice commands or System AppFunctions shortcuts.
     */
    suspend fun executeLogBloodSugarAction(glucoseValueMgDl: Int, slotName: String = "FASTING"): AppFunctionResult = withContext(Dispatchers.IO) {
        try {
            val today = DateTimeUtils.getTodayIso()
            val now = System.currentTimeMillis()
            val slot = SugarSlot.fromString(slotName)

            sugarRepository.logSugar(
                date = today,
                slot = slot,
                sugarMgDl = glucoseValueMgDl,
                insulinUnits = 0.0,
                timestamp = now,
                note = "Voice Assistant Log"
            )

            AppFunctionResult(
                isSuccess = true,
                feedbackMessage = "Logged blood sugar $glucoseValueMgDl mg/dL for ${slot.title}."
            )
        } catch (e: Exception) {
            AppFunctionResult(
                isSuccess = false,
                feedbackMessage = "Could not log blood sugar: ${e.localizedMessage}"
            )
        }
    }
}
