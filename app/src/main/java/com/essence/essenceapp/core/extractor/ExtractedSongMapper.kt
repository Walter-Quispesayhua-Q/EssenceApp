package com.essence.essenceapp.core.extractor

import com.essence.essenceapp.feature.song.data.dto.SongSyncRequestApiDTO

fun ExtractedSong.toSyncRequestDTO() = SongSyncRequestApiDTO(
    videoId = videoId,
    title = title,
    durationMs = durationMs,
    uploaderName = uploaderName,
    uploaderUrl = uploaderUrl,
    thumbnailUrl = thumbnailUrl,
    streamingUrl = streamingUrl,
    viewCount = viewCount,
    releaseDate = releaseDate
)
