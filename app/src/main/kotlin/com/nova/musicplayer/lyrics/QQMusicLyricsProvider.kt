// QQMusicLyricsProvider.kt

package com.nova.musicplayer.lyrics

import android.content.Context
import com.music.directlyrics.QQMusic
import com.nova.musicplayer.constants.EnableQQMusicKey
import com.nova.musicplayer.utils.dataStore
import com.nova.musicplayer.utils.get

object QQMusicLyricsProvider : LyricsProvider {
    override val name = "QQMusic"
    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableQQMusicKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = QQMusic.getLyrics(title, artist, duration)
}
