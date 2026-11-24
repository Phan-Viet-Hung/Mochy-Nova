package com.nova.musicplayer.di

import com.nova.musicplayer.eq.AudioLabStore
import com.nova.musicplayer.playback.SongTuning
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** for UI outside a Hilt view model (the player's sheets) that shares Audio Lab's settings */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AudioLabEntryPoint {
    fun audioLabStore(): AudioLabStore
    fun songTuning(): SongTuning
}
