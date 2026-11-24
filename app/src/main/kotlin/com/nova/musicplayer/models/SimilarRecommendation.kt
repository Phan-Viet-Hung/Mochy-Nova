// SimilarRecommendation.kt

package com.nova.musicplayer.models

import com.music.innertube.models.YTItem
import com.nova.musicplayer.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
