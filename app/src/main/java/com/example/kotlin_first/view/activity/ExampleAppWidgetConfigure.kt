package com.example.kotlin_first.view.activity

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.kotlin_first.R
import com.example.kotlin_first.interfaces.RandomMusicLoader
import com.example.kotlin_first.utils.MUSIC_RANDOM_SEED
import com.example.kotlin_first.view.widget.ExampleAppWidgetProvider


class ExampleAppWidgetConfigure : Activity(), View.OnClickListener {

    private val MY_PERMISSION_REQUEST_CODE = 100

    override fun onClick(v: View?) {
        checkPermission()
        val resultVal = Intent()
        resultVal.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultVal)
    }

    var appWidgetId: Int = INVALID_APPWIDGET_ID

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
        val musicLoader = RandomMusicLoader
        ExampleAppWidgetProvider.updateWidget(
            this, getInstance(this), appWidgetId,
            musicLoader.getRandomMusic(contentResolver, MUSIC_RANDOM_SEED)
        )
        finish();
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