package com.trackwell.app.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.repository.SugarRepository
import com.trackwell.app.domain.util.DateTimeUtils
import com.trackwell.app.ui.theme.TrackWellTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetQuickEntryActivity : ComponentActivity() {

    @Inject
    lateinit var fluidRepository: FluidRepository

    @Inject
    lateinit var sugarRepository: SugarRepository

    @Inject
    lateinit var dailyHealthRepository: DailyHealthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_QUICK_FLUID

        setContent {
            TrackWellTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    when (mode) {
                        MODE_WEIGHT -> WeightQuickEntry(
                            onDismiss = { finish() },
                            onSave = { weight ->
                                lifecycleScope.launch {
                                    dailyHealthRepository.updateMorningWeight(DateTimeUtils.getTodayIso(), weight)
                                    FluidQuickWidget.updateAll(this@WidgetQuickEntryActivity)
                                    FluidMediumWidget.updateAll(this@WidgetQuickEntryActivity)
                                    WeightWidget.updateAll(this@WidgetQuickEntryActivity)
                                    finish()
                                }
                            }
                        )

                        MODE_SUGAR -> SugarQuickEntry(
                            onDismiss = { finish() },
                            onSave = { slot, reading, dose ->
                                lifecycleScope.launch {
                                    sugarRepository.logSugar(
                                        date = DateTimeUtils.getTodayIso(),
                                        slot = slot,
                                        sugarMgDl = reading,
                                        insulinUnits = dose,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    SugarWidget.updateAll(this@WidgetQuickEntryActivity)
                                    finish()
                                }
                            }
                        )

                        else -> FluidQuickEntry(
                            onDismiss = { finish() },
                            onSave = { amount, type ->
                                lifecycleScope.launch {
                                    fluidRepository.logFluid(
                                        date = DateTimeUtils.getTodayIso(),
                                        timestamp = System.currentTimeMillis(),
                                        amountMl = amount,
                                        type = type
                                    )
                                    FluidQuickWidget.updateAll(this@WidgetQuickEntryActivity)
                                    FluidMediumWidget.updateAll(this@WidgetQuickEntryActivity)
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_QUICK_FLUID = "mode_quick_fluid"
        const val MODE_WEIGHT = "mode_weight"
        const val MODE_SUGAR = "mode_sugar"
    }
}

@Composable
fun WeightQuickEntry(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var weightText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Log Morning Weight",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Weight in kg (e.g. 68.5)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Button(
                    onClick = {
                        val parsed = weightText.toDoubleOrNull()
                        if (parsed != null && parsed in 30.0..250.0) {
                            onSave(parsed)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun SugarQuickEntry(
    onDismiss: () -> Unit,
    onSave: (SugarSlot, Int, Double) -> Unit
) {
    val suggestedSlot = SugarSlot.suggestCurrentSlot()
    var readingText by remember { mutableStateOf("") }
    var doseText by remember { mutableStateOf("0") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Log ${suggestedSlot.title}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = readingText,
                onValueChange = { if (it.all { c -> c.isDigit() }) readingText = it },
                label = { Text("Blood Sugar (mg/dL)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = doseText,
                onValueChange = { doseText = it },
                label = { Text("Insulin Dose (Units)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Button(
                    onClick = {
                        val r = readingText.toIntOrNull()
                        val d = doseText.toDoubleOrNull() ?: 0.0
                        if (r != null && r > 0) {
                            onSave(suggestedSlot, r, d)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun FluidQuickEntry(
    onDismiss: () -> Unit,
    onSave: (Int, FluidType) -> Unit
) {
    var amountText by remember { mutableStateOf("200") }
    var isIntake by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Quick Fluid Entry",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isIntake = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isIntake) "✓ Intake" else "Intake")
                }
                Button(
                    onClick = { isIntake = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (!isIntake) "✓ Urine" else "Urine")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.all { c -> c.isDigit() }) amountText = it },
                label = { Text("Amount (ml)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Button(
                    onClick = {
                        val amt = amountText.toIntOrNull()
                        if (amt != null && amt > 0) {
                            onSave(amt, if (isIntake) FluidType.INTAKE else FluidType.URINE)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}
