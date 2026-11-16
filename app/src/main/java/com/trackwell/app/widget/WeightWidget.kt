package com.trackwell.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.EntryPointAccessors

class WeightWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val dailyRepo = entryPoint.dailyHealthRepository()
        val today = DateTimeUtils.getTodayIso()
        val overview = dailyRepo.getDayOverview(today)

        val component = android.content.ComponentName(context, WidgetQuickEntryActivity::class.java)

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Morning Weight",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    val weightText = overview.morningWeightKg?.let { "$it kg" } ?: "Not logged"
                    Text(
                        text = weightText,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Button(
                        text = if (overview.morningWeightKg != null) "Edit Weight" else "Log Weight",
                        onClick = actionStartActivity(component)
                    )
                }
            }
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(WeightWidget::class.java)
            glanceIds.forEach { glanceId ->
                WeightWidget().update(context, glanceId)
            }
        }
    }
}

class WeightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeightWidget()
}
