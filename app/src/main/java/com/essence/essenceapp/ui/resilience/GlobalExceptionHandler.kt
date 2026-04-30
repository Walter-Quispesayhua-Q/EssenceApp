package com.essence.essenceapp.ui.resilience

import android.os.Looper
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineExceptionHandler

private const val TAG = "RESILIENCE"

@Singleton
class GlobalExceptionHandler @Inject constructor(
    private val crashReporter: CrashReporter
) {

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception captured", throwable)
        crashReporter.report(throwable, isFatal = false)
    }

    fun installAsDefault() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught exception in ${thread.name}", throwable)
            val isMainThread = Looper.getMainLooper().thread === thread
            crashReporter.report(throwable, isFatal = isMainThread)
            if (isMainThread) {
                previous?.uncaughtException(thread, throwable)
            }
        }
    }
}
