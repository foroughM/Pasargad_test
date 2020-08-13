package com.example.kotlin_first.view.activity

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager.*
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.kotlin_first.R
import com.example.kotlin_first.data.model.Music
import com.example.kotlin_first.service.MusicPlayerService
import com.example.kotlin_first.view.widget.ExampleAppWidgetProvider
import kotlin.random.Random


class ExampleAppWidgetConfigure : Activity(), View.OnClickListener {

    private val MY_PERMISSION_REQUEST_CODE = 100

    override fun onClick(v: View?) {
        checkPermission()
        val resultVal = Intent()
        resultVal.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultVal)
    }

    var appWidgetId: Int = INVALID_APPWIDGET_ID
    var musicList = ArrayList<Music>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appwidget_config)
        appWidgetId = intent?.extras?.getInt(
            EXTRA_APPWIDGET_ID,
            INVALID_APPWIDGET_ID
        ) ?: INVALID_APPWIDGET_ID
        if (appWidgetId == INVALID_APPWIDGET_ID)
            finish()
        val saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener(this)
    }

    private fun getMusicList() {
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = contentResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn =
                musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn =
                musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn =
                musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn =
                musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val durationColumn =
                musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            do {
                musicList.add(
                    Music().apply {
                        songId = musicCursor.getInt(idColumn)
                        title = musicCursor.getString(titleColumn)
                        artist = musicCursor.getString(artistColumn)
                        album = musicCursor.getString(albumColumn)
                        duration = musicCursor.getString(durationColumn)
                        path = Uri.withAppendedPath(musicUri, "" + musicCursor.getInt(idColumn))
                    })
            } while (musicCursor.moveToNext())
            musicCursor.close()
            val random = Random(2)
            val music = musicList.get(random.nextInt(musicList.size - 1))
            ExampleAppWidgetProvider.updateWidget(this, getInstance(this), appWidgetId, music)
            finish();
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // show an alert dialog
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.need_permission))
                    builder.setTitle(getString(R.string.get_permission))
                    builder.setPositiveButton(
                        getString(R.string.save)
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            MY_PERMISSION_REQUEST_CODE
                        )
                    }
                    builder.setNeutralButton(getString(R.string.cancel), null)
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                // Permission already granted
                getMusicList()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getMusicList()
                } else {
                    finish()
                }
            }
        }
    }

}