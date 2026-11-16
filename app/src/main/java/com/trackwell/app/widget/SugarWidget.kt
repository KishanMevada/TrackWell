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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.domain.util.DateTimeUtils
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class SugarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val sugarRepo = entryPoint.sugarRepository()
        val today = DateTimeUtils.getTodayIso()
        val entries = sugarRepo.observeEntriesForDate(today).first()

        val nextSlot = SugarSlot.suggestCurrentSlot()
        val nextEntry = entries[nextSlot]

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
                        text = "Next: ${nextSlot.title}",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    if (nextEntry != null) {
                        Text(
                            text = "Logged: ${nextEntry.sugarMgDl} mg/dL (${nextEntry.insulinUnits} U)",
                            style = TextStyle(fontSize = 12.sp)
                        )
                    } else {
                        Text(
                            text = "Pending recording",
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Button(
                        text = if (nextEntry != null) "Edit Sugar" else "Log Reading",
                        onClick = actionStartActivity(component)
                    )
                }
            }
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(SugarWidget::class.java)
            glanceIds.forEach { glanceId ->
                SugarWidget().update(context, glanceId)
            }
        }
    }
}

class SugarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SugarWidget()
}
