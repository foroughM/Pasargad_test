package com.example.kotlin_first.data.model

import android.net.Uri
import java.io.Serializable

class Music : Serializable {
    var songId: Int? = null
    var title: String? = null
    var artist: String? = null
    var album: String? = null
    var duration: String? = null
    var path: Uri? = null

    fun getInfo(): String {
        return "$title $artist $album"
    }
}