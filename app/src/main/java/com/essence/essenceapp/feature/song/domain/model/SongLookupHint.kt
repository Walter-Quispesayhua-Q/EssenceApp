package com.essence.essenceapp.feature.song.domain.model

sealed interface SongLookupHint {
    data object Unknown : SongLookupHint
    data class KnownPersisted(val id: Long) : SongLookupHint
    data object KnownNotPersisted : SongLookupHint
}
