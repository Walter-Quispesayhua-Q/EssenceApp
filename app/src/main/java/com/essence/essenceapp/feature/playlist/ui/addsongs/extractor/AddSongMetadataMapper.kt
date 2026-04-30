package com.essence.essenceapp.feature.playlist.ui.addsongs.extractor

import com.essence.essenceapp.feature.song.data.dto.SongSyncRequestApiDTO

fun AddSongMetadata.toSyncRequestDTO(): SongSyncRequestApiDTO = SongSyncRequestApiDTO(
    videoId = videoId,
    title = title,
    durationMs = durationMs,
    uploaderName = uploaderName,
    uploaderUrl = uploaderUrl,
    thumbnailUrl = thumbnailUrl,
    streamingUrl = null,
    viewCount = viewCount,
    releaseDate = releaseDate
)
