package com.essence.essenceapp.feature.song.ui.manager

data class NowPlayingInfo(
    val songLookup: String,
    val title: String,
    val artistName: String,
    val imageKey: String?
)