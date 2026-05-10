package com.essence.essenceapp.shared.streaming

interface AudioPrewarmPort {
    fun prewarm(url: String)
    fun cancel()
}
