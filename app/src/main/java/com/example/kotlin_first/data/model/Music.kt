package com.example.kotlin_first.data.model

import android.net.Uri

class Music(
    private val songId: Int, private val title: String,
    private val artist: String, private val album: String,
    private val duration: String, private val path: Uri
) {
    fun getInfo(): String {
        return "$title $artist $album"
    }
}