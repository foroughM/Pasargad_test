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
import com.example.kotlin_first.service.UpdateProgressService
import com.example.kotlin_first.utils.*


class ExampleAppWidgetProvider : AppWidgetProvider() {

    override fun onDisabled(context: Context?) {
        context?.stopService(playerServiceIntent)
        context?.stopService(updateIntent)
        super.onDisabled(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(playPauseAction)) {
            appWidgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            updateView(context, appWidgetId, MusicPlayerService.isPlaying())
            if (updateIntent == null)
                updateIntent =
                    Intent(context, UpdateProgressService::class.java).apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId
                        )
                        putExtra(musicDurationExtra, music.duration)
                    }
            updateIntent?.action = if (MusicPlayerService.isPlaying())
                cancelProgressAction
            else
                updateProgressAction
            context?.startService(updateIntent)
            Intent(context, MusicPlayerService::class.java).apply {
                this.action = playPauseAction
                context?.startService(this)
            }
        } else if (intent?.action.equals(completedPlayerAction)) {
            updateView(context, appWidgetId, true)
        }
        super.onReceive(context, intent)
    }

    private fun updateView(
        context: Context?,
        appWidgetId: Int?,
        showPlayBtn: Boolean
    ) {
        val views = RemoteViews(context?.packageName, R.layout.example_appwidget).apply {
            setImageViewResource(
                R.id.play_btn,
                if (showPlayBtn) R.drawable.ic_play_arrow_black_24dp
                else R.drawable.ic_pause_black_24dp
            )
        }
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId!!, views)
    }

    companion object {
        lateinit var music: Music
        var appWidgetId: Int? = null
        lateinit var playerServiceIntent: Intent
        var updateIntent: Intent? = null
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            randomMusic: Music
        ) {
            music = randomMusic
            val views = RemoteViews(
                context.packageName,
                R.layout.example_appwidget
            ).apply {
                val clickIntent = Intent(context, ExampleAppWidgetProvider::class.java)
                clickIntent.action = playPauseAction
                clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setOnClickPendingIntent(
                    R.id.play_btn,
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        clickIntent,
                        0
                    )
                )
                setTextViewText(R.id.song_info, music.getInfo())
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
            playerServiceIntent = Intent(context, MusicPlayerService::class.java).apply {
                this.action = initMusicAction
                this.putExtra(initMusicExtra, music.path)
                context.startService(this)
            }
        }
    }
}