package com.essence.essenceapp.ui.resilience

data class CrashEvent(
    val throwable: Throwable,
    val isFatal: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
