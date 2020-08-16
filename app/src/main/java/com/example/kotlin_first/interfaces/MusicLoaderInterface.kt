package com.example.kotlin_first.interfaces

import android.content.ContentResolver
import com.example.kotlin_first.data.model.Music

interface MusicLoaderInterface {
    fun loadMusic(contentResolver: ContentResolver): ArrayList<Music>
}