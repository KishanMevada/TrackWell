package com.trackwell.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
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
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class FluidMediumWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val fluidRepo = entryPoint.fluidRepository()
        val today = DateTimeUtils.getTodayIso()
        val summary = fluidRepo.observeDailySummary(today).first()

        val component = android.content.ComponentName(context, WidgetQuickEntryActivity::class.java)

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Fluid Balance",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Intake: ${summary.totalIntakeMl} ml",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = GlanceModifier.width(12.dp))
                        Text(
                            text = "Urine: ${summary.totalUrineMl} ml",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = GlanceModifier.width(12.dp))
                        val sign = if (summary.balanceMl > 0) "+" else ""
                        Text(
                            text = "Bal: $sign${summary.balanceMl} ml",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // Buttons: +100ml, +200ml, and Custom "+"
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            text = "+100 ml",
                            onClick = actionRunCallback<QuickLogActionCallback>(
                                actionParametersOf(
                                    QuickLogActionCallback.KEY_AMOUNT to 100,
                                    QuickLogActionCallback.KEY_TYPE to FluidType.INTAKE.name
                                )
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Button(
                            text = "+200 ml",
                            onClick = actionRunCallback<QuickLogActionCallback>(
                                actionParametersOf(
                                    QuickLogActionCallback.KEY_AMOUNT to 200,
                                    QuickLogActionCallback.KEY_TYPE to FluidType.INTAKE.name
                                )
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Button(
                            text = "+ Custom",
                            onClick = actionStartActivity(component)
                        )
                    }
                }
            }
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(FluidMediumWidget::class.java)
            glanceIds.forEach { glanceId ->
                FluidMediumWidget().update(context, glanceId)
            }
        }
    }
}

class FluidMediumWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FluidMediumWidget()
}
