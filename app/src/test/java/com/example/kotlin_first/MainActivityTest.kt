package com.example.kotlin_first

import android.os.Build
import android.widget.TextView
import com.example.kotlin_first.view.activity.MainActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MainActivityTest {

    @Test
    fun itShouldDisplayWelcomeText() {
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        val textView = activity.findViewById(R.id.welcome_view) as TextView

        assertEquals(textView.text, activity.getString(R.string.welcome_txt))
    }
}