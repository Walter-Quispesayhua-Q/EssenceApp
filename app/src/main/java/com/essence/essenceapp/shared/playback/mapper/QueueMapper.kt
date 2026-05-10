package com.essence.essenceapp.shared.playback.mapper

import com.essence.essenceapp.feature.song.domain.model.SongLookupHint
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import com.essence.essenceapp.shared.playback.model.PlaybackQueueItem

fun SongSimple.toQueueItem() = PlaybackQueueItem(
    songLookup = hlsMasterKey,
    songId = id,
    title = title,
    artistName = artistName,
    imageKey = imageKey,
    durationMs = durationMs.toLong()
)

fun List<SongSimple>.toQueueItems() = map { it.toQueueItem() }

fun PlaybackQueueItem.toLookupHint(): SongLookupHint =
    songId?.let { SongLookupHint.KnownPersisted(it) } ?: SongLookupHint.KnownNotPersisted
