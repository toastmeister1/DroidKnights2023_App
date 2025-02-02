package com.droidknights.app2023.widget

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import com.droidknights.app2023.widget.DroidKnightsWidgetReceiver.Companion.KEY_SESSION_IDS
import com.droidknights.app2023.core.designsystem.theme.KnightsGlanceTheme
import com.droidknights.app2023.core.model.Session
import com.droidknights.app2023.widget.di.WidgetModule
import dagger.hilt.EntryPoints

class DroidKnightsWidget : GlanceAppWidget() {

    companion object {
        const val KEY_SESSION_ID = "KEY_SESSION_ID"
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetModule: WidgetModule = EntryPoints.get(
            context.applicationContext,
            WidgetModule::class.java
        )

        provideContent {
            KnightsGlanceTheme {
                val state = currentState(stringSetPreferencesKey(KEY_SESSION_IDS))
                var list: List<Session> by remember(state) { mutableStateOf(listOf()) }

                LaunchedEffect(state) {
                    list = arrayListOf<Session>().apply {
                        state?.forEach {
                            this.add(widgetModule.getSessionUseCase().invoke(it))
                        }
                    }
                }

                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .background(GlanceTheme.colors.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WidgetTitle()
                    Spacer(modifier = GlanceModifier.height(16.dp))
                    LazyColumn {
                        items(list) {
                            WidgetSessionCard(it)
                        }
                    }
                }
            }
        }
    }
}