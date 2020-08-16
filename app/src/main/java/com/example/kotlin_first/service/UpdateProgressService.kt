package com.example.kotlin_first.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.RemoteViews
import com.example.kotlin_first.R
import com.example.kotlin_first.utils.CANCEL_PROGRESS_ACTION
import com.example.kotlin_first.utils.MUSIC_DURATION_EXTRA
import com.example.kotlin_first.utils.UPDATE_PROGRESS_ACTION
import java.text.DecimalFormat

class UpdateProgressService : Service() {

    private val finished: Long = 0
    private var durationMin: Long? = null
    private var durationSec: Long? = null
    private var millisLeft: Long? = null
    private var countDown: CountDownTimer? = null
    private var duration: Long? = null
    var appwidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getExtras(intent)
        durationMin = duration?.div(60 * 1000)
        durationSec = duration?.div(1000)?.rem(60)
        if (intent?.action.equals(UPDATE_PROGRESS_ACTION))
            startCounter()
        else if (intent?.action.equals(CANCEL_PROGRESS_ACTION))
            cancelCounter()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun getExtras(intent: Intent?) {
        duration = intent?.extras?.getLong(MUSIC_DURATION_EXTRA)
        appwidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private fun cancelCounter() {
        countDown?.cancel()
        countDown = null
    }

    private fun startCounter() {
        countDown = object : CountDownTimer(
            if (millisLeft == null || millisLeft == finished) duration!!
            else duration!!.minus(millisLeft!!), 1000
        ) {
            override fun onFinish() {
                millisLeft = finished
                updateProgressWidget(durationMin, durationSec, appwidgetId)
            }

            override fun onTick(millisUntilFinished: Long) {
                millisLeft = duration?.minus(millisUntilFinished)
                val min = millisLeft?.div(60 * 1000)
                val sec = millisLeft?.div(1000)?.rem(60)
                updateProgressWidget(min, sec, appwidgetId)
            }
        }
        countDown?.start()
    }

    private fun updateProgressWidget(min: Long?, sec: Long?, appWidgetId: Int) {
        val views = RemoteViews(
            packageName,
            R.layout.player_appwidget
        ).apply {
            val formatter = DecimalFormat(getString(R.string.decimal_format))
            setTextViewText(
                R.id.progress_info,
                "${formatter.format(min)}:${formatter.format(sec)}/" +
                        "${formatter.format(durationMin)}:${formatter.format(durationSec)}"
            )
        }
        AppWidgetManager.getInstance(applicationContext).updateAppWidget(appWidgetId, views)
    }
}