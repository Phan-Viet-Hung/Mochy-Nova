// DeezerLyricsProvider.kt

package com.nova.musicplayer.lyrics

import android.content.Context
import com.music.directlyrics.Deezer
import com.nova.musicplayer.constants.EnableDeezerKey
import com.nova.musicplayer.utils.dataStore
import com.nova.musicplayer.utils.get

object DeezerLyricsProvider : LyricsProvider {
    override val name = "Deezer"
    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableDeezerKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = Deezer.getLyrics(title, artist, duration)
}
