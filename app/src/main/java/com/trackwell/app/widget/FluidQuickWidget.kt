package com.trackwell.app.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

class FluidQuickWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val fluidRepo = entryPoint.fluidRepository()
        val prefs = entryPoint.userPreferences()

        val today = DateTimeUtils.getTodayIso()
        val summary = fluidRepo.observeDailySummary(today).first()
        val settings = prefs.settingsFlow.first()
        val defaultAmount = settings.defaultWidgetAmountMl

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "In: ${summary.totalIntakeMl} ml  |  Out: ${summary.totalUrineMl} ml",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            text = "+$defaultAmount Water",
                            onClick = actionRunCallback<QuickLogActionCallback>(
                                actionParametersOf(
                                    QuickLogActionCallback.KEY_AMOUNT to defaultAmount,
                                    QuickLogActionCallback.KEY_TYPE to FluidType.INTAKE.name
                                )
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Button(
                            text = "+$defaultAmount Urine",
                            onClick = actionRunCallback<QuickLogActionCallback>(
                                actionParametersOf(
                                    QuickLogActionCallback.KEY_AMOUNT to defaultAmount,
                                    QuickLogActionCallback.KEY_TYPE to FluidType.URINE.name
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(FluidQuickWidget::class.java)
            glanceIds.forEach { glanceId ->
                FluidQuickWidget().update(context, glanceId)
            }
        }
    }
}

class QuickLogActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val amount = parameters[KEY_AMOUNT] ?: 200
        val typeStr = parameters[KEY_TYPE] ?: FluidType.INTAKE.name
        val type = FluidType.valueOf(typeStr)

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val fluidRepo = entryPoint.fluidRepository()

        fluidRepo.logFluid(
            date = DateTimeUtils.getTodayIso(),
            timestamp = System.currentTimeMillis(),
            amountMl = amount,
            type = type
        )

        FluidQuickWidget.updateAll(context)
        FluidMediumWidget.updateAll(context)
    }

    companion object {
        val KEY_AMOUNT = ActionParameters.Key<Int>("key_amount")
        val KEY_TYPE = ActionParameters.Key<String>("key_type")
    }
}

class FluidQuickWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FluidQuickWidget()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun fluidRepository(): com.trackwell.app.domain.repository.FluidRepository
    fun sugarRepository(): com.trackwell.app.domain.repository.SugarRepository
    fun dailyHealthRepository(): com.trackwell.app.domain.repository.DailyHealthRepository
    fun userPreferences(): com.trackwell.app.data.preferences.UserPreferences
}
