package com.essence.essenceapp.feature.song.ui.playback.mapper

import com.essence.essenceapp.feature.song.domain.model.Song
import com.essence.essenceapp.feature.song.ui.playback.model.NowPlayingInfo

fun Song.toNowPlayingInfo(): NowPlayingInfo? {
    val url = streamingUrl?.takeIf { it.isNotBlank() } ?: return null

    return NowPlayingInfo(
        songId = id,
        songLookup = hlsMasterKey,
        title = title,
        artistName = artists.firstOrNull()?.nameArtist.orEmpty(),
        imageKey = imageKey,
        durationMs = durationMs.toLong(),
        streamingUrl = url
    )
}