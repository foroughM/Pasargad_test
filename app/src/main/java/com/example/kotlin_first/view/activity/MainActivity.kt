package com.example.kotlin_first.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlin_first.R

class MainActivity : AppCompatActivity() {

    val message : String = "hello kotlin :*"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println(message)
    }
}