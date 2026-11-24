// PillReadings.kt

package com.nova.musicplayer.floating

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.nova.musicplayer.constants.LyricsRomanizeAsMainKey
import com.nova.musicplayer.constants.TranslateLanguageKey
import com.nova.musicplayer.constants.TranslateModeKey
import com.nova.musicplayer.constants.TranslateSourceLanguageKey
import com.nova.musicplayer.db.entities.LyricsEntity
import com.nova.musicplayer.lyrics.GoogleTranslateEngine
import com.nova.musicplayer.lyrics.LyricsEntry
import com.nova.musicplayer.lyrics.LyricsTranslationHelper
import com.nova.musicplayer.lyrics.YouTubeCaptionTranslation
import com.nova.musicplayer.ui.player.LineReading
import com.nova.musicplayer.ui.player.RomanizeLanguageKeys
import com.nova.musicplayer.ui.player.readSong
import com.nova.musicplayer.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// the reading and translation settings the lyrics page uses, read straight from the store since the pill has no screen of its own
internal data class ReadingSettings(
    val romanize: Map<Preferences.Key<Boolean>, Boolean>,
    val romanizedAsMain: Boolean,
    val language: String,
    val sourceLanguage: String,
    val mode: String,
)

internal fun readingSettings(context: Context): Flow<ReadingSettings> = context.dataStore.data
    .map { prefs ->
        ReadingSettings(
            romanize = RomanizeLanguageKeys.associateWith { prefs[it] ?: true },
            romanizedAsMain = prefs[LyricsRomanizeAsMainKey] ?: false,
            language = prefs[TranslateLanguageKey]?.takeIf { it.isNotBlank() } ?: "en",
            sourceLanguage = prefs[TranslateSourceLanguageKey]?.takeIf { it.isNotBlank() } ?: "auto",
            mode = prefs[TranslateModeKey] ?: "line",
        )
    }
    .distinctUntilChanged()

// every line's romanisation, worked out the way the lyrics page does it; null when the song is in nothing mochy romanises
internal suspend fun romanizedLines(lines: List<LyricsEntry>, settings: ReadingSettings): List<LineReading?>? {
    val out = arrayOfNulls<LineReading>(lines.size)
    readSong(lines, allowed = { settings.romanize[it] != false }) { index, reading -> out[index] = reading }
    return out.takeIf { found -> found.any { !it?.text.isNullOrBlank() } }?.toList()
}

// the translation the lyrics page would show: the saved one when it's in the chosen language, otherwise google's, kept in memory so it never replaces one the page saved
internal suspend fun translatedLines(lines: List<LyricsEntry>, saved: LyricsEntity?, settings: ReadingSettings): List<String?>? {
    val sung = lines.withIndex().filter { it.value.text.isNotBlank() }
    if (sung.isEmpty()) return null
    val target = settings.language
    val tags = setOf(
        target,
        LyricsTranslationHelper.languageTag(settings.sourceLanguage, target),
        YouTubeCaptionTranslation.storedLanguageTag(target),
    )
    val stored = saved?.takeIf { it.translatedLyrics.isNotBlank() && it.translationMode == settings.mode && it.translationLanguage in tags }
    val texts = stored?.translatedLyrics?.lines()
        ?: GoogleTranslateEngine.translate(sung.map { it.value.text }, target, settings.sourceLanguage)
        ?: return null
    val out = arrayOfNulls<String>(lines.size)
    sung.forEachIndexed { k, (index, entry) ->
        // a line already in the target language comes back the same, and showing it twice only doubles it
        out[index] = texts.getOrNull(k)?.takeIf { it.isNotBlank() && !it.trim().equals(entry.text.trim(), ignoreCase = true) }
    }
    return out.toList().takeIf { found -> found.any { it != null } }
}
