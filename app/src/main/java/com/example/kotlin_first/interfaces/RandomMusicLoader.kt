package com.example.kotlin_first.interfaces

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.example.kotlin_first.data.model.Music
import kotlin.random.Random

object RandomMusicLoader : MusicLoaderInterface {

    private var musicList = ArrayList<Music>()

    override fun loadMusic(contentResolver: ContentResolver): ArrayList<Music> {
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
                        duration = musicCursor.getLong(durationColumn)
                        path = Uri.withAppendedPath(musicUri, "" + musicCursor.getInt(idColumn))
                    })
            } while (musicCursor.moveToNext())
            musicCursor.close()
        }
        return musicList
    }

    /**
     * Load musics and then return a random music
     */
    fun getRandomMusic(contentResolver: ContentResolver, seed: Int): Music {
        loadMusic(contentResolver)
        val random = Random(seed)
        return musicList[random.nextInt(musicList.size - 1)]
    }

}