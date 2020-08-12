package com.example.kotlin_first.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.kotlin_first.R
import com.example.kotlin_first.data.model.Music

class ExampleAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(
                context?.packageName,
                R.layout.example_appwidget
            )
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            music: Music
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.example_appwidget
            )
            views.setTextViewText(R.id.song_info, music.getInfo())
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }
}