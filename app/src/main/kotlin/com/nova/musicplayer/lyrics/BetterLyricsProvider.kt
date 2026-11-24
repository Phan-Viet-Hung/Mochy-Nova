// BetterLyricsProvider.kt

package com.nova.musicplayer.lyrics

import android.content.Context
import com.nova.musicplayer.betterlyrics.BetterLyrics
import com.nova.musicplayer.constants.EnableBetterLyricsKey
import com.nova.musicplayer.utils.dataStore
import com.nova.musicplayer.utils.get

object BetterLyricsProvider : LyricsProvider {
    override val name = "BetterLyrics"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableBetterLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = BetterLyrics.getLyrics(title, artist, duration, album)

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
        callback: (String) -> Unit,
    ) {
        BetterLyrics.getAllLyrics(title, artist, duration, album, callback)
    }
}
