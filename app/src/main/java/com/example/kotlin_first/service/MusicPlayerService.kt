package com.example.kotlin_first.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import com.example.kotlin_first.utils.COMPLETE_PLAYER_ACTION
import com.example.kotlin_first.utils.INIT_MUSIC_ACTION
import com.example.kotlin_first.utils.INIT_MUSIC_EXTRA
import com.example.kotlin_first.utils.PLAY_PAUSE_ACTION
import com.example.kotlin_first.view.widget.PlayerAppWidgetProvider

class MusicPlayerService : Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private var finishIntent: Intent? = null
    private lateinit var musicPath: Uri

    override fun onCreate() {
        finishIntent =
            Intent(applicationContext, PlayerAppWidgetProvider::class.java).apply {
                action = COMPLETE_PLAYER_ACTION
            }
        initMediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(INIT_MUSIC_ACTION))
            musicPath = intent?.extras?.get(INIT_MUSIC_EXTRA) as Uri
        else if (intent?.action.equals(PLAY_PAUSE_ACTION))
            if (mediaPlayer.isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initMediaPlayer() {
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
        } else
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun playMusic() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(applicationContext, musicPath)
        mediaPlayer.prepareAsync()
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        length = mediaPlayer.currentPosition
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.seekTo(length)
        mp?.start()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        length = 0
        sendBroadcast(finishIntent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer.stop()
        mediaPlayer.release()
        stopService(finishIntent)
        return false
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }

    companion object {
        val mediaPlayer: MediaPlayer = MediaPlayer()
        var length = 0
        fun isPlaying() = mediaPlayer.isPlaying
    }

}