package com.example.kotlin_first.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.kotlin_first.R
import com.example.kotlin_first.data.model.Music
import com.example.kotlin_first.service.MusicPlayerService

class ExampleAppWidgetProvider : AppWidgetProvider() {

    override fun onDisabled(context: Context?) {
        context?.stopService(playerServiceIntent)
        super.onDisabled(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(playPauseAction)) {
            Intent(context, MusicPlayerService::class.java).apply {
                this.action = playPauseAction
                context?.startService(this)
            }
        }
        super.onReceive(context, intent)
    }

    companion object {
        const val playPauseAction = "musicPlayer.playPause"
        const val initMusicAction = "musicPlayer.initMusic"
        const val initMusicExtra = "music_path"
        lateinit var playerServiceIntent: Intent
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            music: Music
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.example_appwidget
            ).apply {
                val clickIntent = Intent(context, ExampleAppWidgetProvider::class.java)
                clickIntent.action = playPauseAction
                setOnClickPendingIntent(
                    R.id.play_btn,
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        clickIntent,
                        0
                    )
                )
            }
            views.setTextViewText(R.id.song_info, music.getInfo())
            println("********************** on widgetId $appWidgetId")
            appWidgetManager.updateAppWidget(appWidgetId, views)
            playerServiceIntent = Intent(context, MusicPlayerService::class.java).apply {
                this.action = initMusicAction
                this.putExtra(initMusicExtra, music.path)
                context.startService(this)
            }
        }
    }
}